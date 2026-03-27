/**
 * @format
 */

import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text } from 'react-native';

const mockCleanupApp = jest.fn();
const mockInitializeApp = jest.fn().mockResolvedValue(undefined);
const mockUserUnsubscribe = jest.fn();
const mockHomeUnsubscribe = jest.fn();
const mockUserSubscribe = jest.fn(() => mockUserUnsubscribe);
const mockHomeSubscribe = jest.fn(() => mockHomeUnsubscribe);
const mockInitializeFromProps = jest.fn();

jest.mock('../src/page/ProfilePage/ProfilePage', () => {
  const ReactMock = require('react');
  const { Text: MockText } = require('react-native');

  return function MockProfilePage() {
    return ReactMock.createElement(MockText, null, 'ProfilePage');
  };
});

jest.mock('../src/utils/appInit', () => ({
  initializeApp: () => mockInitializeApp(),
  cleanupApp: () => mockCleanupApp(),
}));

jest.mock('../src/page/ProfilePage/store/userStore', () => ({
  useUserStore: Object.assign(jest.fn(() => ({})), {
    subscribe: mockUserSubscribe,
  }),
}));

jest.mock('../src/page/ProfilePage/store/BookStore', () => ({
  useHomeStore: Object.assign(jest.fn(() => ({})), {
    subscribe: mockHomeSubscribe,
  }),
}));

jest.mock('../src/utils/theme/themeStore', () => ({
  useThemeStore: () => ({
    initializeFromProps: mockInitializeFromProps,
  }),
}));

jest.mock('../src/utils/theme/colors', () => ({
  useNovelColors: () => ({
    novelBackground: '#ffffff',
  }),
}));

test('renders app shell correctly', async () => {
  const App = require('../App').default as typeof import('../App').default;
  let renderer!: ReactTestRenderer.ReactTestRenderer;

  await ReactTestRenderer.act(async () => {
    renderer = ReactTestRenderer.create(<App />);
  });

  const texts = renderer.root.findAllByType(Text).map((node) => node.props.children);

  expect(texts).toContain('ProfilePage');
  expect(mockInitializeApp).toHaveBeenCalledTimes(1);
  expect(mockUserSubscribe).toHaveBeenCalledTimes(1);
  expect(mockHomeSubscribe).toHaveBeenCalledTimes(1);
});
