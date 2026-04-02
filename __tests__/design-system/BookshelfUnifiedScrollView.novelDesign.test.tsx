import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

const mockGridView = jest.fn(() => null);
const mockListView = jest.fn(() => null);
const mockWaterfallGrid = jest.fn(() => null);
const mockRecommendationFlow = jest.fn(() => null);
const mockStyles = { marker: 'bookshelf-styles' } as any;

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/styles/BookshelfPageStyles', () => ({
  createBookshelfPageStyles: () => mockStyles,
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/GridView', () => ({
  GridView: (props: any) => {
    mockGridView(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'GridView');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/ListView', () => ({
  ListView: (props: any) => {
    mockListView(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'ListView');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/WaterfallGrid', () => ({
  WaterfallGrid: (props: any) => {
    mockWaterfallGrid(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'WaterfallGrid');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/RecommendationFlow', () => ({
  RecommendationFlow: (props: any) => {
    mockRecommendationFlow(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'RecommendationFlow');
  },
}));

import { UnifiedScrollView } from '../../src/page/BookshelfPage/pages/Bookshelf/components/UnifiedScrollView';

describe('Bookshelf UnifiedScrollView novelDesign', () => {
  beforeEach(() => {
    mockGridView.mockClear();
    mockListView.mockClear();
    mockWaterfallGrid.mockClear();
    mockRecommendationFlow.mockClear();
  });

  it('passes shared styles to grid and recommendation branches', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <UnifiedScrollView
          styles={mockStyles}
          bookshelfData={[]}
          onBookPress={jest.fn()}
          onBookLongPress={jest.fn()}
          onLoadMore={jest.fn()}
          hasMore={false}
          isLoading={false}
          recommendations={[]}
          onRecommendationPress={jest.fn()}
          onLoadMoreRecommendations={jest.fn()}
          hasMoreRecommendations={false}
          isRecommendationLoading={false}
          currentView="grid"
          isEditMode={false}
          selectedItems={[]}
          isRefreshing={false}
          onRefresh={jest.fn()}
        />,
      );
    });

    const texts = renderer.root.findAllByType(Text).map((node) => node.props.children).flat();
    expect(texts).toEqual(expect.arrayContaining(['GridView', 'RecommendationFlow']));

    expect(mockGridView).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
    expect(mockRecommendationFlow).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
  });

  it('routes shared styles into list and waterfall branches too', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <UnifiedScrollView
          styles={mockStyles}
          bookshelfData={[]}
          onBookPress={jest.fn()}
          onBookLongPress={jest.fn()}
          onLoadMore={jest.fn()}
          hasMore={false}
          isLoading={false}
          recommendations={[]}
          onRecommendationPress={jest.fn()}
          onLoadMoreRecommendations={jest.fn()}
          hasMoreRecommendations={false}
          isRecommendationLoading={false}
          currentView="list"
          isEditMode={false}
          selectedItems={[]}
          isRefreshing={false}
          onRefresh={jest.fn()}
        />,
      );

      ReactTestRenderer.create(
        <UnifiedScrollView
          styles={mockStyles}
          bookshelfData={[]}
          onBookPress={jest.fn()}
          onBookLongPress={jest.fn()}
          onLoadMore={jest.fn()}
          hasMore={false}
          isLoading={false}
          recommendations={[]}
          onRecommendationPress={jest.fn()}
          onLoadMoreRecommendations={jest.fn()}
          hasMoreRecommendations={false}
          isRecommendationLoading={false}
          currentView="waterfall"
          isEditMode={false}
          selectedItems={[]}
          isRefreshing={false}
          onRefresh={jest.fn()}
        />,
      );
    });

    expect(mockListView).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
    expect(mockWaterfallGrid).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
  });
});
