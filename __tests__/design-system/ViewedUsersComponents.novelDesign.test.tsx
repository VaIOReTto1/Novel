import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/ViewedUsersPage/styles/ViewedUsersPageStyles', () => ({
  createViewedUsersPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { EmptyState } from '../../src/page/ScrollBox/ViewedUsersPage/components/EmptyState';
import { TabsSection } from '../../src/page/ScrollBox/ViewedUsersPage/components/TabsSection';
import { TopBar } from '../../src/page/ScrollBox/ViewedUsersPage/components/TopBar';
import { UsersList } from '../../src/page/ScrollBox/ViewedUsersPage/components/UsersList';

describe('Viewed users components novelDesign', () => {
  it('renders readable top-bar and tabs copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            userName="最近访客"
            onBackPress={jest.fn()}
          />
          <TabsSection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="viewed"
            onTabChange={jest.fn()}
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
      expect.arrayContaining(['最近访客', '看过的人', '推荐', '关注', '粉丝']),
    );
  });

  it('renders readable users list and empty-state copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <UsersList
            styles={new Proxy({}, { get: () => ({}) })}
            users={[
              {
                id: 'user-1',
                name: '测试用户',
                avatar: '',
                description: '简介',
                isFollowed: false,
                hasVBadge: false,
                tags: [],
              },
            ] as any}
            onUserPress={jest.fn()}
            onFollowPress={jest.fn()}
          />
          <EmptyState
            styles={new Proxy({}, { get: () => ({}) })}
            data={{
              icon: '空',
              title: '暂无访客',
              subtitle: '再等等，也许很快就有人来了',
              buttonText: '去广场看看',
            }}
            onButtonPress={jest.fn()}
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
        '测试用户',
        '+ 关注',
        '暂无访客',
        '再等等，也许很快就有人来了',
        '去广场看看',
      ]),
    );
  });
});
