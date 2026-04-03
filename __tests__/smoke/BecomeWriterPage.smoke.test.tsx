import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/BecomeWriterPage/styles/BecomeWriterPageStyles', () => ({
  createBecomeWriterPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelBackground: '#fff',
  }),
}));

jest.mock('../../src/page/ScrollBox/BecomeWriterPage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: () => ReactMock.createElement(MockText, null, 'BecomeWriterTopBar'),
    UserSection: () => ReactMock.createElement(MockText, null, 'BecomeWriterUser'),
    DataStatsSection: () => ReactMock.createElement(MockText, null, 'BecomeWriterDataStats'),
    AuthorExclusiveSection: () => ReactMock.createElement(MockText, null, 'BecomeWriterExclusive'),
    CopyrightSection: () => ReactMock.createElement(MockText, null, 'BecomeWriterCopyright'),
    CreativeActivitySection: () => ReactMock.createElement(MockText, null, 'BecomeWriterActivity'),
    WriterClassroomSection: () => ReactMock.createElement(MockText, null, 'BecomeWriterClassroom'),
    BottomButton: () => ReactMock.createElement(MockText, null, 'BecomeWriterBottomButton'),
    WelcomeModal: () => ReactMock.createElement(MockText, null, 'BecomeWriterWelcomeModal'),
  };
});

jest.mock('../../src/page/ScrollBox/BecomeWriterPage/store/becomeWriterStore', () => ({
  useBecomeWriterStore: () => ({
    loading: false,
    error: null,
    userInfo: { name: '测试作者' },
    announcement: { tag: '公告', text: '最新活动已上线' },
    selectedDataTab: 'novel',
    dataStats: {},
    isDataStatsExpanded: false,
    selectedAuthorTab: 'benefits',
    benefits: [],
    roadTimeline: [],
    platforms: [],
    copyrightWorks: [],
    selectedActivityTab: 'novel',
    activities: { novel: [], short: [] },
    courses: [],
    showWelcomeModal: false,
    isAgreementChecked: false,
    loadInitialData: jest.fn(),
    setSelectedDataTab: jest.fn(),
    setSelectedAuthorTab: jest.fn(),
    setSelectedActivityTab: jest.fn(),
    toggleDataStatsExpanded: jest.fn(),
    hideWelcomeModal: jest.fn(),
    setAgreementChecked: jest.fn(),
    handleImmediateRegister: jest.fn(),
    handleMorePress: jest.fn(),
    works: [],
    isAuthor: false,
  }),
}));

jest.mock('../../src/page/ScrollBox/BecomeWriterPage/domain/becomeWriterPageModel', () => ({
  bootstrapBecomeWriterPage: jest.fn(),
  createBecomeWriterPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleAIPress: jest.fn(),
    handleDataTabChange: jest.fn(),
    handlePressWork: jest.fn(),
    handleCreateChapter: jest.fn(),
    handleAuthorTabChange: jest.fn(),
    handleActivityTabChange: jest.fn(),
    handleActivityMorePress: jest.fn(),
    handleCourseMorePress: jest.fn(),
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
  getAuthorBooks: jest.fn(),
  navigateToAIPage: jest.fn(),
  navigateToBookManage: jest.fn(),
  navigateToWritePage: jest.fn(),
}));

import BecomeWriterPage from '../../src/page/ScrollBox/BecomeWriterPage/BecomeWriterPage';

describe('BecomeWriterPage smoke', () => {
  it('renders core become-writer sections without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<BecomeWriterPage />);
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
        'BecomeWriterTopBar',
        'BecomeWriterUser',
        'BecomeWriterDataStats',
        'BecomeWriterExclusive',
        'BecomeWriterActivity',
        'BecomeWriterClassroom',
        'BecomeWriterBottomButton',
        'BecomeWriterWelcomeModal',
      ]),
    );
  });
});
