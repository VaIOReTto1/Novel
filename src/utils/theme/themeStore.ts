import { create } from 'zustand';
import { DeviceEventEmitter, NativeModules } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export type ThemeMode = 'light' | 'dark' | 'auto';

interface ThemeState {
  currentTheme: ThemeMode;
  setTheme: (theme: ThemeMode) => void;
  isDarkMode: boolean;
  isInitialized: boolean;
  initializeFromNative: () => Promise<void>;
  initializeFromProps: (props: { initialThemeMode?: string; initialActualTheme?: string; initialIsDarkMode?: boolean }) => void;
}

const THEME_STORAGE_KEY = 'novel_theme_mode';

export const useThemeStore = create<ThemeState>((set) => ({
  currentTheme: 'auto',
  isDarkMode: false, // 这个初始值会被props覆盖
  isInitialized: false,

  setTheme: async (theme: ThemeMode) => {
    const isDark = theme === 'dark' || (theme === 'auto' && isSystemDarkMode());

    set({
      currentTheme: theme,
      isDarkMode: isDark,
    });

    // 保存到AsyncStorage
    try {
      await AsyncStorage.setItem(THEME_STORAGE_KEY, theme);
      console.log('[ThemeStore] 主题已保存到AsyncStorage:', theme);
    } catch (e) {
      console.warn('[ThemeStore] 保存主题到AsyncStorage失败:', e);
    }

    // 同步到Android原生
    try {
      if (NativeModules.NavigationUtil?.changeTheme) {
        await NativeModules.NavigationUtil.changeTheme(theme);
        console.log('[ThemeStore] 主题已同步到Android:', theme);
      }
    } catch (e) {
      console.warn('[ThemeStore] 同步主题到Android失败:', e);
    }
  },

  // 🎯 新增：从props初始化主题状态（同步，避免闪烁）
  initializeFromProps: (props: { initialThemeMode?: string; initialActualTheme?: string; initialIsDarkMode?: boolean }) => {
    const { initialThemeMode, initialActualTheme, initialIsDarkMode } = props;
    
    if (initialThemeMode && initialActualTheme !== undefined && initialIsDarkMode !== undefined) {
      console.log('[ThemeStore] 🎯 从props初始化主题状态:', { initialThemeMode, initialActualTheme, initialIsDarkMode });
      
      set({
        currentTheme: initialThemeMode as ThemeMode,
        isDarkMode: initialIsDarkMode,
        isInitialized: true,
      });
      
      // 异步保存到AsyncStorage
      AsyncStorage.setItem(THEME_STORAGE_KEY, initialThemeMode).catch(e => {
        console.warn('[ThemeStore] 保存主题到AsyncStorage失败:', e);
      });
      
      console.log('[ThemeStore] ✅ 主题状态已从props初始化完成');
    } else {
      console.warn('[ThemeStore] ⚠️ props中缺少主题信息，跳过初始化');
    }
  },

  // 🎯 优化：从原生端获取主题状态（仅在props初始化失败时使用）
  initializeFromNative: async () => {
    const currentState = useThemeStore.getState();
    if (currentState.isInitialized) {
      console.log('[ThemeStore] 主题已初始化，跳过从原生端获取');
      return;
    }
    
    try {
      console.log('[ThemeStore] 🎯 开始从原生端获取主题状态');

      // 从Android端获取当前实际主题状态
      const actualTheme = await new Promise<string>((resolve, reject) => {
        if (NativeModules.NavigationUtil?.getCurrentActualTheme) {
          NativeModules.NavigationUtil?.getCurrentActualTheme((error: string | null, result: string) => {
            if (error) {
              reject(new Error(error));
            } else {
              resolve(result);
            }
          });
        } else {
          reject(new Error('SettingsBridge.getCurrentActualTheme not available'));
        }
      });

      console.log('[ThemeStore] ✅ 从原生端获取到主题状态:', actualTheme);

      // 获取当前主题模式设置
      const currentMode = await new Promise<string>((resolve, reject) => {
        if (NativeModules.NavigationUtil?.getCurrentNightMode) {
          NativeModules.NavigationUtil?.getCurrentNightMode((error: string | null, result: string) => {
            if (error) {
              reject(new Error(error));
            } else {
              resolve(result);
            }
          });
        } else {
          reject(new Error('SettingsBridge.getCurrentNightMode not available'));
        }
      });

      console.log('[ThemeStore] ✅ 从原生端获取到主题模式:', currentMode);

      // 更新状态
      const isDark = actualTheme === 'dark';
      set({
        currentTheme: currentMode as ThemeMode,
        isDarkMode: isDark,
        isInitialized: true,
      });

      // 同步保存到AsyncStorage
      await AsyncStorage.setItem(THEME_STORAGE_KEY, currentMode);

      console.log('[ThemeStore] ✅ 主题状态已同步:', { currentMode, actualTheme, isDark });

    } catch (e) {
      console.warn('[ThemeStore] ⚠️ 从原生端获取主题状态失败，使用默认设置:', e);

      // 失败时从AsyncStorage恢复
      const savedTheme = await restoreThemeFromStorage();
      const isDark = savedTheme === 'dark' || (savedTheme === 'auto' && isSystemDarkMode());

      set({
        currentTheme: savedTheme,
        isDarkMode: isDark,
        isInitialized: true,
      });
    }
  },
}));

// 检测系统是否为深色模式（简化版本）
function isSystemDarkMode(): boolean {
  if (typeof window !== 'undefined' && window.matchMedia) {
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }
  return false;
}

/**
 * 从AsyncStorage恢复主题设置
 */
export const restoreThemeFromStorage = async (): Promise<ThemeMode> => {
  try {
    const savedTheme = await AsyncStorage.getItem(THEME_STORAGE_KEY);
    if (savedTheme && ['light', 'dark', 'auto'].includes(savedTheme)) {
      console.log('[ThemeStore] 从AsyncStorage恢复主题:', savedTheme);
      return savedTheme as ThemeMode;
    }
  } catch (e) {
    console.warn('[ThemeStore] 从AsyncStorage恢复主题失败:', e);
  }

  // 默认返回auto模式
  console.log('[ThemeStore] 使用默认主题: auto');
  return 'auto';
};

/**
 * 清除主题缓存
 */
export const clearThemeCache = async (): Promise<void> => {
  try {
    await AsyncStorage.removeItem(THEME_STORAGE_KEY);
    console.log('[ThemeStore] 主题缓存已清除');
  } catch (e) {
    console.warn('[ThemeStore] 清除主题缓存失败:', e);
  }
};

// 初始化主题
export const initializeTheme = async (): Promise<() => void> => {
  try {
    // 从AsyncStorage恢复主题
    const savedTheme = await restoreThemeFromStorage();

    // 如果是跟随系统主题，从Android端获取当前实际主题状态
    let actualIsDark = savedTheme === 'dark' || (savedTheme === 'auto' && isSystemDarkMode());

    if (savedTheme === 'auto') {
      try {
        // 从Android端获取当前实际主题状态
        const actualTheme = await new Promise<string>((resolve, reject) => {
          if (NativeModules.NavigationUtil?.getCurrentActualTheme) {
            NativeModules.NavigationUtil.getCurrentActualTheme((error: string | null, result: string) => {
              if (error) {
                reject(new Error(error));
              } else {
                resolve(result);
              }
            });
          } else {
            reject(new Error('NavigationUtil not available'));
          }
        });

        actualIsDark = actualTheme === 'dark';
        console.log('[ThemeStore] 从Android获取实际主题状态:', actualTheme, 'isDark:', actualIsDark);
      } catch (e) {
        console.warn('[ThemeStore] 获取Android实际主题失败，使用系统检测:', e);
        actualIsDark = isSystemDarkMode();
      }
    }

    // 设置主题（不触发保存，因为已经是从存储中恢复的）
    useThemeStore.setState({
      currentTheme: savedTheme,
      isDarkMode: actualIsDark,
      isInitialized: true,
    });

    // 监听原生主题变更事件
    const subscription = DeviceEventEmitter.addListener('ThemeChanged', (data: { 
      colorScheme: string;
      currentThemeMode?: string;
      followSystem?: boolean;
    }) => {
      console.log('[ThemeStore] 🎯 收到Android主题变更事件:', JSON.stringify(data));
      const { colorScheme, currentThemeMode, followSystem } = data;

      const currentState = useThemeStore.getState();
      console.log('[ThemeStore] 当前状态 - currentTheme:', currentState.currentTheme, 'isDarkMode:', currentState.isDarkMode);

      // 如果事件包含完整的主题信息（来自SettingsViewModel）
      if (currentThemeMode !== undefined && followSystem !== undefined) {
        console.log('[ThemeStore] ✅ 收到完整主题信息 - mode:', currentThemeMode, 'followSystem:', followSystem, 'actualTheme:', colorScheme);
        
        const isDark = colorScheme === 'dark';
        
        // 更新完整的主题状态
        useThemeStore.setState({
          currentTheme: currentThemeMode as ThemeMode,
          isDarkMode: isDark,
        });

        // 保存用户设置到AsyncStorage
        AsyncStorage.setItem(THEME_STORAGE_KEY, currentThemeMode).catch(e => {
          console.warn('[ThemeStore] 保存主题设置失败:', e);
        });
        
        console.log('[ThemeStore] ✅ 主题状态已更新 - currentTheme:', currentThemeMode, 'isDarkMode:', isDark);
      } else {
        // 兼容旧格式事件（只有colorScheme），但这种情况应该很少见
        // 为了避免状态不一致，我们跳过不完整的事件，等待完整的事件
        console.log('[ThemeStore] ⚠️ 收到不完整的主题事件，等待完整事件 - colorScheme:', colorScheme);
        return;
      }
    });

    console.log('[ThemeStore] 主题初始化完成:', savedTheme, 'actualIsDark:', actualIsDark);

    // 返回清理函数
    return () => {
      subscription?.remove();
      console.log('[ThemeStore] 主题监听器已清理');
    };
  } catch (e) {
    console.warn('[ThemeStore] 主题初始化失败:', e);
    // 失败时使用默认设置
    useThemeStore.setState({
      currentTheme: 'auto',
      isDarkMode: isSystemDarkMode(),
      isInitialized: true,
    });
    return () => {};
  }
};
