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

  // 缓存的完整消息数据
  cachedMessages: MessageItem[];
  unreadCount: number;
}

interface MessageActions {
  // 基础状态更新
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setRefreshing: (refreshing: boolean) => void;
  setLoadingMore: (loadingMore: boolean) => void;
  setSelectedTab: (tab: 'main' | 'comment' | 'reply' | 'like') => void;
  setSearchQuery: (query: string) => void;

  // 数据更新
  setMessages: (messages: MessageItem[]) => void;
  appendMessages: (messages: MessageItem[]) => void;
  markMessageAsRead: (messageId: number) => void;
  markAllAsRead: () => void;

  // 异步操作
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

// 模拟API数据
const generateMockMessages = (page: number, pageSize: number, type?: string): MessageItem[] => {
  const messages: MessageItem[] = [];
  const startIndex = (page - 1) * pageSize;

  for (let i = 0; i < pageSize; i++) {
    const index = startIndex + i + 1;
    let messageType: MessageItem['type'];
    let title: string;
    let content: string;
    let icon: string;

    if (type === 'main') {
      messageType = index % 2 === 0 ? 'system' : 'fan';
      if (messageType === 'system') {
        title = '系统通知';
        content = index === 1 ? '《暗星长曜》1&2，套装限时优惠20元' : `系统消息 ${index}`;
        icon = '🔔';
      } else {
        title = '粉丝';
        content = index === 2 ? '暂无粉丝消息' : `粉丝消息 ${index}`;
        icon = '👤';
      }
    } else {
      // 根据指定类型生成数据
      messageType = type as MessageItem['type'];
      title = getTypeTitle(messageType);

      // 为不同类型生成丰富的内容
      if (messageType === 'comment') {
        const commentContents = [
          '用户@小说迷 回复了你的评论："这个情节太精彩了！"',
          '用户@书虫阿宅 评论了你的动态："推荐几本好看的小说吧"',
          '用户@夜读人 在《修仙传奇》中@了你',
          '用户@文学青年 回复："作者的文笔真的很棒"',
          '用户@快乐读者 评论："什么时候更新下一章？"',
        ];
        content = commentContents[index % commentContents.length] + ` #${index}`;
      } else if (messageType === 'reply') {
        const replyContents = [
          '你在话题"最喜欢的修仙小说"中的回帖收到了3个赞',
          '话题"2024年必读小说推荐"有新的回复',
          '你参与的话题"网络小说发展趋势"有新动态',
          '用户@读书达人 在话题"经典重温"中回复了你',
          '你发起的话题"小说人物讨论"收到新回复',
          '话题"作者写作技巧分享"中有人@了你',
          '你的话题回帖"情节分析"被设为精华回复',
          '热门话题"年度最佳小说"有新的讨论',
          '你关注的话题"玄幻世界观构建"有更新',
          '话题"读书心得交流"中收到新的点赞',
        ];
        content = replyContents[index % replyContents.length] + ` #${index}`;
      } else if (messageType === 'like') {
        const likeContents = [
          '你的书评《论修仙小说的世界观构建》收到了10个赞',
          '你收藏的小说《星辰大海》有新章节更新',
          '你的评论"这个反转太意外了"被点赞',
          '你点赞的书单"必读经典"被推荐到首页',
          '你的动态"今日阅读感悟"收到了5个赞',
        ];
        content = likeContents[index % likeContents.length] + ` #${index}`;
      } else {
        // 默认内容
        content = `${getTypeTitle(messageType)}消息内容 ${index}`;
      }

      icon = getTypeIcon(messageType);
    }

    messages.push({
      id: index,
      type: messageType,
      title,
      content,
      time: index <= 2 ? '7-20' : getRandomTime(),
      isRead: index > 10, // 前10条未读，增加未读数量
      hasNotification: index <= 10,
      icon,
    });
  }

  return messages;
};

const getTypeTitle = (type: MessageItem['type']): string => {
  switch (type) {
    case 'system': return '系统通知';
    case 'fan': return '粉丝';
    case 'comment': return '评论';
    case 'reply': return '回复';
    case 'like': return '点赞';
    default: return '消息';
  }
};

const getTypeIcon = (type: MessageItem['type']): string => {
  switch (type) {
    case 'system': return '🔔';
    case 'fan': return '👤';
    case 'comment': return '💬';
    case 'reply': return '↩️';
    case 'like': return '❤️';
    default: return '📧';
  }
};

const getRandomTime = (): string => {
  const times = ['昨天', '前天', '3天前', '1周前', '2周前'];
  return times[Math.floor(Math.random() * times.length)];
};

export const useMessageStore = create<MessageStore>()(
  immer((set, get) => ({
    ...initialState,

    // 基础状态更新
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

    // 数据更新
    setMessages: (messages) => set((state) => {
      state.messages = messages;
      state.unreadCount = messages.filter(msg => !msg.isRead).length;
    }),

    appendMessages: (messages) => set((state) => {
      state.messages.push(...messages);
      state.unreadCount = state.messages.filter(msg => !msg.isRead).length;
    }),

    markMessageAsRead: (messageId) => set((state) => {
      const message = state.messages.find(msg => msg.id === messageId);
      if (message && !message.isRead) {
        message.isRead = true;
        message.hasNotification = false;
        state.unreadCount = Math.max(0, state.unreadCount - 1);
      }

      const cachedMessage = state.cachedMessages.find(msg => msg.id === messageId);
      if (cachedMessage && !cachedMessage.isRead) {
        cachedMessage.isRead = true;
        cachedMessage.hasNotification = false;
      }
    }),

    markAllAsRead: () => set((state) => {
      state.messages.forEach(msg => {
        msg.isRead = true;
        msg.hasNotification = false;
      });
      state.cachedMessages.forEach(msg => {
        msg.isRead = true;
        msg.hasNotification = false;
      });
      state.unreadCount = 0;
    }),

    // 异步操作
    loadMessages: async (isRefresh = false, targetType?: string) => {
      const state = get();
      set((draft) => {
        draft.loading = true;
        if (isRefresh) {
          draft.error = null;
        }
      });

      try {
        await new Promise(resolve => setTimeout(resolve, 800)); // 模拟网络延迟

        const currentPage = isRefresh ? 1 : state.currentPage;
        const typeToLoad = targetType || state.selectedTab;

        // 直接生成对应类型的数据，不需要过滤
        const mockMessages = generateMockMessages(
          currentPage,
          state.pageSize,
          typeToLoad
        );

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
          draft.unreadCount = draft.messages.filter(msg => !msg.isRead).length;
        });

        console.log(`消息加载完成：当前显示${get().messages.length}条，类型：${typeToLoad}`);

      } catch (error) {
        console.error('加载消息失败:', error);
        set((draft) => {
          draft.loading = false;
          draft.isRefreshing = false;
          draft.error = error instanceof Error ? error.message : '加载消息失败';
        });
      }
    },

    loadMoreMessages: async (targetType?: string) => {
      const state = get();
      if (!state.hasMore || state.loading) {return;}

      set((draft) => {
        draft.loading = true;
        draft.error = null;
      });

      try {
        await new Promise(resolve => setTimeout(resolve, 600));

        const nextPage = state.currentPage + 1;
        const typeToLoad = targetType || state.selectedTab;

        const mockMessages = generateMockMessages(
          nextPage,
          state.pageSize,
          typeToLoad
        );

        set((draft) => {
          draft.messages = [...draft.messages, ...mockMessages];
          draft.cachedMessages = [...draft.cachedMessages, ...mockMessages];
          draft.loading = false;
          draft.hasMore = mockMessages.length === draft.pageSize;
          draft.currentPage = nextPage;
          draft.unreadCount = draft.messages.filter(msg => !msg.isRead).length;
        });

      } catch (error) {
        console.error('加载更多消息失败:', error);
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
        await new Promise(resolve => setTimeout(resolve, 500));

        const filteredMessages = state.cachedMessages.filter(msg =>
          msg.title.toLowerCase().includes(query.toLowerCase()) ||
          msg.content.toLowerCase().includes(query.toLowerCase())
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
        const deletedMessage = draft.messages.find(msg => msg.id === messageId);
        if (deletedMessage && !deletedMessage.isRead) {
          draft.unreadCount = Math.max(0, draft.unreadCount - 1);
        }

        draft.messages = draft.messages.filter(msg => msg.id !== messageId);
        draft.cachedMessages = draft.cachedMessages.filter(msg => msg.id !== messageId);
      });

      // 这里可以添加实际的删除API调用
      console.log(`删除消息项: ${messageId}`);
    },
  }))
);
