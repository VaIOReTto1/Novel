import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { BookBasic, DraftInfo, ChapterItem } from '../types';

interface BookManageState {
  book: BookBasic | null;
  draft: DraftInfo;
  chapters: ChapterItem[];
  loading: boolean;
}

interface BookManageActions {
  load: () => Promise<void>;
}

type Store = BookManageState & BookManageActions;

export const useBookManageStore = create<Store>()(
  immer((set) => ({
    book: null,
    draft: { exists: false },
    chapters: [],
    loading: false,

    load: async () => {
      set((s) => { s.loading = true; });
      // 模拟数据
      await new Promise((r) => setTimeout(r, 300));
      set((s) => {
        s.book = { id: '1', title: '安国的尹锋的新书', authorName: '安国的尹锋', statusText: '待审核' };
        s.draft = { exists: true, updatedAt: Date.now() - 3600_000 };
        s.chapters = []; // 空态
        s.loading = false;
      });
    },
  }))
);


