import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { 
  UserInfo, 
  RecommendUser, 
  EmptyStateData
} from '../types';

export interface ViewedUsersState {
  loading: boolean;
  error: string | null;
  
  // User Info
  userInfo: UserInfo | null;
  
  // Current Tab
  selectedTab: 'viewed' | 'recommend' | 'following' | 'fans';
  
  // Data for each tab
  viewedUsers: RecommendUser[];
  recommendUsers: RecommendUser[];
  followingUsers: RecommendUser[];
  fansUsers: RecommendUser[];
  
  // Empty states
  emptyStates: Record<'viewed' | 'recommend' | 'following' | 'fans', EmptyStateData>;
}

interface ViewedUsersActions {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSelectedTab: (tab: 'viewed' | 'recommend' | 'following' | 'fans') => void;
  loadInitialData: () => Promise<void>;
  handleFollowUser: (userId: string) => void;
  handleViewMoreRecommend: () => void;
  handleUserPress: (userId: string) => void;
}

type ViewedUsersStore = ViewedUsersState & ViewedUsersActions;

const initialState: ViewedUsersState = {
  loading: false,
  error: null,
  
  userInfo: null,
  
  selectedTab: 'viewed',
  
  viewedUsers: [],
  recommendUsers: [],
  followingUsers: [],
  fansUsers: [],
  
  emptyStates: {
    viewed: {
      icon: '📦',
      title: '暂无看过的人',
      buttonText: '查看更多推荐的人'
    },
    recommend: {
      icon: '📦',
      title: '暂无推荐',
    },
    following: {
      icon: '📦',
      title: '暂无关注的用户',
    },
    fans: {
      icon: '🍁',
      title: '暂无粉丝，多和书友互动吧',
    }
  }
};

// Mock data generators
const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '安国的尹锋',
  avatar: 'https://placehold.co/96'
});

const generateMockRecommendUsers = (): RecommendUser[] => [
  {
    id: '1',
    name: '番茄小说团队',
    avatar: 'https://placehold.co/96',
    hasVBadge: true,
    tags: [{ text: '官方', type: 'official' }, { text: '热门', type: 'hot' }],
    description: 'TA累计拯救1.2万人书荒',
    isFollowed: false
  },
  {
    id: '2',
    name: '天蚕土豆',
    avatar: 'https://placehold.co/96?text=JD',
    hasVBadge: true,
    tags: [{ text: '作家', type: 'author' }, { text: '殿堂作家', type: 'hall' }, { text: 'VIP', type: 'vip' }],
    description: 'TA被2.6万人点赞过',
    isFollowed: false
  },
  {
    id: '3',
    name: '夜来风雨声丶',
    avatar: 'https://placehold.co/96?text=YL',
    hasVBadge: false,
    tags: [{ text: '金番作家', type: 'gold' }, { text: '新人', type: 'new' }],
    description: '代表作《诡舍》',
    isFollowed: false
  },
  {
    id: '4',
    name: '三九音域',
    avatar: 'https://placehold.co/96?text=39',
    hasVBadge: false,
    tags: [{ text: '殿堂作家', type: 'hall' }],
    description: '代表作《我在精神病院学斩神》',
    isFollowed: false
  },
  {
    id: '5',
    name: '杀虫队队员',
    avatar: 'https://placehold.co/96?text=SC',
    hasVBadge: false,
    tags: [{ text: '殿堂作家', type: 'hall' }, { text: '热门', type: 'hot' }],
    description: '代表作《十日终焉》',
    isFollowed: false
  },
  {
    id: '6',
    name: '推书小番茄',
    avatar: 'https://placehold.co/96?text=PJ',
    hasVBadge: true,
    tags: [{ text: '官方', type: 'official' }, { text: 'VIP', type: 'vip' }],
    description: 'TA累计拯救612.5万人书荒',
    isFollowed: false
  },
  {
    id: '7',
    name: '一月九十秋',
    avatar: 'https://placehold.co/96?text=19',
    hasVBadge: false,
    tags: [{ text: '金番作家', type: 'gold' }],
    description: '代表作《诸神愚戏》',
    isFollowed: false
  },
  {
    id: '8',
    name: '羲人烟火',
    avatar: 'https://placehold.co/96?text=YR',
    hasVBadge: false,
    tags: [{ text: '作家Lv.5', type: 'level' }, { text: '新人', type: 'new' }, { text: '热门', type: 'hot' }],
    description: '代表作《让你契约鬼，你契约钟馗？》',
    isFollowed: false
  },
  {
    id: '9',
    name: '徐二家的猫',
    avatar: 'https://placehold.co/96?text=XZ',
    hasVBadge: false,
    tags: [{ text: '金番作家', type: 'gold' }, { text: 'VIP', type: 'vip' }],
    description: '代表作《从前有座镇妖关》',
    isFollowed: false
  },
  {
    id: '10',
    name: '辰东',
    avatar: 'https://placehold.co/96?text=CD',
    hasVBadge: true,
    tags: [{ text: '作家', type: 'author' }, { text: '殿堂作家', type: 'hall' }],
    description: '代表作《完美世界》《遮天》',
    isFollowed: true
  },
  {
    id: '11',
    name: '我吃西红柿',
    avatar: 'https://placehold.co/96?text=XHS',
    hasVBadge: true,
    tags: [{ text: '作家', type: 'author' }, { text: 'VIP', type: 'vip' }, { text: '热门', type: 'hot' }],
    description: '代表作《盘龙》《星辰变》',
    isFollowed: false
  },
  {
    id: '12',
    name: '唐家三少',
    avatar: 'https://placehold.co/96?text=TJ',
    hasVBadge: true,
    tags: [{ text: '作家', type: 'author' }, { text: '殿堂作家', type: 'hall' }],
    description: '代表作《斗罗大陆》',
    isFollowed: false
  },
  {
    id: '13',
    name: '烽火戏诸侯',
    avatar: 'https://placehold.co/96?text=FH',
    hasVBadge: false,
    tags: [{ text: '金番作家', type: 'gold' }, { text: '新人', type: 'new' }],
    description: '代表作《雪中悍刀行》',
    isFollowed: false
  },
  {
    id: '14',
    name: '猫腻',
    avatar: 'https://placehold.co/96?text=MN',
    hasVBadge: false,
    tags: [{ text: '作家Lv.8', type: 'level' }, { text: 'VIP', type: 'vip' }],
    description: '代表作《庆余年》《将夜》',
    isFollowed: true
  },
  {
    id: '15',
    name: '善良的蜜蜂',
    avatar: 'https://placehold.co/96?text=MF',
    hasVBadge: false,
    tags: [{ text: '金番作家', type: 'gold' }],
    description: '代表作《修真聊天群》',
    isFollowed: false
  },
  {
    id: '16',
    name: '会说话的肘子',
    avatar: 'https://placehold.co/96?text=ZZ',
    hasVBadge: false,
    tags: [{ text: '作家Lv.6', type: 'level' }, { text: '热门', type: 'hot' }, { text: '新人', type: 'new' }],
    description: '代表作《大王饶命》',
    isFollowed: false
  },
  {
    id: '17',
    name: '爱潜水的乌贼',
    avatar: 'https://placehold.co/96?text=WZ',
    hasVBadge: false,
    tags: [{ text: '殿堂作家', type: 'hall' }, { text: 'VIP', type: 'vip' }],
    description: '代表作《诡秘之主》',
    isFollowed: true
  },
  {
    id: '18',
    name: '老鹰吃小鸡',
    avatar: 'https://placehold.co/96?text=LY',
    hasVBadge: false,
    tags: [{ text: '金番作家', type: 'gold' }, { text: '热门', type: 'hot' }],
    description: '代表作《万族之劫》',
    isFollowed: false
  }
];

export const useViewedUsersStore = create<ViewedUsersStore>()(
  immer((set, _get) => ({
    ...initialState,

    setLoading: (loading) => set((state) => {
      state.loading = loading;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    setSelectedTab: (tab) => set((state) => {
      state.selectedTab = tab;
    }),

    loadInitialData: async () => {
      set((state) => {
        state.loading = true;
        state.error = null;
      });

      try {
        // Simulate API loading
        await new Promise(resolve => setTimeout(resolve, 500));

        set((state) => {
          state.userInfo = generateMockUserInfo();
          // 只有推荐Tab有数据，其他Tab为空（按照UI图设计）
          state.recommendUsers = generateMockRecommendUsers();
          state.viewedUsers = [];
          state.followingUsers = [];
          state.fansUsers = [];
          state.loading = false;
        });

        console.log('[ViewedUsersStore] 初始数据加载完成');

      } catch (error) {
        console.error('[ViewedUsersStore] 加载失败:', error);
        set((state) => {
          state.loading = false;
          state.error = error instanceof Error ? error.message : '加载失败';
        });
      }
    },

    handleFollowUser: (userId: string) => {
      set((state) => {
        // 更新推荐用户的关注状态
        const user = state.recommendUsers.find(u => u.id === userId);
        if (user) {
          user.isFollowed = !user.isFollowed;
        }
      });
      console.log(`[ViewedUsersStore] 用户${userId}关注状态切换`);
    },

    handleViewMoreRecommend: () => {
      console.log('[ViewedUsersStore] 查看更多推荐的人');
      // 切换到推荐Tab
      set((state) => {
        state.selectedTab = 'recommend';
      });
    },

    handleUserPress: (userId: string) => {
      console.log(`[ViewedUsersStore] 用户点击: ${userId}`);
      alert('用户详情功能开发中...');
    },
  }))
);