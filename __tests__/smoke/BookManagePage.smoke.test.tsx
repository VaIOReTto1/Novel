import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/Writer/BookManage/styles/bookManageStyles', () => ({
  createBookManageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  NavigationBridge: {
    navigateBack: jest.fn(),
    navigateToWritePage: jest.fn(),
    navigateToBookManage: jest.fn(),
  },
}));

jest.mock('../../src/page/Writer/BookManage/domain/bookManagePageModel', () => ({
  bootstrapBookManagePage: jest.fn(),
  createBookManagePageHandlers: () => ({
    handleBack: jest.fn(),
    handleContinueDraft: jest.fn(),
  }),
}));

const mockBookManageState = {
  book: {
    id: 'book-1',
    title: '书名',
    authorName: '作者',
    statusText: '待审核',
  },
  draft: { exists: true },
  chapters: [] as Array<{ id: string; title: string; words: number; updatedAt: number }>,
  load: jest.fn(),
};

jest.mock('../../src/page/Writer/BookManage/store/bookManageStore', () => ({
  useBookManageStore: () => mockBookManageState,
}));

jest.mock('../../src/page/Writer/BookManage/components/Header', () => ({
  Header: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookManageHeader');
  },
}));

jest.mock('../../src/page/Writer/BookManage/components/Banner', () => ({
  Banner: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookManageBanner');
  },
}));

jest.mock('../../src/page/Writer/BookManage/components/DraftBar', () => ({
  DraftBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'DraftBar');
  },
}));

jest.mock('../../src/page/Writer/BookManage/components/EmptyChapter', () => ({
  EmptyChapter: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'EmptyChapter');
  },
}));

jest.mock('../../src/page/Writer/BookManage/components/ChapterSection', () => ({
  ChapterSection: ({ chapters }: { chapters: Array<{ id: string }> }) => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(
      MockText,
      null,
      chapters.length === 0 ? 'ChapterSection:empty' : 'ChapterSection:list',
    );
  },
}));

jest.mock('../../src/page/Writer/BookManage/components/Footer', () => ({
  Footer: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'BookManageFooter');
  },
}));

import BookManagePage from '../../src/page/Writer/BookManage/BookManagePage';

describe('BookManagePage smoke', () => {
  it('renders draft and empty-chapter shell without crashing', () => {
    mockBookManageState.chapters = [];
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<BookManagePage />);
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
        'BookManageHeader',
        'BookManageBanner',
        'DraftBar',
        'ChapterSection:empty',
        'EmptyChapter',
        'BookManageFooter',
      ]),
    );
  });

  it('renders chapter-list state without crashing', () => {
    mockBookManageState.chapters = [
      { id: 'chapter-1', title: '第1章', words: 1200, updatedAt: Date.now() },
    ];
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(<BookManagePage />);
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
        'BookManageHeader',
        'BookManageBanner',
        'DraftBar',
        'ChapterSection:list',
        'BookManageFooter',
      ]),
    );
  });
});
