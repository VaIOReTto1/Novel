import { bootstrapSettingsPage, createSettingsSections } from '../../src/page/SettingsPage/settingspage/domain/settingsPageModel';

describe('settingsPageModel domain helpers', () => {
  test('boots cache size and initialization when settings state is not initialized', async () => {
    const calculateCacheSize = jest.fn().mockResolvedValue(undefined);
    const initializeSettings = jest.fn().mockResolvedValue(undefined);

    await bootstrapSettingsPage({
      isInitialized: false,
      calculateCacheSize,
      initializeSettings,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(calculateCacheSize).toHaveBeenCalledTimes(1);
    expect(initializeSettings).toHaveBeenCalledTimes(1);
  });

  test('skips initialization when settings state is already initialized', async () => {
    const calculateCacheSize = jest.fn().mockResolvedValue(undefined);
    const initializeSettings = jest.fn().mockResolvedValue(undefined);

    await bootstrapSettingsPage({
      isInitialized: true,
      calculateCacheSize,
      initializeSettings,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(calculateCacheSize).toHaveBeenCalledTimes(1);
    expect(initializeSettings).not.toHaveBeenCalled();
  });

  test('builds settings sections with logout section only for logged-in users', () => {
    const sections = createSettingsSections({
      cacheSize: '1MB',
      pushNotificationEnabled: true,
      benefitNotificationEnabled: false,
      followSystemTheme: true,
      autoSwitchNightMode: false,
      useMobileDataWhenWiFiPoor: true,
      enableFloatingWindow: false,
      youthModeEnabled: false,
      displayTheme: 'light',
      isLoggedIn: true,
      handlers: {
        clearCache: jest.fn(),
        setPushNotification: jest.fn(),
        setBenefitNotification: jest.fn(),
        setFollowSystemTheme: jest.fn(),
        toggleColorScheme: jest.fn(),
        navigateToTimedSwitch: jest.fn(),
        setEnableFloatingWindow: jest.fn(),
        setUseMobileDataWhenWiFiPoor: jest.fn(),
        navigateToPrivacyPolicy: jest.fn(),
        setYouthMode: jest.fn(),
        navigateToCustomerService: jest.fn(),
        navigateToAbout: jest.fn(),
        navigateToFontSettings: jest.fn(),
        showLogoutModal: jest.fn(),
      },
    });

    expect(sections.some((section) => section.id === 'account')).toBe(true);
    expect(
      sections
        .flatMap((section) => section.items)
        .find((item) => item.id === 'nightModeSwitch')?.disabled,
    ).toBe(true);
  });
});
