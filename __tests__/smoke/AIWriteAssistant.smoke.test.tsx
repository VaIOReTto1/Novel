import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/page/Writer/AIWriteAssistant/styles/aiStyles', () => ({
  createAIStyles: () =>
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
    novelTextGray: '#7F7F7F',
  }),
}));

jest.mock('../../src/utils/runtime/backNavigation', () => ({
  registerHardwareBackHandler: () => jest.fn(),
}));

jest.mock('../../src/utils/bridge/NavigationBridge', () => ({
  NavigationBridge: {
    navigateBack: jest.fn(),
  },
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/domain/aiWriteAssistantPageModel', () => ({
  bootstrapAIWriteAssistantPage: jest.fn(),
  createAIWriteAssistantHandlers: () => ({
    handleBack: jest.fn(),
    handleToggleIdea: jest.fn(),
    handleCloseIdea: jest.fn(),
    handleSelectIdea: jest.fn(),
    handleInputFocus: jest.fn(),
  }),
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/hooks/useAIShortcuts', () => ({
  useAIShortcuts: () => ({
    toggleDeepThinkMode: jest.fn(),
  }),
}));

const mockAiState = {
  messages: [
    { id: 'intro', role: 'assistant', text: '', done: true },
    { id: 'assistant-1', role: 'assistant', text: 'assistant', thinking: 'thinking', done: true },
    { id: 'user-1', role: 'user', text: 'user', done: true },
  ],
  input: 'outline',
  setInput: jest.fn(),
  send: jest.fn(),
  dailyRemaining: 4,
  sending: false,
  rehydrate: jest.fn(),
  setIdeaCategory: jest.fn(),
  ideaCategory: '悬疑',
  setIdeaPromptActive: jest.fn(),
  hydrated: true,
  deepThinkEnabled: true,
};

jest.mock('../../src/page/Writer/AIWriteAssistant/store/aiStore', () => ({
  useAIStore: (selector?: (state: any) => any) =>
    selector ? selector(mockAiState) : mockAiState,
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/components/Header', () => ({
  Header: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'AIHeader');
  },
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/components/IntroExample', () => ({
  IntroExample: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'IntroExample');
  },
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/components/Suggestions', () => ({
  Suggestions: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'Suggestions');
  },
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/components/InputBar', () => ({
  InputBar: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'InputBar');
  },
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/components/ChatRow', () => ({
  ChatRow: ({ item }: { item: { id: string } }) => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, `ChatRow:${item.id}`);
  },
}));

jest.mock('../../src/page/Writer/AIWriteAssistant/components/IdeaSelector', () => ({
  IdeaSelector: () => {
    const ReactMock = require('react');
    const { Text: MockText } = require('react-native');
    return ReactMock.createElement(MockText, null, 'IdeaSelector');
  },
}));

import AIWriteAssistant from '../../src/page/Writer/AIWriteAssistant/AIWriteAssistant';

describe('AIWriteAssistant smoke', () => {
  beforeEach(() => {
    jest.spyOn(global, 'setTimeout').mockImplementation(() => 0 as any);
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('renders header and bottom controls without crashing', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    await ReactTestRenderer.act(async () => {
      renderer = ReactTestRenderer.create(<AIWriteAssistant />);
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
        'AIHeader',
        'Suggestions',
        'InputBar',
      ]),
    );
  });
});
