import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { HistoryItem } from '../types';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';

export interface HistoryState {
  historyItems: HistoryItem[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  pageSize: number;
  hasMore: boolean;

  isRefreshing: boolean;
  isLoadingMore: boolean;

  selectedTab: 'all' | 'book' | 'audio' | 'video' | 'drama';
  viewType: 'grid' | 'list';
  searchQuery: string;

  // 缓存的完整历史数据
  cachedHistoryItems: HistoryItem[];
}

interface HistoryActions {
  // 基础状态更新
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setRefreshing: (refreshing: boolean) => void;
  setLoadingMore: (loadingMore: boolean) => void;
  setSelectedTab: (tab: 'all' | 'book' | 'audio' | 'video' | 'drama') => void;
  setViewType: (viewType: 'grid' | 'list') => void;
  setSearchQuery: (query: string) => void;

  // 数据更新
  setHistoryItems: (items: HistoryItem[]) => void;
  appendHistoryItems: (items: HistoryItem[]) => void;

  // 异步操作
  loadHistoryItems: (isRefresh?: boolean) => Promise<void>;
  refreshHistory: () => Promise<void>;
  loadMoreHistory: () => Promise<void>;
  searchHistory: (query: string) => Promise<void>;
  deleteHistoryItem: (itemId: number) => Promise<void>;
}

type HistoryStore = HistoryState & HistoryActions;

const initialState: HistoryState = {
  historyItems: [],
  loading: false,
  error: null,
  currentPage: 1,
  pageSize: 20,
  hasMore: true,

  isRefreshing: false,
  isLoadingMore: false,

  selectedTab: 'all',
  viewType: 'grid',
  searchQuery: '',

  cachedHistoryItems: [],
};

export const useHistoryStore = create<HistoryStore>()(
  immer((set, get) => ({
    ...initialState,

    // 基础状态更新
    setLoading: (loading) => set((state) => {
      state.loading = loading;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    setRefreshing: (refreshing) => set((state) => {
      state.isRefreshing = refreshing;
    }),

    setLoadingMore: (loadingMore) => set((state) => {
      state.isLoadingMore = loadingMore;
    }),

    setSelectedTab: (tab) => set((state) => {
      state.selectedTab = tab;
      state.currentPage = 1;
      state.hasMore = true;
    }),

    setViewType: (viewType) => {
      const currentState = get();
      // 避免相同值的重复设置
      if (currentState.viewType === viewType) {
        return;
      }
      set((state) => {
        state.viewType = viewType;
      });
    },

    setSearchQuery: (query) => set((state) => {
      state.searchQuery = query;
    }),

    // 数据更新
    setHistoryItems: (items) => set((state) => {
      state.historyItems = items;
    }),

    appendHistoryItems: (items) => set((state) => {
      state.historyItems.push(...items);
    }),

    // 异步操作
    loadHistoryItems: async (isRefresh = false) => {
      const { selectedTab } = get();
      
      if (isRefresh) {
        set((state) => {
          state.currentPage = 1;
          state.hasMore = true;
          state.error = null;
        });
      }

      set((state) => {
        if (isRefresh) {
          state.isRefreshing = true;
        } else {
          state.loading = true;
        }
      });

      try {
        // 使用真实的桥接方法获取历史数据
        const historyData = await NavigationBridge.getReadingHistory();
        
        // 转换数据格式以匹配HistoryItem接口
        const convertedItems: HistoryItem[] = historyData.historyItems.map((item: any, index: number) => ({
          id: index + 1,
          title: item.title || '未知标题',
          author: item.author || '未知作者',
          description: item.description || '暂无描述',
          coverUrl: item.coverUrl || 'https://placehold.co/220x300?text=封面',
          lastReadTime: new Date(item.lastReadTime).toISOString(),
          readProgress: Math.round(item.readProgress * 100), // 转换为百分比
          type: item.type || 'book',
          categoryId: item.categoryId || 1,
          readCount: item.readCount || 0,
          rating: item.rating || 0,
        }));

        // 根据选中的tab过滤数据
        const filteredItems = selectedTab === 'all' ? 
          convertedItems : 
          convertedItems.filter(item => item.type === selectedTab);

        set((state) => {
          state.historyItems = filteredItems;
          state.cachedHistoryItems = convertedItems;
          state.hasMore = false; // 目前一次性加载所有数据
          state.loading = false;
          state.isRefreshing = false;
          state.error = null;
        });
      } catch (error) {
        console.error('加载历史记录失败:', error);
        set((state) => {
          state.error = error instanceof Error ? error.message : '加载历史记录失败';
          state.loading = false;
          state.isRefreshing = false;
        });
      }
    },

    loadMoreHistory: async () => {
      const state = get();
      if (!state.hasMore || state.loading) {return;}

      set((draft) => {
        draft.hasMore = false;
        draft.loading = false;
        draft.isLoadingMore = false;
        draft.error = null;
      });
    },

    refreshHistory: async () => {
      const { loadHistoryItems } = get();
      await loadHistoryItems(true);
    },

    searchHistory: async (query) => {
      const state = get();
      set((draft) => {
        draft.searchQuery = query;
        draft.loading = true;
      });

      try {
        await new Promise(resolve => setTimeout(resolve, 500));

        const filteredItems = state.cachedHistoryItems.filter(item =>
          item.title.toLowerCase().includes(query.toLowerCase()) ||
          item.author.toLowerCase().includes(query.toLowerCase())
        );

        set((draft) => {
          draft.historyItems = filteredItems;
          draft.loading = false;
        });

      } catch (error) {
        set((draft) => {
          draft.loading = false;
          draft.error = error instanceof Error ? error.message : '搜索失败';
        });
      }
    },

    deleteHistoryItem: async (itemId) => {
      set((draft) => {
        draft.historyItems = draft.historyItems.filter(item => item.id !== itemId);
        draft.cachedHistoryItems = draft.cachedHistoryItems.filter(item => item.id !== itemId);
      });

      // 这里可以添加实际的删除API调用
      console.log(`删除历史记录项: ${itemId}`);
    },
  }))
);
