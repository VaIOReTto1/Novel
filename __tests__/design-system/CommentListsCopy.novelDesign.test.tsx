import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelBookBackground: '#E8E3CF',
    novelDivider: '#E8DDD1',
  }),
}));

jest.mock('../../src/page/comment/CommentPage/styles/CommentPageStyles', () => ({
  createCommentPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/hooks/useReviewDetailPageStyles', () => ({
  useReviewDetailPageStyles: () => ({
    colors: {
      novelMain: '#C96A34',
      novelText: '#201A17',
      novelTextGray: '#6F6258',
      novelBackground: '#FFFDFC',
      novelBookBackground: '#E8E3CF',
      novelDivider: '#E8DDD1',
    },
    styles: new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
  }),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  NavigationBridge: {
    navigateToReviewDetail: jest.fn(),
  },
}));

jest.mock('../../src/utils/time/timeUtils', () => ({
  parseNewsDate: () => '刚刚',
}));

jest.mock('../../src/page/comment/CommentPage/store/commentStore', () => ({
  useCommentStore: () => ({
    likeComment: jest.fn(),
    comments: [
      {
        id: 'comment-1',
        commentContent:
          '评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文评论正文',
        commentUser: '评论用户',
        commentUserId: 1,
        commentUserPhoto: '',
        commentTime: '2026-04-03',
        rating: 4,
        likeCount: 9,
        isLiked: false,
        tag: 'first_comment',
      },
    ],
    loading: false,
    hasMore: false,
  }),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/store/reviewDetailStore', () => ({
  useReviewDetailStore: () => ({
    hasMoreComments: false,
    isLoadingMore: false,
    toggleCommentLike: jest.fn(),
    toggleCommentDislike: jest.fn(),
    comments: [
      {
        id: 'reply-root',
        userName: '主评论用户',
        userAvatar: '',
        content: '主评论内容',
        createTime: '2026-04-03',
        likeCount: 4,
        isLiked: false,
        isDisliked: false,
        replies: [
          {
            id: 'reply-1',
            userName: '回复用户',
            content: '回复内容',
            createTime: '2026-04-03',
            likeCount: 2,
            isLiked: false,
            isDisliked: false,
          },
          {
            id: 'reply-2',
            userName: '回复用户2',
            content: '回复内容2',
            createTime: '2026-04-03',
            likeCount: 1,
            isLiked: false,
            isDisliked: false,
          },
          {
            id: 'reply-3',
            userName: '回复用户3',
            content: '回复内容3',
            createTime: '2026-04-03',
            likeCount: 0,
            isLiked: false,
            isDisliked: false,
          },
        ],
      },
    ],
  }),
}));

import { CommentList as PageCommentList } from '../../src/page/comment/CommentPage/components/CommentList';
import { CommentList as ReviewDetailCommentList } from '../../src/page/comment/ReviewDetailPage/components/CommentList';

describe('Comment list copy novelDesign', () => {
  it('renders readable comment-page list copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<PageCommentList />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining([
        '评论用户',
        '首评',
        '+ 关注',
        '阅读 2 小时后点评',
        '查看评论 >',
      ]),
    );
  });

  it('renders readable review-detail list copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<ReviewDetailCommentList />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining([
        '全部评论',
        '写下你的评论...',
        '主评论用户',
        '回复',
        '查看全部评论 (3)',
      ]),
    );
  });
});
