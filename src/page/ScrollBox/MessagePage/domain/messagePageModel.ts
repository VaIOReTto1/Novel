type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapMessagePageDeps = {
  setMessages: (messages: any[]) => void;
  logger?: Logger;
};

type CreateMessagePageHandlersDeps = {
  setSecondaryTab: (tab: 'comment' | 'reply' | 'like') => void;
  setShouldScrollToTab: (value: boolean) => void;
  markAllAsRead: () => void;
  markMessageAsRead: (messageId: number) => void;
  handleViewMoreRecommend: () => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapMessagePage = async ({
  setMessages,
}: BootstrapMessagePageDeps): Promise<void> => {
  setMessages([]);
};

export const getSecondaryEmptyMessage = (
  secondaryTab: 'comment' | 'reply' | 'like',
): string => {
  switch (secondaryTab) {
    case 'comment':
      return '暂无评论和@消息';
    case 'reply':
      return '暂无回复消息';
    case 'like':
      return '暂无赞和收藏';
    default:
      return '暂无消息';
  }
};

export const createMessagePageHandlers = ({
  setSecondaryTab,
  setShouldScrollToTab,
  markAllAsRead,
  markMessageAsRead,
  handleViewMoreRecommend,
  logger = defaultLogger,
}: CreateMessagePageHandlersDeps) => ({
  handleTabPress: (tabId: string) => {
    if (tabId === 'comment' || tabId === 'reply' || tabId === 'like') {
      setSecondaryTab(tabId);
      setShouldScrollToTab(true);
    }
  },

  handleMarkAllReadPress: () => {
    markAllAsRead();
  },

  handleMessagePress: (message: { id: number; title?: string; isRead?: boolean }) => {
    logger.log('[MessagePage] Message pressed', message.title);
    if (!message.isRead) {
      markMessageAsRead(message.id);
    }
  },

  handleViewMorePress: () => {
    handleViewMoreRecommend();
  },
});
