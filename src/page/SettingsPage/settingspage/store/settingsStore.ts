import { create } from 'zustand';
import { NativeModules } from 'react-native';
import { SettingsStore, ColorScheme } from '../types';
import { useThemeStore, ThemeMode } from '../../../../utils/theme/themeStore';

const { SettingsBridge, NavigationBridge } = NativeModules;

// Android原生缓存清理调用
const clearAppCache = async (): Promise<string> => {
  return new Promise((resolve, reject) => {
    if (SettingsBridge?.clearAllCache) {
      SettingsBridge.clearAllCache((error: string | null, result: string) => {
        if (error) {
          reject(new Error(error));
        } else {
          resolve(result);
        }
      });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      setTimeout(() => {
        resolve('已清理 12.5MB 缓存');
      }, 2000);
    }
  });
};

// Android原生计算缓存大小
const calculateCacheSize = async (): Promise<string> => {
  return new Promise((resolve, reject) => {
    if (SettingsBridge?.calculateCacheSize) {
      SettingsBridge.calculateCacheSize((error: string | null, result: string) => {
        if (error) {
          reject(new Error(error));
        } else {
          resolve(result);
        }
      });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      setTimeout(() => {
        const sizes = ['12.5MB', '25.3MB', '8.7MB', '45.2MB', '67.1MB'];
        const randomSize = sizes[Math.floor(Math.random() * sizes.length)];
        resolve(randomSize);
      }, 1000);
    }
  });
};

// Android原生切换夜间模式 - 修复版本（重新添加）
const toggleNightModeNative = async (): Promise<{ message: string; currentThemeMode: string; actualTheme: string; followSystem: boolean }> => {
  return new Promise((resolve, reject) => {
    console.log('[SettingsStore] 调用SettingsBridge.toggleNightMode，bridge可用:', !!SettingsBridge?.toggleNightMode);
    
    if (SettingsBridge?.toggleNightMode) {
      SettingsBridge.toggleNightMode((error: string | null, result: any) => {
        console.log('[SettingsStore] 🎯 toggleNightMode回调触发 - error:', error, 'result:', result, 'result类型:', typeof result);
        
        if (error) {
          console.error('[SettingsStore] ❌ toggleNightMode失败:', error);
          reject(new Error(error));
        } else {
          // 检查Android端返回的数据结构
          if (typeof result === 'object' && result !== null) {
            // Android端返回Map格式的完整状态信息
            const response = {
              message: result.message || '主题切换成功',
              currentThemeMode: result.currentThemeMode || 'light',
              actualTheme: result.actualTheme || 'light',
              followSystem: result.followSystem === true // 明确布尔值转换
            };
            console.log('[SettingsStore] ✅ 解析Android端返回数据:', response);
            resolve(response);
          } else {
            // 兼容旧版本返回格式（字符串）
            console.log('[SettingsStore] 收到旧格式返回数据，使用fallback');
            resolve({ 
              message: result || '主题切换成功', 
              currentThemeMode: 'light', 
              actualTheme: 'light', 
              followSystem: false 
            });
          }
        }
      });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      const currentTheme = useThemeStore.getState().isDarkMode ? 'light' : 'dark';
      resolve({ 
        message: `已切换至${currentTheme === 'dark' ? '深色' : '浅色'}模式`, 
        currentThemeMode: currentTheme,
        actualTheme: currentTheme,
        followSystem: false
      });
    }
  });
};

// Android原生设置夜间模式 - 修复版本
const setNightModeNative = (mode: string): Promise<{ message: string; currentThemeMode: string; actualTheme: string; followSystem: boolean }> => {
  return new Promise((resolve, reject) => {
    if (SettingsBridge?.setNightMode) {
      SettingsBridge.setNightMode(mode, (error: string | null, result: any) => {
        if (error) {
          reject(new Error(error));
        } else {
          // Android端现在返回完整的状态信息
          if (typeof result === 'object' && result.message) {
            resolve({
              message: result.message,
              currentThemeMode: result.currentThemeMode,
              actualTheme: result.actualTheme,
              followSystem: result.followSystem
            });
          } else {
            // 兼容旧版本返回格式
            SettingsBridge.getCurrentActualTheme((err: string | null, actualTheme: string) => {
              if (err) {
                resolve({ message: result, currentThemeMode: mode, actualTheme: mode === 'dark' ? 'dark' : 'light', followSystem: mode === 'auto' });
              } else {
                resolve({ message: result, currentThemeMode: mode, actualTheme, followSystem: mode === 'auto' });
              }
            });
          }
        }
      });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      resolve({ 
        message: `夜间模式已设置为: ${mode}`, 
        currentThemeMode: mode,
        actualTheme: mode === 'dark' ? 'dark' : 'light',
        followSystem: mode === 'auto'
      });
    }
  });
};

// Android原生获取当前夜间模式
const getCurrentNightModeNative = (): Promise<string> => {
  return new Promise((resolve, reject) => {
    if (SettingsBridge?.getCurrentNightMode) {
      SettingsBridge.getCurrentNightMode((error: string | null, result: string) => {
        if (error) {
          reject(new Error(error));
        } else {
          resolve(result);
        }
      });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      resolve('auto');
    }
  });
};

// Android原生获取当前实际主题
const getCurrentActualThemeNative = (): Promise<string> => {
  return new Promise((resolve, reject) => {
    if (SettingsBridge?.getCurrentActualTheme) {
      SettingsBridge.getCurrentActualTheme((error: string | null, result: string) => {
        if (error) {
          reject(new Error(error));
        } else {
          resolve(result);
        }
      });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      resolve(useThemeStore.getState().isDarkMode ? 'dark' : 'light');
    }
  });
};

// Android原生获取是否跟随系统主题
const isFollowSystemThemeNative = (): Promise<boolean> => {
  return new Promise((resolve, reject) => {
    if (SettingsBridge?.isFollowSystemTheme) {
      SettingsBridge.isFollowSystemTheme((error: string | null, result: boolean) => {
        if (error) {
          reject(new Error(error));
        } else {
          resolve(result);
        }
      });
    } else {
      console.warn('[SettingsStore] isFollowSystemTheme not available, falling back to true.');
      resolve(true); // 默认启用
    }
  });
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

  // 获取当前实际的主题模式（用于显示icon）
  getCurrentDisplayTheme: () => {
    const themeStore = useThemeStore.getState();

    // 始终返回实际的主题状态（从themeStore中获取）
    // 这样可以正确反映Android端传来的实际主题状态
    return themeStore.isDarkMode ? 'dark' : 'light';
  },

  // 主题设置
  setFollowSystemTheme: async (follow: boolean) => {
    try {
      console.log('[SettingsStore] 🎯 开始设置跟随系统主题:', follow);
      
      // 立即更新本地状态，确保UI响应及时
      set({ 
        followSystemTheme: follow,
        colorScheme: follow ? 'auto' : get().colorScheme,
      });

      if (SettingsBridge?.setFollowSystemTheme) {
        SettingsBridge.setFollowSystemTheme(follow, (error: string | null, result: any) => {
          if (error) {
            console.error('[SettingsStore] 设置跟随系统主题失败:', error);
            // 如果出错，回滚状态
            set({ followSystemTheme: !follow });
            return;
          }

          console.log('[SettingsStore] ✅ Android端设置跟随系统主题成功:', result);

          // 如果Android端返回了完整的状态信息，使用它更新状态
          if (result && typeof result === 'object' && 'followSystem' in result) {
            set({
              followSystemTheme: result.followSystem,
              colorScheme: result.currentThemeMode as ColorScheme,
            });

            // 同步更新themeStore
            useThemeStore.getState().setTheme(result.currentThemeMode as ThemeMode);
            
            console.log('[SettingsStore] ✅ 跟随系统主题设置完成 (新):', {
              设置模式: result.currentThemeMode,
              实际主题: result.actualTheme,
              跟随系统: result.followSystem
            });
          } else {
            // 如果Android端只返回了消息字符串，使用本地状态
            const targetTheme = follow ? 'auto' : get().colorScheme;
            useThemeStore.getState().setTheme(targetTheme);
            
            console.log('[SettingsStore] ✅ 跟随系统主题设置完成 (简单):', {
              跟随系统: follow,
              设置模式: targetTheme
            });
          }
        });
      } else {
        // Fallback for non-native environment
        const targetTheme = follow ? 'auto' : get().colorScheme;
        useThemeStore.getState().setTheme(targetTheme);
        console.log('[SettingsStore] ✅ 跟随系统主题设置完成 (fallback):', follow);
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
      console.log('[SettingsStore] 🎯 开始设置主题模式:', scheme);
      
      const result = await setNightModeNative(scheme);
      console.log('[SettingsStore] ✅ Android端设置完成:', result);

      // 直接使用Android端返回的完整状态信息
      set({
        colorScheme: result.currentThemeMode as ColorScheme,
        followSystemTheme: result.followSystem,
      });
      
      // 同步更新themeStore的currentTheme设置
      const { setTheme } = useThemeStore.getState();
      await setTheme(result.currentThemeMode as any);
      
      console.log('[SettingsStore] ✅ 主题设置完成:', {
        设置模式: result.currentThemeMode,
        实际主题: result.actualTheme,
        跟随系统: result.followSystem
      });
      
    } catch (error) {
      console.error('[SettingsStore] ❌ 设置主题模式失败:', error);
    }
  },

  toggleColorScheme: async () => {
    try {
      console.log('[SettingsStore] 🎯 开始切换主题');
      const currentState = get();
      console.log('[SettingsStore] 当前状态:', {
        colorScheme: currentState.colorScheme,
        followSystemTheme: currentState.followSystemTheme
      });

      // 使用toggleNightModeNative，它会正确处理跟随系统主题的情况
      // Android端的toggleNightMode方法会：
      // 1. 如果当前是跟随系统主题，自动关闭并切换到相反的固定主题
      // 2. 如果当前是固定主题，直接切换到相反主题
      console.log('[SettingsStore] 🔄 调用toggleNightModeNative...');
      const result = await toggleNightModeNative();
      console.log('[SettingsStore] ✅ Android端切换完成:', result);

      // 使用从原生端返回的权威状态更新本地store
      console.log('[SettingsStore] 📝 更新本地状态...');
      set({
        colorScheme: result.currentThemeMode as ColorScheme,
        followSystemTheme: result.followSystem,
      });

      // 同步到全局的themeStore，确保UI一致性
      console.log('[SettingsStore] 🔗 同步到themeStore...');
      useThemeStore.getState().setTheme(result.currentThemeMode as any);

      console.log('[SettingsStore] ✅ 主题切换完成:', {
        新模式: result.currentThemeMode,
        实际主题: result.actualTheme,
        跟随系统: result.followSystem,
        消息: result.message
      });
    } catch (error) {
      console.error('[SettingsStore] ❌ 切换夜间模式失败:', error);
    }
  },

  setAutoSwitchNightMode: (enabled: boolean) => {
    console.log('[SettingsStore] 🎯 开始设置自动切换夜间模式:', enabled);
    set({ autoSwitchNightMode: enabled });

    // 同步到Android端
    if (SettingsBridge?.setAutoNightMode) {
      SettingsBridge.setAutoNightMode(enabled, (error: string | null, result: string) => {
        if (error) {
          console.error('[SettingsStore] ❌ 设置自动切换夜间模式失败:', error);
        } else {
          console.log('[SettingsStore] ✅ 自动切换夜间模式设置成功:', result);
        }
      });
    } else {
      console.warn('[SettingsStore] ⚠️ SettingsBridge.setAutoNightMode 不可用');
    }
  },

  setNightModeTime: (start: string, end: string) => {
    console.log('[SettingsStore] 🎯 开始设置夜间模式时间:', { start, end });
    set({
      nightModeStartTime: start,
      nightModeEndTime: end,
    });

    // 同步到Android端
    if (SettingsBridge?.setNightModeTime) {
      SettingsBridge.setNightModeTime(start, end, (error: string | null, result: string) => {
        if (error) {
          console.error('[SettingsStore] ❌ 设置夜间模式时间失败:', error);
        } else {
          console.log('[SettingsStore] ✅ 夜间模式时间设置成功:', result);
        }
      });
    } else {
      console.warn('[SettingsStore] ⚠️ SettingsBridge.setNightModeTime 不可用');
    }
  },

  // 初始化设置状态 - 新增方法
  initializeSettings: async () => {
    try {
      console.log('[SettingsStore] 🎯 开始初始化设置状态');
      
      // 并行获取所有设置
      const [currentMode, actualTheme, followSystem, autoEnabled, startTime, endTime] = await Promise.all([
        getCurrentNightModeNative().catch(() => 'auto'),
        getCurrentActualThemeNative().catch(() => 'light'),
        isFollowSystemThemeNative().catch(() => true),
        new Promise<boolean>((resolve) => {
          if (SettingsBridge?.isAutoNightModeEnabled) {
            SettingsBridge.isAutoNightModeEnabled((err: string | null, result: boolean) => {
              resolve(err ? false : result);
            });
          } else {
            resolve(false);
          }
        }),
        new Promise<string>((resolve) => {
          if (SettingsBridge?.getNightModeStartTime) {
            SettingsBridge.getNightModeStartTime((err: string | null, result: string) => {
              resolve(err ? '22:00' : result);
            });
          } else {
            resolve('22:00');
          }
        }),
        new Promise<string>((resolve) => {
          if (SettingsBridge?.getNightModeEndTime) {
            SettingsBridge.getNightModeEndTime((err: string | null, result: string) => {
              resolve(err ? '06:00' : result);
            });
          } else {
            resolve('06:00');
          }
        })
      ]);

      // 更新状态
      set({
        colorScheme: currentMode as ColorScheme,
        followSystemTheme: followSystem,
        autoSwitchNightMode: autoEnabled,
        nightModeStartTime: startTime,
        nightModeEndTime: endTime,
        isInitialized: true, // 标记为已初始化
      });

      // 同步更新themeStore的currentTheme设置
      const { setTheme } = useThemeStore.getState();
      await setTheme(currentMode as any);

      console.log('[SettingsStore] ✅ 设置初始化完成:', {
        currentMode,
        actualTheme,
        followSystem,
        autoEnabled,
        nightModeTime: `${startTime}-${endTime}`
      });
      
    } catch (error) {
      console.error('[SettingsStore] ❌ 初始化设置状态失败:', error);
    }
  },

  // 获取夜间模式时间设置
  loadNightModeTime: async () => {
    const { isInitialized } = get();
    if (isInitialized) {
      console.log('[SettingsStore] 夜间模式时间设置已初始化，跳过从原生获取');
      return;
    }

    try {
      if (SettingsBridge?.getNightModeStartTime && SettingsBridge?.getNightModeEndTime) {
        const startTime = await new Promise<string>((resolve, reject) => {
          SettingsBridge.getNightModeStartTime((error: string | null, result: string) => {
            if (error) {reject(new Error(error));}
            else {resolve(result);}
          });
        });

        const endTime = await new Promise<string>((resolve, reject) => {
          SettingsBridge.getNightModeEndTime((error: string | null, result: string) => {
            if (error) {reject(new Error(error));}
            else {resolve(result);}
          });
        });

        set({
          nightModeStartTime: startTime,
          nightModeEndTime: endTime,
        });

        console.log('[SettingsStore] 夜间模式时间已加载:', startTime, '-', endTime);
      }
    } catch (error) {
      console.error('[SettingsStore] 加载夜间模式时间失败:', error);
    }
  },

  // 加载自动切换夜间模式设置
  loadAutoSwitchNightMode: async () => {
    const { isInitialized } = get();
    if (isInitialized) {
      console.log('[SettingsStore] 自动切换夜间模式设置已初始化，跳过从原生获取');
      return;
    }

    try {
      if (SettingsBridge?.isAutoNightModeEnabled) {
        const enabled = await new Promise<boolean>((resolve, reject) => {
          SettingsBridge.isAutoNightModeEnabled((error: string | null, result: boolean) => {
            if (error) {reject(new Error(error));}
            else {resolve(result);}
          });
        });

        set({ autoSwitchNightMode: enabled });
        console.log('[SettingsStore] 自动切换夜间模式设置已加载:', enabled);
      }
    } catch (error) {
      console.error('[SettingsStore] 加载自动切换夜间模式设置失败:', error);
    }
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
    if (NavigationBridge?.navigateToHelpSupport) {
      NavigationBridge.navigateToHelpSupport();
    } else {
      // Fallback：直接创建 RN 视图
      if (NavigationBridge?.navigateToReactNativePage) {
        NavigationBridge.navigateToReactNativePage('HelpSupportPageComponent');
      } else {
        console.warn('[SettingsStore] NavigationBridge.navigateToHelpSupport not available');
      }
    }
  },

  navigateToPrivacyPolicy: () => {
    console.log('[SettingsStore] 导航到隐私政策页面');
    if (NavigationBridge?.navigateToPrivacyPolicy) {
      NavigationBridge.navigateToPrivacyPolicy();
    } else {
      if (NavigationBridge?.navigateToReactNativePage) {
        NavigationBridge.navigateToReactNativePage('PrivacyPolicyPageComponent');
      } else {
        console.warn('[SettingsStore] NavigationBridge.navigateToPrivacyPolicy not available');
      }
    }
  },

  navigateToFontSettings: () => {
    console.log('[SettingsStore] 导航到字体设置页面');
    // TODO: 实现导航逻辑
  },

  loadFollowSystemTheme: async () => {
    const { isInitialized } = get();
    if (isInitialized) {
      console.log('[SettingsStore] 跟随系统主题设置已初始化，跳过从原生获取');
      return;
    }

    try {
      const enabled = await isFollowSystemThemeNative();
      set({ followSystemTheme: enabled });
      console.log('[SettingsStore] 跟随系统主题设置已加载:', enabled);
    } catch (error) {
      console.error('[SettingsStore] 加载跟随系统主题设置失败:', error);
    }
  },

  // 退出登录
  logout: async () => {
    try {
      console.log('[SettingsStore] 开始退出登录');

      // 调用Android端清空token - 使用Promise方式
      if (SettingsBridge?.logout) {
        await new Promise<string>((resolve, reject) => {
          SettingsBridge.logout()
            .then((result: string) => {
              console.log('[SettingsStore] Android端退出登录成功:', result);
              resolve(result);
            })
            .catch((error: any) => {
              console.error('[SettingsStore] Android端退出登录失败:', error);
              reject(error);
            });
        });
      } else {
        console.warn('[SettingsStore] SettingsBridge.logout 不可用');
      }

      console.log('[SettingsStore] 退出登录完成');
    } catch (error) {
      console.error('[SettingsStore] 退出登录失败:', error);
      throw error;
    }
  },
}));

// 初始化时计算缓存大小和加载设置
setTimeout(() => {
  const store = useSettingsStore.getState();
  store.calculateCacheSize();
  store.initializeSettings(); // 加载所有设置状态
}, 1000);
