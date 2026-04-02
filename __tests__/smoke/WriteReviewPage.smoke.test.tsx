import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelMainLight: '#F86827',
    novelBookBackground: '#E8E3CF',
    novelBackground: '#FFFDFC',
    novelDivider: '#E8DDD1',
    novelSecondaryBackground: '#F3ECE3',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelLightGray: '#DDDDDD',
    novelChipBackground: '#EBEDF0',
    novelError: '#B3453C',
    outline: '#E0E0E0',
    novelThemeMode: 'light',
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  NavigationBridge: {
    navigateBack: jest.fn(),
  },
}));

jest.mock('../../src/page/comment/WriteReviewPage/domain/writeReviewPageModel', () => ({
  bootstrapWriteReviewPage: jest.fn(),
  createWriteReviewPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleSubmit: jest.fn(),
    handleDismissKeyboard: jest.fn(),
    handleInputFocus: jest.fn(),
  }),
  canSubmitWriteReview: () => true,
}));

jest.mock('../../src/page/comment/WriteReviewPage/hooks/useWriteReview', () => ({
  useWriteReview: () => ({
    rating: 4,
    content: '内容',
    contentError: '',
    contentLength: 2,
    isSubmitting: false,
    starAnimations: [],
    setRating: jest.fn(),
    setContent: jest.fn(),
    submitReview: jest.fn(),
    clearErrors: jest.fn(),
  }),
}));

jest.mock('../../src/page/comment/WriteReviewPage/components', () => ({
  TopBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'WriteReviewTopBar');
  },
  RatingInput: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'WriteReviewRating');
  },
  ReviewForm: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'WriteReviewForm');
  },
}));

import WriteReviewPage from '../../src/page/comment/WriteReviewPage/WriteReviewPage';

describe('WriteReviewPage smoke', () => {
  it('renders write-review shell without crashing', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    await ReactTestRenderer.act(async () => {
      renderer = ReactTestRenderer.create(
        <WriteReviewPage bookId="book-1" source="comment" initialRating={4} />,
      );
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['WriteReviewTopBar', 'WriteReviewRating', 'WriteReviewForm']),
    );
  });
});
