import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/BecomeWriterPage/styles/BecomeWriterPageStyles', () => ({
  createBecomeWriterPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('react-native-linear-gradient', () => 'LinearGradient');

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
  }),
}));

import { AuthorExclusiveSection } from '../../src/page/ScrollBox/BecomeWriterPage/components/AuthorExclusiveSection';
import { BottomButton } from '../../src/page/ScrollBox/BecomeWriterPage/components/BottomButton';
import { CopyrightSection } from '../../src/page/ScrollBox/BecomeWriterPage/components/CopyrightSection';
import { CreativeActivitySection } from '../../src/page/ScrollBox/BecomeWriterPage/components/CreativeActivitySection';
import { DataStatsSection } from '../../src/page/ScrollBox/BecomeWriterPage/components/DataStatsSection';
import { TopBar } from '../../src/page/ScrollBox/BecomeWriterPage/components/TopBar';
import { UserSection } from '../../src/page/ScrollBox/BecomeWriterPage/components/UserSection';
import { WelcomeModal } from '../../src/page/ScrollBox/BecomeWriterPage/components/WelcomeModal';
import { WriterClassroomSection } from '../../src/page/ScrollBox/BecomeWriterPage/components/WriterClassroomSection';

describe('Become writer components novelDesign', () => {
  it('renders readable top-bar, user greeting and bottom CTA', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            onBackPress={jest.fn()}
            showAI
            onAIPress={jest.fn()}
          />
          <UserSection
            styles={new Proxy({}, { get: () => ({}) })}
            userInfo={{ avatar: '', name: '测试作者' } as any}
            announcement={{ tag: '公告', text: '最新活动已上线' } as any}
          />
          <BottomButton
            styles={new Proxy({}, { get: () => ({}) })}
            onPress={jest.fn()}
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
        '成为作家',
        'AI',
        'Hi，测试作者',
        '最新活动已上线',
        '更多 >',
        '成为番茄作家',
      ]),
    );
  });

  it('renders readable exclusive, activity, data and classroom copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <AuthorExclusiveSection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="benefits"
            benefits={[{ id: 'b1', icon: '福', title: '签约扶持', description: '福利说明' }] as any}
            roadTimeline={[{ id: 'r1', icon: '路', title: '成长计划', subTitle: '步骤说明' }] as any}
            platforms={[{ id: 'p1', name: '番茄', logo: '' }] as any}
            onTabChange={jest.fn()}
          />
          <CreativeActivitySection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="novel"
            activities={{
              novel: [{ id: 'a1', title: '小说活动', time: '进行中', coverUrl: '' }],
              short: [],
            } as any}
            onTabChange={jest.fn()}
            onMorePress={jest.fn()}
          />
          <DataStatsSection
            styles={new Proxy({}, { get: () => ({}) })}
            selectedTab="novel"
            dataStats={{
              novel: { wordCount: 12, readers: 34, urgers: 5, dailyIncome: 6 },
              short: { wordCount: 0, readers: 0, urgers: 0, dailyIncome: 0 },
            } as any}
            isExpanded={false}
            onTabChange={jest.fn()}
            onToggleExpanded={jest.fn()}
            isAuthor={false}
          />
          <WriterClassroomSection
            styles={new Proxy({}, { get: () => ({}) })}
            courses={[{ id: 'c1', title: '写作课程', description: '', coverUrl: '' }] as any}
            onMorePress={jest.fn()}
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
        '作者专属',
        '超多福利',
        '签约扶持',
        '查看更多 >',
        '创作活动',
        '小说',
        '短故事',
        '去查看',
        '作品数据',
        '数据说明',
        '成为番茄作家，开始创作',
        '作者课堂',
        '写作课程',
      ]),
    );
  });

  it('renders refreshed copyright and onboarding modal copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <CopyrightSection
            styles={new Proxy({}, { get: () => ({}) })}
            copyrightWorks={[{ id: 'cw1', title: '作品改编案例', coverUrl: '' }] as any}
          />
          <WelcomeModal
            styles={new Proxy({}, { get: () => ({}) })}
            visible
            isAgreementChecked
            onClose={jest.fn()}
            onRegister={jest.fn()}
            onAgreementChange={jest.fn()}
            onAgreementPress={jest.fn()}
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
        '版权衍生',
        '支持作品延展到动漫、短剧等更多内容形态',
        '成为番茄作家',
        '入驻番茄，开启你的创作成长计划',
        '从连载起步到作品运营，创作路上持续给你支持',
        '申请入驻',
        '我已阅读并同意',
        '《个人信息保护声明》',
      ]),
    );
  });
});
