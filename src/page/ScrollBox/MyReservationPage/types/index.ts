export interface UserInfo {
  id: number;
  name: string;
  avatar: string;
}

export interface ReservationItem {
  id: string;
  title: string;
  coverUrl: string;
  reserveCount: string; // 预约人数，如"4.9万人预约"
  status: 'upcoming' | 'online'; // 即将上线 | 已上线
  type: 'drama' | 'book' | 'movie'; // 剧集 | 图书 | 电影
  description?: string; // 简介
  releaseDate?: string; // 上线日期
  tags: string[]; // 标签
  isReserved: boolean; // 是否已预约
}

export interface TabData {
  id: string;
  name: string;
  type: 'new' | 'mine';
}

export interface SubTabData {
  id: string;
  name: string;
  type: 'online' | 'offline';
  count: number;
}

export interface EmptyStateData {
  icon: string;
  title: string;
  subtitle?: string;
  buttonText?: string;
}

export interface TopBarProps {
  onBackPress: () => void;
}
