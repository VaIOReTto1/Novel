import { Alert } from 'react-native';
import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

import { DataStats, ServiceItem, TaskItem, UserInfo } from '../types';

export interface RecommendBookState {
  loading: boolean;
  error: string | null;
  userInfo: UserInfo | null;
  selectedDataTab: 'recommend' | 'book';
  dataStats: DataStats;
  services: ServiceItem[];
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

const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '测试作者',
});

const generateMockServices = (): ServiceItem[] => [
  {
    id: 'trend-review',
    icon: 'trend',
    title: '趋势复盘',
    description: '查看本周涨粉与互动波动',
  },
  {
    id: 'content-diagnosis',
    icon: 'content',
    title: '内容诊断',
    description: '复盘高表现选题与转化动作',
  },
  {
    id: 'engagement-followup',
    icon: 'engage',
    title: '互动维护',
    description: '优先跟进高意向评论与回复',
  },
  {
    id: 'revenue-rhythm',
    icon: 'revenue',
    title: '收益节奏',
    description: '查看近期收益节奏与提现准备',
  },
];

const generateMockTasks = (): Record<'recommend' | 'book', TaskItem[]> => ({
  recommend: [
    {
      id: 'recommend-hot-rank',
      title: '热门好书冲榜计划',
      description: '连续推荐热门图书，提升曝光与转化。',
      maxEarnings: 520,
      type: 'recommend',
      tags: ['热门冲榜', '连续任务'],
      growthGoal: '把评论回复率拉回到本周高点',
      coverLabel: '冲榜',
      coverTone: 'sunrise',
    },
    {
      id: 'recommend-new-book',
      title: '新书首推合作',
      description: '提前锁定新书首推窗口，提升内容增长惯性。',
      maxEarnings: 360,
      type: 'recommend',
      tags: ['新书合作', '首推机会'],
      growthGoal: '抢到首发曝光并带动新增收藏',
      coverLabel: '首推',
      coverTone: 'sand',
    },
    {
      id: 'recommend-comment-loop',
      title: '评论区回流运营',
      description: '围绕高反馈评论做二次推荐，提高互动回流。',
      maxEarnings: 260,
      type: 'recommend',
      tags: ['评论回流', '互动维护'],
      growthGoal: '提升回复深度和读者停留时长',
      coverLabel: '回流',
      coverTone: 'sage',
    },
  ],
  book: [
    {
      id: 'book-list',
      title: '精品书单策划',
      description: '搭建可持续分发的书单内容矩阵，带动稳定增粉。',
      maxEarnings: 450,
      type: 'book',
      tags: ['书单策划', '长期分发'],
      growthGoal: '提高书单收藏率和书架转化率',
      coverLabel: '书单',
      coverTone: 'ink',
    },
    {
      id: 'book-review',
      title: '深度解读专栏',
      description: '制作更完整的图书解读内容，提升读者信任度。',
      maxEarnings: 300,
      type: 'book',
      tags: ['深度解读', '读者增长'],
      growthGoal: '提升高价值读者留存和复访',
      coverLabel: '解读',
      coverTone: 'sand',
    },
  ],
});

export const useRecommendBookStore = create<RecommendBookStore>()(
  immer((set) => ({
    ...initialState,

    setLoading: (loading) =>
      set((state) => {
        state.loading = loading;
      }),

    setError: (error) =>
      set((state) => {
        state.error = error;
      }),

    setSelectedDataTab: (tab) =>
      set((state) => {
        state.selectedDataTab = tab;
      }),

    setSelectedTaskTab: (tab) =>
      set((state) => {
        state.selectedTaskTab = tab;
      }),

    loadInitialData: async () => {
      set((state) => {
        state.loading = true;
        state.error = null;
      });

      try {
        await Promise.resolve();

        set((state) => {
          state.userInfo = generateMockUserInfo();
          state.services = generateMockServices();
          state.tasks = generateMockTasks();
          state.dataStats = {
            fans: 128,
            likes: 342,
            replies: 56,
            withdrawable: 680,
          };
          state.loading = false;
        });
      } catch (error) {
        set((state) => {
          state.loading = false;
          state.error = error instanceof Error ? error.message : '加载失败';
        });
      }
    },

    handleViewAllTasks: () => {
      Alert.alert('全部任务功能开发中...');
    },

    handleServicePress: (_serviceId: string) => {
      Alert.alert('运营工具功能开发中...');
    },

    handleTaskPress: (_taskId: string) => {
      Alert.alert('任务详情功能开发中...');
    },
  })),
);
