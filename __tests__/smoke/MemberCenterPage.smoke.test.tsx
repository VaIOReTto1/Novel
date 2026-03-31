import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/MemberCenterPage/styles/MemberCenterPageStyles', () => ({
  createMemberCenterPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
  getVIPThemeColors: () => ({
    primary: '#C96A34',
    light: '#E08B56',
    gradient: ['#C96A34', '#E08B56'],
  }),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelBackground: '#fff',
  }),
}));

jest.mock('../../src/page/ScrollBox/MemberCenterPage/components', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return {
    TopBar: ({ title }: { title: string }) => ReactMock.createElement(MockText, null, title),
    VIPCardCarousel: () => ReactMock.createElement(MockText, null, 'VIPCardCarousel'),
    MemberBenefits: () => ReactMock.createElement(MockText, null, 'MemberBenefits'),
    PricePackages: () => ReactMock.createElement(MockText, null, 'PricePackages'),
    BenefitComparison: () => ReactMock.createElement(MockText, null, 'BenefitComparison'),
    VIPRecommendation: () => ReactMock.createElement(MockText, null, 'VIPRecommendation'),
    BottomPurchase: () => ReactMock.createElement(MockText, null, 'BottomPurchase'),
    TaskCards: () => ReactMock.createElement(MockText, null, 'TaskCards'),
  };
});

jest.mock('../../src/page/ScrollBox/MemberCenterPage/store/memberCenterStore', () => ({
  useMemberCenterStore: () => ({
    loading: false,
    error: null,
    vipCards: [{ id: 'member', type: 'member', title: '会员中心', subtitle: '', bgGradient: [], isActive: true }],
    currentCardIndex: 0,
    currentBenefits: [],
    pricePackages: [],
    selectedPackageId: null,
    benefitComparison: [],
    vipRecommendations: [],
    taskCards: [],
    loadInitialData: jest.fn(),
    setCurrentCard: jest.fn(),
    selectPricePackage: jest.fn(),
    handlePurchase: jest.fn(),
    handlePrivacyPress: jest.fn(),
    handleTermsPress: jest.fn(),
    handleTaskPress: jest.fn(),
  }),
}));

jest.mock('../../src/page/ScrollBox/MemberCenterPage/domain/memberCenterPageModel', () => ({
  bootstrapMemberCenterPage: jest.fn(),
  createMemberCenterPageHandlers: () => ({
    handleBackPress: jest.fn(),
    handleCardChange: jest.fn(),
    handlePackageSelect: jest.fn(),
    handlePurchasePress: jest.fn(),
    handlePrivacyLinkPress: jest.fn(),
    handleTermsLinkPress: jest.fn(),
    handleTaskCardPress: jest.fn(),
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateBack: jest.fn(),
}));

import MemberCenterPage from '../../src/page/ScrollBox/MemberCenterPage/MemberCenterPage';

describe('MemberCenterPage smoke', () => {
  it('renders core member-center sections without crashing', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<MemberCenterPage />);
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
        '会员中心',
        'VIPCardCarousel',
        'MemberBenefits',
        'PricePackages',
        'BenefitComparison',
        'BottomPurchase',
      ]),
    );
  });
});
