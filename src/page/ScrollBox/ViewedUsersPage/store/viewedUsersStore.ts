import { Alert } from 'react-native';
import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

import {
  EmptyStateData,
  RecommendUser,
  UserInfo,
} from '../types';

export interface ViewedUsersState {
  loading: boolean;
  error: string | null;
  userInfo: UserInfo | null;
  selectedTab: 'viewed' | 'recommend' | 'following' | 'fans';
  viewedUsers: RecommendUser[];
  recommendUsers: RecommendUser[];
  followingUsers: RecommendUser[];
  fansUsers: RecommendUser[];
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
      icon: '空',
      title: '暂无看过的人',
      buttonText: '查看更多推荐的人',
    },
    recommend: {
      icon: '空',
      title: '暂无推荐',
    },
    following: {
      icon: '空',
      title: '暂无关注的人',
    },
    fans: {
      icon: '空',
      title: '暂无粉丝，去和更多书友互动吧',
    },
  },
};

const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '测试作者',
  avatar: 'https://placehold.co/96',
});

const generateMockRecommendUsers = (): RecommendUser[] => [
  {
    id: '1',
    name: '番茄小说团队',
    avatar: 'https://placehold.co/96',
    hasVBadge: true,
    tags: [
      { text: '官方', type: 'official' },
      { text: '热门', type: 'hot' },
    ],
    description: '帮你发现更多值得关注的创作者',
    isFollowed: false,
  },
  {
    id: '2',
    name: '天蚕土豆',
    avatar: 'https://placehold.co/96?text=TD',
    hasVBadge: true,
    tags: [
      { text: '作者', type: 'author' },
      { text: '殿堂作者', type: 'hall' },
      { text: 'VIP', type: 'vip' },
    ],
    description: '分享热门作品幕后和创作观察',
    isFollowed: false,
  },
  {
    id: '3',
    name: '三九音域',
    avatar: 'https://placehold.co/96?text=39',
    hasVBadge: false,
    tags: [{ text: '殿堂作者', type: 'hall' }],
    description: '代表作《我在精神病院学斩神》',
    isFollowed: false,
  },
  {
    id: '4',
    name: '推书小番茄',
    avatar: 'https://placehold.co/96?text=PJ',
    hasVBadge: true,
    tags: [
      { text: '官方', type: 'official' },
      { text: 'VIP', type: 'vip' },
    ],
    description: '每天整理编辑推荐和高口碑书单',
    isFollowed: false,
  },
  {
    id: '5',
    name: '辰东',
    avatar: 'https://placehold.co/96?text=CD',
    hasVBadge: true,
    tags: [
      { text: '作者', type: 'author' },
      { text: '殿堂作者', type: 'hall' },
    ],
    description: '代表作《完美世界》《遮天》',
    isFollowed: true,
  },
  {
    id: '6',
    name: '爱潜水的乌贼',
    avatar: 'https://placehold.co/96?text=WZ',
    hasVBadge: false,
    tags: [
      { text: '殿堂作者', type: 'hall' },
      { text: 'VIP', type: 'vip' },
    ],
    description: '代表作《诡秘之主》',
    isFollowed: true,
  },
];

export const useViewedUsersStore = create<ViewedUsersStore>()(
  immer((set) => ({
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
        await new Promise((resolve) => setTimeout(resolve, 500));

        set((state) => {
          state.userInfo = generateMockUserInfo();
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
        const user = state.recommendUsers.find((item) => item.id === userId);
        if (user) {
          user.isFollowed = !user.isFollowed;
        }
      });
      console.log(`[ViewedUsersStore] 用户 ${userId} 关注状态切换`);
    },

    handleViewMoreRecommend: () => {
      console.log('[ViewedUsersStore] 查看更多推荐的人');
      set((state) => {
        state.selectedTab = 'recommend';
      });
    },

    handleUserPress: (userId: string) => {
      console.log(`[ViewedUsersStore] 用户点击: ${userId}`);
      Alert.alert('用户详情功能开发中...');
    },
  })),
);
