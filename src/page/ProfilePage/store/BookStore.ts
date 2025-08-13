import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';

export interface Book {
  id: number;
  title: string;
  author: string;
  description: string;
  coverUrl: string;
  width?: number;
  height?: number;
  categoryId?: number;
  readCount?: number;
  rating?: number;
}

// 仿照Android HomeService的数据结构
export interface HomeBook {
  type: number; // 0-轮播图 1-顶部栏 2-本周强推 3-热门推荐 4-精品推荐
  bookId: number;
  picUrl: string;
  bookName: string;
  authorName: string;
  bookDesc: string;
}

export interface HomeBooksResponse {
  code: string;
  message: string;
  data: HomeBook[];
  ok: boolean;
}

export interface HomeState {
  recommendBooks: Book[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  pageSize: number;
  hasMore: boolean;

  isRefreshing: boolean;
  isLoadingMore: boolean;

  searchQuery: string;
  selectedCategoryId: number | null;

  rankBooks: Book[];
  selectedRankType: 'hot' | 'new' | 'recommend';

  // 新增：仿照Android的首页推荐状态
  homeRecommendBooks: HomeBook[];
  homeRecommendLoading: boolean;
  hasMoreHomeRecommend: boolean;
  homeRecommendPage: number;

  // 缓存的完整推荐数据
  cachedHomeBooks: HomeBook[];

  // 当前显示模式
  isRecommendMode: boolean; // true=推荐模式，false=分类模式
}

interface HomeActions {
  // 基础状态更新
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setRefreshing: (refreshing: boolean) => void;
  setLoadingMore: (loadingMore: boolean) => void;
  setSearchQuery: (query: string) => void;
  setCategoryFilter: (categoryId: number | null) => void;
  setRankType: (type: 'hot' | 'new' | 'recommend') => void;

  // 数据更新
  setRecommendBooks: (books: Book[]) => void;
  appendBooks: (books: Book[]) => void;
  setHomeRecommendBooks: (books: HomeBook[]) => void;

  // 异步操作 - 仿照Android实现
  fetchRecommendBooks: () => Promise<void>;
  refreshBooks: () => Promise<void>;
  loadMoreBooks: () => Promise<void>;
  fetchRankBooks: (type: 'hot' | 'new' | 'recommend') => Promise<void>;
  loadHomeRecommendBooks: (isRefresh?: boolean) => Promise<void>;
}

type HomeStore = HomeState & HomeActions;

const initialState: HomeState = {
  recommendBooks: [],
  loading: false,
  error: null,
  currentPage: 0,
  pageSize: 8, // 仿照Android的RECOMMEND_PAGE_SIZE
  hasMore: true,

  isRefreshing: false,
  isLoadingMore: false,

  searchQuery: '',
  selectedCategoryId: null,

  rankBooks: [],
  selectedRankType: 'hot',

  // 新增状态
  homeRecommendBooks: [],
  homeRecommendLoading: false,
  hasMoreHomeRecommend: true,
  homeRecommendPage: 1,
  cachedHomeBooks: [],
  isRecommendMode: true,
};

// API基础URL - 仿照Android的BASE_URL_FRONT
const BASE_URL_FRONT = 'http://47.110.147.60:8080/api/front/';

export const useHomeStore = create<HomeStore>()(
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

    setSearchQuery: (query) => set((state) => {
      state.searchQuery = query;
    }),

    setCategoryFilter: (categoryId) => set((state) => {
      state.selectedCategoryId = categoryId;
    }),

    setRankType: (type) => set((state) => {
      state.selectedRankType = type;
    }),

    // 数据更新
    setRecommendBooks: (books) => set((state) => {
      state.recommendBooks = books;
    }),

    appendBooks: (books) => set((state) => {
      state.recommendBooks.push(...books);
    }),

    setHomeRecommendBooks: (books) => set((state) => {
      state.homeRecommendBooks = books;
    }),

    // 异步操作 - 仿照Android实现

    /**
     * 加载首页推荐书籍 - 仿照Android的loadHomeRecommendBooks
     */
    loadHomeRecommendBooks: async (isRefresh = false) => {
      const state = get();
      set((draft) => {
        draft.homeRecommendLoading = true;
        if (isRefresh) {
          draft.error = null;
        }
      });

      try {
        // 如果是刷新或者缓存为空，重新获取数据
        if (isRefresh || state.cachedHomeBooks.length === 0) {
          console.log('正在获取首页推荐数据(优先走原生)');

          let data: HomeBooksResponse | null = null;
          try {
            data = await NavigationBridge.getHomeBooksHighPriority();
            console.log('原生返回首页推荐数据');
          } catch (nativeErr) {
            console.log('原生接口不可用，回退到HTTP接口:', nativeErr);
          }

          if (!data) {
            const response = await fetch(`${BASE_URL_FRONT}home/books`, {
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

            data = await response.json();
          }

          const finalData = data as HomeBooksResponse;
          console.log('首页推荐数据响应:', finalData);

          if (finalData && finalData.ok && finalData.data) {
            console.log(`获取到${finalData.data.length}本推荐书籍`);
            set((draft) => {
              draft.cachedHomeBooks = finalData.data;
            });
          } else {
            const msg = finalData?.message || '获取推荐书籍失败';
            throw new Error(msg);
          }
        }

        // 基于缓存数据进行分页
        const currentState = get();
        const currentPage = isRefresh ? 1 : currentState.homeRecommendPage;
        const startIndex = (currentPage - 1) * currentState.pageSize;
        const endIndex = startIndex + currentState.pageSize;

        const currentBooks = isRefresh ?
          currentState.cachedHomeBooks.slice(0, currentState.pageSize) :
          currentState.cachedHomeBooks.slice(0, endIndex);

        const hasMore = endIndex < currentState.cachedHomeBooks.length;

        set((draft) => {
          draft.homeRecommendBooks = currentBooks;
          draft.homeRecommendLoading = false;
          draft.hasMoreHomeRecommend = hasMore;
          draft.homeRecommendPage = currentPage;
          draft.isRefreshing = false;
        });

        console.log(`首页推荐数据加载完成：当前显示${currentBooks.length}本，总共${currentState.cachedHomeBooks.length}本，hasMore=${hasMore}`);

      } catch (error) {
        console.error('加载首页推荐书籍失败:', error);
        set((draft) => {
          draft.homeRecommendLoading = false;
          draft.isRefreshing = false;
          draft.error = error instanceof Error ? `加载推荐书籍失败: ${error.message}` : '加载推荐书籍失败';
        });
      }
    },

    /**
     * 加载更多首页推荐书籍
     */
    loadMoreBooks: async () => {
      const state = get();
      if (!state.hasMoreHomeRecommend || state.homeRecommendLoading) {return;}

      set((draft) => {
        draft.homeRecommendLoading = true;
        draft.error = null;
      });

      try {
        const nextPage = state.homeRecommendPage + 1;
        const endIndex = nextPage * state.pageSize;

        const currentBooks = state.cachedHomeBooks.slice(0, endIndex);
        const hasMore = endIndex < state.cachedHomeBooks.length;

        set((draft) => {
          draft.homeRecommendBooks = currentBooks;
          draft.homeRecommendLoading = false;
          draft.hasMoreHomeRecommend = hasMore;
          draft.homeRecommendPage = nextPage;
        });

      } catch (error) {
        console.error('加载更多推荐书籍失败:', error);
        set((draft) => {
          draft.homeRecommendLoading = false;
          draft.error = error instanceof Error ? error.message : '加载更多失败';
        });
      }
    },

    /**
     * 刷新推荐书籍
     */
    refreshBooks: async () => {
      const { loadHomeRecommendBooks } = get();
      await loadHomeRecommendBooks(true);
    },

    // 保持原有的兼容性方法
    fetchRecommendBooks: async () => {
      const { loadHomeRecommendBooks } = get();
      await loadHomeRecommendBooks();
    },

    fetchRankBooks: async (type) => {
      set((draft) => {
        draft.selectedRankType = type;
        draft.loading = true;
        draft.error = null;
      });

      try {
        // 这里可以实现榜单数据的获取逻辑
        // 暂时使用模拟数据
        await new Promise(resolve => setTimeout(resolve, 1000));

        set((draft) => {
          draft.loading = false;
          draft.rankBooks = []; // 榜单数据
        });
      } catch (error) {
        set((draft) => {
          draft.loading = false;
          draft.error = error instanceof Error ? error.message : '获取榜单失败';
        });
      }
    },
  }))
);
