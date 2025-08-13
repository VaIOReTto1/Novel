import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import { useUserStore } from '../../../ProfilePage/store/userStore';
import {
  UserInfo,
  DataStats,
  BenefitItem,
  TimelineItem,
  PlatformItem,
  CopyrightWork,
  ActivityItem,
  CourseItem,
  AnnouncementData,
} from '../types';

export interface BecomeWriterState {
  loading: boolean;
  error: string | null;
  isAuthor: boolean; // 是否已有作家身份

  // User & Announcement
  userInfo: UserInfo | null;
  announcement: AnnouncementData;

  // Data Stats
  selectedDataTab: 'novel' | 'short';
  dataStats: Record<'novel' | 'short', DataStats>;
  isDataStatsExpanded: boolean;

  // Author Exclusive
  selectedAuthorTab: 'benefits' | 'road' | 'platform';
  benefits: BenefitItem[];
  roadTimeline: TimelineItem[];
  platforms: PlatformItem[];

  // Copyright Adaptation
  copyrightWorks: CopyrightWork[];

  // Creative Activities
  selectedActivityTab: 'novel' | 'short';
  activities: Record<'novel' | 'short', ActivityItem[]>;

  // Writer Classroom
  courses: CourseItem[];

  // 弹窗状态
  showWelcomeModal: boolean;
  isAgreementChecked: boolean;
  // 注册结果与作品
  lastRegisterOk?: boolean;
  works: { id: string; title: string; words: number }[];
}

interface BecomeWriterActions {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setIsAuthor: (isAuthor: boolean) => void;
  setSelectedDataTab: (tab: 'novel' | 'short') => void;
  setSelectedAuthorTab: (tab: 'benefits' | 'road' | 'platform') => void;
  setSelectedActivityTab: (tab: 'novel' | 'short') => void;
  toggleDataStatsExpanded: () => void;
  loadInitialData: () => Promise<void>;
  handleBecomeTomatoWriter: () => void;
  // 弹窗相关 actions
  setShowWelcomeModal: (show: boolean) => void;
  hideWelcomeModal: () => void;
  setAgreementChecked: (checked: boolean) => void;
  handleImmediateRegister: () => void;
  handleMorePress: (type: string) => void;
  setWorks: (works: { id: string; title: string; words: number }[]) => void;
}

type BecomeWriterStore = BecomeWriterState & BecomeWriterActions;

const initialState: BecomeWriterState = {
  loading: false,
  error: null,
  isAuthor: false,

  userInfo: null,
  announcement: {
    tag: '公告',
    text: '番茄小说2025殿堂·金番作家名单重磅揭晓！',
  },

  selectedDataTab: 'novel',
  dataStats: {
    novel: { type: 'novel', wordCount: 0, readers: 0, urgers: 0, dailyIncome: 0 },
    short: { type: 'short', wordCount: 0, readers: 0, urgers: 0, dailyIncome: 0 },
  },
  isDataStatsExpanded: false,

  selectedAuthorTab: 'benefits',
  benefits: [],
  roadTimeline: [],
  platforms: [],

  copyrightWorks: [],

  selectedActivityTab: 'novel',
  activities: {
    novel: [],
    short: [],
  },

  courses: [],

  // 弹窗初始状态
  showWelcomeModal: false,
  isAgreementChecked: false,
  lastRegisterOk: undefined,
  works: [],
};

// Mock data generators
const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '安国的尹锋',
  avatar: 'https://placehold.co/80x80',
});

const generateMockBenefits = (): BenefitItem[] => [
  {
    id: '1',
    icon: '✍️',
    title: '签约收益',
    description: '独家分成签约，优质保底签约',
  },
  {
    id: '2',
    icon: '💰',
    title: '创作保障',
    description: '全勤奖、完本续签奖',
  },
  {
    id: '3',
    icon: '🎁',
    title: '番茄专属福利',
    description: '全年奖／优质加更／新书追更／拉新激励奖',
  },
  {
    id: '4',
    icon: '❤️',
    title: '作家关怀',
    description: '网络文学爱心基金',
  },
  {
    id: '5',
    icon: '🔥',
    title: '全渠道影响力',
    description: '多渠道推广，影响力打造',
  },
  {
    id: '6',
    icon: '🖋️',
    title: '短故事福利',
    description: '6000 字签约，享广告分成收益',
  },
];

// "成神之路"补全：加上 subtitle 字段
const generateMockRoadTimeline = (): TimelineItem[] => [
  {
    id: '1',
    icon: '💡',
    title: '开书灵感',
    subTitle: '热门风向／站内稀缺／主编力签，层层灵感解决开书难题',
  },
  {
    id: '2',
    icon: '🚩',
    title: '新手任务',
    subTitle: '番茄签约最全流程引导，不落下任何一个环节',
  },
  {
    id: '3',
    icon: '📚',
    title: '作家课堂',
    subTitle: '资深编辑＋大神作者保姆级指导，教你写好每一本新书',
  },
  {
    id: '4',
    icon: '⛺',
    title: '训练营',
    subTitle: '写作拔高训练营，每一场都干货满满',
  },
  {
    id: '5',
    icon: '🏆',
    title: '创作者大会',
    subTitle: '回首来路，你已站在群山之巅',
  },
];

const generateMockPlatforms = (): PlatformItem[] => [
  { id: '1', name: '今日头条', logo: 'https://placehold.co/80' },
  { id: '2', name: '抖音', logo: 'https://placehold.co/80' },
  { id: '3', name: '番茄小说', logo: 'https://placehold.co/80' },
  { id: '4', name: '番茄畅听', logo: 'https://placehold.co/80' },
  { id: '5', name: '红果短剧', logo: 'https://placehold.co/80' },
];

const generateMockCopyrightWorks = (): CopyrightWork[] => [
  { id: '1', title: '《开局地摊卖炸鸡》', coverUrl: 'https://placehold.co/100x133' },
  { id: '2', title: '《我在精神病院学斩神》', coverUrl: 'https://placehold.co/100x133' },
  { id: '3', title: '《十日终焉之城》', coverUrl: 'https://placehold.co/100x133' },
  { id: '4', title: '《长乐里：盛世如我愿》', coverUrl: 'https://placehold.co/100x133' },
  { id: '5', title: '《缉春情》售罄典藏版', coverUrl: 'https://placehold.co/100x133' },
  { id: '6', title: '《全球冰封》', coverUrl: 'https://placehold.co/100x133' },
  { id: '7', title: '《归于星河》', coverUrl: 'https://placehold.co/100x133' },
  { id: '8', title: '《岁月如歌》', coverUrl: 'https://placehold.co/100x133' },
  { id: '9', title: '《落笔千山》', coverUrl: 'https://placehold.co/100x133' },
];


const generateMockActivities = (): Record<'novel' | 'short', ActivityItem[]> => ({
  novel: [
    {
      id: '1',
      title: '百日万元总奖金池扩大至85万 | 复活赛来袭',
      time: '活动时间：07.21-10.03',
      coverUrl: 'https://placehold.co/60x60?text=复活赛',
      type: 'novel',
    },
    {
      id: '2',
      title: '2025夏季灵感·暑期征文活动 | 19个分类 双重激励',
      time: '活动时间：07.08-09.30',
      coverUrl: 'https://placehold.co/60x60?text=征文',
      type: 'novel',
    },
    {
      id: '3',
      title: '星耀计划 | 聚焦潜力佳作，现金奖励重磅加持',
      time: '活动时间：07.01-09.30',
      coverUrl: 'https://placehold.co/60x60?text=星耀',
      type: 'novel',
    },
  ],
  short: [
    {
      id: '4',
      title: '番茄短故事「千字万金」五百万激励计划上线',
      time: '活动时间：06.21-09.20',
      coverUrl: 'https://placehold.co/60x60?text=千字',
      type: 'short',
    },
    {
      id: '5',
      title: '闪读好书｜番茄短故事推荐榜新鲜出炉',
      time: '活动时间：08.01-08.15',
      coverUrl: 'https://placehold.co/60x60?text=闪读',
      type: 'short',
    },
  ],
});

const generateMockCourses = (): CourseItem[] => [
  {
    id: '1',
    title: '开书不卡壳？大纲这样写才不崩',
    description: '学习如何构建完整的小说大纲',
    coverUrl: 'https://placehold.co/160x80',
  },
  {
    id: '2',
    title: '专访 | 作者爱哭的小十七',
    description: '成功作者的创作经验分享',
    coverUrl: 'https://placehold.co/160x80',
  },
  {
    id: '3',
    title: '六维拆解开书前的准备工作',
    description: '全面了解开书前的各项准备',
    coverUrl: 'https://placehold.co/160x80',
  },
  {
    id: '4',
    title: '轻装写作技巧，心态是关键！',
    description: '掌握轻松写作的心态调整',
    coverUrl: 'https://placehold.co/160x80',
  },
];

export const useBecomeWriterStore = create<BecomeWriterStore>()(
  immer((set, _get) => ({
    ...initialState,

    setLoading: (loading) => set((state) => {
      state.loading = loading;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    setIsAuthor: (flag) => set((state) => {
      state.isAuthor = flag;
    }),

    setSelectedDataTab: (tab) => set((state) => {
      state.selectedDataTab = tab;
    }),

    setSelectedAuthorTab: (tab) => set((state) => {
      state.selectedAuthorTab = tab;
    }),

    setSelectedActivityTab: (tab) => set((state) => {
      state.selectedActivityTab = tab;
    }),

    toggleDataStatsExpanded: () => set((state) => {
      state.isDataStatsExpanded = !state.isDataStatsExpanded;
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
          state.benefits = generateMockBenefits();
          state.roadTimeline = generateMockRoadTimeline();
          state.platforms = generateMockPlatforms();
          state.copyrightWorks = generateMockCopyrightWorks();
          state.activities = generateMockActivities();
          state.courses = generateMockCourses();
          // 添加mock数据
          state.dataStats = {
            novel: { type: 'novel', wordCount: 0, readers: 0, urgers: 0, dailyIncome: 0 },
            short: { type: 'short', wordCount: 0, readers: 0, urgers: 0, dailyIncome: 0 },
          };
          state.loading = false;

          // 数据加载完成后显示欢迎弹窗（非作家才弹）
          if (!state.isAuthor) {
            state.showWelcomeModal = true;
          }
        });

        console.log('[BecomeWriterStore] 初始数据加载完成');

      } catch (error) {
        console.error('[BecomeWriterStore] 加载失败:', error);
        set((state) => {
          state.loading = false;
          state.error = error instanceof Error ? error.message : '加载失败';
        });
      }
    },

    handleBecomeTomatoWriter: () => {
      console.log('[BecomeWriterStore] 成为番茄作家按钮点击');
      NavigationBridge.navigateToWritePage();
    },

    // 弹窗相关 actions
    setShowWelcomeModal: (show) => set((state) => {
      state.showWelcomeModal = show;
    }),

    hideWelcomeModal: () => set((state) => {
      state.showWelcomeModal = false;
      state.isAgreementChecked = false;
    }),

    setAgreementChecked: (checked) => set((state) => {
      state.isAgreementChecked = checked;
    }),

    handleImmediateRegister: () => {
      console.log('[BecomeWriterStore] 立即入驻按钮点击');
      const { nickname, sex } = useUserStore.getState();
      set((state) => {
        if (!state.isAgreementChecked) {
          // 统一提示由页面层处理，避免 lint 警告
          return;
        }
      });

      const penName = nickname || '新作家';
      const sexValue = sex && (sex === '0' || sex === '1') ? Number(sex) : 1;

      // 调原生注册接口
      NavigationBridge.registerAuthor(penName, sexValue)
        .then((res: any) => {
          console.log('[BecomeWriterStore] 注册成功:', res);
          set((state) => {
            state.lastRegisterOk = true;
            state.showWelcomeModal = false; // 跳到写作页，但不再弹窗
            state.isAgreementChecked = false;
            // 创建一个初始作品，用于“作品数据”展示
            if (!state.works) { state.works = []; }
            const workId = Date.now().toString();
            state.works!.unshift({ id: workId, title: `${penName}的新书`, words: 0 });
          });

          // 跳到写作页
          NavigationBridge.navigateToWritePage();
        })
        .catch((e) => {
          console.warn('[BecomeWriterStore] 注册失败:', e);
          set((state) => {
            state.lastRegisterOk = false;
          });
        });
    },

    handleMorePress: (type: string) => {
      console.log(`[BecomeWriterStore] 更多按钮点击: ${type}`);
    },

    setWorks: (works) => set((state) => {
      state.works = works;
    }),
  }))
);
