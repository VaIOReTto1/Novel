import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { Alert } from 'react-native';
import {
  UserInfo,
  VIPCard,
  VIPBenefit,
  PricePackage,
  BenefitComparison,
  VIPRecommendation,
  TaskCard,
} from '../types';

export interface MemberCenterState {
  loading: boolean;
  error: string | null;

  // User Info
  userInfo: UserInfo | null;

  // VIP Cards
  vipCards: VIPCard[];
  currentCardIndex: number;

  // Current VIP Type Benefits
  currentBenefits: VIPBenefit[];

  // Price Packages
  pricePackages: PricePackage[];
  selectedPackageId: string | null;

  // Benefit Comparison
  benefitComparison: BenefitComparison[];

  // VIP Recommendations (only for member and svip)
  vipRecommendations: VIPRecommendation[];

  // Task Cards (only for certain VIP types)
  taskCards: TaskCard[];
}

interface MemberCenterActions {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  loadInitialData: () => Promise<void>;
  setCurrentCard: (index: number) => void;
  selectPricePackage: (packageId: string) => void;
  handlePurchase: () => void;
  handlePrivacyPress: () => void;
  handleTermsPress: () => void;
  handleTaskPress: (taskId: string) => void;
}

type MemberCenterStore = MemberCenterState & MemberCenterActions;

const initialState: MemberCenterState = {
  loading: false,
  error: null,

  userInfo: null,

  vipCards: [],
  currentCardIndex: 0,

  currentBenefits: [],

  pricePackages: [],
  selectedPackageId: null,

  benefitComparison: [],

  vipRecommendations: [],

  taskCards: [],
};

// Mock data generators
const generateMockUserInfo = (): UserInfo => ({
  id: 1,
  name: '安国的尹锋',
  avatar: 'https://placehold.co/96',
});

const generateMockVIPCards = (): VIPCard[] => [
  {
    id: 'member',
    type: 'member',
    title: '会员VIP',
    subtitle: '享受优质体验',
    bgGradient: ['#ff6b35', '#ff8c42', '#ffa726'],
    isActive: true,
  },
  {
    id: 'svip',
    type: 'svip',
    title: 'SVIP',
    subtitle: '尊享至上服务',
    bgGradient: ['#ff6b6b', '#ff8e8e', '#ffb3b3'],
    isActive: false,
  },
  {
    id: 'adfree',
    type: 'adfree',
    title: '免广告VIP',
    subtitle: '纯净阅读体验',
    bgGradient: ['#4ecdc4', '#7fcdcd', '#a8e6cf'],
    isActive: false,
  },
];

export const vipBenefitsMock: Record<string, VIPBenefit[]> = {
  /* 免广告 VIP（4 项）*/
  adfree: [
    { id: 'adfree', icon: '🎬', title: '免广告特权', description: '阅读 / 视频无广告干扰' },
    { id: 'offline', icon: '📥', title: '离线阅读', description: '一键缓存，随时可看' },
    { id: 'autoRead', icon: '📖', title: '自动阅读', description: '解放双手，自动翻页' },
    { id: 'sticker', icon: '😊', title: '专属贴纸', description: '表情包随心发' },
  ],

  /* SVIP（8 项）*/
  svip: [
    { id: 'adfree', icon: '🎬', title: '免广告特权', description: '沉浸阅读不分心' },
    { id: 'fullPage', icon: '🖥️', title: '全文阅读', description: 'PC / 移动页无缝切换' },
    { id: 'library', icon: '📚', title: '海量出版书库', description: '优选 2w+ 册精品' },
    { id: 'fast', icon: '🚀', title: '极速畅读', description: 'CDN 加速即开即读' },
    { id: 'offline', icon: '📥', title: '离线阅读', description: '旅途中也能看' },
    { id: 'autoRead', icon: '📖', title: '自动阅读', description: '解放双手' },
    { id: 'badge', icon: '👑', title: '尊贵标识', description: '昵称旁独特皇冠' },
    { id: 'sticker', icon: '😊', title: '专属贴纸', description: 'SVIP 表情专属' },
  ],

  /* 出版 VIP（5 项）*/
  member: [
    { id: 'library', icon: '📚', title: '海量出版书库', description: '精选出版好书' },
    { id: 'offline', icon: '📥', title: '离线阅读', description: '随时随地畅读' },
    { id: 'autoRead', icon: '📖', title: '自动阅读', description: '智能翻页' },
    { id: 'sticker', icon: '😊', title: '专属贴纸', description: '可爱贴纸包' },
    { id: 'badge', icon: '👑', title: '尊贵标识', description: '昵称旁特别标识' },
  ],
};


const generateMockPricePackages = (cardType: string): PricePackage[] => {
  if (cardType === 'member') {
    return [
      {
        id: 'monthly',
        duration: '连续包月出版',
        price: '¥10',
        originalPrice: '',
        discount: '',
        isRecommended: true,
        isSelected: true, // 默认选择第一个
      },
      {
        id: 'weekly',
        duration: '7天出版',
        price: '¥5',
        originalPrice: '',
        discount: '',
        isRecommended: false,
        isSelected: false,
      },
      {
        id: 'monthly_single',
        duration: '出版月卡',
        price: '¥13',
        originalPrice: '',
        discount: '',
        isRecommended: false,
        isSelected: false,
      },
    ];
  } else if (cardType === 'svip') {
    return [
      {
        id: 'monthly',
        duration: '连续包月',
        price: '¥27',
        originalPrice: '',
        discount: '',
        isRecommended: true,
        isSelected: true, // 默认选择第一个
      },
      {
        id: 'daily',
        duration: '1天免广',
        price: '¥2',
        originalPrice: '',
        discount: '',
        isRecommended: false,
        isSelected: false,
      },
      {
        id: 'monthly_single',
        duration: '1个月',
        price: '¥30',
        originalPrice: '',
        discount: '',
        isRecommended: false,
        isSelected: false,
      },
    ];
  } else if (cardType === 'adfree') {
    return [
      {
        id: 'monthly',
        duration: '连续包月免广',
        price: '¥16',
        originalPrice: '',
        discount: '',
        isRecommended: true,
        isSelected: true, // 默认选择第一个
      },
      {
        id: 'daily',
        duration: '1天免广',
        price: '¥2',
        originalPrice: '',
        discount: '',
        isRecommended: false,
        isSelected: false,
      },
      {
        id: 'weekly',
        duration: '7天免广',
        price: '¥6',
        originalPrice: '',
        discount: '',
        isRecommended: false,
        isSelected: false,
      },
    ];
  }

  return [];
};

const generateMockBenefitComparison = (cardType: string): BenefitComparison[] => {
  switch (cardType) {
    case 'member':
      return [
        { category: '免广告特权', normal: '有听读广告', member: '听读无广告', svip: '听读无广告', adfree: '听读无广告' },
        { category: '今日阅读量', normal: '——', member: '阅读可获金币', svip: '阅读可获金币', adfree: '——' },
        { category: '海量出版书库', normal: '——', member: '购买出版', svip: '免费阅读出版', adfree: '——' },
        { category: '极速书购买', normal: '——', member: '购买极速书', svip: '免费阅读极速书', adfree: '——' },
        { category: '离线阅读', normal: '——', member: '不限时免广告', svip: '不限时免广告', adfree: '——' },
        { category: '自动阅读', normal: '——', member: '不限时免广告', svip: '不限时免广告', adfree: '——' },
        { category: '专属标识', normal: '——', member: 'VIP标识', svip: 'SVIP标识', adfree: '——' },
        { category: '专属贴纸', normal: '——', member: '全套贴纸任选', svip: '全套贴纸任选', adfree: '——' },
      ];

    case 'svip':
      return [
        { category: '免广告特权', normal: '有听读广告', member: '听读无广告', svip: '完全无广告', adfree: '听读无广告' },
        { category: '今日阅读量', normal: '——', member: '阅读可获金币', svip: '阅读获得更多金币', adfree: '——' },
        { category: '海量出版书库', normal: '——', member: '购买出版', svip: '免费畅读', adfree: '——' },
        { category: '极速书购买', normal: '——', member: '购买极速书', svip: '免费畅读', adfree: '——' },
        { category: '离线阅读', normal: '——', member: '不限时免广告', svip: '无限制离线', adfree: '——' },
        { category: '自动阅读', normal: '——', member: '不限时免广告', svip: '无限制自动阅读', adfree: '——' },
        { category: '专属标识', normal: '——', member: 'VIP标识', svip: 'SVIP尊享标识', adfree: '——' },
        { category: '专属贴纸', normal: '——', member: '全套贴纸任选', svip: 'SVIP专属贴纸', adfree: '——' },
        { category: '专属客服', normal: '——', member: '——', svip: '1对1专属客服', adfree: '——' },
      ];

    case 'adfree':
      return [
        { category: '免广告特权', normal: '有听读广告', member: '听读无广告', svip: '完全无广告', adfree: '听读无广告' },
        { category: '今日阅读量', normal: '——', member: '——', svip: '阅读获得更多金币', adfree: '——' },
        { category: '海量出版书库', normal: '——', member: '——', svip: '免费畅读', adfree: '——' },
        { category: '极速书购买', normal: '——', member: '——', svip: '免费畅读', adfree: '——' },
        { category: '离线阅读', normal: '——', member: '——', svip: '无限制离线', adfree: '——' },
        { category: '自动阅读', normal: '——', member: '——', svip: '无限制自动阅读', adfree: '——' },
        { category: '专属标识', normal: '——', member: '——', svip: 'SVIP尊享标识', adfree: 'VIP标识' },
        { category: '专属贴纸', normal: '——', member: '——', svip: 'SVIP专属贴纸', adfree: '——' },
        { category: '专属客服', normal: '——', member: '——', svip: '1对1专属客服', adfree: '——' },
      ];

    default:
      return generateMockBenefitComparison('member');
  }
};

const generateMockVIPRecommendations = (): VIPRecommendation[] => [
  {
    id: '1',
    title: '圣人三韩当（全三册）',
    coverUrl: 'https://placehold.co/150x200?text=Book1',
    description: '《圣人三韩当》全册为韩国文学史上的经典之作——集教育思想、史学分析为一体...',
  },
  {
    id: '2',
    title: '特殊罪案调查组',
    coverUrl: 'https://placehold.co/150x200?text=Book2',
    description: '真实案例改编，展示现代刑侦技术...',
  },
  {
    id: '3',
    title: '打造第二大脑',
    coverUrl: 'https://placehold.co/150x200?text=Book3',
    description: '知识管理新理念，提升个人效率...',
  },
  {
    id: '4',
    title: '黑川雨十',
    coverUrl: 'https://placehold.co/150x200?text=Book4',
    description: '日本推理小说巅峰之作...',
  },
];

const generateMockTaskCards = (cardType: string): TaskCard[] => {
  /** 仅“会员VIP”（出版 VIP）才展示任务卡，其他返回空数组 */
  if (cardType !== 'member') {return [];}

  return [
    {
      id: 'read_1',
      title: '读 1 分钟',
      badgeText: '还差 1 分钟',
      rewardText: '可得 1 天体验卡',
      buttonText: '未完成',
      buttonType: 'disabled',
      isCompleted: true,
    },
    {
      id: 'read_30',
      title: '读 30 分钟',
      badgeText: '还差 30 分钟',
      rewardText: '可得 1 天体验卡',
      buttonText: '未完成',
      buttonType: 'disabled',
      isCompleted: false,
    },
    {
      id: 'read_180',
      title: '读 3 个小时',
      badgeText: '还差 180 分钟',
      rewardText: '可得 1 天体验卡',
      buttonText: '未完成',
      buttonType: 'disabled',
      isCompleted: false,
    },
    {
      id: 'read_2d',
      title: '读 2 天',
      badgeText: '还差 2 天',
      rewardText: '可得 1 天体验卡',
      buttonText: '未完成',
      buttonType: 'disabled',
      isCompleted: false,
    },
    {
      id: 'read_4d',
      title: '读 4 天',
      badgeText: '还差 4 天',
      rewardText: '可得 1 天体验卡',
      buttonText: '未完成',
      buttonType: 'disabled',
      isCompleted: false,
    },
    {
      id: 'read_6d',
      title: '读 6 天',
      badgeText: '还差 6 天',
      rewardText: '可得 2 天体验卡',
      buttonText: '未完成',
      buttonType: 'disabled',
      isCompleted: false,
    },
    {
      id: 'coin_redeem',
      title: '金币兑换',
      badgeText: '',
      rewardText: '500 金币可兑换 1 天体验卡',
      buttonText: '兑换',
      buttonType: 'highlight',       // 橙色高亮按钮
      isCompleted: false,
    },
  ];
};


export const useMemberCenterStore = create<MemberCenterStore>()(
  immer((set, get) => ({
    ...initialState,

    setLoading: (loading) => set((state) => {
      state.loading = loading;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    loadInitialData: async () => {
      set((state) => {
        state.loading = true;
        state.error = null;
      });

      try {
        // Simulate API loading
        await new Promise(resolve => setTimeout(resolve, 500));

        const vipCards = generateMockVIPCards();
        const currentCard = vipCards[0];

        set((state) => {
          state.userInfo = generateMockUserInfo();
          state.vipCards = vipCards;
          state.currentCardIndex = 0;
          state.currentBenefits = vipBenefitsMock[currentCard.type];
          state.pricePackages = generateMockPricePackages(currentCard.type);
          state.selectedPackageId = state.pricePackages.find(p => p.isSelected)?.id || null;
          state.benefitComparison = generateMockBenefitComparison(currentCard.type);
          state.vipRecommendations = generateMockVIPRecommendations();
          state.taskCards = generateMockTaskCards(currentCard.type);
          state.loading = false;
        });

        console.log('[MemberCenterStore] 初始数据加载完成');

      } catch (error) {
        console.error('[MemberCenterStore] 加载失败:', error);
        set((state) => {
          state.loading = false;
          state.error = error instanceof Error ? error.message : '加载失败';
        });
      }
    },

    setCurrentCard: (index: number) => {
      const state = get();
      const newCard = state.vipCards[index];

      if (!newCard) {return;}

      set((draft) => {
        // 更新当前卡片索引
        draft.currentCardIndex = index;

        // 重置所有卡片的激活状态
        draft.vipCards.forEach(card => {
          card.isActive = false;
        });

        // 激活当前卡片
        draft.vipCards[index].isActive = true;

        // 更新相关数据
        draft.currentBenefits = vipBenefitsMock[newCard.type];
        draft.pricePackages = generateMockPricePackages(newCard.type);
        draft.selectedPackageId = draft.pricePackages.find(p => p.isSelected)?.id || null;

        // 根据卡片类型生成不同的权益对比
        draft.benefitComparison = generateMockBenefitComparison(newCard.type);

        // 根据卡片类型生成任务卡片
        draft.taskCards = generateMockTaskCards(newCard.type);

        // 免广告VIP不显示推荐
        if (newCard.type === 'adfree') {
          draft.vipRecommendations = [];
        } else {
          draft.vipRecommendations = generateMockVIPRecommendations();
        }
      });

      console.log(`[MemberCenterStore] 切换到卡片: ${newCard.type}`);
    },

    selectPricePackage: (packageId: string) => {
      set((state) => {
        // 重置所有套餐选中状态
        state.pricePackages.forEach(pkg => {
          pkg.isSelected = pkg.id === packageId;
        });
        state.selectedPackageId = packageId;
      });
      console.log(`[MemberCenterStore] 选择套餐: ${packageId}`);
    },

    handlePurchase: () => {
      const state = get();
      const selectedPackage = state.pricePackages.find(p => p.isSelected);
      const currentCard = state.vipCards[state.currentCardIndex];

      console.log(`[MemberCenterStore] 购买: ${currentCard?.title} - ${selectedPackage?.duration}`);
      Alert.alert(`购买 ${currentCard?.title} - ${selectedPackage?.duration} 功能开发中...`);
    },

    handlePrivacyPress: () => {
      console.log('[MemberCenterStore] 隐私政策点击');
      Alert.alert('隐私政策功能开发中...');
    },

    handleTermsPress: () => {
      console.log('[MemberCenterStore] 服务条款点击');
      Alert.alert('服务条款功能开发中...');
    },

    handleTaskPress: (taskId: string) => {
      const state = get();
      const task = state.taskCards.find(t => t.id === taskId);

      if (!task) {return;}

      if (task.isCompleted) {
        console.log(`[MemberCenterStore] 任务已完成: ${task.title}`);
        Alert.alert(`任务"${task.title}"已完成！`);
        return;
      }

      console.log(`[MemberCenterStore] 执行任务: ${task.title}`);
      Alert.alert(`正在跳转到"${task.title}"任务页面...`);

      // 这里可以添加实际的任务跳转逻辑
    },
  }))
);
