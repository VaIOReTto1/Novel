import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/comment/ReviewDetailPage/hooks/useReviewDetailPageStyles', () => ({
  useReviewDetailPageStyles: () => ({
    colors: {
      novelMain: '#C96A34',
      novelBackground: '#FFFDFC',
      novelTextGray: '#6F6258',
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
    navigateBack: jest.fn(),
  },
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/store/reviewDetailStore', () => ({
  useReviewDetailStore: () => ({
    reviewDetail: null,
    comments: [],
    loadReviewDetail: jest.fn(),
    loadMoreComments: jest.fn(),
    isRefreshing: false,
    isLoading: false,
    error: null,
    reset: jest.fn(),
    toggleCommentLike: jest.fn(),
    toggleCommentDislike: jest.fn(),
    refreshReviewDetail: jest.fn(),
  }),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/hooks', () => ({
  useRefresh: (_fn: any) => ({
    isRefreshing: false,
    onRefresh: jest.fn(),
  }),
  useAnimations: () => ({
    fadeAnim: 1,
    scaleAnim: 1,
  }),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/domain/reviewDetailPageModel', () => ({
  bootstrapReviewDetailPage: jest.fn().mockResolvedValue(() => undefined),
  createReviewDetailPageHandlers: () => ({
    handleBack: jest.fn(),
    handleLoadMore: jest.fn(),
    handleLike: jest.fn(),
    handleDislike: jest.fn(),
    handleReply: jest.fn(),
    handleViewMoreReplies: jest.fn(),
  }),
}));

jest.mock('../../src/page/comment/ReviewDetailPage/components/TopBar', () => ({
  TopBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'ReviewTopBar');
  },
}));

jest.mock('../../src/page/comment/ReviewDetailPage/components/ReviewContent', () => ({
  ReviewContent: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'ReviewContent');
  },
}));

jest.mock('../../src/page/comment/ReviewDetailPage/components/CommentList', () => ({
  CommentList: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'ReviewCommentList');
  },
}));

jest.mock('../../src/page/comment/ReviewDetailPage/components/RepliesSheet', () => ({
  RepliesSheet: () => null,
}));

import { ReviewDetailPage } from '../../src/page/comment/ReviewDetailPage/ReviewDetailPage';

describe('ReviewDetailPage smoke', () => {
  it('renders review detail shell without crashing', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    await ReactTestRenderer.act(async () => {
      renderer = ReactTestRenderer.create(<ReviewDetailPage commentData="{}" />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['ReviewTopBar', 'ReviewCommentList']),
    );
  });
});
