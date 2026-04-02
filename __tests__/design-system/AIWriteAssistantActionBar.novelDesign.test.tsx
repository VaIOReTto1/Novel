import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelMain: '#FF995D',
    novelMainLight: '#F86827',
    novelBookBackground: '#E8E3CF',
    novelBackground: '#FFFFFF',
    novelDivider: '#F7F7F8',
    novelSecondaryBackground: '#F7F7F8',
    novelText: '#000000',
    novelTextGray: '#7F7F7F',
    novelLightGray: '#DDDDDD',
    novelChipBackground: '#EBEDF0',
    novelError: '#FF995D',
    outline: '#E0E0E0',
    novelThemeMode: 'light',
  }),
}));

jest.mock('react-native-vector-icons/Feather', () => (props: any) => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');
  return ReactMock.createElement(MockText, null, props.name);
});

import { ActionBar } from '../../src/page/Writer/AIWriteAssistant/components/ActionBar';

describe('AIWriteAssistant ActionBar novelDesign', () => {
  it('renders labeled editorial action pills instead of icon-only controls', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <ActionBar
          onLike={jest.fn()}
          onDislike={jest.fn()}
          onCopy={jest.fn()}
          onRetry={jest.fn()}
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
        '\u6709\u7528',
        '\u4e0d\u559c\u6b22',
        '\u590d\u5236',
        '\u91cd\u8bd5',
      ]),
    );
  });
});
