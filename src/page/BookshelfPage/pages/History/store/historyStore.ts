import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { HistoryItem, TabData } from '../types';
import { NavigationBridge } from '../../../../../utils/bridge/NavigationBridge';

// 历史记录状态接口
export interface HistoryState {
  // 历史记录数据
  historyItems: HistoryItem[];
  loading: boolean;
  isLoading: boolean;
  error: string | null;
  currentPage: number;
  pageSize: number;
  hasMore: boolean;

  isRefreshing: boolean;
  isLoadingMore: boolean;

  // Tab和视图相关
  currentTab: 'all' | 'book' | 'listening' | 'drama';
  viewType: 'grid' | 'list';
  sortType: 'lastRead' | 'addTime' | 'title' | 'progress';
  searchQuery: string;

  // 编辑相关
  isEditing: boolean;
  selectedItems: string[];

  // 缓存的完整历史数据
  cachedHistoryItems: HistoryItem[];
}

// 历史记录操作接口
interface HistoryActions {
  // 基础状态更新
  setLoading: (loading: boolean) => void;
  setIsLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
  setRefreshing: (refreshing: boolean) => void;
  setLoadingMore: (loadingMore: boolean) => void;
  setCurrentTab: (tab: 'all' | 'book' | 'listening' | 'drama') => void;
  setViewType: (viewType: 'grid' | 'list') => void;
  setSortType: (sortType: 'lastRead' | 'addTime' | 'title' | 'progress') => void;
  setSearchQuery: (query: string) => void;

  // 编辑相关
  setEditing: (editing: boolean) => void;
  toggleItemSelection: (itemId: string) => void;
  selectAllItems: () => void;
  clearSelection: () => void;
  removeSelectedItems: () => void;

  // 数据更新
  setHistoryItems: (items: HistoryItem[]) => void;
  appendHistoryItems: (items: HistoryItem[]) => void;

  // 异步操作
  loadHistoryItems: (isRefresh?: boolean) => Promise<void>;
  refreshHistory: () => Promise<void>;
  loadMoreHistory: () => Promise<void>;
  searchHistory: (query: string) => Promise<void>;
  deleteHistoryItem: (itemId: string) => Promise<void>;
  toggleShelfStatus: (item: HistoryItem) => Promise<void>;
}

type HistoryStore = HistoryState & HistoryActions;

const initialState: HistoryState = {
  historyItems: [],
  loading: false,
  isLoading: false,
  error: null,
  currentPage: 1,
  pageSize: 20,
  hasMore: true,

  isRefreshing: false,
  isLoadingMore: false,

  currentTab: 'all',
  viewType: 'grid',
  sortType: 'lastRead',
  searchQuery: '',

  isEditing: false,
  selectedItems: [],

  cachedHistoryItems: [],
};

// 模拟API延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const useHistoryStore = create<HistoryStore>()(
  immer((set, get) => ({
    ...initialState,

    // 基础状态更新
    setLoading: (loading: any) => set((state: { loading: any; }) => {
      state.loading = loading;
    }),

    setError: (error: any) => set((state: { error: any; }) => {
      state.error = error;
    }),

    setIsLoading: (isLoading: any) => set((state: { isLoading: any; }) => {
      state.isLoading = isLoading;
    }),

    setRefreshing: (refreshing: any) => set((state: { isRefreshing: any; }) => {
      state.isRefreshing = refreshing;
    }),

    setLoadingMore: (loadingMore: any) => set((state: { isLoadingMore: any; }) => {
      state.isLoadingMore = loadingMore;
    }),

    setCurrentTab: (tab: any) => set((state: { currentTab: any; currentPage: number; hasMore: boolean; }) => {
      state.currentTab = tab;
      state.currentPage = 1;
      state.hasMore = true;
    }),

    setViewType: (viewType: any) => {
      set((state: { viewType: any; }) => {
        state.viewType = viewType;
      });
      console.log('[HistoryStore] View type changed to:', viewType);
    },

    setSortType: (sortType: string) => set((state: { sortType: any; historyItems: HistoryItem[]; }) => {
      state.sortType = sortType;
      // 重新排序当前数据
      state.historyItems = sortHistoryItems(state.historyItems, sortType);
    }),

    setSearchQuery: (query: any) => set((state: { searchQuery: any; }) => {
      state.searchQuery = query;
    }),

    // 编辑相关
    setEditing: (editing: boolean) => set((state) => {
      state.isEditing = editing;
      if (!editing) {
        state.selectedItems = [];
      }
    }),

    toggleItemSelection: (itemId: any) => set((state: { selectedItems: any[]; }) => {
      const index = state.selectedItems.indexOf(itemId);
      if (index > -1) {
        state.selectedItems.splice(index, 1);
      } else {
        state.selectedItems.push(itemId);
      }
    }),

    selectAllItems: () => set((state: { selectedItems: any; historyItems: any[]; }) => {
      state.selectedItems = state.historyItems.map((item: { id: any; }) => item.id);
    }),

    clearSelection: () => set((state) => {
      state.selectedItems = [];
    }),

    removeSelectedItems: () => set((state: { selectedItems: Iterable<unknown> | null | undefined; historyItems: any[]; }) => {
      const selectedIds = new Set(state.selectedItems);
      state.historyItems = state.historyItems.filter((item: { id: unknown; }) => !selectedIds.has(item.id));
      state.selectedItems = [];
    }),

    // 数据更新
    setHistoryItems: (items: any) => set((state: { historyItems: any; }) => {
      state.historyItems = items;
    }),

    appendHistoryItems: (items: any) => set((state: { historyItems: any[]; }) => {
      state.historyItems.push(...items);
    }),

    // 异步操作
    loadHistoryItems: async (isRefresh = false) => {
      const { currentTab } = get();

      try {
        if (isRefresh) {
          set((state) => {
            state.isRefreshing = true;
            state.currentPage = 1;
            state.hasMore = true;
            state.error = null;
          });
        } else {
          set((state) => {
            state.loading = true;
            state.error = null;
          });
        }

        // 使用真实的桥接方法获取历史数据
        const historyData = await NavigationBridge.getReadingHistory();
        
        // 转换数据格式以匹配HistoryItem接口
        const convertedItems: HistoryItem[] = historyData.historyItems.map((item: any) => ({
          id: item.id?.toString() || Math.random().toString(),
          title: item.title || '未知标题',
          author: item.author || '未知作者',
          description: item.description || '暂无描述',
          cover: item.coverUrl || 'https://placehold.co/220x300?text=封面',
          coverUrl: item.coverUrl || 'https://placehold.co/220x300?text=封面',
          lastReadTime: item.lastReadTime || Date.now(),
          lastChapter: item.chapterTitle || '第1章',
          readProgress: Math.round(item.readProgress * 100), // 转换为百分比
          progress: Math.round(item.readProgress * 100),
          type: item.type || 'book',
          categoryId: item.categoryId || 1,
          readCount: item.readCount || 0,
          rating: item.rating || 0,
          tags: [],
          isFinished: false,
          updateStatus: '连载中',
          isInShelf: false,
        }));

        // 根据选中的tab过滤数据
        const filteredItems = currentTab === 'all' ? 
          convertedItems : 
          convertedItems.filter(item => item.type === currentTab);

        set((state: { historyItems: HistoryItem[]; cachedHistoryItems: HistoryItem[]; isRefreshing: boolean; loading: boolean; hasMore: boolean; }) => {
          state.historyItems = filteredItems;
          state.cachedHistoryItems = convertedItems;
          state.hasMore = false; // 目前一次性加载所有数据
          state.loading = false;
          state.isRefreshing = false;
        });

        console.log('[HistoryStore] Loaded history items:', filteredItems.length);
      } catch (error) {
        console.error('加载历史记录失败:', error);
        set((state) => {
          state.error = error instanceof Error ? error.message : '加载历史记录失败';
          state.loading = false;
          state.isRefreshing = false;
        });
        console.error('[HistoryStore] Load error:', error);
      }
    },

    loadMoreHistory: async () => {
      const { hasMore, isLoadingMore } = get();

      if (!hasMore || isLoadingMore) return;

      try {
      set((state) => {
        state.hasMore = false;
        state.isLoadingMore = false;
        state.error = null;
      });

      } catch (error) {
        set((state) => {
          state.isLoadingMore = false;
          state.error = '加载更多失败';
        });
      }
    },

    refreshHistory: async () => {
      await get().loadHistoryItems(true);
    },

    searchHistory: async (query: string) => {
      set((state: { searchQuery: any; loading: boolean; }) => {
        state.searchQuery = query;
        state.loading = true;
      });

      try {
        await delay(300);

        const { cachedHistoryItems } = get();
        const filteredItems = cachedHistoryItems.filter((item: HistoryItem) =>
          item.title.toLowerCase().includes(query.toLowerCase()) ||
          item.author.toLowerCase().includes(query.toLowerCase()) ||
          item.tags?.some((tag: string) => tag.toLowerCase().includes(query.toLowerCase()))
        );

        set((state) => {
          state.historyItems = filteredItems;
          state.loading = false;
        });
      } catch (error) {
        set((state) => {
          state.loading = false;
          state.error = '搜索失败';
        });
      }
    },

    deleteHistoryItem: async (itemId: any) => {
      try {
        await delay(200);

        set((state: { historyItems: any[]; cachedHistoryItems: any[]; }) => {
          state.historyItems = state.historyItems.filter((item: { id: any; }) => item.id !== itemId);
          state.cachedHistoryItems = state.cachedHistoryItems.filter((item: { id: any; }) => item.id !== itemId);
        });

        console.log('[HistoryStore] Deleted item:', itemId);
      } catch (error) {
        set((state) => {
          state.error = '删除失败';
        });
      }
    },

    // 切换书架状态
    toggleShelfStatus: async (item: HistoryItem) => {
      try {
        // 模拟API调用延迟
        await delay(500);
        
        set((state) => {
          const itemIndex = state.historyItems.findIndex(historyItem => historyItem.id === item.id);
          if (itemIndex !== -1) {
            state.historyItems[itemIndex].isInShelf = !state.historyItems[itemIndex].isInShelf;
          }
          
          // 同时更新缓存数据
          const cachedIndex = state.cachedHistoryItems.findIndex(historyItem => historyItem.id === item.id);
          if (cachedIndex !== -1) {
            state.cachedHistoryItems[cachedIndex].isInShelf = !state.cachedHistoryItems[cachedIndex].isInShelf;
          }
        });
      } catch (error) {
        console.error('切换书架状态失败:', error);
        set((state) => {
          state.error = '操作失败';
        });
      }
    },
  })));

// 排序函数
const sortHistoryItems = (items: HistoryItem[], sortType: string): HistoryItem[] => {
  return [...items].sort((a, b) => {
    switch (sortType) {
      case 'lastRead':
        return new Date(b.lastReadTime).getTime() - new Date(a.lastReadTime).getTime();
      case 'addTime':
        return new Date(b.lastReadTime).getTime() - new Date(a.lastReadTime).getTime();
      case 'title':
        return a.title.localeCompare(b.title);
      case 'progress':
        return b.readProgress - a.readProgress;
      default:
        return 0;
    }
  });
};

// 获取Tab数据
export const getTabsData = (): TabData[] => [
  { id: 'all', name: '全部', type: 'all', count: 0 },
  { id: 'book', name: '阅读', type: 'book', count: 0 },
  { id: 'listening', name: '听书', type: 'listening', count: 0 },
  { id: 'drama', name: '短剧', type: 'drama', count: 0 },
];

console.log('[HistoryStore] Store initialized');
