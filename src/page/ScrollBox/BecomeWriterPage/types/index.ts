export interface UserInfo {
  id: number;
  name: string;
  avatar: string;
}

export interface DataStats {
  type: 'novel' | 'short';
  wordCount: number;     // 码字数
  readers: number;       // 阅读人数
  urgers: number;        // 催更人数
  dailyIncome: number;   // 每日收益（元）
}

export interface BenefitItem {
  id: string;
  icon: string;
  title: string;
  description?: string;
}

export interface TimelineItem {
  id: string;
  icon: string;
  title: string;
  subTitle?: string;
}

export interface PlatformItem {
  id: string;
  name: string;
  logo: string;
}

export interface CopyrightWork {
  id: string;
  title: string;
  coverUrl: string;
}

export interface ActivityItem {
  id: string;
  title: string;
  time: string;
  coverUrl: string;
  type: 'novel' | 'short';
}

export interface CourseItem {
  id: string;
  title: string;
  description: string;
  coverUrl: string;
}

export interface TabData {
  id: string;
  name: string;
  type: string;
}

export interface AnnouncementData {
  tag: string;
  text: string;
}

export interface TopBarProps {
  onBackPress: () => void;
}
