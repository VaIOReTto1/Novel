import { create } from 'zustand';
import { CommunityState, CommunityPost, CommunityTab, CommunitySortType, CommunityCategory, CommunityCircle } from '../types';

// Mock数据
const mockCommunityPosts: CommunityPost[] = [
  {
    id: 'post1',
    title: '斗破苍穹讨论',
    content: '作者是天才啊！这个设定太有意思了，看得我根本停不下来。真的是越看越上瘾，期待后续剧情发展！',
    author: {
      id: 'user1',
      name: '天心导师',
      avatar: 'https://example.com/avatar1.jpg',
      level: '书友',
    },
    publishTime: '刚刚',
    likeCount: 8,
    commentCount: 3,
    shareCount: 1,
    isLiked: false,
    isHot: true,
    isTop: false,
    category: 'discussion',
    novelName: '斗破苍穹',
  },
  {
    id: 'post2',
    title: '完美世界剧情',
    content: '明天的十一个剧！！！',
    author: {
      id: 'user2',
      name: '林夕雨落',
      avatar: 'https://example.com/avatar2.jpg',
      level: '书友',
    },
    publishTime: '5分钟前',
    likeCount: 0,
    commentCount: 4,
    shareCount: 0,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'sharing',
    novelName: '完美世界',
  },
  {
    id: 'post3',
    title: '遮天感想',
    content: '这小说真是越看越上瘾了啊',
    author: {
      id: 'user3',
      name: '时光荏苒',
      avatar: 'https://example.com/avatar3.jpg',
      level: '书友',
    },
    publishTime: '10分钟前',
    likeCount: 0,
    commentCount: 20,
    shareCount: 0,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'recommendation',
    novelName: '遮天',
  },
  {
    id: 'post4',
    title: 'COS分享',
    content: '石榴红，出来看一下你的粉丝 cos 你小说',
    author: {
      id: 'user4',
      name: '夜雨',
      avatar: 'https://example.com/avatar4.jpg',
      level: '书友',
    },
    publishTime: '15分钟前',
    images: ['https://example.com/post4_1.jpg'],
    likeCount: 1,
    commentCount: 122,
    shareCount: 323,
    isLiked: false,
    isHot: true,
    isTop: false,
    category: 'sharing',
    novelName: '武动乾坤',
  },
  {
    id: 'post5',
    title: '新作品展示',
    content: '看看我的新作品怎么样',
    author: {
      id: 'user5',
      name: '天心导师',
      avatar: 'https://example.com/avatar5.jpg',
      level: '书友',
    },
    publishTime: '20分钟前',
    likeCount: 0,
    commentCount: 0,
    shareCount: 0,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'question',
    novelName: '大主宰',
  },
  {
    id: 'post6',
    title: '元尊剧情分析',
    content: '周元这个角色塑造得真的很棒，从废材到强者的成长历程让人印象深刻。特别是他和夭夭的感情线，写得很细腻。',
    author: {
      id: 'user6',
      name: '书海遨游',
      avatar: 'https://example.com/avatar6.jpg',
      level: '书虫',
    },
    publishTime: '25分钟前',
    likeCount: 15,
    commentCount: 8,
    shareCount: 2,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'discussion',
    novelName: '元尊',
  },
  {
    id: 'post7',
    title: '斗罗大陆推荐',
    content: '强烈推荐斗罗大陆！唐三的双生武魂设定太精彩了，还有各种魂技的描述，看得热血沸腾！',
    author: {
      id: 'user7',
      name: '魂师传说',
      avatar: 'https://example.com/avatar7.jpg',
      level: '书友',
    },
    publishTime: '30分钟前',
    likeCount: 23,
    commentCount: 12,
    shareCount: 5,
    isLiked: true,
    isHot: true,
    isTop: false,
    category: 'recommendation',
    novelName: '斗罗大陆',
  },
  {
    id: 'post8',
    title: '神墓疑问',
    content: '有没有人能解释一下神墓中辰南复活的原理？感觉有点复杂，没太看懂。',
    author: {
      id: 'user8',
      name: '迷茫读者',
      avatar: 'https://example.com/avatar8.jpg',
      level: '新手',
    },
    publishTime: '35分钟前',
    likeCount: 3,
    commentCount: 25,
    shareCount: 0,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'question',
    novelName: '神墓',
  },
  {
    id: 'post9',
    title: '完美世界精彩片段',
    content: '"一粒尘可填海，一根草斩尽日月星辰"这句话真的太霸气了！石昊的成长之路充满了传奇色彩。',
    author: {
      id: 'user9',
      name: '荒天帝',
      avatar: 'https://example.com/avatar9.jpg',
      level: '书虫',
    },
    publishTime: '40分钟前',
    likeCount: 45,
    commentCount: 18,
    shareCount: 12,
    isLiked: false,
    isHot: true,
    isTop: true,
    category: 'sharing',
    novelName: '完美世界',
  },
  {
    id: 'post10',
    title: '武动乾坤感悟',
    content: '林动从一个普通少年成长为武道巅峰的过程真的很励志，每一次突破都让人热血澎湃！',
    author: {
      id: 'user10',
      name: '武道至尊',
      avatar: 'https://example.com/avatar10.jpg',
      level: '书友',
    },
    publishTime: '45分钟前',
    likeCount: 12,
    commentCount: 6,
    shareCount: 3,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'discussion',
    novelName: '武动乾坤',
  },
  {
    id: 'post11',
    title: '大主宰世界观',
    content: '大千世界的设定真的很宏大，各种种族和势力的描述让人仿佛身临其境。牧尘的冒险之旅太精彩了！',
    author: {
      id: 'user11',
      name: '大千探索者',
      avatar: 'https://example.com/avatar11.jpg',
      level: '书虫',
    },
    publishTime: '50分钟前',
    likeCount: 18,
    commentCount: 9,
    shareCount: 4,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'discussion',
    novelName: '大主宰',
  },
  {
    id: 'post12',
    title: '遮天经典语录',
    content: '"叶凡，你变了"这个梗真的太经典了！还有"我要打十个"，每次看到都忍不住笑。',
    author: {
      id: 'user12',
      name: '叶天帝粉丝',
      avatar: 'https://example.com/avatar12.jpg',
      level: '书友',
    },
    publishTime: '55分钟前',
    likeCount: 67,
    commentCount: 34,
    shareCount: 15,
    isLiked: true,
    isHot: true,
    isTop: false,
    category: 'sharing',
    novelName: '遮天',
  },
  {
    id: 'post13',
    title: '斗破苍穹续集期待',
    content: '听说斗破苍穹要出续集了，不知道会不会继续萧炎的故事，还是会有新的主角？好期待啊！',
    author: {
      id: 'user13',
      name: '炎帝追随者',
      avatar: 'https://example.com/avatar13.jpg',
      level: '书友',
    },
    publishTime: '1小时前',
    likeCount: 28,
    commentCount: 16,
    shareCount: 7,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'discussion',
    novelName: '斗破苍穹',
  },
  {
    id: 'post14',
    title: '新人求推荐',
    content: '刚入坑玄幻小说，大家能推荐几本经典的吗？最好是已经完结的，谢谢！',
    author: {
      id: 'user14',
      name: '小说新人',
      avatar: 'https://example.com/avatar14.jpg',
      level: '新手',
    },
    publishTime: '1小时前',
    likeCount: 5,
    commentCount: 42,
    shareCount: 1,
    isLiked: false,
    isHot: false,
    isTop: false,
    category: 'question',
    novelName: '',
  },
  {
    id: 'post15',
    title: '作者更新速度讨论',
    content: '现在的作者更新速度都好慢啊，一天一更都算快的了。怀念以前日万的时代！',
    author: {
      id: 'user15',
      name: '催更大队长',
      avatar: 'https://example.com/avatar15.jpg',
      level: '书虫',
    },
    publishTime: '1小时前',
    likeCount: 89,
    commentCount: 56,
    shareCount: 23,
    isLiked: true,
    isHot: true,
    isTop: false,
    category: 'discussion',
    novelName: '',
  },
];

// 标签页数据
export const getCommunityTabsData = (posts: CommunityPost[]): CommunityTab[] => {
  return [
    { id: 'all', name: '全部', count: posts.length },
    { id: 'following', name: '关注', count: 0 },
    { id: 'hot', name: '热门', count: posts.filter(p => p.isHot).length },
  ];
};

// Mock分类数据
const mockCategories: CommunityCategory[] = [
  { id: 'all', name: '全部' },
  { id: 'discussion', name: '讨论' },
  { id: 'recommendation', name: '推荐' },
  { id: 'question', name: '问答' },
  { id: 'sharing', name: '分享' },
];

// Mock圈子数据
const mockCommunityCircles: CommunityCircle[] = [
  {
    id: 'circle1',
    name: '斗破苍穹',
    icon: '🔥',
    memberCount: 12580,
    isJoined: true,
    description: '三十年河东，三十年河西，莫欺少年穷！',
  },
  {
    id: 'circle2',
    name: '完美世界',
    icon: '⚔️',
    memberCount: 9876,
    isJoined: false,
    description: '一粒尘可填海，一根草斩尽日月星辰',
  },
  {
    id: 'circle3',
    name: '遮天',
    icon: '🌟',
    memberCount: 15432,
    isJoined: true,
    description: '叶凡的修仙之路',
  },
  {
    id: 'circle4',
    name: '武动乾坤',
    icon: '💫',
    memberCount: 7654,
    isJoined: false,
    description: '林动的武道传奇',
  },
  {
    id: 'circle5',
    name: '大主宰',
    icon: '👑',
    memberCount: 11234,
    isJoined: true,
    description: '牧尘的成长之路',
  },
  {
    id: 'circle6',
    name: '元尊',
    icon: '🐉',
    memberCount: 8765,
    isJoined: false,
    description: '周元的逆袭人生',
  },
  {
    id: 'circle7',
    name: '斗罗大陆',
    icon: '🌊',
    memberCount: 18900,
    isJoined: true,
    description: '唐三的魂师世界',
  },
  {
    id: 'circle8',
    name: '神墓',
    icon: '⚰️',
    memberCount: 6543,
    isJoined: false,
    description: '辰南的复活之谜',
  },
];

// Store接口
interface CommunityStore extends CommunityState {
  // Actions
  setActiveTab: (tab: string) => void;
  setSelectedCategory: (category: string) => void;
  setSelectedCircle: (circle: string) => void;
  setSortType: (sortType: CommunitySortType) => void;
  setLoading: (isLoading: boolean) => void;
  setRefreshing: (isRefreshing: boolean) => void;
  setError: (error: string | null) => void;
  clearError: () => void;

  // Data loading
  loadPosts: () => Promise<void>;
  refreshPosts: () => Promise<void>;
  loadMorePosts: () => Promise<void>;

  // Post interactions
  likePosts: (postId: string) => void;
  commentPost: (postId: string) => void;
  sharePost: (postId: string) => void;

  // Utility functions
  getFilteredPosts: () => CommunityPost[];
  getSortedPosts: (posts: CommunityPost[]) => CommunityPost[];
}

const PAGE_SIZE = 10;

export const useCommunityStore = create<CommunityStore>((set, get) => ({
  // Initial state
  posts: [],
  tabs: getCommunityTabsData([]),
  categories: mockCategories,
  circles: mockCommunityCircles,
  activeTab: 'all',
  selectedCategory: 'all',
  selectedCircle: 'all',
  sortType: 'latest',
  filterOptions: {
    category: 'all',
    sortType: 'latest',
  },
  loading: false,
  refreshing: false,
  error: null,
  hasMore: true,
  page: 1,

  // Actions
  setActiveTab: (tab: string) => {
    set({ activeTab: tab });
  },

  setSelectedCategory: (category: string) => {
    set({ selectedCategory: category });
  },

  setSelectedCircle: (circle: string) => {
    set({ selectedCircle: circle });
  },

  setSortType: (sortType: CommunitySortType) => {
    set({ sortType, filterOptions: { ...get().filterOptions, sortType } });
  },

  setLoading: (loading: boolean) => {
    set({ loading });
  },

  setRefreshing: (refreshing: boolean) => {
    set({ refreshing });
  },

  setError: (error: string | null) => {
    set({ error });
  },

  clearError: () => {
    set({ error: null });
  },

  // Data loading
  loadPosts: async () => {
    const { loading } = get();
    if (loading) {return;}

    set({ loading: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      const startIndex = 0;
      const endIndex = Math.min(PAGE_SIZE, mockCommunityPosts.length);
      const newPosts = mockCommunityPosts.slice(startIndex, endIndex);

      set({
        posts: newPosts,
        hasMore: endIndex < mockCommunityPosts.length,
        page: 2,
        loading: false,
        tabs: getCommunityTabsData(newPosts),
      });
    } catch (error) {
      set({ error: '加载失败', loading: false });
    }
  },

  refreshPosts: async () => {
    const { refreshing } = get();
    if (refreshing) {return;}

    set({ refreshing: true, error: null });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 800));

      const newPosts = mockCommunityPosts.slice(0, PAGE_SIZE);

      set({
        posts: newPosts,
        hasMore: PAGE_SIZE < mockCommunityPosts.length,
        page: 2,
        refreshing: false,
        tabs: getCommunityTabsData(newPosts),
      });
    } catch (error) {
      set({ error: '刷新失败', refreshing: false });
    }
  },

  loadMorePosts: async () => {
    const { loading, hasMore, page, posts } = get();
    if (loading || !hasMore) {return;}

    set({ loading: true });

    try {
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000));

      const startIndex = page * PAGE_SIZE;
      const endIndex = Math.min(startIndex + PAGE_SIZE, mockCommunityPosts.length);
      const newPosts = mockCommunityPosts.slice(startIndex, endIndex);

      set({
        posts: [...posts, ...newPosts],
        hasMore: endIndex < mockCommunityPosts.length,
        page: page + 1,
        loading: false,
        tabs: getCommunityTabsData([...posts, ...newPosts]),
      });
    } catch (error) {
      set({ error: '加载更多失败', loading: false });
    }
  },

  // Post interactions
  likePosts: (postId: string) => {
    set(state => ({
      posts: state.posts.map(post =>
        post.id === postId
          ? {
              ...post,
              isLiked: !post.isLiked,
              likeCount: post.isLiked ? post.likeCount - 1 : post.likeCount + 1,
            }
          : post
      ),
    }));
  },

  commentPost: (postId: string) => {
    set(state => ({
      posts: state.posts.map(post =>
        post.id === postId
          ? { ...post, commentCount: post.commentCount + 1 }
          : post
      ),
    }));
  },

  sharePost: (postId: string) => {
    set(state => ({
      posts: state.posts.map(post =>
        post.id === postId
          ? { ...post, shareCount: post.shareCount + 1 }
          : post
      ),
    }));
  },

  // Utility functions
  getFilteredPosts: () => {
    const { posts, activeTab, selectedCategory, selectedCircle } = get();

    let filteredPosts = posts;

    // 按标签页过滤
    switch (activeTab) {
      case 'hot':
        filteredPosts = filteredPosts.filter(post => post.isHot);
        break;
      case 'following':
        // 这里应该根据用户关注列表过滤
        filteredPosts = filteredPosts.filter(() => false); // 暂时返回空数组
        break;
      default:
        break;
    }

    // 按圈子过滤
    if (selectedCircle !== 'all') {
      const circle = mockCommunityCircles.find(c => c.id === selectedCircle);
      if (circle) {
        filteredPosts = filteredPosts.filter(post => post.novelName === circle.name);
      }
    }

    // 按分类过滤
    if (selectedCategory !== 'all') {
      filteredPosts = filteredPosts.filter(post => post.category === selectedCategory);
    }

    return filteredPosts;
  },

  getSortedPosts: (posts: CommunityPost[]) => {
    const { sortType } = get();

    switch (sortType) {
      case 'hot':
        return [...posts].sort((a, b) => (b.likeCount + b.commentCount) - (a.likeCount + a.commentCount));
      case 'recommended':
        return [...posts].sort((a, b) => b.likeCount - a.likeCount);
      case 'latest':
      default:
        return [...posts].sort((a, b) => {
          // 由于publishTime是字符串，这里简化处理
          if (a.publishTime === '刚刚') {return -1;}
          if (b.publishTime === '刚刚') {return 1;}
          return 0;
        });
    }
  },
}));
