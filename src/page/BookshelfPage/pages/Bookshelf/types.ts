// 书架页面类型定义

// 书架项目
export interface BookshelfItem {
  id: string;
  title: string;
  author: string;
  cover?: string; // 封面图片URL
  coverUrl?: string; // 兼容旧版本的封面URL
  category: 'reading' | 'listening' | 'drama';
  progress: number; // 阅读进度百分比
  isFinished: boolean;
  lastReadTime: string;
  updateStatus?: string; // 更新状态，如"已完结"、"连载中"
  description?: string; // 简介
  chapterCount?: number; // 总章节数
  currentChapter?: number; // 当前章节
  tags?: string[]; // 标签
  rating?: number; // 评分
}

// 推荐项目
export interface RecommendationItem {
  id: string;
  title: string;
  author: string;
  cover?: string;
  coverUrl?: string;
  description?: string;
  rating?: number;
  tags?: string[];
  category?: string;
}

// 书架标签页
export interface BookshelfTab {
  id: string;
  name: string;
  count: number;
}

// 视图类型
export type ViewType = 'grid' | 'list' | 'waterfall';

// 排序类型
export type SortType = 'lastRead' | 'addTime' | 'progress' | 'title';

// 书架状态
export interface BookshelfState {
  bookshelfItems: BookshelfItem[];
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
}

// 推荐状态
export interface RecommendationState {
  recommendations: RecommendationItem[];
  isLoading: boolean;
  hasMore: boolean;
  page: number;
  error: string | null;
}
