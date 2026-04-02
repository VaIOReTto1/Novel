import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/component/IconComponent', () => ({
  __esModule: true,
  default: () => null,
}));

jest.mock('../../src/page/ProfilePage/utils/constants', () => ({
  PAGE_WIDTH: 320,
}));

jest.mock('../../src/page/ProfilePage/utils/constants', () => ({
  ...jest.requireActual('../../src/page/ProfilePage/utils/constants'),
  PAGE_WIDTH: 320,
}));

import { ScrollableArea } from '../../src/page/ProfilePage/components/ScrollableArea';
import { WaterfallGrid } from '../../src/page/ProfilePage/components/WaterfallGrid';

describe('Profile scrollable components novelDesign', () => {
  it('renders readable author entry and editorial ad copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <ScrollableArea
          styles={new Proxy({}, { get: () => ({}) })}
          scrollX={{ value: 0 }}
          animatedContainerStyle={{}}
          firstPageIconsStyle={{}}
          secondPageIconsStyle={{}}
          thirdPageIconsStyle={{}}
          firstPageAdStyle={{}}
          colors={{ novelMain: '#C96A34', novelDivider: '#E8DDD1' }}
          isAuthor={false}
          onBeWriterPress={jest.fn()}
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
      expect.arrayContaining(['成为作家', '编辑精选，新章节上新后可以从这里继续追更。', '继续阅读 >']),
    );
  });

  it('renders readable recommendation empty copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <WaterfallGrid
          styles={new Proxy({}, { get: () => ({}) })}
          books={[]}
          loading={false}
          hasMore={false}
          onBookPress={jest.fn()}
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

    expect(texts).toContain('暂无推荐书籍');
  });
});
