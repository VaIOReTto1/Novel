import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/comment/CommentPage/styles/CommentPageStyles', () => ({
  createCommentPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  NavigationBridge: {
    navigateBack: jest.fn(),
    navigateToWriteReview: jest.fn(),
  },
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/page/comment/CommentPage/store/commentStore', () => ({
  useCommentStore: () => ({
    loadComments: jest.fn(),
    loadMoreComments: jest.fn(),
    isRefreshing: false,
    reset: jest.fn(),
    refreshComments: jest.fn(),
  }),
}));

jest.mock('../../src/page/comment/CommentPage/domain/commentPageModel', () => ({
  bootstrapCommentPage: jest.fn().mockResolvedValue(() => undefined),
  createCommentPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleLoadMore: jest.fn(),
    handleSearch: jest.fn(),
    handleCategoryChange: jest.fn(),
    handleWriteReview: jest.fn(),
  }),
}));

jest.mock('../../src/page/comment/CommentPage/components/TopBar', () => ({
  TopBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'CommentTopBar');
  },
}));

jest.mock('../../src/page/comment/CommentPage/components/CommentList', () => ({
  CommentList: ({ ListHeaderComponent }: { ListHeaderComponent?: React.ReactNode }) => {
    const ReactMock = require('react');
    const { View: MockView, Text: MockText } = require('react-native');
    return ReactMock.createElement(
      MockView,
      null,
      ListHeaderComponent,
      ReactMock.createElement(MockText, null, 'CommentList'),
    );
  },
}));

jest.mock('../../src/page/comment/CommentPage/components/RatingSection', () => ({
  RatingSection: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'RatingSection');
  },
}));

jest.mock('../../src/page/comment/CommentPage/components/CategorySection', () => ({
  CategorySection: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'CategorySection');
  },
}));

import CommentPage from '../../src/page/comment/CommentPage/CommentPage';

describe('CommentPage smoke', () => {
  it('renders core comment page sections without crashing', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    await ReactTestRenderer.act(async () => {
      renderer = ReactTestRenderer.create(<CommentPage bookId="1" />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['CommentTopBar', 'RatingSection', 'CategorySection', 'CommentList']),
    );
  });
});
