import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/FeedbackHelpPage/styles/FeedbackHelpPageStyles', () => ({
  createFeedbackHelpPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelBackground: '#fff',
  }),
}));

jest.mock('../../src/page/ScrollBox/FeedbackHelpPage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: () => ReactMock.createElement(MockText, null, 'FeedbackHelpTopBar'),
    UserSection: () => ReactMock.createElement(MockText, null, 'FeedbackHelpUser'),
    ConsultSection: () => ReactMock.createElement(MockText, null, 'FeedbackHelpConsult'),
    FrequentQuestions: () => ReactMock.createElement(MockText, null, 'FeedbackHelpQuestions'),
    ContactSection: () => ReactMock.createElement(MockText, null, 'FeedbackHelpContact'),
  };
});

jest.mock('../../src/page/ScrollBox/FeedbackHelpPage/store/feedbackHelpStore', () => ({
  useFeedbackHelpStore: () => ({
    consultCategories: [],
    frequentQuestions: [],
    selectCategory: jest.fn(),
    selectQuestion: jest.fn(),
    resetToMain: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/FeedbackHelpPage/domain/feedbackHelpPageModel', () => ({
  bootstrapFeedbackHelpMainPage: jest.fn(),
  createFeedbackHelpMainHandlers: () => ({
    handleBack: jest.fn(),
    handleSearch: jest.fn(),
    handleCategoryPress: jest.fn(),
    handleQuestionPress: jest.fn(),
    handleContactPress: jest.fn(),
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
  navigateToQuestionList: jest.fn(),
  navigateToQuestionDetail: jest.fn(),
}));

import { FeedbackHelpMainPage } from '../../src/page/ScrollBox/FeedbackHelpPage/FeedbackHelpMainPage';

describe('FeedbackHelpMainPage smoke', () => {
  it('renders feedback-help shell without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<FeedbackHelpMainPage />);
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
        'FeedbackHelpTopBar',
        'FeedbackHelpUser',
        'FeedbackHelpConsult',
        'FeedbackHelpQuestions',
        'FeedbackHelpContact',
      ]),
    );
  });
});
