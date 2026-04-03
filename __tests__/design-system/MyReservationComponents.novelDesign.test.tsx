import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

import { useMyReservationStore } from '../../src/page/ScrollBox/MyReservationPage/store/myReservationStore';

jest.mock('react-native-linear-gradient', () => 'LinearGradient');

import { EmptyState } from '../../src/page/ScrollBox/MyReservationPage/components/EmptyState';
import { ReservationGrid } from '../../src/page/ScrollBox/MyReservationPage/components/ReservationGrid';
import { SubTabsSection } from '../../src/page/ScrollBox/MyReservationPage/components/SubTabsSection';
import { TopBar } from '../../src/page/ScrollBox/MyReservationPage/components/TopBar';

describe('My reservation components novelDesign', () => {
  afterEach(() => {
    useMyReservationStore.setState({
      loading: false,
      error: null,
      userInfo: null,
      selectedTab: 'new',
      selectedSubTab: 'online',
      newReservations: [],
      myOnlineReservations: [],
      myOfflineReservations: [],
      emptyStates: {
        online: { icon: '空', title: '暂无预约内容' },
        offline: { icon: '空', title: '暂无预约内容' },
      },
    });
  });

  it('renders readable top tabs, sub tabs, reservation cards and empty-state copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="mine"
            onTabChange={jest.fn()}
            onBackPress={jest.fn()}
          />
          <SubTabsSection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="online"
            onlineCount={2}
            offlineCount={3}
            onTabChange={jest.fn()}
          />
          <ReservationGrid
            styles={new Proxy({}, { get: () => ({}) })}
            items={[
              {
                id: 'r1',
                title: '未来事务所',
                coverUrl: '',
                reserveCount: '5.4万人预约',
                status: 'upcoming',
                type: 'drama',
                tags: ['即将上线'],
                isReserved: false,
              },
              {
                id: 'r2',
                title: '青梅竹马',
                coverUrl: '',
                reserveCount: '2.2万人预约',
                status: 'upcoming',
                type: 'drama',
                tags: ['即将上线'],
                isReserved: true,
              },
            ] as any}
            onItemPress={jest.fn()}
            onReservePress={jest.fn()}
          />
          <EmptyState
            styles={new Proxy({}, { get: () => ({}) })}
            data={{ icon: '空', title: '暂无预约内容' }}
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
        '<',
        '新剧预约',
        '我的预约',
        '已上线 2',
        '待上线 3',
        '即将上线',
        '未来事务所',
        '5.4万人预约',
        '免费预约',
        '青梅竹马',
        '2.2万人预约',
        '已预约',
        '暂无预约内容',
      ]),
    );
  });

  it('loads readable reservation mock data into the store', async () => {
    await ReactTestRenderer.act(async () => {
      await useMyReservationStore.getState().loadInitialData();
    });

    const state = useMyReservationStore.getState();

    expect(state.userInfo?.name).toBe('测试作者');
    expect(state.emptyStates.online.title).toBe('暂无预约内容');
    expect(state.newReservations[0]).toMatchObject({
      title: '未来事务所',
      reserveCount: '5.4万人预约',
      description: '科幻悬疑剧',
      tags: ['即将上线'],
    });
    expect(state.newReservations.some((item) => item.isReserved)).toBe(true);
  });
});
