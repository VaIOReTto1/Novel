import { create } from 'zustand';
import { SettingsStore, ColorScheme } from '../types';
import { useThemeStore, ThemeMode } from '../../../../utils/theme/themeStore';
import NavigationBridge from '../../../../utils/bridge/NavigationBridge';
import SettingsBridge from '../../../../utils/bridge/SettingsBridge';

// Android原生缓存清理调用
const clearAppCache = async (): Promise<string> => {
  return new Promise((resolve, reject) => {
    SettingsBridge.clearAllCache()
      .then((result: string) => resolve(result))
      .catch((error: any) => reject(new Error(error)));
  });
};

// Android原生计算缓存大小
const calculateCacheSize = async (): Promise<string> => {
  return new Promise((resolve, reject) => {
    SettingsBridge.calculateCacheSize()
      .then((result: string) => resolve(result))
      .catch((error: any) => reject(new Error(error)));
  });
};

// 🎯 优化：简化主题切换，单向数据流
const changeThemeUnified = async (theme: string): Promise<void> => {
  try {
    console.log('[SettingsStore] 🎯 统一主题切换:', theme);

    // 🎯 立即更新本地themeStore状态
    const { setTheme } = useThemeStore.getState();
    await setTheme(theme as ThemeMode);

    // 🎯 通知原生，不等待回传（单向数据流）
    SettingsBridge.changeTheme(theme)
      .then((result: string) => {
        console.log('[SettingsStore] ✅ 原生主题设置完成:', result);
      })
      .catch((error: any) => {
        console.warn('[SettingsStore] ⚠️ 原生主题设置失败:', error);
      });

    console.log('[SettingsStore] ✅ 主题切换完成:', theme);
  } catch (error) {
    console.error('[SettingsStore] ❌ 主题切换失败:', error);
    throw error;
  }
};

// 🎯 优化：简化设置同步，单向通知
const syncSettingToNative = async (operation: string, ...args: any[]): Promise<void> => {
  try {
    console.log('[SettingsStore] 🎯 同步设置到原生:', operation, args);

    let promise: Promise<any> | undefined;

    switch (operation) {
      case 'setFollowSystemTheme':
        promise = SettingsBridge.setFollowSystemTheme(args[0]);
        break;
      case 'setAutoNightMode':
        promise = SettingsBridge.setAutoNightMode(args[0]);
        break;
      case 'setNightModeTime':
        promise = SettingsBridge.setNightModeTime(args[0], args[1]);
        break;
      case 'checkCurrentTimeTheme':
        promise = SettingsBridge.checkCurrentTimeTheme();
        break;
      default:
        console.warn('[SettingsStore] ⚠️ 未知的设置操作:', operation);
        return;
    }

    if (promise) {
      promise
        .then((result: string) => {
          console.log('[SettingsStore] ✅ 设置同步完成:', operation, result);
        })
        .catch((error: any) => {
          console.warn('[SettingsStore] ⚠️ 设置同步失败:', operation, error);
        });
    }
  } catch (error) {
    console.error('[SettingsStore] ❌ 设置同步异常:', operation, error);
  }
};

export const useSettingsStore = create<SettingsStore>((set, get) => ({
  // 初始状态
  isInitialized: false,
  cacheSize: '计算中...',
  pushNotificationEnabled: true,
  benefitNotificationEnabled: true,
  followSystemTheme: true,
  colorScheme: 'auto' as ColorScheme,
  autoSwitchNightMode: false,
  nightModeStartTime: '22:00',
  nightModeEndTime: '06:00',
  fontSize: 16,
  useMobileDataWhenWiFiPoor: true,
  enableFloatingWindow: false,
  youthModeEnabled: false,
  privacySettingsVersion: '1.0.0',

  // 缓存操作
  clearCache: async () => {
    try {
      set({ cacheSize: '清理中...' });
      const result = await clearAppCache();
      console.log('[SettingsStore] 缓存清理结果:', result);
      // 重新计算缓存大小
      const newSize = await calculateCacheSize();
      set({ cacheSize: newSize });
    } catch (error) {
      console.error('[SettingsStore] 清理缓存失败:', error);
      set({ cacheSize: '计算失败' });
    }
  },

  calculateCacheSize: async () => {
    try {
      const size = await calculateCacheSize();
      set({ cacheSize: size });
    } catch (error) {
      console.error('[SettingsStore] 计算缓存大小失败:', error);
      set({ cacheSize: '计算失败' });
    }
  },

  // 通知设置
  setPushNotification: (enabled: boolean) => {
    set({ pushNotificationEnabled: enabled });
    console.log('[SettingsStore] 推送通知设置:', enabled);
  },

  setBenefitNotification: (enabled: boolean) => {
    set({ benefitNotificationEnabled: enabled });
    console.log('[SettingsStore] 福利通知设置:', enabled);
  },

  // 🎯 优化：获取当前实际的主题模式（从themeStore获取）
  getCurrentDisplayTheme: () => {
    const themeStore = useThemeStore.getState();
    return themeStore.isDarkMode ? 'dark' : 'light';
  },

  // 🎯 NEW: 新增一个方法，用于从组件层同步系统主题
  syncThemeWithSystem: (systemTheme: 'light' | 'dark') => {
    console.log('[SettingsStore] 🎯 同步系统主题:', systemTheme);
    const { setTheme } = useThemeStore.getState();
    set({
      followSystemTheme: true,
      colorScheme: 'auto',
    });
    setTheme('auto'); // 设置为'auto'以记录用户意图
    // 立即应用正确的明暗模式
    useThemeStore.getState().setDarkMode(systemTheme === 'dark');
  },

  // 🎯 优化：主题设置（单向数据流）
  setFollowSystemTheme: async (follow: boolean) => {
    try {
      console.log('[SettingsStore] 🎯 设置跟随系统主题:', follow);

      if (follow) {
        // 当开启“跟随系统”时，不再直接调用setTheme('auto')
        // 而是依赖组件层调用 syncThemeWithSystem
        console.log('[SettingsStore] 开启跟随系统，等待组件层同步');
        set({ followSystemTheme: true, colorScheme: 'auto' });
        // 异步通知原生
        await syncSettingToNative('setFollowSystemTheme', true);
      } else {
        // 关闭时，设置为当前实际的主题
        const targetTheme = get().getCurrentDisplayTheme();
        set({
          followSystemTheme: false,
          colorScheme: targetTheme as ColorScheme,
        });
        useThemeStore.getState().setTheme(targetTheme as ThemeMode);
        await syncSettingToNative('setFollowSystemTheme', false);
      }

      console.log('[SettingsStore] ✅ 跟随系统主题设置完成:', follow);
    } catch (error) {
      console.error('[SettingsStore] ❌ 设置跟随系统主题失败:', error);
      // 如果出错，回滚状态
      set({ followSystemTheme: !follow });
    }
  },

  setColorScheme: async (scheme: ColorScheme) => {
    try {
      console.log('[SettingsStore] 🎯 设置主题模式:', scheme);

      // 🎯 立即更新本地状态
      set({
        colorScheme: scheme,
        followSystemTheme: scheme === 'auto',
      });

      // 🎯 立即同步到themeStore
      await changeThemeUnified(scheme);

      console.log('[SettingsStore] ✅ 主题设置完成:', scheme);

    } catch (error) {
      console.error('[SettingsStore] ❌ 设置主题模式失败:', error);
    }
  },

  toggleColorScheme: async () => {
    try {
      console.log('[SettingsStore] 🎯 切换主题');
      const currentState = get();
      console.log('[SettingsStore] 当前状态:', {
        colorScheme: currentState.colorScheme,
        followSystemTheme: currentState.followSystemTheme,
      });

      // 🎯 确定切换逻辑
      let newScheme: ColorScheme;

      if (currentState.followSystemTheme) {
        // 如果当前跟随系统，根据当前实际主题切换到相反的固定主题
        const currentDisplay = currentState.getCurrentDisplayTheme();
        newScheme = currentDisplay === 'dark' ? 'light' : 'dark';
      } else {
        // 如果是固定主题，在浅色和深色之间切换
        newScheme = currentState.colorScheme === 'dark' ? 'light' : 'dark';
      }

      console.log('[SettingsStore] 🔄 切换到主题:', newScheme);

      // 🎯 立即更新本地状态
      set({
        colorScheme: newScheme,
        followSystemTheme: false, // 切换后关闭跟随系统
      });

      // 🎯 立即同步到themeStore
      await changeThemeUnified(newScheme);

      console.log('[SettingsStore] ✅ 主题切换完成:', newScheme);
    } catch (error) {
      console.error('[SettingsStore] ❌ 切换夜间模式失败:', error);
    }
  },

  setAutoSwitchNightMode: async (enabled: boolean) => {
    console.log('[SettingsStore] 🎯 设置自动切换夜间模式:', enabled);

    // 🎯 立即更新本地状态
    set({ autoSwitchNightMode: enabled });

    // 🎯 异步同步到原生
    await syncSettingToNative('setAutoNightMode', enabled);

    console.log('[SettingsStore] ✅ 自动切换夜间模式设置完成:', enabled);
  },

  setNightModeTime: async (start: string, end: string) => {
    console.log('[SettingsStore] 🎯 设置夜间模式时间:', { start, end });

    // 🎯 立即更新本地状态
    set({
      nightModeStartTime: start,
      nightModeEndTime: end,
    });

    // 🎯 异步同步到原生
    await syncSettingToNative('setNightModeTime', start, end);

    console.log('[SettingsStore] ✅ 夜间模式时间设置完成:', { start, end });
  },

  // 🎯 优化：简化初始化设置状态
  initializeSettings: async () => {
    try {
      console.log('[SettingsStore] 🎯 开始初始化设置状态');

      // 🎯 只在必要时从原生获取设置，减少跨桥调用
      const [autoEnabled, startTime, endTime] = await Promise.all([
        new Promise<boolean>((resolve) => {
          SettingsBridge.isAutoNightModeEnabled()
            .then(resolve)
            .catch(() => resolve(false));
        }),
        new Promise<string>((resolve) => {
          SettingsBridge.getNightModeStartTime()
            .then(resolve)
            .catch(() => resolve('22:00'));
        }),
        new Promise<string>((resolve) => {
          SettingsBridge.getNightModeEndTime()
            .then(resolve)
            .catch(() => resolve('06:00'));
        }),
      ]);

      // 🎯 从themeStore获取主题状态，避免重复从原生获取
      const themeState = useThemeStore.getState();

      // 更新状态
      set({
        colorScheme: themeState.currentTheme as ColorScheme,
        followSystemTheme: themeState.currentTheme === 'auto',
        autoSwitchNightMode: autoEnabled,
        nightModeStartTime: startTime,
        nightModeEndTime: endTime,
        isInitialized: true,
      });

      console.log('[SettingsStore] ✅ 设置初始化完成:', {
        colorScheme: themeState.currentTheme,
        autoEnabled,
        nightModeTime: `${startTime}-${endTime}`,
      });

    } catch (error) {
      console.error('[SettingsStore] ❌ 初始化设置状态失败:', error);
    }
  },

  // 🎯 简化：减少不必要的原生调用
  loadNightModeTime: async () => {
    const { isInitialized } = get();
    if (isInitialized) {
      console.log('[SettingsStore] 夜间模式时间设置已初始化，跳过获取');
      return;
    }
    // 此方法已集成到initializeSettings中
  },

  loadAutoSwitchNightMode: async () => {
    const { isInitialized } = get();
    if (isInitialized) {
      console.log('[SettingsStore] 自动切换夜间模式设置已初始化，跳过获取');
      return;
    }
    // 此方法已集成到initializeSettings中
  },

  loadFollowSystemTheme: async () => {
    const { isInitialized } = get();
    if (isInitialized) {
      console.log('[SettingsStore] 跟随系统主题设置已初始化，跳过获取');
      return;
    }
    // 此方法已集成到initializeSettings中
  },

  // 字体设置
  setFontSize: (size: number) => {
    const clampedSize = Math.max(12, Math.min(24, size));
    set({ fontSize: clampedSize });
    console.log('[SettingsStore] 字体大小:', clampedSize);
  },

  // 网络设置
  setUseMobileDataWhenWiFiPoor: (enabled: boolean) => {
    set({ useMobileDataWhenWiFiPoor: enabled });
    console.log('[SettingsStore] WiFi较差时使用移动网络:', enabled);
  },

  // 播放设置
  setEnableFloatingWindow: (enabled: boolean) => {
    set({ enableFloatingWindow: enabled });
    console.log('[SettingsStore] 小窗播放:', enabled);
  },

  // 青少年模式
  setYouthMode: (enabled: boolean) => {
    set({ youthModeEnabled: enabled });
    console.log('[SettingsStore] 青少年模式:', enabled);
  },

  // 导航操作
  navigateToAbout: () => {
    console.log('[SettingsStore] 导航到关于页面');
    // TODO: 实现导航逻辑
  },

  navigateToCustomerService: () => {
    console.log('[SettingsStore] 导航到客服页面');
      NavigationBridge.navigateToHelpSupport();
  },

  navigateToPrivacyPolicy: () => {
    console.log('[SettingsStore] 导航到隐私政策页面');
      NavigationBridge.navigateToPrivacyPolicy();
  },

  navigateToFontSettings: () => {
    console.log('[SettingsStore] 导航到字体设置页面');
    // TODO: 实现导航逻辑
  },

  // 退出登录
  logout: async () => {
    try {
      console.log('[SettingsStore] 开始退出登录');

      // 调用Android端清空token - 使用Promise方式
      await SettingsBridge.logout();
      console.log('[SettingsStore] ✅ Android端退出登录成功');

      console.log('[SettingsStore] 退出登录完成');
    } catch (error) {
      console.error('[SettingsStore] 退出登录失败:', error);
      throw error;
    }
  },
}));

