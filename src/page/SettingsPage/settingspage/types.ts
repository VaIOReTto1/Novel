 // 基础类型定义
export type ColorScheme = 'light' | 'dark' | 'auto';

// 状态管理接口
export interface SettingsStore {
  // 状态字段
  cacheSize: string;
  pushNotificationEnabled: boolean;
  benefitNotificationEnabled: boolean;
  followSystemTheme: boolean;
  colorScheme: ColorScheme;
  autoSwitchNightMode: boolean;
  nightModeStartTime: string;
  nightModeEndTime: string;
  fontSize: number;
  useMobileDataWhenWiFiPoor: boolean;
  enableFloatingWindow: boolean;
  youthModeEnabled: boolean;
  privacySettingsVersion: string;

  // 行为方法
  clearCache: () => Promise<void>;
  calculateCacheSize: () => Promise<void>;
  
  setPushNotification: (enabled: boolean) => void;
  setBenefitNotification: (enabled: boolean) => void;
  
  getCurrentDisplayTheme: () => 'light' | 'dark';
  setFollowSystemTheme: (follow: boolean) => Promise<void>;
  setColorScheme: (scheme: ColorScheme) => Promise<void>;
  toggleColorScheme: () => Promise<void>;
  setAutoSwitchNightMode: (enabled: boolean) => void;
  setNightModeTime: (start: string, end: string) => void;
  
  // 新增初始化方法
  initializeSettings: () => Promise<void>;
  
  loadNightModeTime: () => Promise<void>;
  loadAutoSwitchNightMode: () => Promise<void>;
  loadFollowSystemTheme: () => Promise<void>;
  
  setFontSize: (size: number) => void;
  setUseMobileDataWhenWiFiPoor: (enabled: boolean) => void;
  setEnableFloatingWindow: (enabled: boolean) => void;
  setYouthMode: (enabled: boolean) => void;
  
  navigateToAbout: () => void;
  navigateToCustomerService: () => void;
  navigateToPrivacyPolicy: () => void;
  navigateToFontSettings: () => void;

  logout: () => void;
}