// 书籍信息
export interface BookItem {
  id: string;
  title: string;
  author: string;
  coverUrl?: string;
  category: 'reading' | 'listening' | 'drama' | 'community';
  progress: number; // 阅读进度百分比
  isFinished: boolean;
  lastReadTime: string;
  updateStatus?: string; // 更新状态，如"已完结"、"连载中"
  tags?: string[]; // 标签
  rating?: number; // 评分
  description?: string; // 简介
}

// 历史记录项
export interface HistoryItem {
  id: string;
  title: string;
  author: string;
  coverUrl?: string;
  progress: number;
  lastReadTime: string;
  readChapters?: number;
  totalChapters?: number;
  tags?: string[];
}

// 追剧项
export interface WatchlistItem {
  id: string;
  title: string;
  coverUrl?: string;
  episodeProgress: number;
  totalEpisodes: number;
  lastWatchTime: string;
  status: 'airing' | 'completed' | 'upcoming';
  description?: string;
}

// 圈子项
export interface CommunityItem {
  id: string;
  author: string;
  avatar?: string;
  content: string;
  time: string;
  images?: string[];
  likeCount: number;
  commentCount: number;
  shareCount: number;
  isFollowed: boolean;
  isLiked: boolean;
}

// Tab数据
export interface TabData {
  id: string;
  name: string;
  type: 'bookshelf' | 'history' | 'watchlist' | 'community';
}

// 主页面状态
export interface MainBookshelfState {
  currentTab: string;
  isLoading: boolean;
  error: string | null;

  // 方法
  setCurrentTab: (tabId: string) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
}

// TabBar属性
export interface MainTabBarProps {
  styles: any;
  tabs: TabData[];
  selectedTab: string;
  onTabPress: (tabId: string) => void;
}

// 子页面通用属性 - 重构后不再需要isActive和styles
export interface SubPageProps {
  // 移除了styles和isActive，子页面自行管理样式和显示状态
}

// 书籍项组件属性
export interface BookItemProps {
  styles: any;
  item: any;
  index?: number;
  viewType: 'grid' | 'list';
  isEditing: boolean;
  isSelected: boolean;
  onPress: (item: any) => void;
  onSelect: (item: any) => void;
}

// BookshelfContent组件的Props接口
export interface BookshelfContentProps {
  styles: any;
  books: BookItem[];
  viewType: 'grid' | 'list';
  isEditing: boolean;
  selectedBooks: string[];
  onBookPress: (item: BookItem) => void;
  onBookSelect: (item: BookItem) => void;
  loading: boolean;
  hasMore: boolean;
}
