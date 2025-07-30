export interface UserInfo {
  id: number;
  name: string;
  avatar: string;
}

export interface VIPCard {
  id: string;
  type: 'member' | 'svip' | 'adfree'; // 会员VIP、SVIP、免广告VIP
  title: string;
  subtitle: string;
  bgGradient: string[]; // 渐变背景色
  isActive: boolean;
}

export interface VIPBenefit {
  id: string;
  icon: string;
  title: string;
  description: string;
}

export interface PricePackage {
  id: string;
  duration: string; // 时长如"连续包月"、"7天"、"1个月"
  price: string; // 价格如"¥10"、"¥5"、"¥13"
  originalPrice?: string; // 原价（划线价格）
  discount?: string; // 折扣标签
  isRecommended: boolean; // 是否推荐
  isSelected: boolean; // 是否选中
}

export interface BenefitComparison {
  category: string; // 权益类别
  normal: string; // 普通用户
  member: string; // 会员VIP
  svip: string; // SVIP
  adfree?: string; // 免广告VIP
}

export interface VIPRecommendation {
  id: string;
  title: string;
  coverUrl: string;
  description: string;
}

export type TaskButtonType = 'default' | 'disabled' | 'highlight';

export interface TaskCard {
  id: string;
  /** 行标题，如 “读 1 分钟” */
  title: string;
  /** 橙色小标签，如 “还差 1 分钟” */
  badgeText: string;
  /** 奖励说明，如 “可得 1 天体验卡” */
  rewardText: string;
  /** 按钮显示文字 */
  buttonText: string;
  /** 按钮样式 */
  buttonType: TaskButtonType;
  /** 是否已完成，true 则按钮不可点 */
  isCompleted: boolean;
}

export interface TopBarProps {
  onBackPress: () => void;
  title: string;
}

export interface BottomPurchaseProps {
  selectedPackage: PricePackage | null;
  onPurchase: () => void;
  onPrivacyPress: () => void;
  onTermsPress: () => void;
}