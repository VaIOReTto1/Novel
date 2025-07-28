import { create } from 'zustand';
import { DeviceEventEmitter, NativeModules, Appearance} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';

export type ThemeMode = 'light' | 'dark' | 'auto';

interface ThemeState {
  currentTheme: ThemeMode;
  setTheme: (theme: ThemeMode) => void;
  isDarkMode: boolean;
  isInitialized: boolean;
  initializeFromNative: () => Promise<void>;
  initializeFromProps: (props: { initialThemeMode?: string; initialActualTheme?: string; initialIsDarkMode?: boolean }) => void;
  // 🎯 新增：系统主题监听
  listenToSystemTheme: () => () => void;
  // 🎯 新增：优化的选择器
  getThemeSelector: () => { currentTheme: ThemeMode; isDarkMode: boolean };
}

const THEME_STORAGE_KEY = 'novel_theme_mode';

// 🎯 优化：单向数据流Store
export const useThemeStore = create<ThemeState>((set, get) => ({
  currentTheme: 'auto',
  isDarkMode: false,
  isInitialized: false,

  // 🎯 优化：单向数据流setTheme，立即更新本地状态
  setTheme: (theme: ThemeMode) => {
    console.log('[ThemeStore] 🎯 设置主题模式:', theme);
    
    // 🎯 FIX: 当设置为'auto'时，立即从Appearance获取当前系统主题
    const isSystemDark = Appearance.getColorScheme() === 'dark';
    const isDark = theme === 'dark' || (theme === 'auto' && isSystemDark);

    set({
      currentTheme: theme,
      isDarkMode: isDark,
    });

    console.log('[ThemeStore] ✅ 本地状态已立即更新:', { theme, isDark, isSystemDark });

    // 异步保存和同步到原生
    AsyncStorage.setItem(THEME_STORAGE_KEY, theme).catch(e => {
      console.warn('[ThemeStore] 保存主题到AsyncStorage失败:', e);
    });

    if (NativeModules.SettingsBridge?.changeTheme) {
      NativeModules.SettingsBridge.changeTheme(theme).catch((e: any) => {
        console.warn('[ThemeStore] 同步主题到Android失败:', e);
      });
    }
  },

  // 🎯 新增：系统主题监听器
  listenToSystemTheme: () => {
    console.log('[ThemeStore] 🎯 启动系统主题监听');
    
    // 🎯 使用定时器检测系统主题变化（兜底方案）
    let lastSystemTheme = isSystemDarkMode();
    
    const checkSystemTheme = () => {
      const currentState = get();
      
      // 只有在跟随系统主题模式下才响应系统变化
      if (currentState.currentTheme === 'auto') {
        const newSystemTheme = isSystemDarkMode();
        
        if (lastSystemTheme !== newSystemTheme) {
          console.log('[ThemeStore] 🔄 系统主题变化检测:', { from: lastSystemTheme, to: newSystemTheme });
          lastSystemTheme = newSystemTheme;
          
          set({
            isDarkMode: newSystemTheme,
          });

          // 🎯 发送自定义事件，与原生事件格式对齐
          DeviceEventEmitter.emit('ThemeChanged', {
            colorScheme: newSystemTheme ? 'dark' : 'light',
            currentThemeMode: 'auto',
            followSystem: true,
          });
        }
      }
    };

    // 每5秒检查一次系统主题变化
    const intervalId = setInterval(checkSystemTheme, 5000);

    // 返回清理函数
    return () => {
      console.log('[ThemeStore] 🧹 清理系统主题监听器');
      clearInterval(intervalId);
    };
  },

  // 🎯 新增：优化的选择器，减少重渲染
  getThemeSelector: () => {
    const state = get();
    return {
      currentTheme: state.currentTheme,
      isDarkMode: state.isDarkMode,
    };
  },

  // 🎯 优化：从props初始化主题状态（同步，避免闪烁）
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
    const currentState = get();
    if (currentState.isInitialized) {
      console.log('[ThemeStore] 主题已初始化，跳过从原生端获取');
      return;
    }
    
    try {
      console.log('[ThemeStore] 🎯 开始从原生端获取主题状态');

      // 🎯 简化：只获取必要的状态，减少跨桥调用
      const savedTheme = await restoreThemeFromStorage();
      
      // 检测当前实际主题状态
      const actualIsDark = savedTheme === 'dark' || (savedTheme === 'auto' && isSystemDarkMode());
      
      // 更新状态
      set({
        currentTheme: savedTheme,
        isDarkMode: actualIsDark,
        isInitialized: true,
      });

      console.log('[ThemeStore] ✅ 主题状态已同步:', { savedTheme, actualIsDark });

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

// 🎯 优化：更可靠的系统主题检测
function isSystemDarkMode(): boolean {
  try {
    // Web环境检测
    if (typeof window !== 'undefined' && window.matchMedia) {
      return window.matchMedia('(prefers-color-scheme: dark)').matches;
    }
    
    // 移动端默认返回false（由原生端管理）
    return false;
  } catch (e) {
    console.warn('[ThemeStore] 系统主题检测失败:', e);
    return false;
  }
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

// 🎯 优化：简化初始化流程
export const initializeTheme = async (): Promise<() => void> => {
  try {
    console.log('[ThemeStore] 🎯 开始初始化主题系统');
    
    // 1. 从AsyncStorage恢复主题
    const savedTheme = await restoreThemeFromStorage();
    const actualIsDark = savedTheme === 'dark' || (savedTheme === 'auto' && isSystemDarkMode());

    // 2. 设置初始主题状态
    useThemeStore.setState({
      currentTheme: savedTheme,
      isDarkMode: actualIsDark,
      isInitialized: true,
    });

    console.log('[ThemeStore] ✅ 主题初始状态设置完成:', { savedTheme, actualIsDark });

    // 3. 启动系统主题监听
    const cleanupSystemListener = useThemeStore.getState().listenToSystemTheme();

    // 4. 监听原生主题变更事件（优化版）
    const nativeThemeSubscription = DeviceEventEmitter.addListener('ThemeChanged', (data: { 
      colorScheme: string;
      currentThemeMode?: string;
      followSystem?: boolean;
    }) => {
      console.log('[ThemeStore] 🎯 收到Android主题变更事件:', JSON.stringify(data));
      const { colorScheme, currentThemeMode, followSystem } = data;

      const currentState = useThemeStore.getState();
      console.log('[ThemeStore] 当前状态 - currentTheme:', currentState.currentTheme, 'isDarkMode:', currentState.isDarkMode);

      // 🎯 优化：只处理完整的主题信息（来自SettingsViewModel）
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
        // 🎯 忽略不完整的事件，避免状态不一致
        console.log('[ThemeStore] ⚠️ 收到不完整的主题事件，忽略 - colorScheme:', colorScheme);
      }
    });

    console.log('[ThemeStore] ✅ 主题系统初始化完成');

    // 返回清理函数
    return () => {
      cleanupSystemListener();
      nativeThemeSubscription?.remove();
      console.log('[ThemeStore] 🧹 主题监听器已清理');
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

// 🎯 新增：主题选择器Hook，减少重渲染
export const useThemeSelector = () => {
  return useThemeStore(state => ({
    currentTheme: state.currentTheme,
    isDarkMode: state.isDarkMode,
  }));
};

// 🎯 新增：主题操作Hook
export const useThemeActions = () => {
  return useThemeStore(state => ({
    setTheme: state.setTheme,
    initializeFromProps: state.initializeFromProps,
    initializeFromNative: state.initializeFromNative,
  }));
};
