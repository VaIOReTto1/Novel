import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/styles/BookshelfPageStyles', () => ({
  createBookshelfPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/BookItem', () => ({
  BookItem: ({ item, viewType }: { item: { title: string }; viewType: string }) => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, `${viewType}:${item.title}`);
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/LoadMoreIndicator', () => ({
  LoadMoreIndicator: ({ isLoading }: { isLoading: boolean }) => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, isLoading ? 'LoadMoreLoading' : 'LoadMoreIdle');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/EmptyState', () => ({
  EmptyState: ({ type }: { type: string }) => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, `EmptyState:${type}`);
  },
}));

import { GridView } from '../../src/page/BookshelfPage/pages/Bookshelf/components/GridView';
import { ListView } from '../../src/page/BookshelfPage/pages/Bookshelf/components/ListView';
import { WaterfallGrid } from '../../src/page/BookshelfPage/pages/Bookshelf/components/WaterfallGrid';

describe('Bookshelf core views novelDesign', () => {
  it('renders shared empty state for empty bookshelf wrappers', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <GridView data={[]} />
          <ListView data={[]} />
          <WaterfallGrid data={[]} loading={false} hasMore={false} onBookPress={jest.fn()} />
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
        'EmptyState:bookshelf',
      ]),
    );
  });

  it('renders shared load-more feedback for non-empty bookshelf wrappers', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    const data = [{ id: 'book-1', title: '书籍一' }] as any;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <GridView data={data} isLoading hasMore />
          <ListView data={data} isLoading hasMore />
          <WaterfallGrid data={data} isLoading hasMore onBookPress={jest.fn()} />
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

    expect(texts.filter((value) => value === 'LoadMoreLoading').length).toBeGreaterThanOrEqual(3);
  });
});
