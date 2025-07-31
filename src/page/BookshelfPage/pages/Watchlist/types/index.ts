// 追剧页面类型定义

// 追剧项目
export interface WatchlistItem {
  id: string;
  title: string;
  director?: string; // 导演
  actors?: string[]; // 主演
  cover?: string; // 封面图片URL
  coverUrl?: string; // 兼容旧版本的封面URL
  category: 'drama' | 'variety' | 'movie' | 'documentary';
  progress: number; // 观看进度百分比
  isFinished: boolean;
  isCompleted?: boolean; // 是否已完结
  lastWatchTime: string;
  lastUpdate?: string; // 最后更新时间，如"12分钟前"
  updateStatus?: string; // 更新状态，如"已完结"、"连载中"
  description?: string; // 简介
  episodeCount?: number; // 总集数
  currentEpisode?: number; // 当前集数
  episodeTitle?: string; // 当前集标题
  tags?: string[]; // 标签
  rating?: number; // 评分
  duration?: string; // 时长，如"45分钟"
  year?: string; // 年份
}

// 推荐剧集项目
export interface DramaRecommendationItem {
  id: string;
  title: string;
  director?: string;
  actors?: string[];
  cover?: string;
  coverUrl?: string;
  description?: string;
  rating?: number;
  tags?: string[];
  category?: string;
  duration?: string;
  year?: string;
}

// 追剧标签页
export interface WatchlistTab {
  id: string;
  name: string;
  count: number;
}

// 视图类型
export type ViewType = 'grid' | 'list' | 'waterfall';

// 排序类型
export type SortType = 'lastWatch' | 'addTime' | 'progress' | 'title' | 'rating';

// 筛选类型
export interface FilterOptions {
  category?: string[];
  status?: string[]; // 完结状态
  rating?: number; // 最低评分
  year?: string[];
}

// 追剧页面状态
export interface WatchlistState {
  watchlistItems: WatchlistItem[];
  currentTab: string;
  viewType: ViewType;
  sortType: SortType;
  isEditing: boolean;
  selectedItems: Set<string>;
  isLoading: boolean;
  isRefreshing: boolean;
  error: string | null;
  hasMore: boolean;
  page: number;
  filterOptions: FilterOptions;
}

// 推荐剧集状态
export interface DramaRecommendationState {
  recommendations: DramaRecommendationItem[];
  isLoading: boolean;
  hasMore: boolean;
  page: number;
  error: string | null;
}

// 编辑模式操作类型
export type EditAction = 'delete' | 'move' | 'mark_finished' | 'mark_unfinished';

// 菜单操作类型
export type MenuAction = 'continue_watch' | 'mark_finished' | 'remove' | 'share' | 'details';