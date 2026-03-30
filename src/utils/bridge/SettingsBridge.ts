import { NativeModules } from 'react-native';

const { SettingsBridge: NativeSettingsBridge } = NativeModules;

const promisifyCallback = <T>(
  invoke: (callback: (error: string | null, result: T) => void) => void,
  fallback: T,
): Promise<T> =>
  new Promise((resolve) => {
    if (!invoke) {
      resolve(fallback);
      return;
    }

    invoke((error, result) => {
      if (error) {
        resolve(fallback);
        return;
      }
      resolve(result);
    });
  });

export const SettingsBridge = {
  clearAllCache: async (): Promise<string> => {
    if (!NativeSettingsBridge?.clearAllCache) {
      return '已清理 12.5MB 缓存';
    }
    return NativeSettingsBridge.clearAllCache();
  },

  calculateCacheSize: async (): Promise<string> => {
    if (!NativeSettingsBridge?.calculateCacheSize) {
      const sizes = ['12.5MB', '25.3MB', '8.7MB', '45.2MB', '67.1MB'];
      return sizes[Math.floor(Math.random() * sizes.length)];
    }
    return NativeSettingsBridge.calculateCacheSize();
  },

  changeTheme: async (theme: string): Promise<unknown> => {
    return NativeSettingsBridge?.changeTheme?.(theme);
  },

  setFollowSystemTheme: async (follow: boolean): Promise<unknown> => {
    return NativeSettingsBridge?.setFollowSystemTheme?.(follow);
  },

  setAutoNightMode: async (enabled: boolean): Promise<unknown> => {
    return NativeSettingsBridge?.setAutoNightMode?.(enabled);
  },

  setNightModeTime: async (start: string, end: string): Promise<unknown> => {
    return NativeSettingsBridge?.setNightModeTime?.(start, end);
  },

  checkCurrentTimeTheme: async (): Promise<unknown> => {
    return NativeSettingsBridge?.checkCurrentTimeTheme?.();
  },

  getNightModeStartTime: async (): Promise<string> =>
    promisifyCallback(
      NativeSettingsBridge?.getNightModeStartTime?.bind(NativeSettingsBridge),
      '22:00',
    ),

  getNightModeEndTime: async (): Promise<string> =>
    promisifyCallback(
      NativeSettingsBridge?.getNightModeEndTime?.bind(NativeSettingsBridge),
      '06:00',
    ),

  isAutoNightModeEnabled: async (): Promise<boolean> =>
    promisifyCallback(
      NativeSettingsBridge?.isAutoNightModeEnabled?.bind(NativeSettingsBridge),
      false,
    ),

  logout: async (): Promise<void> => {
    if (!NativeSettingsBridge?.logout) {
      return;
    }
    await NativeSettingsBridge.logout();
  },
};

export default SettingsBridge;
