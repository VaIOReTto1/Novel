export interface SettingItem {
  id: string;
  title: string;
  type: 'switch' | 'arrow' | 'toggle' | 'action';
  value?: boolean | string;
  icon?: string;
  disabled?: boolean;
  onPress?: () => void;
  onValueChange?: (value: boolean | string) => void;
}

export interface SettingsSection {
  id: string;
  title?: string;
  items: SettingItem[];
}

export type ColorScheme = 'light' | 'dark' | 'auto';

export interface SettingsState {
  // 初始化状态
  isInitialized: boolean;

  // 缓存设置
  cacheSize: string;

  // 通知设置
  pushNotificationEnabled: boolean;
  benefitNotificationEnabled: boolean;

  // 主题设置
  followSystemTheme: boolean;
  colorScheme: ColorScheme;
  autoSwitchNightMode: boolean;
  nightModeStartTime: string;
  nightModeEndTime: string;

  // 字体设置
  fontSize: number;

  // 网络设置
  useMobileDataWhenWiFiPoor: boolean;

  // 播放设置
  enableFloatingWindow: boolean;

  // 青少年模式
  youthModeEnabled: boolean;

  // 其他
  privacySettingsVersion: string;
}

export interface SettingsActions {
  // 缓存操作
  clearCache: () => Promise<void>;
  calculateCacheSize: () => Promise<void>;

  // 通知设置
  setPushNotification: (enabled: boolean) => void;
  setBenefitNotification: (enabled: boolean) => void;

  // 主题设置
  getCurrentDisplayTheme: () => ColorScheme;
  setFollowSystemTheme: (follow: boolean) => void;
  loadFollowSystemTheme: () => Promise<void>;
  setColorScheme: (scheme: ColorScheme) => void;
  setAutoSwitchNightMode: (enabled: boolean) => void;
  setNightModeTime: (start: string, end: string) => void;
  loadNightModeTime: () => Promise<void>;
  loadAutoSwitchNightMode: () => Promise<void>;
  toggleColorScheme: () => void;

  // 字体设置
  setFontSize: (size: number) => void;

  // 网络设置
  setUseMobileDataWhenWiFiPoor: (enabled: boolean) => void;

  // 播放设置
  setEnableFloatingWindow: (enabled: boolean) => void;

  // 青少年模式
  setYouthMode: (enabled: boolean) => void;

  // 导航操作
  navigateToAbout: () => void;
  navigateToCustomerService: () => void;
  navigateToPrivacyPolicy: () => void;
  navigateToFontSettings: () => void;

  logout: () => void;
}

export type SettingsStore = SettingsState & SettingsActions;
