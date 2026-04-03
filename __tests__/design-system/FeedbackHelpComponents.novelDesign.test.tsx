import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/ScrollBox/FeedbackHelpPage/styles/FeedbackHelpPageStyles', () => ({
  createFeedbackHelpPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('react-native-linear-gradient', () => 'LinearGradient');

import { ConsultSection } from '../../src/page/ScrollBox/FeedbackHelpPage/components/ConsultSection';
import { ContactSection } from '../../src/page/ScrollBox/FeedbackHelpPage/components/ContactSection';
import { FrequentQuestions } from '../../src/page/ScrollBox/FeedbackHelpPage/components/FrequentQuestions';
import { QuestionDetail } from '../../src/page/ScrollBox/FeedbackHelpPage/components/QuestionDetail';
import { QuestionList } from '../../src/page/ScrollBox/FeedbackHelpPage/components/QuestionList';
import { TopBar } from '../../src/page/ScrollBox/FeedbackHelpPage/components/TopBar';
import { UserSection } from '../../src/page/ScrollBox/FeedbackHelpPage/components/UserSection';

jest.mock('../../src/page/ScrollBox/FeedbackHelpPage/store/feedbackHelpStore', () => ({
  useFeedbackHelpStore: () => ({
    consultCategories: [
      { id: 'member', title: '会员相关', icon: '会员' },
    ],
    getCategoryQuestions: () => [],
  }),
}));

describe('Feedback help components novelDesign', () => {
  it('renders readable main-shell copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <TopBar
            styles={new Proxy({}, { get: () => ({}) })}
            title="帮助与反馈"
            onBack={jest.fn()}
            onSearch={jest.fn()}
            pageType="main"
          />
          <UserSection styles={new Proxy({}, { get: () => ({}) })} userName="测试用户" />
          <ConsultSection
            styles={new Proxy({}, { get: () => ({}) })}
            categories={[
              { id: 'member', title: '会员相关', items: ['开通', '续费'], bgGradient: ['#fff', '#eee'] },
            ] as any}
            onCategoryPress={jest.fn()}
          />
          <FrequentQuestions
            styles={new Proxy({}, { get: () => ({}) })}
            questions={[
              { id: 'q1', title: '如何开通会员？' },
            ] as any}
            onQuestionPress={jest.fn()}
          />
          <ContactSection
            styles={new Proxy({}, { get: () => ({}) })}
            onContactPress={jest.fn()}
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
        '帮助与反馈',
        'Hi，测试用户',
        '很高兴为您服务',
        '咨询场景',
        '更多',
        '会员相关',
        '大家都在问',
        '如何开通会员？',
        '意见反馈',
      ]),
    );

    const input = renderer.root.findByType(require('react-native').TextInput);
    expect(input.props.placeholder).toBe('搜索帮助内容');
  });

  it('renders readable question-list and detail copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <>
          <QuestionList
            styles={new Proxy({}, { get: () => ({}) })}
            questions={[
              { id: 'q1', title: '如何修改密码？', category: 'member' },
            ] as any}
            category="all"
            onQuestionPress={jest.fn()}
          />
          <QuestionDetail
            styles={new Proxy({}, { get: () => ({}) })}
            detail={{
              title: '如何修改密码？',
              tags: ['账号'],
              content: '进入设置后修改。',
              relatedQuestions: ['q2'],
            } as any}
            onResolve={jest.fn()}
            onRelatedQuestionPress={jest.fn()}
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
        '全部',
        '全部问题',
        '会员',
        '会员相关',
        '如何修改密码？',
        '相关问题',
        '查看相关问题 #q2',
        '已解决',
        '未解决',
      ]),
    );
  });
});
