// 下拉刷新阈值
export const PULL_THRESHOLD = 80;

// 分页大小
export const PAGE_SIZE = 20;

// 推荐流分页大小
export const RECOMMENDATION_PAGE_SIZE = 10;

// 瀑布流列数
export const WATERFALL_COLUMNS = 2;

// 网格列数
export const GRID_COLUMNS = 3;

// 动画持续时间
export const ANIMATION_DURATION = 300;

// 防抖延迟
export const DEBOUNCE_DELAY = 300;

// 默认封面占位符
export const DEFAULT_COVER_PLACEHOLDER = '暂无封面';

// 书架分类
export const BOOKSHELF_CATEGORIES = {
  ALL: 'all',
  READING: 'reading',
  LISTENING: 'listening',
  DRAMA: 'drama',
} as const;

// 视图类型
export const VIEW_TYPES = {
  GRID: 'grid',
  LIST: 'list',
  WATERFALL: 'waterfall',
} as const;

// 排序类型
export const SORT_TYPES = {
  LAST_READ: 'lastRead',
  ADD_TIME: 'addTime',
  PROGRESS: 'progress',
  TITLE: 'title',
} as const;