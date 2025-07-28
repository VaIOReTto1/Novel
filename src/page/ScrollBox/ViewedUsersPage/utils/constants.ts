import { TabData } from '../types';

// Tab数据
export const TAB_DATA: TabData[] = [
  { id: 'viewed', name: '看过的人', type: 'viewed' },
  { id: 'recommend', name: '推荐', type: 'recommend' },
  { id: 'following', name: '关注', type: 'following' },
  { id: 'fans', name: '粉丝', type: 'fans' },
];

// 默认用户头像
export const DEFAULT_AVATAR = 'https://placehold.co/96';

// 空状态图标
export const EMPTY_ICONS = {
  BOX: '��',
  LEAF: '🍁',
};
