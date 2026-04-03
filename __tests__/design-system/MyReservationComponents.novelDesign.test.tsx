import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/MyReservationPage/styles/MyReservationPageStyles', () => ({
  createMyReservationPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('react-native-linear-gradient', () => 'LinearGradient');

import { EmptyState } from '../../src/page/ScrollBox/MyReservationPage/components/EmptyState';
import { ReservationGrid } from '../../src/page/ScrollBox/MyReservationPage/components/ReservationGrid';
import { SubTabsSection } from '../../src/page/ScrollBox/MyReservationPage/components/SubTabsSection';
import { TopBar } from '../../src/page/ScrollBox/MyReservationPage/components/TopBar';

describe('My reservation components novelDesign', () => {
  it('renders readable top-bar and sub-tab copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="new"
            onTabChange={jest.fn()}
            onBackPress={jest.fn()}
          />
          <SubTabsSection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="online"
            onlineCount={3}
            offlineCount={2}
            onTabChange={jest.fn()}
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
      expect.arrayContaining(['新剧预约', '我的预约', '已上线 3', '待上线 2']),
    );
  });

  it('renders readable reservation card and empty-state copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <ReservationGrid
            styles={new Proxy({}, { get: () => ({}) })}
            items={[
              {
                id: 'r1',
                title: '预约短剧',
                coverUrl: '',
                reserveCount: '已有 128 人预约',
                isReserved: false,
              },
            ] as any}
            onItemPress={jest.fn()}
            onReservePress={jest.fn()}
          />
          <EmptyState
            styles={new Proxy({}, { get: () => ({}) })}
            data={{ title: '暂无预约内容' } as any}
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
      expect.arrayContaining(['即将上线', '预约短剧', '已有 128 人预约', '免费预约', '暂无预约内容']),
    );
  });
});
