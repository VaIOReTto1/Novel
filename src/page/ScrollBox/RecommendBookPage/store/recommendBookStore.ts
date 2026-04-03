import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { Alert } from 'react-native';
import {
  UserInfo,
  DataStats,
  ServiceItem,
  TaskItem,
} from '../types';

export interface RecommendBookState {
  loading: boolean;
  error: string | null;

  // User Info
  userInfo: UserInfo | null;

  // Data Stats
  selectedDataTab: 'recommend' | 'book';
  dataStats: DataStats;

  // Creative Services
  services: ServiceItem[];

  // Creative Tasks
  selectedTaskTab: 'recommend' | 'book';
  tasks: Record<'recommend' | 'book', TaskItem[]>;
}

interface RecommendBookActions {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSelectedDataTab: (tab: 'recommend' | 'book') => void;
  setSelectedTaskTab: (tab: 'recommend' | 'book') => void;
  loadInitialData: () => Promise<void>;
  handleViewAllTasks: () => void;
  handleServicePress: (serviceId: string) => void;
  handleTaskPress: (taskId: string) => void;
}

type RecommendBookStore = RecommendBookState & RecommendBookActions;

const initialState: RecommendBookState = {
  loading: false,
  error: null,

  userInfo: null,

  selectedDataTab: 'recommend',
  dataStats: { fans: 0, likes: 0, replies: 0, withdrawable: 0 },

  services: [],

  selectedTaskTab: 'recommend',
  tasks: {
    recommend: [],
    book: [],
  },
};

// Mock data generators
const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '测试作者',
  avatar: 'https://placehold.co/80x80',
});

const generateMockServices = (): ServiceItem[] => [
  {
    id: '1',
    icon: '📊',
    title: '收益分析',
    description: '查看详细收益数据和趋势',
  },
  {
    id: '2',
    icon: '📈',
    title: '推广统计',
    description: '查看推书效果统计',
  },
  {
    id: '3',
    icon: '🎯',
    title: '任务中心',
    description: '发现更多推书任务',
  },
];

const generateMockTasks = (): Record<'recommend' | 'book', TaskItem[]> => ({
  recommend: [
    {
      id: '1',
      title: '种草好书赚现金',
      description: '发布优质推荐内容，完成推书任务',
      coverUrl: 'https://placehold.co/120x80',
      maxEarnings: 69033.62,
      type: 'recommend',
      tags: ['现金奖励', '推书任务'],
    },
    {
      id: '2',
      title: '热门图书推荐',
      description: '推荐当下热门图书，获得佣金收益',
      coverUrl: 'https://placehold.co/120x80',
      maxEarnings: 45000.00,
      type: 'recommend',
      tags: ['佣金收益', '热门图书'],
    },
    {
      id: '3',
      title: '新书首推计划',
      description: '成为新书首推达人，享受专属奖励',
      coverUrl: 'https://placehold.co/120x80',
      maxEarnings: 28500.00,
      type: 'recommend',
      tags: ['新书推广', '专属奖励'],
    },
  ],
  book: [
    {
      id: '4',
      title: '精品书单制作',
      description: '制作高质量书单，吸引更多读者',
      coverUrl: 'https://placehold.co/120x80',
      maxEarnings: 35000.00,
      type: 'book',
      tags: ['书单制作', '读者增长'],
    },
    {
      id: '5',
      title: '经典图书解读',
      description: '深度解读经典图书，分享阅读心得',
      coverUrl: 'https://placehold.co/120x80',
      maxEarnings: 22000.00,
      type: 'book',
      tags: ['经典解读', '阅读心得'],
    },
  ],
});

export const useRecommendBookStore = create<RecommendBookStore>()(
  immer((set, _get) => ({
    ...initialState,

    setLoading: (loading) => set((state) => {
      state.loading = loading;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    setSelectedDataTab: (tab) => set((state) => {
      state.selectedDataTab = tab;
    }),

    setSelectedTaskTab: (tab) => set((state) => {
      state.selectedTaskTab = tab;
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
          state.services = generateMockServices();
          state.tasks = generateMockTasks();
          // Mock data for stats (all zeros as shown in UI)
          state.dataStats = {
            fans: 0,
            likes: 0,
            replies: 0,
            withdrawable: 0,
          };
          state.loading = false;
        });

        console.log('[RecommendBookStore] 初始数据加载完成');

      } catch (error) {
        console.error('[RecommendBookStore] 加载失败:', error);
        set((state) => {
          state.loading = false;
          state.error = error instanceof Error ? error.message : '加载失败';
        });
      }
    },

    handleViewAllTasks: () => {
      console.log('[RecommendBookStore] 查看更多任务');
      Alert.alert('查看更多任务功能开发中...');
    },

    handleServicePress: (serviceId: string) => {
      console.log(`[RecommendBookStore] 服务点击: ${serviceId}`);
      Alert.alert('服务功能开发中...');
    },

    handleTaskPress: (taskId: string) => {
      console.log(`[RecommendBookStore] 任务点击: ${taskId}`);
      Alert.alert('任务详情功能开发中...');
    },
  }))
);
