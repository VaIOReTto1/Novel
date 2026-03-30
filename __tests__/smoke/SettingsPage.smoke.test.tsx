import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { BackHandler, NativeModules, Text } from 'react-native';

jest.mock('../../src/page/SettingsPage/settingspage/components', () => {
  const MockReact = require('react');
  const { Text: MockText } = require('react-native');

  return {
    SettingRow: ({ item }: { item: { title: string } }) =>
      MockReact.createElement(MockText, null, item.title),
  };
});

jest.mock('../../src/page/SettingsPage/settingspage/styles/SettingsPageStyles', () => ({
  createSettingsPageStyles: () =>
    new Proxy(
      {},
      {
        get: () => ({}),
      },
    ),
}));

jest.mock('../../src/utils/theme/colors', () => ({
  useNovelColors: () => ({}),
}));

jest.mock('../../src/page/ProfilePage/store/userStore', () => ({
  useUserStore: () => ({
    isLoggedIn: true,
    logout: jest.fn(),
  }),
}));

jest.mock('../../src/page/SettingsPage/settingspage/store/settingsStore', () => {
  const initializeSettings = jest.fn().mockResolvedValue(undefined);

  const state = {
    isInitialized: true,
    cacheSize: '0MB',
    pushNotificationEnabled: true,
    benefitNotificationEnabled: true,
    followSystemTheme: true,
    autoSwitchNightMode: false,
    useMobileDataWhenWiFiPoor: true,
    enableFloatingWindow: false,
    youthModeEnabled: false,
    clearCache: jest.fn(),
    calculateCacheSize: jest.fn().mockResolvedValue(undefined),
    setPushNotification: jest.fn(),
    setBenefitNotification: jest.fn(),
    setFollowSystemTheme: jest.fn(),
    toggleColorScheme: jest.fn(),
    setUseMobileDataWhenWiFiPoor: jest.fn(),
    setEnableFloatingWindow: jest.fn(),
    setYouthMode: jest.fn(),
    navigateToAbout: jest.fn(),
    navigateToCustomerService: jest.fn(),
    navigateToPrivacyPolicy: jest.fn(),
    navigateToFontSettings: jest.fn(),
    getCurrentDisplayTheme: jest.fn(() => 'light'),
    initializeSettings,
    logout: jest.fn().mockResolvedValue(undefined),
  };

  const useSettingsStore = Object.assign(jest.fn(() => state), {
    getState: () => ({
      isInitialized: true,
      initializeSettings,
    }),
  });

  return {
    useSettingsStore,
  };
});

BackHandler.addEventListener = jest.fn(() => ({
  remove: jest.fn(),
})) as any;
(NativeModules as any).NavigationBridge = {
  navigateBack: jest.fn(),
  navigateToTimedSwitch: jest.fn(),
};

import SettingsPage from '../../src/page/SettingsPage/settingspage/SettingsPage';

describe('SettingsPage smoke', () => {
  it('renders the core settings sections without crashing', async () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    await ReactTestRenderer.act(async () => {
      renderer = ReactTestRenderer.create(<SettingsPage />);
    });

    const texts = renderer.root
      .findAllByType(Text)
      .flatMap((node) => {
        const { children } = node.props;
        return Array.isArray(children) ? children : [children];
      })
      .filter((value): value is string => typeof value === 'string');

    expect(texts).toEqual(
      expect.arrayContaining(['设置', '主题设置', '隐私设置']),
    );
  });
});
