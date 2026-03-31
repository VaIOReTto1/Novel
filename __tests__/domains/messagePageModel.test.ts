import {
  bootstrapMessagePage,
  createMessagePageHandlers,
  getSecondaryEmptyMessage,
} from '../../src/page/ScrollBox/MessagePage/domain/messagePageModel';

describe('message page model helpers', () => {
  test('bootstraps message page into comment empty state', async () => {
    const setMessages = jest.fn();

    await bootstrapMessagePage({
      setMessages,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(setMessages).toHaveBeenCalledWith([]);
  });

  test('delegates secondary tab, mark all read and message actions', () => {
    const setSecondaryTab = jest.fn();
    const setShouldScrollToTab = jest.fn();
    const markAllAsRead = jest.fn();
    const markMessageAsRead = jest.fn();
    const handlers = createMessagePageHandlers({
      setSecondaryTab,
      setShouldScrollToTab,
      markAllAsRead,
      markMessageAsRead,
      handleViewMoreRecommend: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleTabPress('reply');
    handlers.handleMarkAllReadPress();
    handlers.handleMessagePress({ id: 1, title: 'msg', isRead: false });

    expect(setSecondaryTab).toHaveBeenCalledWith('reply');
    expect(setShouldScrollToTab).toHaveBeenCalledWith(true);
    expect(markAllAsRead).toHaveBeenCalledTimes(1);
    expect(markMessageAsRead).toHaveBeenCalledWith(1);
  });

  test('returns empty copy by secondary tab', () => {
    expect(getSecondaryEmptyMessage('comment')).toContain('评论');
    expect(getSecondaryEmptyMessage('reply')).toContain('回复');
    expect(getSecondaryEmptyMessage('like')).toContain('赞');
  });
});
