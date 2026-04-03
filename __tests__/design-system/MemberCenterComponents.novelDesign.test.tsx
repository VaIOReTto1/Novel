import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/MemberCenterPage/styles/MemberCenterPageStyles', () => ({
  VIP_COLORS: {
    memberGradient: ['#C96A34', '#8B4A2C'],
  },
}));

jest.mock('react-native-linear-gradient', () => 'LinearGradient');

import { BottomPurchase } from '../../src/page/ScrollBox/MemberCenterPage/components/BottomPurchase';
import { TopBar } from '../../src/page/ScrollBox/MemberCenterPage/components/TopBar';
import { VIPRecommendation } from '../../src/page/ScrollBox/MemberCenterPage/components/VIPRecommendation';

describe('Member center components novelDesign', () => {
  it('renders readable top-bar and purchase copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            title="会员中心"
            onBackPress={jest.fn()}
          />
          <BottomPurchase
            styles={new Proxy({}, { get: () => ({}) })}
            selectedPackage={{ price: '¥10' } as any}
            onPurchase={jest.fn()}
            onPrivacyPress={jest.fn()}
            onTermsPress={jest.fn()}
          />
        </>,
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
      expect.arrayContaining([
        '会员中心',
        '¥10 立即开通',
        '到期前自动续费 ¥10/月',
        '会员服务条款',
        '自动续费协议',
      ]),
    );
  });

  it('renders readable VIP recommendation title', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <VIPRecommendation
          styles={new Proxy({}, { get: () => ({}) })}
          recommendations={[
            {
              id: 'rec-1',
              title: '会员推荐',
              description: '推荐描述',
              coverUrl: '',
            },
          ] as any}
          cardType="member"
        />,
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
      expect.arrayContaining(['会员专属推荐', '会员推荐', '推荐描述']),
    );
  });
});
