import { TabData, SubTabData } from '../types';

// 主要Tab数据
export const MAIN_TAB_DATA: TabData[] = [
  { id: 'new', name: '新剧预约', type: 'new' },
  { id: 'mine', name: '我的预约', type: 'mine' },
];

// 子Tab数据
export const SUB_TAB_DATA: SubTabData[] = [
  { id: 'online', name: '已上线', type: 'online', count: 0 },
  { id: 'offline', name: '待上线', type: 'offline', count: 0 },
];

// 默认封面图
export const DEFAULT_COVER = 'https://placehold.co/300x450';

// 空状态图标
export const EMPTY_ICONS = {
  BOX: '📦',
};