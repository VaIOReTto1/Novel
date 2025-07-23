export interface MessageItem {
  id: number;
  type: 'system' | 'fan' | 'comment' | 'reply' | 'like';
  title: string;
  content: string;
  time: string;
  isRead: boolean;
  hasNotification: boolean;
  icon?: string;
  avatar?: string;
}

export interface MessageItemProps {
  item: MessageItem;
  onPress?: () => void;
  index: number;
}

export interface TabData {
  id: string;
  name: string;
  type: 'main' | 'comment' | 'reply' | 'like';
}

export interface TopBarProps {
  onBackPress: () => void;
  onMarkAllReadPress: () => void;
}

export interface EmptyStateProps {
  message: string;
  icon?: string;
}

export interface LoadMoreIndicatorProps {
  loading: boolean;
  hasMore: boolean;
}

export interface RefreshIndicatorState {
  isPullingDown: boolean;
  isRefreshing: boolean;
  pullDistance: number;
  threshold: number;
} 