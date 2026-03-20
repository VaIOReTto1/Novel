import { create } from 'zustand';
import { BookshelfState, BookshelfItem, BookshelfTab, ViewType, SortType, RecommendationState, RecommendationItem } from '../types';
import { BOOKSHELF_CATEGORIES, VIEW_TYPES, SORT_TYPES } from '../utils/constants';

// Mock数据
const mockBookshelfItems: BookshelfItem[] = [
  {
    id: 'book1',
    title: '重生韩漫，开局就成祝女配的',
    author: '书封页',
    coverUrl: 'https://example.com/cover1.jpg',
    category: 'reading',
    progress: 45,
    isFinished: false,
    lastReadTime: '2024-01-20 15:30:00',
    updateStatus: '连载中',
    description: '一个关于重生的精彩故事...',
    chapterCount: 100,
    currentChapter: 45,
    tags: ['重生', '韩漫', '都市'],
    rating: 4.5,
  },
  {
    id: 'book2',
    title: '斗罗大陆',
    author: '唐家三少',
    coverUrl: 'https://example.com/cover2.jpg',
    category: 'reading',
    progress: 78,
    isFinished: false,
    lastReadTime: '2024-01-19 20:15:00',
    updateStatus: '已完结',
    description: '斗罗大陆的精彩冒险...',
    chapterCount: 336,
    currentChapter: 262,
    tags: ['玄幻', '修炼', '冒险'],
    rating: 4.8,
  },
  {
    id: 'book3',
    title: '卡牌天降：成就恶魔序列',
    author: '第1章',
    coverUrl: 'https://example.com/cover3.jpg',
    category: 'reading',
    progress: 23,
    isFinished: false,
    lastReadTime: '2024-01-18 11:45:00',
    updateStatus: '连载中',
    description: '卡牌与恶魔的奇幻世界...',
    chapterCount: 150,
    currentChapter: 35,
    tags: ['卡牌', '恶魔', '奇幻'],
    rating: 4.2,
  },
  {
    id: 'book4',
    title: '说好的才艺主播，你竟然还亲',
    author: '第001章',
    coverUrl: 'https://example.com/cover4.jpg',
    category: 'listening',
    progress: 67,
    isFinished: false,
    lastReadTime: '2024-01-17 14:20:00',
    updateStatus: '连载中',
    description: '主播的有趣故事...',
    chapterCount: 80,
    currentChapter: 54,
    tags: ['都市', '主播', '轻松'],
    rating: 4.0,
  },
  {
    id: 'book5',
    title: '重生6天老子要当来日杜',
    author: '已看到第1集',
    coverUrl: 'https://example.com/cover5.jpg',
    category: 'drama',
    progress: 12,
    isFinished: false,
    lastReadTime: '2024-01-16 09:10:00',
    updateStatus: '连载中',
    description: '重生题材的短剧...',
    chapterCount: 50,
    currentChapter: 6,
    tags: ['重生', '短剧', '都市'],
    rating: 3.8,
  },
  {
    id: 'book6',
    title: '天不负',
    author: '第1章',
    coverUrl: 'https://example.com/cover6.jpg',
    category: 'reading',
    progress: 89,
    isFinished: true,
    lastReadTime: '2024-01-15 16:00:00',
    updateStatus: '已完结',
    description: '一个关于坚持的故事...',
    chapterCount: 200,
    currentChapter: 200,
    tags: ['励志', '都市', '完结'],
    rating: 4.6,
  },
];

// Mock推荐数据
const mockRecommendations: RecommendationItem[] = [
  {
    id: 'rec1',
    title: '全职高手',
    author: '蝴蝶蓝',
    coverUrl: 'https://example.com/rec1.jpg',
    description: '网游竞技小说的巅峰之作，讲述了职业选手叶修的传奇故事...',
    tags: ['网游', '竞技', '热血'],
    rating: 4.9,
    category: '网游',
  },
  {
    id: 'rec2',
    title: '诡秘之主',
    author: '爱潜水的乌贼',
    coverUrl: 'https://example.com/rec2.jpg',
    description: '蒸汽与机械的时代，谁能触及非凡？历史和黑暗的迷雾里，又是谁在耳语？',
    tags: ['克苏鲁', '蒸汽朋克', '神秘'],
    rating: 4.8,
    category: '奇幻',
  },
  {
    id: 'rec3',
    title: '庆余年',
    author: '猫腻',
    coverUrl: 'https://example.com/rec3.jpg',
    description: '一个有着神秘身世的现代青年，因为一次意外来到了完全不同的世界...',
    tags: ['穿越', '权谋', '古代'],
    rating: 4.7,
    category: '历史',
  },
  {
    id: 'rec4',
    title: '斗破苍穹',
    author: '天蚕土豆',
    coverUrl: 'https://example.com/rec4.jpg',
    description: '这里是属于斗气的世界，没有花俏的魔法，有的，仅仅是繁衍到巅峰的斗气！',
    tags: ['玄幻', '修炼', '热血'],
    rating: 4.6,
    category: '玄幻',
  },
  {
    id: 'rec5',
    title: '完美世界',
    author: '辰东',
    coverUrl: 'https://example.com/rec5.jpg',
    description: '一粒尘可填海，一根草斩尽日月星辰，弹指间天翻地覆...',
    tags: ['玄幻', '仙侠', '热血'],
    rating: 4.5,
    category: '仙侠',
  },
];

// 获取标签页数据
export const getTabsData = (bookshelfItems: BookshelfItem[]): BookshelfTab[] => {
  const allCount = bookshelfItems.length;
  const readingCount = bookshelfItems.filter(item => item.category === 'reading').length;
  const listeningCount = bookshelfItems.filter(item => item.category === 'listening').length;
  const dramaCount = bookshelfItems.filter(item => item.category === 'drama').length;

  return [
    { id: BOOKSHELF_CATEGORIES.ALL, name: '全部', count: allCount },
    { id: BOOKSHELF_CATEGORIES.READING, name: '阅读', count: readingCount },
    { id: BOOKSHELF_CATEGORIES.LISTENING, name: '听书', count: listeningCount },
    { id: BOOKSHELF_CATEGORIES.DRAMA, name: '短剧', count: dramaCount },
  ];
};

// 书架状态管理
interface BookshelfStore extends BookshelfState {
  // 基础操作
  setCurrentTab: (tab: string) => void;
  setViewType: (viewType: ViewType) => void;
  setSortType: (sortType: SortType) => void;
  setEditing: (isEditing: boolean) => void;
  setLoading: (isLoading: boolean) => void;
  setRefreshing: (isRefreshing: boolean) => void;
  setError: (error: string | null) => void;

  // 数据操作
  loadBookshelfItems: () => Promise<void>;
  refreshBookshelfItems: () => Promise<void>;
  loadMoreBookshelfItems: () => Promise<void>;
  removeBookshelfItems: (itemIds: string[]) => Promise<void>;
  toggleItemSelection: (itemId: string) => void;
  selectAllItems: () => void;
  clearSelection: () => void;

  // 获取过滤后的数据
  getFilteredItems: () => BookshelfItem[];
  getSortedItems: (items: BookshelfItem[]) => BookshelfItem[];
}

// 推荐状态管理
interface RecommendationStore extends RecommendationState {
  loadRecommendations: () => Promise<void>;
  loadMoreRecommendations: () => Promise<void>;
  refreshRecommendations: () => Promise<void>;
  setRecommendationLoading: (isLoading: boolean) => void;
  setRecommendationError: (error: string | null) => void;
}

// 书架Store
export const useBookshelfStore = create<BookshelfStore>((set, get) => ({
  // 初始状态
  bookshelfItems: [],
  currentTab: BOOKSHELF_CATEGORIES.ALL,
  viewType: VIEW_TYPES.GRID as ViewType,
  sortType: SORT_TYPES.LAST_READ as SortType,
  isEditing: false,
  selectedItems: new Set(),
  isLoading: false,
  isRefreshing: false,
  error: null,
  hasMore: true,
  page: 1,

  // 基础操作
  setCurrentTab: (tab: string) => {
    set({ currentTab: tab });
  },

  setViewType: (viewType: ViewType) => {
    set({ viewType });
  },

  setSortType: (sortType: SortType) => {
    set({ sortType });
  },

  setEditing: (isEditing: boolean) => {
    set({ isEditing, selectedItems: new Set() });
  },

  setLoading: (isLoading: boolean) => {
    set({ isLoading });
  },

  setRefreshing: (isRefreshing: boolean) => {
    set({ isRefreshing });
  },

  setError: (error: string | null) => {
    set({ error });
  },

  // 数据操作
  loadBookshelfItems: async () => {
    const { isLoading } = get();
    if (isLoading) {return;}

    set({ isLoading: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      set({
        bookshelfItems: mockBookshelfItems,
        isLoading: false,
        page: 1,
        hasMore: false,
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '加载失败',
        isLoading: false,
      });
    }
  },

  refreshBookshelfItems: async () => {
    set({ isRefreshing: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      set({
        bookshelfItems: mockBookshelfItems,
        isRefreshing: false,
        page: 1,
        hasMore: false,
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '刷新失败',
        isRefreshing: false,
      });
    }
  },

  loadMoreBookshelfItems: async () => {
    const { isLoading, hasMore } = get();
    if (isLoading || !hasMore) {return;}

    set({ isLoading: true });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 模拟没有更多数据
      set({
        isLoading: false,
        hasMore: false,
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '加载更多失败',
        isLoading: false,
      });
    }
  },

  removeBookshelfItems: async (itemIds: string[]) => {
    const { bookshelfItems } = get();

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 500));

      const updatedItems = bookshelfItems.filter(item => !itemIds.includes(item.id));
      set({
        bookshelfItems: updatedItems,
        selectedItems: new Set(),
        isEditing: false,
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '删除失败',
      });
    }
  },

  toggleItemSelection: (itemId: string) => {
    const { selectedItems } = get();
    const newSelectedItems = new Set(selectedItems);

    if (newSelectedItems.has(itemId)) {
      newSelectedItems.delete(itemId);
    } else {
      newSelectedItems.add(itemId);
    }

    set({ selectedItems: newSelectedItems });
  },

  selectAllItems: () => {
    const filteredItems = get().getFilteredItems();
    const allItemIds = new Set(filteredItems.map(item => item.id));

    set({ selectedItems: allItemIds });
  },

  clearSelection: () => {
    set({ selectedItems: new Set() });
  },

  // 获取过滤后的数据
  getFilteredItems: () => {
    const { bookshelfItems, currentTab } = get();

    if (currentTab === BOOKSHELF_CATEGORIES.ALL) {
      return bookshelfItems;
    }

    return bookshelfItems.filter(item => item.category === currentTab);
  },

  // 获取排序后的数据
  getSortedItems: (items: BookshelfItem[]) => {
    const { sortType } = get();

    return [...items].sort((a, b) => {
      switch (sortType) {
        case SORT_TYPES.LAST_READ:
          return new Date(b.lastReadTime).getTime() - new Date(a.lastReadTime).getTime();
        case SORT_TYPES.ADD_TIME:
          return new Date(b.lastReadTime).getTime() - new Date(a.lastReadTime).getTime(); // 暂时用lastReadTime代替
        case SORT_TYPES.PROGRESS:
          return b.progress - a.progress;
        case SORT_TYPES.TITLE:
          return a.title.localeCompare(b.title);
        default:
          return 0;
      }
    });
  },
}));

// 推荐Store
export const useRecommendationStore = create<RecommendationStore>((set, get) => ({
  // 初始状态
  recommendations: [],
  isLoading: false,
  hasMore: true,
  page: 1,
  error: null,

  loadRecommendations: async () => {
    const { isLoading } = get();
    if (isLoading) {return;}

    set({ isLoading: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      set({
        recommendations: mockRecommendations,
        isLoading: false,
        page: 1,
        hasMore: true,
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '加载推荐失败',
        isLoading: false,
      });
    }
  },

  loadMoreRecommendations: async () => {
    const { isLoading, hasMore, recommendations, page } = get();
    if (isLoading || !hasMore) {return;}

    set({ isLoading: true });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 模拟加载更多数据
      const moreRecommendations = mockRecommendations.map((item, index) => ({
        ...item,
        id: `${item.id}_page${page + 1}_${index}`,
      }));

      set({
        recommendations: [...recommendations, ...moreRecommendations],
        isLoading: false,
        page: page + 1,
        hasMore: page < 2, // 模拟只有2页数据
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '加载更多推荐失败',
        isLoading: false,
      });
    }
  },

  refreshRecommendations: async () => {
    set({ isLoading: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      set({
        recommendations: mockRecommendations,
        isLoading: false,
        page: 1,
        hasMore: true,
      });
    } catch (error) {
      set({
        error: error instanceof Error ? error.message : '刷新推荐失败',
        isLoading: false,
      });
    }
  },

  setRecommendationLoading: (isLoading: boolean) => {
    set({ isLoading });
  },

  setRecommendationError: (error: string | null) => {
    set({ error });
  },
}));
