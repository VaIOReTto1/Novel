import { TabData } from '../types';

// 下拉刷新相关常量
export const PULL_THRESHOLD = 80;

// Tab数据 - 基于ui.html的设计
export const MESSAGE_TABS: TabData[] = [
  { id: 'comment', name: '评论和@', type: 'comment' },
  { id: 'reply', name: '话题回帖', type: 'reply' },
  { id: 'like', name: '赞和收藏', type: 'like' },
];

// 分页配置
export const PAGE_SIZE = 20;

// 消息类型标签映射
export const MESSAGE_TYPE_LABELS: Record<string, string> = {
  system: '系统通知',
  fan: '粉丝',
  comment: '评论',
  reply: '回复',
  like: '点赞',
};

// 消息类型图标映射
export const MESSAGE_TYPE_ICONS: Record<string, string> = {
  system: '🔔',
  fan: '👤',
  comment: '💬',
  reply: '↩️',
  like: '❤️',
};
