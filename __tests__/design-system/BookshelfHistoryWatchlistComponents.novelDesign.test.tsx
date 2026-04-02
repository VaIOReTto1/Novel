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

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  navigateToReader: jest.fn(),
}));

import { RefreshIndicator } from '../../src/page/BookshelfPage/pages/History/components/RefreshIndicator';
import { HistoryItem } from '../../src/page/BookshelfPage/pages/History/components/HistoryItem';
import { TopBar as WatchlistTopBar } from '../../src/page/BookshelfPage/pages/Watchlist/components/TopBar';
import { EditToolbar as WatchlistEditToolbar } from '../../src/page/BookshelfPage/pages/Watchlist/components/EditToolbar';
import { WatchlistGrid } from '../../src/page/BookshelfPage/pages/Watchlist/components/WatchlistGrid';

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

  it('renders readable selection and shelf copy for history cards', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <HistoryItem
          styles={new Proxy({}, { get: () => ({}) })}
          item={{
            id: 'history-1',
            title: '测试书籍',
            author: '作者',
            description: '',
            cover: '',
            coverUrl: '',
            lastReadTime: 0,
            lastChapter: '第10章',
            readProgress: 0.6,
            progress: 0.6,
            type: 'book',
            categoryId: 1,
            readCount: 1,
            rating: 5,
            isFinished: false,
            updateStatus: 'ongoing',
            isInShelf: true,
          }}
          viewType="grid"
          isEditing
          isSelected
          onPress={jest.fn()}
          onSelect={jest.fn()}
          onAddToShelf={jest.fn()}
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
      expect.arrayContaining(['已选', '已在书架']),
    );
  });

  it('renders readable watchlist selection and loading copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <WatchlistGrid
          data={[
            {
              id: 'watch-1',
              title: '短剧',
              cover: '',
              category: 'drama',
              progress: 0.3,
              isFinished: false,
              lastWatchTime: 'today',
              currentEpisode: 2,
              episodeCount: 8,
            },
          ] as any}
          isEditMode
          selectedItems={new Set(['watch-1'])}
          onItemPress={jest.fn()}
          onItemSelect={jest.fn()}
          loading
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
      expect.arrayContaining(['已选', '加载中...']),
    );
  });
});
