import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { ActivityIndicator, Text } from 'react-native';

jest.mock('../../src/page/ProfilePage/styles/ProfilePageStyles', () => ({
  createHomePageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { BottomBox } from '../../src/page/ProfilePage/components/BottomBox';
import { LoadMoreIndicator } from '../../src/page/ProfilePage/components/LoadMoreIndicator';
import { LoginBar } from '../../src/page/ProfilePage/components/LoginBar';
import { RefreshIndicator } from '../../src/page/ProfilePage/components/RefreshIndicator';

describe('Profile components novelDesign', () => {
  it('renders readable login and balance copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <LoginBar
            styles={new Proxy({}, { get: () => ({}) })}
            isLoggedIn={false}
            onLogin={jest.fn()}
          />
          <BottomBox
            styles={new Proxy({}, { get: () => ({}) })}
            coins={10}
            balance={5}
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
        '点击登录 / 注册',
        '10 金币',
        '5.00 余额（元）',
        '微信提现 >',
        '继续阅读 >',
      ]),
    );
  });

  it('renders readable loading and refresh feedback', () => {
    let loadRenderer!: ReactTestRenderer.ReactTestRenderer;
    let refreshRenderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      loadRenderer = ReactTestRenderer.create(
        <LoadMoreIndicator
          loading
          hasMore
          styles={new Proxy({}, { get: () => ({}) })}
        />,
      );
      refreshRenderer = ReactTestRenderer.create(
        <RefreshIndicator
          styles={new Proxy({}, { get: () => ({}) })}
          isPullingDown
          isRefreshing={false}
          pullDistance={80}
          threshold={60}
          spinStyle={{}}
        />,
      );
    });

    const loadingTexts = loadRenderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');
    expect(loadingTexts).toEqual(expect.arrayContaining(['加载中...']));

    const indicator = refreshRenderer.root.findByType(ActivityIndicator);
    expect(indicator).toBeTruthy();

    const refreshTexts = refreshRenderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');
    expect(refreshTexts).toEqual(expect.arrayContaining(['释放刷新']));
  });
});
