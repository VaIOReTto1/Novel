import { create } from 'zustand';
import { ReviewDetail, Comment } from '../types/reviewDetailTypes';
import { reviewDetailApi } from '../api/reviewDetailApi';

interface ReviewDetailState {
  // 数据状态
  reviewDetail: ReviewDetail | null;
  comments: Comment[];

  // 加载状态
  isLoading: boolean;
  isRefreshing: boolean;
  isLoadingMore: boolean;

  // 分页状态
  currentPage: number;
  hasMoreComments: boolean;

  // 错误状态
  error: string | null;

  // Actions
  loadReviewDetail: (reviewId: string) => Promise<void>;
  refreshReviewDetail: () => Promise<void>;
  loadMoreComments: () => Promise<void>;
  toggleLike: (reviewId: string) => Promise<void>;
  toggleCommentLike: (commentId: string) => Promise<void>;
  toggleCommentDislike: (commentId: string) => Promise<void>;
  reset: () => void;
}

const initialState = {
  reviewDetail: null,
  comments: [],
  isLoading: false,
  isRefreshing: false,
  isLoadingMore: false,
  currentPage: 1,
  hasMoreComments: true,
  error: null,
};

export const useReviewDetailStore = create<ReviewDetailState>((set, get) => ({
  ...initialState,

  loadReviewDetail: async (reviewId: string) => {
    try {
      set({ isLoading: true, error: null });

      const [reviewResponse, commentsResponse] = await Promise.all([
        reviewDetailApi.getReviewDetail(reviewId),
        reviewDetailApi.getComments(reviewId, 1),
      ]);

      set({
        reviewDetail: reviewResponse.data,
        comments: commentsResponse.data.list,
        currentPage: 1,
        hasMoreComments: commentsResponse.data.hasMore,
        isLoading: false,
      });
    } catch (error) {
      console.error('[ReviewDetailStore] 加载评论详情失败:', error);
      set({
        error: error instanceof Error ? error.message : '加载失败',
        isLoading: false,
      });
    }
  },

  refreshReviewDetail: async () => {
    const { reviewDetail } = get();
    if (!reviewDetail) {return;}

    try {
      set({ isRefreshing: true, error: null });

      const [reviewResponse, commentsResponse] = await Promise.all([
        reviewDetailApi.getReviewDetail(reviewDetail.id),
        reviewDetailApi.getComments(reviewDetail.id, 1),
      ]);

      set({
        reviewDetail: reviewResponse.data,
        comments: commentsResponse.data.list,
        currentPage: 1,
        hasMoreComments: commentsResponse.data.hasMore,
        isRefreshing: false,
      });
    } catch (error) {
      console.error('[ReviewDetailStore] 刷新评论详情失败:', error);
      set({
        error: error instanceof Error ? error.message : '刷新失败',
        isRefreshing: false,
      });
    }
  },

  loadMoreComments: async () => {
    const { reviewDetail, currentPage, hasMoreComments, isLoadingMore } = get();

    if (!reviewDetail || !hasMoreComments || isLoadingMore) {return;}

    try {
      set({ isLoadingMore: true, error: null });

      const nextPage = currentPage + 1;
      const response = await reviewDetailApi.getComments(reviewDetail.id, nextPage);

      set((state) => ({
        comments: [...state.comments, ...response.data.list],
        currentPage: nextPage,
        hasMoreComments: response.data.hasMore,
        isLoadingMore: false,
      }));
    } catch (error) {
      console.error('[ReviewDetailStore] 加载更多评论失败:', error);
      set({
        error: error instanceof Error ? error.message : '加载失败',
        isLoadingMore: false,
      });
    }
  },

  toggleLike: async (reviewId: string) => {
    const { reviewDetail } = get();
    if (!reviewDetail || reviewDetail.id !== reviewId) {return;}

    // 乐观更新
    const newIsLiked = !reviewDetail.isLiked;
    const newLikeCount = newIsLiked
      ? reviewDetail.likeCount + 1
      : reviewDetail.likeCount - 1;

    set({
      reviewDetail: {
        ...reviewDetail,
        isLiked: newIsLiked,
        likeCount: newLikeCount,
      },
    });

    try {
      await reviewDetailApi.toggleLike(reviewId);
    } catch (error) {
      console.error('[ReviewDetailStore] 点赞操作失败:', error);
      // 回滚状态
      set({
        reviewDetail: {
          ...reviewDetail,
          isLiked: !newIsLiked,
          likeCount: reviewDetail.likeCount,
        },
      });
    }
  },

  toggleCommentLike: async (commentId: string) => {
    const { comments } = get();

    // 递归查找评论（包括回复）
    const findAndUpdateComment = (commentsList: Comment[], id: string): Comment[] => {
      return commentsList.map(comment => {
        if (comment.id === id) {
          const newIsLiked = !comment.isLiked;
          const newLikeCount = newIsLiked
            ? comment.likeCount + 1
            : comment.likeCount - 1;
          const newIsDisliked = newIsLiked ? false : comment.isDisliked;
          const newDislikeCount = newIsLiked && comment.isDisliked
            ? Math.max(0, comment.dislikeCount - 1)
            : comment.dislikeCount;

          return {
            ...comment,
            isLiked: newIsLiked,
            likeCount: Math.max(0, newLikeCount),
            isDisliked: newIsDisliked,
            dislikeCount: newDislikeCount,
          };
        }

        if (comment.replies && comment.replies.length > 0) {
          return {
            ...comment,
            replies: findAndUpdateComment(comment.replies, id),
          };
        }

        return comment;
      });
    };

    const originalComments = [...comments];
    const updatedComments = findAndUpdateComment(comments, commentId);

    set({ comments: updatedComments });

    try {
      await reviewDetailApi.toggleCommentLike(commentId);
    } catch (error) {
      console.error('[ReviewDetailStore] 评论点赞操作失败:', error);
      // 回滚状态
      set({ comments: originalComments });
    }
  },

  toggleCommentDislike: async (commentId: string) => {
    const { comments } = get();

    // 递归查找评论（包括回复）
    const findAndUpdateComment = (commentsList: Comment[], id: string): Comment[] => {
      return commentsList.map(comment => {
        if (comment.id === id) {
          const newIsDisliked = !comment.isDisliked;
          const newDislikeCount = newIsDisliked
            ? comment.dislikeCount + 1
            : comment.dislikeCount - 1;
          const newIsLiked = newIsDisliked ? false : comment.isLiked;
          const newLikeCount = newIsDisliked && comment.isLiked
            ? Math.max(0, comment.likeCount - 1)
            : comment.likeCount;

          return {
            ...comment,
            isDisliked: newIsDisliked,
            dislikeCount: Math.max(0, newDislikeCount),
            isLiked: newIsLiked,
            likeCount: newLikeCount,
          };
        }

        if (comment.replies && comment.replies.length > 0) {
          return {
            ...comment,
            replies: findAndUpdateComment(comment.replies, id),
          };
        }

        return comment;
      });
    };

    const originalComments = [...comments];
    const updatedComments = findAndUpdateComment(comments, commentId);

    set({ comments: updatedComments });

    try {
      await reviewDetailApi.toggleCommentDislike(commentId);
    } catch (error) {
      console.error('[ReviewDetailStore] 评论踩操作失败:', error);
      // 回滚状态
      set({ comments: originalComments });
    }
  },

  reset: () => {
    set(initialState);
  },
}));
