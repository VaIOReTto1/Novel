import { SettingsSection } from '../types/index';

type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapSettingsPageDeps = {
  isInitialized: boolean;
  calculateCacheSize: () => Promise<void>;
  initializeSettings: () => Promise<void>;
  logger?: Logger;
};

type SettingsPageHandlers = {
  clearCache: () => void | Promise<void>;
  setPushNotification: (enabled: boolean) => void;
  setBenefitNotification: (enabled: boolean) => void;
  setFollowSystemTheme: (enabled: boolean) => void;
  toggleColorScheme: () => void;
  navigateToTimedSwitch: () => void;
  setEnableFloatingWindow: (enabled: boolean) => void;
  setUseMobileDataWhenWiFiPoor: (enabled: boolean) => void;
  navigateToPrivacyPolicy: () => void;
  setYouthMode: (enabled: boolean) => void;
  navigateToCustomerService: () => void;
  navigateToAbout: () => void;
  navigateToFontSettings: () => void;
  showLogoutModal: () => void;
};

type CreateSettingsSectionsArgs = {
  cacheSize: string;
  pushNotificationEnabled: boolean;
  benefitNotificationEnabled: boolean;
  followSystemTheme: boolean;
  autoSwitchNightMode: boolean;
  useMobileDataWhenWiFiPoor: boolean;
  enableFloatingWindow: boolean;
  youthModeEnabled: boolean;
  displayTheme: string;
  isLoggedIn: boolean;
  handlers: SettingsPageHandlers;
};

const defaultLogger: Logger = console;

export const bootstrapSettingsPage = async ({
  isInitialized,
  calculateCacheSize,
  initializeSettings,
  logger = defaultLogger,
}: BootstrapSettingsPageDeps): Promise<void> => {
  await calculateCacheSize().catch((error) => {
    logger.error('[SettingsPage] Failed to calculate cache size', error);
  });

  if (isInitialized) {
    logger.log('[SettingsPage] Settings already initialized');
    return;
  }

  try {
    await initializeSettings();
  } catch (error) {
    logger.error('[SettingsPage] Failed to initialize settings', error);
  }
};

export const createSettingsSections = ({
  cacheSize,
  pushNotificationEnabled,
  benefitNotificationEnabled,
  followSystemTheme,
  autoSwitchNightMode,
  useMobileDataWhenWiFiPoor,
  enableFloatingWindow,
  youthModeEnabled,
  displayTheme,
  isLoggedIn,
  handlers,
}: CreateSettingsSectionsArgs): SettingsSection[] => {
  const sections: SettingsSection[] = [
    {
      id: 'cache',
      title: '存储管理',
      items: [
        {
          id: 'clearCache',
          title: '清理缓存',
          type: 'action',
          value: cacheSize,
          onPress: handlers.clearCache,
        },
      ],
    },
    {
      id: 'notifications',
      title: '通知设置',
      items: [
        {
          id: 'pushNotification',
          title: '推送通知',
          type: 'switch',
          value: pushNotificationEnabled,
          onValueChange: (value) => handlers.setPushNotification(value as boolean),
        },
        {
          id: 'benefitNotification',
          title: '福利领取提示',
          type: 'switch',
          value: benefitNotificationEnabled,
          onValueChange: (value) => handlers.setBenefitNotification(value as boolean),
        },
      ],
    },
    {
      id: 'reading',
      title: '阅读设置',
      items: [
        {
          id: 'fontSize',
          title: '字体大小',
          type: 'arrow',
          onPress: handlers.navigateToFontSettings,
        },
      ],
    },
    {
      id: 'theme',
      title: '主题设置',
      items: [
        {
          id: 'colorSchemeToggle',
          title: '主题模式',
          type: 'toggle',
          value: displayTheme,
          onPress: handlers.toggleColorScheme,
        },
        {
          id: 'followSystemTheme',
          title: '跟随系统主题',
          type: 'switch',
          value: followSystemTheme,
          onValueChange: (value) => handlers.setFollowSystemTheme(value as boolean),
        },
        {
          id: 'nightModeSwitch',
          title: '定时切换日夜间模式',
          type: 'arrow',
          value: autoSwitchNightMode ? '已开启' : '已关闭',
          onPress: handlers.navigateToTimedSwitch,
          disabled: followSystemTheme,
        },
      ],
    },
    {
      id: 'playback',
      title: '播放设置',
      items: [
        {
          id: 'floatingWindow',
          title: '退出应用后开启小窗播放',
          type: 'switch',
          value: enableFloatingWindow,
          onValueChange: (value) => handlers.setEnableFloatingWindow(value as boolean),
        },
      ],
    },
    {
      id: 'network',
      title: '网络设置',
      items: [
        {
          id: 'mobileData',
          title: 'WiFi 较差时使用移动网络优化体验',
          type: 'switch',
          value: useMobileDataWhenWiFiPoor,
          onValueChange: (value) => handlers.setUseMobileDataWhenWiFiPoor(value as boolean),
        },
      ],
    },
    {
      id: 'privacy',
      title: '隐私设置',
      items: [
        {
          id: 'privacyPolicy',
          title: '第三方信息共享清单',
          type: 'arrow',
          onPress: handlers.navigateToPrivacyPolicy,
        },
      ],
    },
    {
      id: 'youth',
      title: '青少年模式',
      items: [
        {
          id: 'youthMode',
          title: '青少年模式',
          type: 'switch',
          value: youthModeEnabled,
          onValueChange: (value) => handlers.setYouthMode(value as boolean),
        },
      ],
    },
    {
      id: 'support',
      title: '帮助与支持',
      items: [
        {
          id: 'customerService',
          title: '客服',
          type: 'arrow',
          onPress: handlers.navigateToCustomerService,
        },
        {
          id: 'about',
          title: '关于我们',
          type: 'arrow',
          onPress: handlers.navigateToAbout,
        },
      ],
    },
  ];

  if (isLoggedIn) {
    sections.push({
      id: 'account',
      title: '账户管理',
      items: [
        {
          id: 'logout',
          title: '退出登录',
          type: 'arrow',
          onPress: handlers.showLogoutModal,
        },
      ],
    });
  }

  return sections;
};
