export interface UserInfo {
  id: number;
  name: string;
  avatar?: string;
}

export interface DataStats {
  fans: number;
  likes: number;
  replies: number;
  withdrawable: number;
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
  coverUrl?: string;
  maxEarnings: number;
  type: 'recommend' | 'book';
  tags: string[];
  growthGoal?: string;
  coverLabel?: string;
  coverTone?: 'sand' | 'sunrise' | 'sage' | 'ink';
}

export interface TabData {
  id: string;
  name: string;
  type: string;
}

export interface TopBarProps {
  onBackPress: () => void;
}
