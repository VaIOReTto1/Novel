import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

import { useViewedUsersStore } from '../../src/page/ScrollBox/ViewedUsersPage/store/viewedUsersStore';

jest.mock('react-native-linear-gradient', () => 'LinearGradient');

import { EmptyState } from '../../src/page/ScrollBox/ViewedUsersPage/components/EmptyState';
import { TabsSection } from '../../src/page/ScrollBox/ViewedUsersPage/components/TabsSection';
import { TopBar } from '../../src/page/ScrollBox/ViewedUsersPage/components/TopBar';
import { UsersList } from '../../src/page/ScrollBox/ViewedUsersPage/components/UsersList';

describe('Viewed users components novelDesign', () => {
  afterEach(() => {
    useViewedUsersStore.setState({
      loading: false,
      error: null,
      userInfo: null,
      selectedTab: 'viewed',
      viewedUsers: [],
      recommendUsers: [],
      followingUsers: [],
      fansUsers: [],
      emptyStates: {
        viewed: { icon: '空', title: '暂无看过的人', buttonText: '查看更多推荐的人' },
        recommend: { icon: '空', title: '暂无推荐' },
        following: { icon: '空', title: '暂无关注的人' },
        fans: { icon: '空', title: '暂无粉丝，去和更多书友互动吧' },
      },
    });
  });

  it('renders readable title, tabs, user cards and empty-state copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            userName="测试作者"
            onBackPress={jest.fn()}
          />
          <TabsSection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="recommend"
            onTabChange={jest.fn()}
          />
          <UsersList
            styles={new Proxy({}, { get: () => ({}) })}
            users={[
              {
                id: 'u1',
                name: '番茄小说团队',
                avatar: '',
                hasVBadge: true,
                tags: [{ text: '官方', type: 'official' }],
                description: '帮你发现更多值得关注的创作者',
                isFollowed: false,
              },
              {
                id: 'u2',
                name: '测试作者',
                avatar: '',
                hasVBadge: false,
                tags: [{ text: '作者', type: 'author' }],
                description: '分享写作心得与阅读观察',
                isFollowed: true,
              },
            ] as any}
            onUserPress={jest.fn()}
            onFollowPress={jest.fn()}
          />
          <EmptyState
            styles={new Proxy({}, { get: () => ({}) })}
            data={{
              icon: '空',
              title: '暂无看过的人',
              subtitle: '去推荐列表认识更多有趣的人',
              buttonText: '查看更多推荐的人',
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
        '<',
        '测试作者',
        '看过的人',
        '推荐',
        '关注',
        '粉丝',
        '番茄小说团队',
        '官方',
        '帮你发现更多值得关注的创作者',
        '+ 关注',
        '已关注',
        '暂无看过的人',
        '去推荐列表认识更多有趣的人',
        '查看更多推荐的人',
      ]),
    );
  });

  it('loads readable mock user and empty-state data into the store', async () => {
    await ReactTestRenderer.act(async () => {
      await useViewedUsersStore.getState().loadInitialData();
    });

    const state = useViewedUsersStore.getState();

    expect(state.userInfo?.name).toBe('测试作者');
    expect(state.emptyStates.viewed).toMatchObject({
      title: '暂无看过的人',
      buttonText: '查看更多推荐的人',
    });
    expect(state.recommendUsers[0]).toMatchObject({
      name: '番茄小说团队',
      tags: [{ text: '官方', type: 'official' }, { text: '热门', type: 'hot' }],
      description: '帮你发现更多值得关注的创作者',
      isFollowed: false,
    });
    expect(state.recommendUsers.some((user) => user.isFollowed)).toBe(true);
  });
});
