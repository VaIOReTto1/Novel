export interface ReviewDetail {
  id: string;
  bookId: string;
  userId: string;
  userName: string;
  userAvatar?: string;
  title?: string;
  content: string;
  rating: number;
  likeCount: number;
  commentCount: number;
  isLiked: boolean;
  createTime: string;
  updateTime: string;
}

export interface Comment {
  id: string;
  reviewId: string;
  userId: string;
  userName: string;
  userAvatar?: string;
  content: string;
  likeCount: number;
  dislikeCount: number;
  isLiked: boolean;
  isDisliked: boolean;
  createTime: string;
  parentId?: string;
  replyToUser?: string;
  replies?: Comment[];
  // 评论标签（仅一级评论有）
  tag?: string;
}

export interface CommentListResponse {
  list: Comment[];
  total: number;
  page: number;
  pageSize: number;
  hasMore: boolean;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}