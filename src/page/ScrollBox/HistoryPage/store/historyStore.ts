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

// 模拟API数据
const generateMockHistoryItems = (page: number, pageSize: number, type?: string): HistoryItem[] => {
  const items: HistoryItem[] = [];
  const startIndex = (page - 1) * pageSize;

  for (let i = 0; i < pageSize; i++) {
    const index = startIndex + i + 1;
    const itemType = type === 'all' ?
      (['book', 'audio', 'video', 'drama'] as const)[index % 4] :
      (type as 'book' | 'audio' | 'video' | 'drama');

    items.push({
      id: index,
      title: `${getTypeTitle(itemType)} ${index}`,
      author: `作者${index}`,
      description: `这是${getTypeTitle(itemType)}的描述内容，讲述了一个精彩的故事...`,
      coverUrl: `https://placehold.co/220x300?text=${index}`,
      lastReadTime: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      readProgress: Math.floor(Math.random() * 100),
      type: itemType,
      categoryId: Math.floor(Math.random() * 10) + 1,
      readCount: Math.floor(Math.random() * 10000),
      rating: Math.random() * 5,
    });
  }

  return items;
};

const getTypeTitle = (type: string): string => {
  switch (type) {
    case 'book': return '小说';
    case 'audio': return '听书';
    case 'video': return '视频';
    case 'drama': return '短剧';
    default: return '内容';
  }
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
        draft.loading = true;
        draft.error = null;
      });

      try {
        await new Promise(resolve => setTimeout(resolve, 600));

        const nextPage = state.currentPage + 1;
        const mockItems = generateMockHistoryItems(
          nextPage,
          state.pageSize,
          state.selectedTab
        );

        const filteredItems = state.selectedTab === 'all' ?
          mockItems :
          mockItems.filter(item => item.type === state.selectedTab);

        set((draft) => {
          draft.historyItems = [...draft.historyItems, ...filteredItems];
          draft.cachedHistoryItems = [...draft.cachedHistoryItems, ...filteredItems];
          draft.loading = false;
          draft.hasMore = filteredItems.length === draft.pageSize;
          draft.currentPage = nextPage;
        });

      } catch (error) {
        console.error('加载更多历史记录失败:', error);
        set((draft) => {
          draft.loading = false;
          draft.error = error instanceof Error ? error.message : '加载更多失败';
        });
      }
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
