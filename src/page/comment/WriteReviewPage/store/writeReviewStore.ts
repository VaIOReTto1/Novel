import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

export interface ReviewData {
  rating: number;
  title: string;
  content: string;
}

export interface WriteReviewState {
  // 表单数据
  reviewData: ReviewData;

  // 状态管理
  isSubmitting: boolean;
  loading: boolean;
  error: string | null;

  // 页面参数
  bookId: string | null;
  source: string | null;
}

interface WriteReviewActions {
  // 初始化
  initialize: (bookId?: string, source?: string) => void;
  reset: () => void;

  // 表单操作
  updateRating: (rating: number) => void;
  updateTitle: (title: string) => void;
  updateContent: (content: string) => void;
  updateReviewData: (data: Partial<ReviewData>) => void;

  // 提交操作
  submitReview: () => Promise<boolean>;
  setSubmitting: (isSubmitting: boolean) => void;

  // 错误处理
  setError: (error: string | null) => void;
  clearError: () => void;
}

type WriteReviewStore = WriteReviewState & WriteReviewActions;

const initialState: WriteReviewState = {
  reviewData: {
    rating: 5,
    title: '',
    content: '',
  },
  isSubmitting: false,
  loading: false,
  error: null,
  bookId: null,
  source: null,
};

export const useWriteReviewStore = create<WriteReviewStore>()(
  immer((set, get) => ({
    ...initialState,

    initialize: (bookId, source) => set((state) => {
      console.log('[WriteReviewStore] 初始化页面，bookId:', bookId, 'source:', source);
      state.bookId = bookId || null;
      state.source = source || null;
      state.error = null;
    }),

    reset: () => set((state) => {
      console.log('[WriteReviewStore] 重置状态');
      Object.assign(state, initialState);
    }),

    updateRating: (rating) => set((state) => {
      state.reviewData.rating = rating;
      state.error = null;
    }),

    updateTitle: (title) => set((state) => {
      state.reviewData.title = title;
      state.error = null;
    }),

    updateContent: (content) => set((state) => {
      state.reviewData.content = content;
      state.error = null;
    }),

    updateReviewData: (data) => set((state) => {
      Object.assign(state.reviewData, data);
      state.error = null;
    }),

    submitReview: async () => {
      const { reviewData, bookId, source } = get();

      // 表单验证
      if (!reviewData.title.trim()) {
        set((state) => {
          state.error = '请输入评论标题';
        });
        return false;
      }

      if (!reviewData.content.trim()) {
        set((state) => {
          state.error = '请输入评论内容';
        });
        return false;
      }

      set((state) => {
        state.isSubmitting = true;
        state.error = null;
      });

      try {
        console.log('[WriteReviewStore] 提交评论:', { ...reviewData, bookId, source });

        // TODO: 实现真实的API调用
        // const response = await submitReviewAPI({ ...reviewData, bookId, source });

        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000));

        console.log('[WriteReviewStore] 评论提交成功');
        return true;
      } catch (error) {
        console.error('[WriteReviewStore] 提交评论失败:', error);
        set((state) => {
          state.error = '评论发表失败，请重试';
        });
        return false;
      } finally {
        set((state) => {
          state.isSubmitting = false;
        });
      }
    },

    setSubmitting: (isSubmitting) => set((state) => {
      state.isSubmitting = isSubmitting;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    clearError: () => set((state) => {
      state.error = null;
    }),
  }))
);
