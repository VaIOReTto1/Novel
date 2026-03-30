import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/Writer/WritePage/styles/WritePageStyles', () => ({
  createWritePageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelTextGray: '#999',
    novelMain: '#333',
    novelBackground: '#fff',
  }),
}));

jest.mock('../../src/page/Writer/WritePage/hooks/useWriteActions', () => ({
  useWriteActions: () => ({
    title: '标题',
    content: '正文',
    setTitle: jest.fn(),
    setContent: jest.fn(),
    publish: jest.fn(),
    undo: jest.fn(),
    redo: jest.fn(),
    goAI: jest.fn(),
    goBack: jest.fn(),
  }),
}));

jest.mock('../../src/page/Writer/WritePage/store/writeStore', () => ({
  useWriteStore: (selector?: (state: any) => any) => {
    const state = {
      isToolbarVisible: false,
      selectedText: '正文',
      updateSelection: jest.fn(),
      releaseSelectionHold: jest.fn(),
      focusRequestNonce: 0,
      suppressKeyboard: false,
      polishSelected: jest.fn(),
      showParamModal: jest.fn(),
      errorModal: { visible: false },
      hideError: jest.fn(),
      retryLastOperation: jest.fn(),
      modal: { visible: false },
      hideParamModal: jest.fn(),
      expandSelected: jest.fn(),
      condenseSelected: jest.fn(),
      continueSelected: jest.fn(),
      overlayLoading: false,
    };
    return selector ? selector(state) : state;
  },
}));

jest.mock('../../src/page/Writer/WritePage/components/TopBar', () => ({
  TopBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'TopBar');
  },
}));

jest.mock('../../src/page/Writer/WritePage/components/WelcomePanel', () => ({
  WelcomePanel: () => null,
}));

jest.mock('../../src/page/Writer/WritePage/components/VolumeBar', () => ({
  VolumeBar: () => null,
}));

jest.mock('../../src/page/Writer/WritePage/components/SelectionToolbar', () => ({
  SelectionToolbar: () => null,
}));

import WritePage from '../../src/page/Writer/WritePage/WritePage';

describe('WritePage smoke', () => {
  it('renders top bar without crashing', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    await ReactTestRenderer.act(async () => {
      renderer = ReactTestRenderer.create(<WritePage />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(expect.arrayContaining(['TopBar']));
  });
});
