import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { MainBookshelfState, TabData } from '../types';

// Tab数据
const MAIN_TABS: TabData[] = [
  { id: 'bookshelf', name: '书架', type: 'bookshelf' },
  { id: 'history', name: '历史', type: 'history' },
  { id: 'watchlist', name: '追剧', type: 'watchlist' },
  { id: 'community', name: '圈子', type: 'community' },
];

export const useMainBookshelfStore = create<MainBookshelfState>()(
  immer((set, _get) => ({
    // 初始状态 - 根据图片，历史Tab被选中
    currentTab: 'bookshelf',
    isLoading: false,
    error: null,
    
    // Actions
    setCurrentTab: (tabId: string) => set(state => {
      console.log('[MainBookshelfStore] 切换到Tab:', tabId);
      state.currentTab = tabId;
    }),
    
    setLoading: (loading: boolean) => set(state => {
      state.isLoading = loading;
    }),
    
    setError: (error: string | null) => set(state => {
      state.error = error;
    }),
  }))
);

// 获取Tab数据
export const getMainTabs = (): TabData[] => {
  return MAIN_TABS;
};

console.log('[MainBookshelfStore] Store initialized with tabs:', MAIN_TABS.length);