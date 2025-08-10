export interface Comment {
  id: string;
  commentContent: string;
  commentUser: string;
  commentUserId: number;
  commentUserPhoto: string;
  commentTime: string;
  rating?: number;
  likeCount?: number;
  isLiked?: boolean;
  // 评论标签（仅一级评论有）
  tag?: 'first_comment' | 'true_fan' | 'vip';
  // 书籍信息
  bookInfo?: {
    picUrl: string;
    authorName: string;
    bookName: string;
    bookId: string;
  };
}

export interface CommentResponse {
  commentTotal: number;
  comments: Comment[];
}

export interface CommentPageState {
  comments: Comment[];
  loading: boolean;
  isRefreshing: boolean;
  hasMore: boolean;
  error: string | null;
  currentBookId: string | null;
  currentPage: number;
  pageSize: number;
}

export interface CommentStore extends CommentPageState {
  loadComments: (bookId: string) => Promise<void>;
  refreshComments: () => Promise<void>;
  loadMoreComments: () => Promise<void>;
  likeComment: (commentId: string) => void;
  reset: () => void;
}