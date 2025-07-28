export interface UserInfo {
  id: number;
  name: string;
  avatar: string;
}

export interface DataStats {
  fans: number;           // 近7天涨粉
  likes: number;          // 近7天获赞
  replies: number;        // 近7天回复
  withdrawable: number;   // 可提现收益（元）
}

export interface ServiceItem {
  id: string;
  icon: string;
  title: string;
  description?: string;
}

export interface TaskItem {
  id: string;
  title: string;
  description: string;
  coverUrl: string;
  maxEarnings: number;    // 最高收益
  type: 'recommend' | 'book';
  tags: string[];
}

export interface TabData {
  id: string;
  name: string;
  type: string;
}

export interface TopBarProps {
  onBackPress: () => void;
}
