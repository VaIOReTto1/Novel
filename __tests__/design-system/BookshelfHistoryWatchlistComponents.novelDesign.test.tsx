import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { ActivityIndicator, Text } from 'react-native';

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelChipBackground: '#EBEDF0',
  }),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelChipBackground: '#EBEDF0',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Watchlist/styles/WatchlistPageStyles', () => ({
  createWatchlistPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { RefreshIndicator } from '../../src/page/BookshelfPage/pages/History/components/RefreshIndicator';
import { TopBar as WatchlistTopBar } from '../../src/page/BookshelfPage/pages/Watchlist/components/TopBar';
import { EditToolbar as WatchlistEditToolbar } from '../../src/page/BookshelfPage/pages/Watchlist/components/EditToolbar';

describe('Bookshelf history/watchlist components novelDesign', () => {
  it('uses ActivityIndicator for pull-to-refresh feedback instead of symbol glyphs', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
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

    const indicator = renderer.root.findByType(ActivityIndicator);
    expect(indicator).toBeTruthy();
  });

  it('renders editorial watchlist top-bar and edit copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <WatchlistTopBar onEditPress={jest.fn()} />
          <WatchlistEditToolbar
            selectedCount={2}
            totalCount={5}
            onSelectAll={jest.fn()}
            onDelete={jest.fn()}
            isAllSelected={false}
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
        '编辑精选',
        '编辑',
        '全选',
        '已选择 2/5 项',
        '删除',
      ]),
    );
  });

  it('switches watchlist select-all copy when everything is selected', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <WatchlistEditToolbar
          selectedCount={3}
          totalCount={3}
          onSelectAll={jest.fn()}
          onDelete={jest.fn()}
          isAllSelected
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

    expect(texts).toContain('取消全选');
  });
});
