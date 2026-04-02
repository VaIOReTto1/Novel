import React from 'react';
import ReactTestRenderer from 'react-test-renderer';

const mockMaterialIcons = jest.fn(() => null);

jest.mock('react-native-vector-icons/MaterialIcons', () => (props: any) => {
  mockMaterialIcons(props);
  return null;
});

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Community/styles/CommunityPageStyles', () => ({
  createCommunityPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

import { TopBar } from '../../src/page/BookshelfPage/pages/Community/components/TopBar';
import { FloatingButton } from '../../src/page/BookshelfPage/pages/Community/components/FloatingButton';
import { PostItem } from '../../src/page/BookshelfPage/pages/Community/components/PostItem';

describe('Community components novelDesign icons', () => {
  beforeEach(() => {
    mockMaterialIcons.mockClear();
  });

  it('uses vector icons for top-bar and floating actions instead of emoji glyphs', () => {
    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(
        <>
          <TopBar />
          <FloatingButton onPress={jest.fn()} />
        </>,
      );
    });

    const names = mockMaterialIcons.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(
      expect.arrayContaining(['search', 'notifications-none', 'edit', 'edit']),
    );
  });

  it('uses vector icons for post actions and toggles liked state icon', () => {
    const basePost = {
      id: 'post-1',
      title: '标题',
      content: '正文',
      novelName: '作品',
      images: [],
      shareCount: 3,
      commentCount: 5,
      likeCount: 8,
      isLiked: false,
      author: {
        id: 'author-1',
        name: '作者',
        avatar: '',
      },
    };

    ReactTestRenderer.act(() => {
      ReactTestRenderer.create(<PostItem post={basePost as any} />);
      ReactTestRenderer.create(<PostItem post={{ ...basePost, isLiked: true } as any} />);
    });

    const names = mockMaterialIcons.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(
      expect.arrayContaining([
        'more-horiz',
        'share',
        'chat-bubble-outline',
        'favorite-border',
        'favorite',
      ]),
    );
  });
});
