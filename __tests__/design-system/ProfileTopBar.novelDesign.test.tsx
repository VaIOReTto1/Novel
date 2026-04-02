import React from 'react';
import ReactTestRenderer from 'react-test-renderer';

const mockSetTheme = jest.fn();
const mockUseThemeStore = jest.fn(() => ({
  currentTheme: 'light',
  setTheme: mockSetTheme,
}));
const mockIconComponent = jest.fn(() => null);

jest.mock('../../src/utils/theme/themeStore', () => ({
  useThemeStore: () => mockUseThemeStore(),
}));

jest.mock('../../src/component/IconComponent', () => ({
  __esModule: true,
  default: (props: any) => {
    mockIconComponent(props);
    return null;
  },
}));

import { TopBar } from '../../src/page/ProfilePage/components/TopBar';

describe('ProfilePage TopBar Stage 7 bridge', () => {
  beforeEach(() => {
    mockSetTheme.mockClear();
    mockIconComponent.mockClear();
  });

  it('uses novelDesign icon bridge and toggles moon/sun icon by theme', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <TopBar styles={{ topBar: {}, topBarButton: {} }} />,
      );
    });

    const names = mockIconComponent.mock.calls.map(([props]) => props.name);
    expect(names).toEqual(['qrscan', 'moon_mode', 'settings']);

    const buttons = renderer.root.findAllByType(require('react-native').TouchableOpacity);
    ReactTestRenderer.act(() => {
      buttons[1].props.onPress();
    });
    expect(mockSetTheme).toHaveBeenCalledWith('dark');
  });
});
