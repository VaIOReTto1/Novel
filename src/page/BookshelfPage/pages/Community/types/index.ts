// Community页面相关类型定义

// 社区帖子作者类型
export interface CommunityAuthor {
  id: string;
  name: string;
  avatar: string;
  level: string;
}

// 社区帖子类型
export interface CommunityPost {
  id: string;
  author: CommunityAuthor;
  content: string;
  images?: string[];
  publishTime: string;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  isLiked: boolean;
  isTop?: boolean;
  isHot?: boolean;
  category?: string;
  novelName?: string; // 来源小说名称
  title?: string; // 帖子标题
}

// 社区分类类型
export interface CommunityCategory {
  id: string;
  name: string;
  count?: number;
}

// 社区标签页类型
export interface CommunityTab {
  id: string;
  name: string;
  count: number;
}

// 圈子类型
export interface CommunityCircle {
  id: string;
  name: string;
  icon: string; // 图标名称或图片URL
  memberCount: number;
  isJoined: boolean;
  description?: string;
}

// 社区排序类型
export type CommunitySortType = 'recommended' | 'latest' | 'hot';

// 社区筛选选项
export interface CommunityFilterOptions {
  category: string;
  sortType: CommunitySortType;
}

// 社区状态类型
export interface CommunityState {
  posts: CommunityPost[];
  tabs: CommunityTab[];
  categories: CommunityCategory[];
  circles: CommunityCircle[];
  activeTab: string;
  selectedCategory: string;
  selectedCircle: string;
  sortType: CommunitySortType;
  filterOptions: CommunityFilterOptions;
  loading: boolean;
  refreshing: boolean;
  error: string | null;
  hasMore: boolean;
  page: number;
}

// 社区操作类型
export type CommunityAction = 
  | 'like'
  | 'comment'
  | 'share'
  | 'follow'
  | 'report';

// 社区菜单操作类型
export type CommunityMenuAction = 
  | 'search'
  | 'notification'
  | 'publish'
  | 'filter'
  | 'refresh';