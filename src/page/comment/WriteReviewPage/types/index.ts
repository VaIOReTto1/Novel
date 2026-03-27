// WriteReviewPage 类型定义

export interface WriteReviewPageProps {
  bookId: string;
  source?: string;
  initialRating?: number;
}

export interface ReviewFormData {
  rating: number;
  content: string;
}

export interface WriteReviewState {
  // 表单数据
  rating: number;
  content: string;

  // UI状态
  isSubmitting: boolean;
  showTips: boolean;

  // 验证状态
  contentError: string;

  // 字符计数
  contentLength: number;
}

export interface WriteReviewActions {
  // 表单操作
  setRating: (rating: number) => void;
  setContent: (content: string) => void;

  // UI操作
  setSubmitting: (submitting: boolean) => void;
  toggleTips: () => void;

  // 验证操作
  validateForm: () => boolean;
  clearErrors: () => void;

  // 提交操作
  submitReview: (bookId: string) => Promise<boolean>;

  // 重置操作
  reset: () => void;
}

export type WriteReviewStore = WriteReviewState & WriteReviewActions;

// API 相关类型
export interface CommentSubmitRequest {
  bookId: number;
  commentContent: string;
  userId?: number;
}

export interface CommentInfo {
  id: number;
  commentContent: string;
  commentUser: string;
  commentUserId: number;
  commentUserPhoto: string;
  commentTime: string;
}

export interface BookCommentResponse {
  commentTotal: number;
  comments: CommentInfo[];
}

// 常量
export const REVIEW_CONSTANTS = {
  TITLE_MAX_LENGTH: 50,
  CONTENT_MAX_LENGTH: 500,
  CONTENT_MIN_LENGTH: 10,
  RATING_MIN: 1,
  RATING_MAX: 5,
} as const;
