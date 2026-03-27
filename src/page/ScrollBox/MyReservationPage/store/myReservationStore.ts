import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { Alert } from 'react-native';
import {
  UserInfo,
  ReservationItem,
  EmptyStateData,
} from '../types';

export interface MyReservationState {
  loading: boolean;
  error: string | null;

  // User Info
  userInfo: UserInfo | null;

  // Current Tab
  selectedTab: 'new' | 'mine';
  selectedSubTab: 'online' | 'offline';

  // Data for each tab
  newReservations: ReservationItem[]; // 新剧预约列表
  myOnlineReservations: ReservationItem[]; // 我的预约-已上线
  myOfflineReservations: ReservationItem[]; // 我的预约-待上线

  // Empty states
  emptyStates: Record<'online' | 'offline', EmptyStateData>;
}

interface MyReservationActions {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSelectedTab: (tab: 'new' | 'mine') => void;
  setSelectedSubTab: (tab: 'online' | 'offline') => void;
  loadInitialData: () => Promise<void>;
  handleReserveItem: (itemId: string) => void;
  handleItemPress: (itemId: string) => void;
}

type MyReservationStore = MyReservationState & MyReservationActions;

const initialState: MyReservationState = {
  loading: false,
  error: null,

  userInfo: null,

  selectedTab: 'new',
  selectedSubTab: 'online',

  newReservations: [],
  myOnlineReservations: [],
  myOfflineReservations: [],

  emptyStates: {
    online: {
      icon: '📦',
      title: '暂无预约内容',
    },
    offline: {
      icon: '📦',
      title: '暂无预约内容',
    },
  },
};

// Mock data generators
const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '安国的尹锋',
  avatar: 'https://placehold.co/96',
});

const generateMockNewReservations = (): ReservationItem[] => [
  {
    id: '1',
    title: '看我几分像从前',
    coverUrl: 'https://placehold.co/300x450?text=A',
    reserveCount: '4.9万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '都市情感剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '2',
    title: '老板当腻了，现在我只想躺平',
    coverUrl: 'https://placehold.co/300x450?text=B',
    reserveCount: '11.5万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '职场喜剧',
    tags: ['即将上线'],
    isReserved: true,
  },
  {
    id: '3',
    title: '假奖真局',
    coverUrl: 'https://placehold.co/300x450?text=C',
    reserveCount: '4181人预约',
    status: 'upcoming',
    type: 'drama',
    description: '悬疑推理剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '4',
    title: '极蟹宿舍',
    coverUrl: 'https://placehold.co/300x450?text=D',
    reserveCount: '2.9万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '校园青春剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '5',
    title: '盛世倾城',
    coverUrl: 'https://placehold.co/300x450?text=E',
    reserveCount: '3.1万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '古装言情剧',
    tags: ['即将上线'],
    isReserved: true,
  },
  {
    id: '6',
    title: '月下鹊桥',
    coverUrl: 'https://placehold.co/300x450?text=F',
    reserveCount: '1.6万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '仙侠奇幻剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '7',
    title: '青梅竹马',
    coverUrl: 'https://placehold.co/300x450?text=G',
    reserveCount: '2.2万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '都市爱情剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '8',
    title: '未来事务所',
    coverUrl: 'https://placehold.co/300x450?text=H',
    reserveCount: '5.4万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '科幻悬疑剧',
    tags: ['即将上线'],
    isReserved: false,
  },
];

export const useMyReservationStore = create<MyReservationStore>()(
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

    setSelectedSubTab: (tab) => set((state) => {
      state.selectedSubTab = tab;
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
          state.newReservations = generateMockNewReservations();
          // 我的预约默认为空，符合UI图
          state.myOnlineReservations = [];
          state.myOfflineReservations = [];
          state.loading = false;
        });

        console.log('[MyReservationStore] 初始数据加载完成');

      } catch (error) {
        console.error('[MyReservationStore] 加载失败:', error);
        set((state) => {
          state.loading = false;
          state.error = error instanceof Error ? error.message : '加载失败';
        });
      }
    },

    handleReserveItem: (itemId: string) => {
      set((state) => {
        // 切换预约状态
        const reservationItem = state.newReservations.find((entry) => entry.id === itemId);
        if (reservationItem) {
          reservationItem.isReserved = !reservationItem.isReserved;

          // 如果是预约，添加到我的预约中
          if (reservationItem.isReserved) {
            if (reservationItem.status === 'online') {
              state.myOnlineReservations.push({ ...reservationItem });
            } else {
              state.myOfflineReservations.push({ ...reservationItem });
            }
          } else {
            // 如果是取消预约，从我的预约中移除
            state.myOnlineReservations = state.myOnlineReservations.filter(r => r.id !== itemId);
            state.myOfflineReservations = state.myOfflineReservations.filter(r => r.id !== itemId);
          }
        }
      });
      console.log(`[MyReservationStore] 预约状态切换: ${itemId}`);
    },

    handleItemPress: (itemId: string) => {
      console.log(`[MyReservationStore] 项目点击: ${itemId}`);
      Alert.alert('预约详情功能开发中...');
    },
  }))
);
