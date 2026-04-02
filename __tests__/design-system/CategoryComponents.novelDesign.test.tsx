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

jest.mock('../../src/page/CategoryPage/styles/CategoryPageStyles', () => ({
  createCategoryPageStyles: () =>
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

import { Sidebar } from '../../src/page/CategoryPage/components/Sidebar';
import { TopTabs } from '../../src/page/CategoryPage/components/TopTabs';
import { BookGrid } from '../../src/page/CategoryPage/components/BookGrid';

describe('Category components novelDesign', () => {
  it('renders clean top-tab and sidebar copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopTabs tab="male" onChange={jest.fn()} />
          <Sidebar
            items={[
              { id: 1, name: '全部' },
              { id: 2, name: '古风' },
            ] as any}
            activeId={1}
            onSelect={jest.fn()}
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
      expect.arrayContaining(['男生', '女生', '全部', '古风']),
    );
  });

  it('renders clean loading and end-state copy in book grid', () => {
    let loadingRenderer!: ReactTestRenderer.ReactTestRenderer;
    let endRenderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      loadingRenderer = ReactTestRenderer.create(
        <BookGrid
          data={[]}
          columns={3}
          isLoading
          hasMore
        />,
      );
      endRenderer = ReactTestRenderer.create(
        <BookGrid
          data={[
            { id: 1, bookName: '书籍', picUrl: '' },
          ] as any}
          columns={3}
          isLoading={false}
          hasMore={false}
        />,
      );
    });

    const loadingTexts = loadingRenderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    const endTexts = endRenderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(loadingTexts).toContain('加载中...');
    expect(endTexts).toContain('已加载全部');
  });
});
