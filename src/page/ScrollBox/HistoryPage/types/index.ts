export interface HistoryItem {
  id: number;
  title: string;
  author: string;
  description: string;
  coverUrl: string;
  lastReadTime: string;
  readProgress: number;
  type: 'book' | 'audio' | 'video' | 'drama';
  categoryId: number;
  readCount: number;
  rating: number;
}

export interface HistoryItemProps {
  item: HistoryItem;
  onPress?: () => void;
  index: number;
  viewType: 'grid' | 'list';
}

export interface TabData {
  id: string;
  name: string;
  type: 'all' | 'book' | 'audio' | 'video' | 'drama';
}

export interface ViewControlProps {
  viewType: 'grid' | 'list';
  onViewTypeChange: (type: 'grid' | 'list') => void;
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

export interface TopBarProps {
  onBackPress: () => void;
  onSearchPress: () => void;
} 