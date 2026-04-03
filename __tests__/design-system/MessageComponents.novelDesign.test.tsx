import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/MessagePage/styles/MessagePageStyles', () => ({
  createMessagePageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { EmptyState } from '../../src/page/ScrollBox/MessagePage/components/EmptyState';
import { LoadMoreIndicator } from '../../src/page/ScrollBox/MessagePage/components/LoadMoreIndicator';
import { MainMessagesSection } from '../../src/page/ScrollBox/MessagePage/components/MainMessagesSection';
import { TabsArea } from '../../src/page/ScrollBox/MessagePage/components/TabsArea';
import { TopBar } from '../../src/page/ScrollBox/MessagePage/components/TopBar';

describe('Message components novelDesign', () => {
  it('renders readable top-bar and tab copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            onBackPress={jest.fn()}
            onMarkAllReadPress={jest.fn()}
          />
          <TabsArea
            styles={new Proxy({}, { get: () => ({}) })}
            tabs={[
              { id: 'all', name: '全部', type: 'all' },
              { id: 'system', name: '系统', type: 'system' },
            ] as any}
            selectedTab="all"
            onTabPress={jest.fn()}
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
      expect.arrayContaining(['我的消息', '全部', '系统', '全部已读']),
    );
  });

  it('renders readable empty-state and load-more copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <EmptyState
            styles={new Proxy({}, { get: () => ({}) })}
            message="暂无消息"
          />
          <LoadMoreIndicator
            styles={new Proxy({}, { get: () => ({}) })}
            loading
            hasMore
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
      expect.arrayContaining(['暂无消息', '加载中...']),
    );
  });

  it('renders readable seeded main message content', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <MainMessagesSection
          styles={new Proxy({}, { get: () => ({}) })}
          onMessagePress={jest.fn()}
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
      expect.arrayContaining(['系统通知', '粉丝', '暂无粉丝消息']),
    );
  });
});
