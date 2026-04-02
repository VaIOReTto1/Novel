import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

const mockStyles = { marker: 'bookshelf-styles' } as any;
const mockTopBar = jest.fn(() => null);
const mockEditToolbar = jest.fn(() => null);
const mockUnifiedScrollView = jest.fn(() => null);

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/styles/BookshelfPageStyles', () => ({
  createBookshelfPageStyles: () => mockStyles,
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/store/bookshelfStore', () => ({
  useBookshelfStore: () => ({
    bookshelfItems: [{ id: 'book-1', title: '书名' }],
    isLoading: false,
    hasMore: false,
    selectedItems: new Set(['book-1']),
    loadBookshelfItems: jest.fn(),
  }),
  useRecommendationStore: () => ({
    recommendations: [],
    isLoading: false,
    hasMore: false,
    loadRecommendations: jest.fn(),
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/hooks/useViewMode', () => ({
  useViewMode: () => ({
    currentView: 'grid',
    isTransitioning: false,
    switchView: jest.fn(),
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/hooks/useEditMode', () => ({
  useEditMode: () => ({
    isEditMode: true,
    selectedCount: 1,
    enterEditMode: jest.fn(),
    exitEditMode: jest.fn(),
    toggleItemSelection: jest.fn(),
    selectAllItems: jest.fn(),
    deleteSelectedItems: jest.fn(),
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/hooks/useRefreshLogic', () => ({
  useRefreshLogic: () => ({
    isRefreshing: false,
    onRefresh: jest.fn(),
    onLoadMore: jest.fn(),
    onLoadMoreRecommendations: jest.fn(),
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/domain/bookshelfPageModel', () => ({
  bootstrapBookshelfPage: jest.fn(),
  createBookshelfPageHandlers: () => ({
    handleBookPress: jest.fn(),
    handleBookLongPress: jest.fn(),
    handleRecommendationPress: jest.fn(),
    handleMenuPress: jest.fn(),
    handleFilterPress: jest.fn(),
    handleEditPress: jest.fn(),
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/TopBar', () => ({
  TopBar: (props: any) => {
    mockTopBar(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookshelfTopBar');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/EditToolbar', () => ({
  EditToolbar: (props: any) => {
    mockEditToolbar(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookshelfEditToolbar');
  },
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/components/UnifiedScrollView', () => ({
  UnifiedScrollView: (props: any) => {
    mockUnifiedScrollView(props);
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookshelfUnifiedScrollView');
  },
}));

import { BookshelfPage } from '../../src/page/BookshelfPage/pages/Bookshelf/BookshelfPage';

describe('Bookshelf nested page smoke', () => {
  beforeEach(() => {
    mockTopBar.mockClear();
    mockEditToolbar.mockClear();
    mockUnifiedScrollView.mockClear();
  });

  it('creates one shared styles object and passes it into nested shell children', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<BookshelfPage />);
    });

    const texts = renderer.root.findAllByType(Text).map((node) => node.props.children).flat();
    expect(texts).toEqual(
      expect.arrayContaining(['BookshelfTopBar', 'BookshelfEditToolbar', 'BookshelfUnifiedScrollView']),
    );

    expect(mockTopBar).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
    expect(mockEditToolbar).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
    expect(mockUnifiedScrollView).toHaveBeenCalledWith(expect.objectContaining({ styles: mockStyles }));
  });
});
