import { TabData } from '../types';

// 下拉刷新相关常量
export const PULL_THRESHOLD = 80;

// Tab数据
export const HISTORY_TABS: TabData[] = [
  { id: 'all', name: '全部', type: 'all', count: 0 },
  { id: 'reading', name: '阅读', type: 'reading', count: 0 },
  { id: 'listening', name: '听书', type: 'listening', count: 0 },
  { id: 'drama', name: '短剧', type: 'drama', count: 0 },
];

// 分页配置
export const PAGE_SIZE = 20;

// 类型标签映射
export const TYPE_LABELS: Record<string, string> = {
  reading: '小说',
  listening: '听书',
  drama: '短剧',
  all: '全部',
};

// 类型图标映射
export const TYPE_ICONS: Record<string, string> = {
  reading: '📖',
  listening: '🎧',
  drama: '🎭',
  all: '📚',
};