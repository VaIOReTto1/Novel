import { create } from 'zustand';
import { WatchlistItem, DramaRecommendationItem, WatchlistState, DramaRecommendationState } from '../types';
import { PAGE_SIZE, RECOMMENDATION_PAGE_SIZE } from '../utils/constants';

// 模拟数据
const mockWatchlistItems: WatchlistItem[] = [
  {
    id: '1',
    title: '十八岁的你们',
    cover: 'https://example.com/cover1.jpg',
    category: 'drama',
    progress: 45,
    isFinished: false,
    lastWatchTime: '2024-01-15T10:30:00Z',
    lastUpdate: '12分钟前',
    episodeCount: 100,
    currentEpisode: 45,
    description: '青春校园爱情剧',
    rating: 8.5,
    duration: '15分钟',
    year: '2024',
  },
  {
    id: '2',
    title: '霸道总裁爱上我',
    cover: 'https://example.com/cover2.jpg',
    category: 'drama',
    progress: 100,
    isFinished: true,
    lastWatchTime: '2024-01-14T20:15:00Z',
    episodeCount: 80,
    currentEpisode: 80,
    description: '现代都市爱情剧',
    rating: 7.8,
    duration: '20分钟',
    year: '2024',
  },
  {
    id: '3',
    title: '重生之商业帝国',
    cover: 'https://example.com/cover3.jpg',
    category: 'drama',
    progress: 25,
    isFinished: false,
    lastWatchTime: '2024-01-13T16:45:00Z',
    lastUpdate: '1小时前',
    episodeCount: 120,
    currentEpisode: 30,
    description: '重生商战题材',
    rating: 8.2,
    duration: '18分钟',
    year: '2024',
  },
  {
    id: '4',
    title: '校园青春恋爱记',
    cover: 'https://example.com/cover4.jpg',
    category: 'drama',
    progress: 75,
    isFinished: false,
    lastWatchTime: '2024-01-12T19:20:00Z',
    episodeCount: 60,
    currentEpisode: 45,
    description: '校园青春剧',
    rating: 8.0,
    duration: '16分钟',
    year: '2024',
  },
  {
    id: '5',
    title: '古装穿越之王妃传',
    cover: 'https://example.com/cover5.jpg',
    category: 'drama',
    progress: 0,
    isFinished: false,
    lastWatchTime: '2024-01-11T11:00:00Z',
    lastUpdate: '2小时前',
    episodeCount: 90,
    currentEpisode: 0,
    description: '古装穿越剧',
    rating: 7.5,
    duration: '22分钟',
    year: '2024',
  },
  {
    id: '6',
    title: '都市修仙传说',
    cover: 'https://example.com/cover6.jpg',
    category: 'drama',
    progress: 60,
    isFinished: false,
    lastWatchTime: '2024-01-10T14:30:00Z',
    lastUpdate: '30分钟前',
    episodeCount: 150,
    currentEpisode: 90,
    description: '都市修仙题材',
    rating: 8.3,
    duration: '25分钟',
    year: '2024',
  },
  {
    id: '7',
    title: '豪门恩怨情仇录',
    cover: 'https://example.com/cover7.jpg',
    category: 'drama',
    progress: 90,
    isFinished: false,
    lastWatchTime: '2024-01-09T21:10:00Z',
    episodeCount: 110,
    currentEpisode: 99,
    description: '豪门恩怨剧',
    rating: 7.9,
    duration: '19分钟',
    year: '2024',
  },
  {
    id: '8',
    title: '职场女强人养成记',
    cover: 'https://example.com/cover8.jpg',
    category: 'drama',
    progress: 100,
    isFinished: true,
    lastWatchTime: '2024-01-08T18:25:00Z',
    episodeCount: 70,
    currentEpisode: 70,
    description: '职场励志剧',
    rating: 8.1,
    duration: '17分钟',
    year: '2024',
  },
  {
    id: '9',
    title: '甜宠小剧场',
    cover: 'https://example.com/cover9.jpg',
    category: 'drama',
    progress: 30,
    isFinished: false,
    lastWatchTime: '2024-01-07T12:50:00Z',
    lastUpdate: '45分钟前',
    episodeCount: 50,
    currentEpisode: 15,
    description: '甜宠爱情剧',
    rating: 8.4,
    duration: '12分钟',
    year: '2024',
  },
];

const mockRecommendations: DramaRecommendationItem[] = [
  {
    id: 'rec1',
    title: '推荐剧集1',
    director: '推荐导演1',
    actors: ['推荐演员A', '推荐演员B'],
    cover: 'https://example.com/rec1.jpg',
    description: '这是一部精彩的推荐剧集...',
    rating: 9.0,
    tags: ['热门', '推荐'],
    category: 'drama',
    duration: '45分钟',
    year: '2024',
  },
  {
    id: 'rec2',
    title: '推荐剧集2',
    director: '推荐导演2',
    actors: ['推荐演员C', '推荐演员D'],
    cover: 'https://example.com/rec2.jpg',
    description: '另一部值得观看的剧集...',
    rating: 8.7,
    tags: ['新剧', '口碑'],
    category: 'variety',
    duration: '60分钟',
    year: '2024',
  },
];

// 追剧列表状态管理
interface WatchlistStore extends WatchlistState {
  loadWatchlistItems: () => Promise<void>;
  removeWatchlistItems: (ids: string[]) => Promise<void>;
  toggleItemSelection: (id: string) => void;
  selectAllItems: () => void;
  clearSelection: () => void;
  setEditMode: (isEditing: boolean) => void;
  refreshWatchlist: () => Promise<void>;
  loadMoreWatchlist: () => Promise<void>;
}

export const useWatchlistStore = create<WatchlistStore>((set, get) => ({
  watchlistItems: [],
  currentTab: 'all',
  viewType: 'grid',
  sortType: 'lastWatch',
  isEditing: false,
  selectedItems: new Set(),
  isLoading: false,
  isRefreshing: false,
  error: null,
  hasMore: true,
  page: 1,
  filterOptions: {},

  loadWatchlistItems: async () => {
    const { isLoading, page } = get();
    if (isLoading) {return;}

    set({ isLoading: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      const startIndex = (page - 1) * PAGE_SIZE;
      const endIndex = startIndex + PAGE_SIZE;
      const newItems = mockWatchlistItems.slice(startIndex, endIndex);

      set(state => ({
        watchlistItems: page === 1 ? newItems : [...state.watchlistItems, ...newItems],
        hasMore: endIndex < mockWatchlistItems.length,
        isLoading: false,
        page: page + 1,
      }));
    } catch (error) {
      set({ error: '加载失败', isLoading: false });
    }
  },

  removeWatchlistItems: async (ids: string[]) => {
    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500));

      set(state => ({
        watchlistItems: state.watchlistItems.filter(item => !ids.includes(item.id)),
        selectedItems: new Set(),
        isEditing: false,
      }));
    } catch (error) {
      set({ error: '删除失败' });
    }
  },

  toggleItemSelection: (id: string) => {
    set(state => {
      const newSelected = new Set(state.selectedItems);
      if (newSelected.has(id)) {
        newSelected.delete(id);
      } else {
        newSelected.add(id);
      }
      return { selectedItems: newSelected };
    });
  },

  selectAllItems: () => {
    set(state => ({
      selectedItems: new Set(state.watchlistItems.map(item => item.id)),
    }));
  },

  clearSelection: () => {
    set({ selectedItems: new Set() });
  },

  setEditMode: (isEditing: boolean) => {
    set({ isEditing, selectedItems: isEditing ? get().selectedItems : new Set() });
  },

  refreshWatchlist: async () => {
    set({ isRefreshing: true, page: 1 });
    await get().loadWatchlistItems();
    set({ isRefreshing: false });
  },

  loadMoreWatchlist: async () => {
    if (get().hasMore && !get().isLoading) {
      await get().loadWatchlistItems();
    }
  },
}));

// 推荐剧集状态管理
interface DramaRecommendationStore extends DramaRecommendationState {
  loadRecommendations: () => Promise<void>;
  loadMoreRecommendations: () => Promise<void>;
}

export const useDramaRecommendationStore = create<DramaRecommendationStore>((set, get) => ({
  recommendations: [],
  isLoading: false,
  hasMore: true,
  page: 1,
  error: null,

  loadRecommendations: async () => {
    const { isLoading, page } = get();
    if (isLoading) {return;}

    set({ isLoading: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 800));

      const startIndex = (page - 1) * RECOMMENDATION_PAGE_SIZE;
      const endIndex = startIndex + RECOMMENDATION_PAGE_SIZE;
      const newItems = mockRecommendations.slice(startIndex, endIndex);

      set(state => ({
        recommendations: page === 1 ? newItems : [...state.recommendations, ...newItems],
        hasMore: endIndex < mockRecommendations.length,
        isLoading: false,
        page: page + 1,
      }));
    } catch (error) {
      set({ error: '加载推荐失败', isLoading: false });
    }
  },

  loadMoreRecommendations: async () => {
    if (get().hasMore && !get().isLoading) {
      await get().loadRecommendations();
    }
  },
}));
