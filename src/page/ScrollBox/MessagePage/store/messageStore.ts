import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

import { MessageItem } from '../types';

export interface MessageState {
  messages: MessageItem[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  pageSize: number;
  hasMore: boolean;
  isRefreshing: boolean;
  isLoadingMore: boolean;
  selectedTab: 'main' | 'comment' | 'reply' | 'like';
  searchQuery: string;
  cachedMessages: MessageItem[];
  unreadCount: number;
}

interface MessageActions {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setRefreshing: (refreshing: boolean) => void;
  setLoadingMore: (loadingMore: boolean) => void;
  setSelectedTab: (tab: 'main' | 'comment' | 'reply' | 'like') => void;
  setSearchQuery: (query: string) => void;
  setMessages: (messages: MessageItem[]) => void;
  appendMessages: (messages: MessageItem[]) => void;
  markMessageAsRead: (messageId: number) => void;
  markAllAsRead: () => void;
  loadMessages: (isRefresh?: boolean, targetType?: string) => Promise<void>;
  refreshMessages: () => Promise<void>;
  loadMoreMessages: (targetType?: string) => Promise<void>;
  searchMessages: (query: string) => Promise<void>;
  deleteMessage: (messageId: number) => Promise<void>;
}

type MessageStore = MessageState & MessageActions;

const initialState: MessageState = {
  messages: [],
  loading: false,
  error: null,
  currentPage: 1,
  pageSize: 20,
  hasMore: true,
  isRefreshing: false,
  isLoadingMore: false,
  selectedTab: 'main',
  searchQuery: '',
  cachedMessages: [],
  unreadCount: 0,
};

const commentTemplates = [
  '用户@小说迷 回复了你的评论：“这个情节反转得太好了。”',
  '用户@阅读星球 评论了你的动态：“这份书单我先收藏了。”',
  '用户@夜读人 在《修仙传奇》中 @了你。',
  '用户@文学青年 回复说：“这段人物弧光写得很完整。”',
  '用户@快乐读者 评论：“作者什么时候更新下一章？”',
];

const replyTemplates = [
  '你在话题“最喜欢的悬疑小说”里的回帖收到了新的讨论。',
  '话题“2026 年必读书单”出现了新的回复。',
  '你参与的话题“网络小说发展趋势”有了新动态。',
  '用户@读书达人 在话题“经典重温”中回复了你。',
  '你发起的话题“人物塑造讨论”收到了新的回帖。',
];

const likeTemplates = [
  '你的书评《论修仙小说的世界观搭建》收到了 10 个赞。',
  '你收藏的小说《星海巡游》更新了新章节。',
  '你的评论“这个反转太意外了”被点赞了。',
  '你点赞的书单“年度必读”被推荐到首页。',
  '你的动态“今天的阅读感悟”收到了新的收藏。',
];

const getTypeTitle = (type: MessageItem['type']): string => {
  switch (type) {
    case 'system':
      return '系统通知';
    case 'fan':
      return '粉丝';
    case 'comment':
      return '评论';
    case 'reply':
      return '回复';
    case 'like':
      return '点赞';
    default:
      return '消息';
  }
};

const getTypeIcon = (type: MessageItem['type']): string => {
  switch (type) {
    case 'system':
      return '通知';
    case 'fan':
      return '粉丝';
    case 'comment':
      return '评论';
    case 'reply':
      return '回复';
    case 'like':
      return '赞藏';
    default:
      return '消息';
  }
};

const getRandomTime = (): string => {
  const times = ['昨天', '前天', '3 天前', '1 周前', '2 周前'];
  return times[Math.floor(Math.random() * times.length)];
};

const buildSecondaryContent = (type: 'comment' | 'reply' | 'like', index: number): string => {
  if (type === 'comment') {
    return `${commentTemplates[index % commentTemplates.length]} #${index + 1}`;
  }

  if (type === 'reply') {
    return `${replyTemplates[index % replyTemplates.length]} #${index + 1}`;
  }

  return `${likeTemplates[index % likeTemplates.length]} #${index + 1}`;
};

const generateMockMessages = (page: number, pageSize: number, type?: string): MessageItem[] => {
  const messages: MessageItem[] = [];
  const startIndex = (page - 1) * pageSize;

  for (let i = 0; i < pageSize; i += 1) {
    const index = startIndex + i;
    let messageType: MessageItem['type'];
    let title: string;
    let content: string;

    if (type === 'main') {
      messageType = index % 2 === 0 ? 'system' : 'fan';
      title = getTypeTitle(messageType);
      content =
        messageType === 'system'
          ? index === 0
            ? '《暗星长歌》套装限时优惠 10 元。'
            : `系统消息 ${index + 1}`
          : index === 1
            ? '暂无粉丝消息'
            : `粉丝消息 ${index + 1}`;
    } else {
      messageType = (type as MessageItem['type']) || 'comment';
      title = getTypeTitle(messageType);
      content = buildSecondaryContent(messageType as 'comment' | 'reply' | 'like', index);
    }

    messages.push({
      id: index + 1,
      type: messageType,
      title,
      content,
      time: index < 2 ? '7-20' : getRandomTime(),
      isRead: index >= 10,
      hasNotification: index < 10,
      icon: getTypeIcon(messageType),
    });
  }

  return messages;
};

export const useMessageStore = create<MessageStore>()(
  immer((set, get) => ({
    ...initialState,

    setLoading: (loading) => set((state) => {
      state.loading = loading;
    }),

    setError: (error) => set((state) => {
      state.error = error;
    }),

    setRefreshing: (refreshing) => set((state) => {
      state.isRefreshing = refreshing;
    }),

    setLoadingMore: (loadingMore) => set((state) => {
      state.isLoadingMore = loadingMore;
    }),

    setSelectedTab: (tab) => set((state) => {
      state.selectedTab = tab;
      state.currentPage = 1;
      state.hasMore = true;
    }),

    setSearchQuery: (query) => set((state) => {
      state.searchQuery = query;
    }),

    setMessages: (messages) => set((state) => {
      state.messages = messages;
      state.unreadCount = messages.filter((item) => !item.isRead).length;
    }),

    appendMessages: (messages) => set((state) => {
      state.messages.push(...messages);
      state.unreadCount = state.messages.filter((item) => !item.isRead).length;
    }),

    markMessageAsRead: (messageId) => set((state) => {
      const message = state.messages.find((item) => item.id === messageId);
      if (message && !message.isRead) {
        message.isRead = true;
        message.hasNotification = false;
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }

      const cachedMessage = state.cachedMessages.find((item) => item.id === messageId);
      if (cachedMessage && !cachedMessage.isRead) {
        cachedMessage.isRead = true;
        cachedMessage.hasNotification = false;
      }
    }),

    markAllAsRead: () => set((state) => {
      state.messages.forEach((item) => {
        item.isRead = true;
        item.hasNotification = false;
      });
      state.cachedMessages.forEach((item) => {
        item.isRead = true;
        item.hasNotification = false;
      });
      state.unreadCount = 0;
    }),

    loadMessages: async (isRefresh = false, targetType?: string) => {
      const state = get();
      set((draft) => {
        draft.loading = true;
        if (isRefresh) {
          draft.error = null;
        }
      });

      try {
        await new Promise((resolve) => setTimeout(resolve, 800));

        const currentPage = isRefresh ? 1 : state.currentPage;
        const typeToLoad = (targetType || state.selectedTab) as string;
        const mockMessages = generateMockMessages(currentPage, state.pageSize, typeToLoad);

        set((draft) => {
          if (isRefresh) {
            draft.messages = mockMessages;
            draft.cachedMessages = mockMessages;
            draft.currentPage = 1;
          } else {
            draft.messages = [...draft.messages, ...mockMessages];
            draft.cachedMessages = [...draft.cachedMessages, ...mockMessages];
          }

          draft.loading = false;
          draft.isRefreshing = false;
          draft.hasMore = mockMessages.length === draft.pageSize;
          draft.unreadCount = draft.messages.filter((item) => !item.isRead).length;
        });

        console.log(`[MessageStore] 消息加载完成，当前显示 ${get().messages.length} 条，类型 ${typeToLoad}`);
      } catch (error) {
        console.error('[MessageStore] 加载消息失败:', error);
        set((draft) => {
          draft.loading = false;
          draft.isRefreshing = false;
          draft.error = error instanceof Error ? error.message : '加载消息失败';
        });
      }
    },

    loadMoreMessages: async (targetType?: string) => {
      const state = get();
      if (!state.hasMore || state.loading) {
        return;
      }

      set((draft) => {
        draft.loading = true;
        draft.error = null;
      });

      try {
        await new Promise((resolve) => setTimeout(resolve, 600));

        const nextPage = state.currentPage + 1;
        const typeToLoad = (targetType || state.selectedTab) as string;
        const mockMessages = generateMockMessages(nextPage, state.pageSize, typeToLoad);

        set((draft) => {
          draft.messages = [...draft.messages, ...mockMessages];
          draft.cachedMessages = [...draft.cachedMessages, ...mockMessages];
          draft.loading = false;
          draft.hasMore = mockMessages.length === draft.pageSize;
          draft.currentPage = nextPage;
          draft.unreadCount = draft.messages.filter((item) => !item.isRead).length;
        });
      } catch (error) {
        console.error('[MessageStore] 加载更多消息失败:', error);
        set((draft) => {
          draft.loading = false;
          draft.error = error instanceof Error ? error.message : '加载更多失败';
        });
      }
    },

    refreshMessages: async () => {
      const { loadMessages } = get();
      await loadMessages(true);
    },

    searchMessages: async (query) => {
      const state = get();
      set((draft) => {
        draft.searchQuery = query;
        draft.loading = true;
      });

      try {
        await new Promise((resolve) => setTimeout(resolve, 500));

        const filteredMessages = state.cachedMessages.filter(
          (item) =>
            item.title.toLowerCase().includes(query.toLowerCase()) ||
            item.content.toLowerCase().includes(query.toLowerCase()),
        );

        set((draft) => {
          draft.messages = filteredMessages;
          draft.loading = false;
        });
      } catch (error) {
        set((draft) => {
          draft.loading = false;
          draft.error = error instanceof Error ? error.message : '搜索失败';
        });
      }
    },

    deleteMessage: async (messageId) => {
      set((draft) => {
        const deletedMessage = draft.messages.find((item) => item.id === messageId);
        if (deletedMessage && !deletedMessage.isRead) {
          draft.unreadCount = Math.max(0, draft.unreadCount - 1);
        }

        draft.messages = draft.messages.filter((item) => item.id !== messageId);
        draft.cachedMessages = draft.cachedMessages.filter((item) => item.id !== messageId);
      });

      console.log(`[MessageStore] 删除消息 ${messageId}`);
    },
  })),
);
