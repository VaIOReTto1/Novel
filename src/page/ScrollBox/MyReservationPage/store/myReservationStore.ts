import { Alert } from 'react-native';
import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

import {
  EmptyStateData,
  ReservationItem,
  UserInfo,
} from '../types';

export interface MyReservationState {
  loading: boolean;
  error: string | null;
  userInfo: UserInfo | null;
  selectedTab: 'new' | 'mine';
  selectedSubTab: 'online' | 'offline';
  newReservations: ReservationItem[];
  myOnlineReservations: ReservationItem[];
  myOfflineReservations: ReservationItem[];
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
      icon: '空',
      title: '暂无预约内容',
    },
    offline: {
      icon: '空',
      title: '暂无预约内容',
    },
  },
};

const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '测试作者',
  avatar: 'https://placehold.co/96',
});

const generateMockNewReservations = (): ReservationItem[] => [
  {
    id: '1',
    title: '未来事务所',
    coverUrl: 'https://placehold.co/300x450?text=A',
    reserveCount: '5.4万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '科幻悬疑剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '2',
    title: '青梅竹马',
    coverUrl: 'https://placehold.co/300x450?text=B',
    reserveCount: '2.2万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '都市爱情剧',
    tags: ['即将上线'],
    isReserved: true,
  },
  {
    id: '3',
    title: '极光宿舍',
    coverUrl: 'https://placehold.co/300x450?text=C',
    reserveCount: '1.8万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '校园青春剧',
    tags: ['即将上线'],
    isReserved: false,
  },
  {
    id: '4',
    title: '月下鹊桥',
    coverUrl: 'https://placehold.co/300x450?text=D',
    reserveCount: '1.6万人预约',
    status: 'upcoming',
    type: 'drama',
    description: '仙侠奇幻剧',
    tags: ['即将上线'],
    isReserved: false,
  },
];

export const useMyReservationStore = create<MyReservationStore>()(
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

    setSelectedSubTab: (tab) => set((state) => {
      state.selectedSubTab = tab;
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
          state.newReservations = generateMockNewReservations();
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
        const reservationItem = state.newReservations.find((entry) => entry.id === itemId);
        if (!reservationItem) {
          return;
        }

        reservationItem.isReserved = !reservationItem.isReserved;

        if (reservationItem.isReserved) {
          if (reservationItem.status === 'online') {
            state.myOnlineReservations.push({ ...reservationItem });
          } else {
            state.myOfflineReservations.push({ ...reservationItem });
          }
          return;
        }

        state.myOnlineReservations = state.myOnlineReservations.filter((item) => item.id !== itemId);
        state.myOfflineReservations = state.myOfflineReservations.filter((item) => item.id !== itemId);
      });
      console.log(`[MyReservationStore] 预约状态切换: ${itemId}`);
    },

    handleItemPress: (itemId: string) => {
      console.log(`[MyReservationStore] 项目点击: ${itemId}`);
      Alert.alert('预约详情功能开发中...');
    },
  })),
);
