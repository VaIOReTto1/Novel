import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { ActivityIndicator, Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/HistoryPage/styles/HistoryPageStyles', () => ({
  createHistoryPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateToReader: jest.fn(),
}));

import { ContentArea } from '../../src/page/ScrollBox/HistoryPage/components/ContentArea';
import { HistoryItem } from '../../src/page/ScrollBox/HistoryPage/components/HistoryItem';
import { RefreshIndicator } from '../../src/page/ScrollBox/HistoryPage/components/RefreshIndicator';
import { TabsArea } from '../../src/page/ScrollBox/HistoryPage/components/TabsArea';
import { TopBar } from '../../src/page/ScrollBox/HistoryPage/components/TopBar';

describe('History page components novelDesign', () => {
  it('renders readable top-bar and tabs copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            onBackPress={jest.fn()}
            onSearchPress={jest.fn()}
          />
          <TabsArea
            styles={new Proxy({}, { get: () => ({}) })}
            tabs={[
              { id: 'all', name: '全部', type: 'all' },
              { id: 'book', name: '小说', type: 'book' },
            ] as any}
            selectedTab="all"
            onTabPress={jest.fn()}
            viewType="grid"
            onViewTypeChange={jest.fn()}
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
      expect.arrayContaining(['浏览历史', '全部', '小说', '列表', '更多']),
    );
  });

  it('renders readable empty, loading and refresh copy', () => {
    let contentRenderer!: ReactTestRenderer.ReactTestRenderer;
    let refreshRenderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      contentRenderer = ReactTestRenderer.create(
        <ContentArea
          styles={new Proxy({}, { get: () => ({}) })}
          items={[]}
          viewType="list"
          loading={false}
          hasMore={false}
          onItemPress={jest.fn()}
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

    const contentTexts = contentRenderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(contentTexts).toContain('暂无浏览历史');

    const indicator = refreshRenderer.root.findByType(ActivityIndicator);
    expect(indicator).toBeTruthy();

    const refreshTexts = refreshRenderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(refreshTexts).toContain('释放刷新');
  });

  it('renders readable history item placeholder and progress copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <HistoryItem
          styles={new Proxy({}, { get: () => ({}) })}
          item={{
            id: 'book-1',
            title: '示例小说',
            author: '示例作者',
            coverUrl: '',
            readProgress: 72,
          } as any}
          index={0}
          viewType="grid"
          onPress={jest.fn()}
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
      expect.arrayContaining(['暂无封面', '示例小说', '示例作者', '已读 72%']),
    );
  });
});
