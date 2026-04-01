import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { ActivityIndicator } from 'react-native';

const mockMaterialIcons = jest.fn(() => null);

jest.mock('react-native-vector-icons/MaterialIcons', () => (props: any) => {
  mockMaterialIcons(props);
  return null;
});

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

import { TopBar } from '../../src/page/BookshelfPage/pages/Bookshelf/components/TopBar';
import { ViewSwitcher } from '../../src/page/BookshelfPage/pages/Bookshelf/components/ViewSwitcher';
import { LoadMoreIndicator } from '../../src/page/BookshelfPage/pages/Bookshelf/components/LoadMoreIndicator';

describe('Bookshelf components novelDesign icons and loaders', () => {
  beforeEach(() => {
    mockMaterialIcons.mockClear();
  });

  it('uses vector icons for view switchers instead of text glyphs', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <>
          <TopBar currentView="grid" onViewChange={jest.fn()} onFilterPress={jest.fn()} onEditPress={jest.fn()} />
          <ViewSwitcher currentView="grid" onViewChange={jest.fn()} />
        </>,
      );
    });

    const names = mockMaterialIcons.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(
      expect.arrayContaining(['grid-view', 'view-list', 'view-quilt']),
    );
  });

  it('uses brand loading color for infinite-load feedback', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<LoadMoreIndicator isLoading />);
    });

    const indicator = renderer.root.findByType(ActivityIndicator);
    expect(indicator.props.color).toBe('#C96A34');
  });
});
