import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
    novelBookBackground: '#E8E3CF',
  }),
}));

jest.mock('../../src/page/comment/CommentPage/styles/CommentPageStyles', () => ({
  createCommentPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  NavigationBridge: {
    navigateBack: jest.fn(),
    navigateToReviewDetail: jest.fn(),
  },
}));

jest.mock('../../src/page/comment/CommentPage/store/commentStore', () => ({
  useCommentStore: () => ({
    likeComment: jest.fn(),
    comments: [],
    loading: false,
    hasMore: false,
  }),
}));

jest.mock('../../src/utils/time/timeUtils', () => ({
  parseNewsDate: () => '刚刚',
}));

import { CategorySection } from '../../src/page/comment/CommentPage/components/CategorySection';
import { CommentList } from '../../src/page/comment/CommentPage/components/CommentList';
import { TopBar } from '../../src/page/comment/CommentPage/components/TopBar';

describe('Comment components novelDesign', () => {
  it('renders readable category labels', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<CategorySection selectedCategory="all" />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['全部 1247', '好评 1128', '差评 2', '剧情', '人物', '文笔']),
    );
  });

  it('renders readable empty-state copy for the comment list shell', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<CommentList />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['暂无评论', '快来发表第一条评论吧']),
    );
  });

  it('renders readable top-bar search placeholder', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<TopBar onBackPress={jest.fn()} />);
    });

    const input = renderer.root.findByProps({ value: '' });
    expect(input.props.placeholder).toBe('搜索书评关键词');
  });
});
