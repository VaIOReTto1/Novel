export interface UserInfo {
  id: number;
  name: string;
  avatar: string;
}

export interface RecommendUser {
  id: string;
  name: string;
  avatar: string;
  hasVBadge: boolean;
  tags: {
    text: string;
    type: 'official' | 'author' | 'gold' | 'hall' | 'level' | 'vip' | 'new' | 'hot';
  }[];
  description: string;
  isFollowed: boolean;
}

export interface TabData {
  id: string;
  name: string;
  type: 'viewed' | 'recommend' | 'following' | 'fans';
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