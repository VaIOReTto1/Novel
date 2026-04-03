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
    novelText: '#201A17',
  }),
}));

jest.mock('react-native-vector-icons/MaterialIcons', () => 'Icon');

import { Header } from '../../src/page/Writer/AIWriteAssistant/components/Header';

describe('AIWriteAssistant Header novelDesign', () => {
  it('renders readable title and quota copy', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <Header
          onBack={jest.fn()}
          onMenu={jest.fn()}
          quotaText="今日剩余：5次"
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
      expect.arrayContaining(['写作助手', '今日剩余：5次']),
    );
  });
});
