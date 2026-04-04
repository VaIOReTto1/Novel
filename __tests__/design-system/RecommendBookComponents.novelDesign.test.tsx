import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Image, Text } from 'react-native';

import { CreativeTaskSection } from '../../src/page/ScrollBox/RecommendBookPage/components/CreativeTaskSection';
import { TopBar } from '../../src/page/ScrollBox/RecommendBookPage/components/TopBar';
import { WorkbenchOverviewSection } from '../../src/page/ScrollBox/RecommendBookPage/components/WorkbenchOverviewSection';
import { useRecommendBookStore } from '../../src/page/ScrollBox/RecommendBookPage/store/recommendBookStore';

const styleProxy = new Proxy(
  {},
  {
    get: () => ({}),
  },
);

const readTexts = (renderer: ReactTestRenderer.ReactTestRenderer) =>
  renderer.root
    .findAllByType(Text)
    .flatMap((node) => {
      const { children } = node.props;
      return Array.isArray(children) ? children : [children];
    })
    .filter((value): value is string => typeof value === 'string');

describe('RecommendBookPage growth dashboard components', () => {
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

  it('renders a growth-first overview without the old utility label', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar onBackPress={jest.fn()} styles={styleProxy} />
          <WorkbenchOverviewSection
            dataStats={{ fans: 128, likes: 342, replies: 56, withdrawable: 680 }}
            focusCount={2}
            focusTask={{
              id: 'focus',
              title: '热门好书冲榜计划',
              description: '连续推荐热门图书，提升曝光与转化。',
              coverUrl: '',
              maxEarnings: 520,
              type: 'recommend',
              tags: ['热门冲榜'],
              growthGoal: '把评论回复率拉回到本周高点',
              coverLabel: '冲榜',
              coverTone: 'sunrise',
            }}
            onFocusPress={jest.fn()}
            onServicePress={jest.fn()}
            services={[
              { id: 'trend', icon: 'trend', title: '趋势复盘', description: '查看本周涨粉变化' },
              { id: 'content', icon: 'content', title: '内容诊断', description: '复盘高表现内容' },
              { id: 'engage', icon: 'engage', title: '互动维护', description: '跟进高意向读者' },
              { id: 'settle', icon: 'settle', title: '收益节奏', description: '查看提现与收益' },
            ]}
            styles={styleProxy}
            userInfo={{ avatar: '', name: '测试作者' } as any}
          />
        </>,
      );
    });

    const texts = readTexts(renderer);

    expect(texts).toEqual(
      expect.arrayContaining([
        '<',
        '推书中心',
        '增长总览',
        'Hi，测试作者',
        '今日重点 2 项',
        '可提现',
        '¥680',
        '涨粉',
        '获赞',
        '回复',
        '本日增长摘要',
        '增长优先',
        '查看重点任务',
        '运营工具',
        '趋势复盘',
        '内容诊断',
        '互动维护',
        '收益节奏',
      ]),
    );
    expect(texts).not.toContain('总览');
  });

  it('renders the growth task queue with local media blocks instead of remote images', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <CreativeTaskSection
          onTabChange={jest.fn()}
          onTaskPress={jest.fn()}
          onViewAllPress={jest.fn()}
          selectedTab="recommend"
          styles={styleProxy}
          tasks={{
            recommend: [
              {
                id: 'focus',
                title: '热门好书冲榜计划',
                description: '连续推荐热门图书，提升曝光与转化。',
                coverUrl: '',
                maxEarnings: 520,
                type: 'recommend',
                tags: ['热门冲榜', '连续任务'],
                growthGoal: '把评论回复率拉回到本周高点',
                coverLabel: '冲榜',
                coverTone: 'sunrise',
              },
              {
                id: 'followup',
                title: '新书首推合作',
                description: '提前锁定新书首推窗口，提升内容增长惯性。',
                coverUrl: '',
                maxEarnings: 360,
                type: 'recommend',
                tags: ['新书合作', '首推机会'],
                growthGoal: '抢到首发曝光并带动新增收藏',
                coverLabel: '首推',
                coverTone: 'sand',
              },
            ],
            book: [],
          }}
        />,
      );
    });

    const texts = readTexts(renderer);

    expect(texts).toEqual(
      expect.arrayContaining([
        '增长任务队列',
        '全部任务 >',
        '推荐',
        '推书',
        '增长目标',
        '预计收益',
        '热门好书冲榜计划',
        '新书首推合作',
        '冲榜',
        '首推',
        '立即跟进',
        '热门冲榜',
        '连续任务',
      ]),
    );
    expect(renderer.root.findAllByType(Image)).toHaveLength(0);
  });

  it('loads growth-oriented dashboard mock data into the store', async () => {
    await ReactTestRenderer.act(async () => {
      await useRecommendBookStore.getState().loadInitialData();
    });

    const state = useRecommendBookStore.getState();

    expect(state.userInfo?.name).toBe('测试作者');
    expect(state.dataStats).toEqual({
      fans: 128,
      likes: 342,
      replies: 56,
      withdrawable: 680,
    });
    expect(state.services.map((item) => item.title)).toEqual(
      expect.arrayContaining(['趋势复盘', '内容诊断', '互动维护', '收益节奏']),
    );
    expect(state.tasks.recommend[0]).toMatchObject({
      title: '热门好书冲榜计划',
      description: '连续推荐热门图书，提升曝光与转化。',
      tags: ['热门冲榜', '连续任务'],
    });
  });
});
