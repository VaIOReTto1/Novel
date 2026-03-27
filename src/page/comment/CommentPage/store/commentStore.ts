import { create } from 'zustand';
import { CommentStore, Comment, CommentResponse } from '../types';

// API基础URL - 仿照BookStore的实现
const BASE_URL_FRONT = 'http://47.110.147.60:8080/api/front/';

// API响应接口定义
interface ApiCommentResponse {
  code: string;
  message: string;
  data: {
    commentTotal: number;
    comments: {
      id: number;
      commentContent: string;
      commentUser: string;
      commentUserId: number;
      commentUserPhoto: string;
      commentTime: string;
    }[];
  } | null;
  ok: boolean;
}

// 从API获取评论数据 - 仿照BookStore的loadHomeRecommendBooks实现
const fetchCommentsFromApi = async (bookId: string): Promise<CommentResponse> => {
  try {
    console.log('正在获取评论数据，bookId:', bookId);

    const response = await fetch(`${BASE_URL_FRONT}book/comment/newest_list?bookId=${bookId}`, {
      method: 'GET',
      headers: {
        'Accept': '*/*',
        'Content-Type': 'application/json',
      },
    });

    console.log('API响应状态:', response.status, response.statusText);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const apiData: ApiCommentResponse = await response.json();
    console.log('API响应数据:', apiData);

    if (apiData.ok && apiData.data && apiData.data.comments.length > 0) {
      // 转换API数据格式并添加缺失字段（使用mock数据）
      const comments: Comment[] = apiData.data.comments.map((comment, index) => ({
        id: comment.id.toString(),
        commentContent: comment.commentContent,
        commentUser: comment.commentUser,
        commentUserId: comment.commentUserId,
        commentUserPhoto: comment.commentUserPhoto || `https://example.com/avatar${(index % 5) + 1}.jpg`,
        commentTime: comment.commentTime,
        // 缺失字段用mock数据生成
        rating: Math.floor(Math.random() * 2) + 4, // 4-5星评分
        likeCount: Math.floor(Math.random() * 200) + 10, // 10-210点赞数
        isLiked: Math.random() > 0.7, // 30%概率已点赞
      }));

      console.log(`获取到${comments.length}条评论数据`);
      return {
        commentTotal: apiData.data.commentTotal,
        comments: comments,
      };
    } else {
      console.log('API数据为空，返回空数据');
      return {
        commentTotal: 0,
        comments: [],
      };
    }
  } catch (error) {
    console.error('获取评论数据失败:', error);
    console.log('API请求失败，返回空数据');
    return {
      commentTotal: 0,
      comments: [],
    };
  }
};

export const useCommentStore = create<CommentStore>((set, get) => ({
  comments: [],
  loading: false,
  isRefreshing: false,
  hasMore: true,
  error: null,
  currentBookId: null, // 添加当前书籍ID字段
  currentPage: 0, // 添加当前页码字段
  pageSize: 6, // 每页显示6条评论

  loadComments: async (bookId: string) => {
    const { loading, isRefreshing, pageSize } = get();
    if (loading || isRefreshing) {return;}

    set({ loading: true, error: null, currentBookId: bookId, currentPage: 0 });

    try {
      console.log('[CommentStore] 开始加载评论数据，bookId:', bookId);
      const response = await fetchCommentsFromApi(bookId);

      // 实现分页逻辑，初始只显示前pageSize条评论
      const initialComments = response.comments.slice(0, pageSize);
      const hasMoreData = response.comments.length > pageSize;

      set({
        comments: initialComments,
        loading: false,
        hasMore: hasMoreData,
        currentPage: 1,
      });

      console.log('[CommentStore] 评论数据加载成功，显示', initialComments.length, '条，总共', response.comments.length, '条');
    } catch (error) {
      console.error('[CommentStore] 加载评论数据失败:', error);
      set({ loading: false, error: '加载评论失败' });
    }
  },

  refreshComments: async () => {
    const { isRefreshing, loading, pageSize } = get();
    if (isRefreshing || loading) {return;}

    set({ isRefreshing: true, error: null });

    try {
      console.log('[CommentStore] 开始刷新评论数据');
      // 获取当前bookId，从comments中获取或使用默认值
      const currentBookId = get().currentBookId || 'default-book-id';
      const response = await fetchCommentsFromApi(currentBookId);

      // 刷新时重置为第一页
      const initialComments = response.comments.slice(0, pageSize);
      const hasMoreData = response.comments.length > pageSize;

      set({
        comments: initialComments,
        isRefreshing: false,
        hasMore: hasMoreData,
        currentPage: 1,
      });

      console.log('[CommentStore] 评论数据刷新成功');
    } catch (error) {
      console.error('[CommentStore] 刷新评论数据失败:', error);
      set({ isRefreshing: false, error: '刷新评论失败' });
    }
  },

  loadMoreComments: async () => {
    const { loading, isRefreshing, hasMore, currentPage, pageSize, comments } = get();
    if (loading || isRefreshing || !hasMore) {return;}

    set({ loading: true });

    try {
      console.log('[CommentStore] 开始加载更多评论数据，当前页:', currentPage);

      // 获取当前bookId
      const currentBookId = get().currentBookId || 'default-book-id';
      const response = await fetchCommentsFromApi(currentBookId);

      // 计算下一页的数据
      const startIndex = currentPage * pageSize;
      const endIndex = startIndex + pageSize;
      const nextPageComments = response.comments.slice(startIndex, endIndex);

      if (nextPageComments.length > 0) {
        // 合并新数据到现有评论列表
        const updatedComments = [...comments, ...nextPageComments];
        const hasMoreData = endIndex < response.comments.length;

        set({
          comments: updatedComments,
          loading: false,
          hasMore: hasMoreData,
          currentPage: currentPage + 1,
        });

        console.log('[CommentStore] 加载更多评论成功，新增', nextPageComments.length, '条，总计', updatedComments.length, '条');
      } else {
        // 没有更多数据
        set({ loading: false, hasMore: false });
        console.log('[CommentStore] 没有更多评论数据');
      }
    } catch (error) {
      console.error('[CommentStore] 加载更多评论数据失败:', error);
      set({ loading: false, error: '加载更多评论失败' });
    }
  },

  likeComment: (commentId: string) => {
    const { comments } = get();
    const updatedComments = comments.map(comment => {
      if (comment.id === commentId) {
        const isLiked = !comment.isLiked;
        const likeCount = (comment.likeCount || 0) + (isLiked ? 1 : -1);
        return {
          ...comment,
          isLiked,
          likeCount: Math.max(0, likeCount),
        };
      }
      return comment;
    });

    set({ comments: updatedComments });
    console.log('[CommentStore] 评论点赞状态已更新，commentId:', commentId);
  },

  reset: () => {
    set({
      comments: [],
      loading: false,
      isRefreshing: false,
      hasMore: true,
      error: null,
      currentBookId: null,
      currentPage: 0,
    });
    console.log('[CommentStore] 状态已重置');
  },
}));
