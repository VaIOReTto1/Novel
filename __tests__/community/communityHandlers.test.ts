import { createCommunityActionHandlers } from '../../src/page/BookshelfPage/pages/Community/hooks/communityHandlers';
import { CommunityPost } from '../../src/page/BookshelfPage/pages/Community/types';

const samplePost: CommunityPost = {
  id: 'post-1',
  title: '社区热帖',
  content: '这里是一段帖子内容',
  author: {
    id: 'user-1',
    name: '作者甲',
    avatar: 'https://example.com/avatar.png',
    level: '书友',
  },
  publishTime: '刚刚',
  likeCount: 12,
  commentCount: 3,
  shareCount: 1,
  isLiked: false,
  novelName: '示例小说',
  images: ['https://example.com/cover.png'],
};

describe('communityHandlers', () => {
  it('navigates comment flow with the current post payload', () => {
    const commentPost = jest.fn();
    const navigateToReviewDetail = jest.fn();
    const handlers = createCommunityActionHandlers({
      posts: [samplePost],
      commentPost,
      sharePost: jest.fn(),
      navigateToReviewDetail,
      navigateToSearch: jest.fn(),
      navigateToMessage: jest.fn(),
      navigateToWritePage: jest.fn(),
      share: jest.fn(),
      alert: jest.fn(),
    });

    handlers.handleComment('post-1');

    expect(commentPost).toHaveBeenCalledWith('post-1');
    expect(navigateToReviewDetail).toHaveBeenCalledTimes(1);
    expect(JSON.parse(navigateToReviewDetail.mock.calls[0][0])).toEqual(
      expect.objectContaining({
        id: 'post-1',
        commentUser: '作者甲',
        commentContent: '这里是一段帖子内容',
        commentTime: '刚刚',
        likeCount: 12,
        bookInfo: expect.objectContaining({
          bookId: 'post-1',
          bookName: '示例小说',
        }),
      }),
    );
  });

  it('shares the post with a synthesized message', async () => {
    const sharePost = jest.fn();
    const share = jest.fn().mockResolvedValue({});
    const handlers = createCommunityActionHandlers({
      posts: [samplePost],
      commentPost: jest.fn(),
      sharePost,
      navigateToReviewDetail: jest.fn(),
      navigateToSearch: jest.fn(),
      navigateToMessage: jest.fn(),
      navigateToWritePage: jest.fn(),
      share,
      alert: jest.fn(),
    });

    await handlers.handleShare('post-1');

    expect(sharePost).toHaveBeenCalledWith('post-1');
    expect(share).toHaveBeenCalledWith(
      expect.objectContaining({
        message: expect.stringContaining('社区热帖'),
      }),
    );
  });

  it('routes notification publish and search to the existing bridge methods', () => {
    const navigateToSearch = jest.fn();
    const navigateToMessage = jest.fn();
    const navigateToWritePage = jest.fn();
    const handlers = createCommunityActionHandlers({
      posts: [samplePost],
      commentPost: jest.fn(),
      sharePost: jest.fn(),
      navigateToReviewDetail: jest.fn(),
      navigateToSearch,
      navigateToMessage,
      navigateToWritePage,
      share: jest.fn(),
      alert: jest.fn(),
    });

    handlers.handleSearch();
    handlers.handleNotification();
    handlers.handlePublish();

    expect(navigateToSearch).toHaveBeenCalledWith('');
    expect(navigateToMessage).toHaveBeenCalledTimes(1);
    expect(navigateToWritePage).toHaveBeenCalledTimes(1);
  });

  it('shows supported actions in the more menu and keeps user/subscribe deferred', () => {
    const alert = jest.fn();
    const handlers = createCommunityActionHandlers({
      posts: [samplePost],
      commentPost: jest.fn(),
      sharePost: jest.fn(),
      navigateToReviewDetail: jest.fn(),
      navigateToSearch: jest.fn(),
      navigateToMessage: jest.fn(),
      navigateToWritePage: jest.fn(),
      share: jest.fn(),
      alert,
    });

    handlers.handleMore('post-1');
    handlers.handleUserPress('user-1');
    handlers.handleSubscribe('user-1');

    expect(alert).toHaveBeenNthCalledWith(
      1,
      '更多操作',
      '请选择操作',
      expect.arrayContaining([
        expect.objectContaining({ text: '查看评论' }),
        expect.objectContaining({ text: '分享' }),
      ]),
    );
    expect(alert).toHaveBeenNthCalledWith(2, '暂未开放', '用户主页暂未开放');
    expect(alert).toHaveBeenNthCalledWith(3, '暂未开放', '订阅功能暂未开放');
  });
});
