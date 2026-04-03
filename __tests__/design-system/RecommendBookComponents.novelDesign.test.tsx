import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

import { useRecommendBookStore } from '../../src/page/ScrollBox/RecommendBookPage/store/recommendBookStore';

jest.mock('../../src/page/ScrollBox/RecommendBookPage/styles/RecommendBookPageStyles', () => ({
  createRecommendBookPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { CreativeServiceSection } from '../../src/page/ScrollBox/RecommendBookPage/components/CreativeServiceSection';
import { CreativeTaskSection } from '../../src/page/ScrollBox/RecommendBookPage/components/CreativeTaskSection';
import { DataStatsSection } from '../../src/page/ScrollBox/RecommendBookPage/components/DataStatsSection';
import { TopBar } from '../../src/page/ScrollBox/RecommendBookPage/components/TopBar';
import { UserSection } from '../../src/page/ScrollBox/RecommendBookPage/components/UserSection';

describe('Recommend book components novelDesign', () => {
  afterEach(() => {
    useRecommendBookStore.setState({
      loading: false,
      error: null,
      userInfo: null,
      selectedDataTab: 'recommend',
      dataStats: { fans: 0, likes: 0, replies: 0, withdrawable: 0 },
      services: [],
      selectedTaskTab: 'recommend',
      tasks: {
        recommend: [],
        book: [],
      },
    });
  });

  it('renders readable top bar, profile, stats and service copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            onBackPress={jest.fn()}
          />
          <UserSection
            styles={new Proxy({}, { get: () => ({}) })}
            userInfo={{ avatar: '', name: '测试作者' } as any}
          />
          <DataStatsSection
            styles={new Proxy({}, { get: () => ({}) })}
            dataStats={{ fans: 12, likes: 34, replies: 5, withdrawable: 6 } as any}
            onWithdrawPress={jest.fn()}
          />
          <CreativeServiceSection
            styles={new Proxy({}, { get: () => ({}) })}
            services={[
              { id: 's1', icon: '分', title: '收益分析' },
              { id: 's2', icon: '推', title: '推广统计' },
            ] as any}
            onServicePress={jest.fn()}
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
        '推书中心',
        'Hi，测试作者',
        '近7天涨粉',
        '近7天获赞',
        '近7天回复',
        '可提现收益(元)',
        '昨日 0',
        '去提现',
        '创作服务',
        '收益分析',
        '推广统计',
        '>',
      ]),
    );
  });

  it('renders readable task tabs, earnings copy and call-to-actions', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CreativeTaskSection
          styles={new Proxy({}, { get: () => ({}) })}
          selectedTab="recommend"
          tasks={{
            recommend: [
              {
                id: 't1',
                title: '种草好书赚现金',
                description: '发布优质推荐内容，完成推书任务',
                coverUrl: '',
                maxEarnings: 1288.5,
                tags: ['现金奖励', '推书任务'],
              },
            ],
            book: [],
          } as any}
          onTabChange={jest.fn()}
          onTaskPress={jest.fn()}
          onViewAllPress={jest.fn()}
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
      expect.arrayContaining([
        '创作任务',
        '更多 >',
        '推荐',
        '推书',
        '种草好书赚现金',
        '发布优质推荐内容，完成推书任务',
        '当前最高收益',
        '¥1288.50',
        '现金奖励',
        '推书任务',
        '查看任务',
        '查看更多任务',
        '>',
      ]),
    );
  });

  it('loads readable mock data into the recommend-book store', async () => {
    await ReactTestRenderer.act(async () => {
      await useRecommendBookStore.getState().loadInitialData();
    });

    const state = useRecommendBookStore.getState();

    expect(state.userInfo?.name).toBe('测试作者');
    expect(state.services.map((item) => item.title)).toEqual(
      expect.arrayContaining(['收益分析', '推广统计', '任务中心']),
    );
    expect(state.tasks.recommend[0]).toMatchObject({
      title: '种草好书赚现金',
      description: '发布优质推荐内容，完成推书任务',
      tags: ['现金奖励', '推书任务'],
    });
    expect(state.tasks.book[0]).toMatchObject({
      title: '精品书单制作',
      description: '制作高质量书单，吸引更多读者',
      tags: ['书单制作', '读者增长'],
    });
  });
});
