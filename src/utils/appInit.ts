import { nativeEventListener } from './nativeEventListener';
import { useThemeStore } from './theme/themeStore';

// 模块级别的页面状态缓存
const pageStateCache: Record<string, any> = {};

/**
 * 保存页面状态
 */
export function savePageState(pageId: string, state: any) {
  console.log(`[AppInit] Saving state for page: ${pageId}`);
  pageStateCache[pageId] = state;
}

/**
 * 获取页面状态
 */
export function getPageState(pageId: string): any {
  const state = pageStateCache[pageId];
  console.log(`[AppInit] Restoring state for page: ${pageId}`, state);
  return state;
}

/**
 * 清除页面状态
 */
export function clearPageState(pageId: string) {
  delete pageStateCache[pageId];
  console.log(`[AppInit] Cleared state for page: ${pageId}`);
}

/**
 * 同步主题状态从原生端
 * 用于RN页面加载时主动获取最新主题
 */
export async function syncThemeFromNative(): Promise<void> {
  try {
    console.log('[AppInit] 🎯 开始同步主题状态从原生端');

    const themeStore = useThemeStore.getState();
    if (themeStore.initializeFromNative) {
      await themeStore.initializeFromNative();
      console.log('[AppInit] ✅ 主题状态同步完成');
    } else {
      console.warn('[AppInit] ⚠️ initializeFromNative方法不可用');
    }
  } catch (error) {
    console.error('[AppInit] ❌ 同步主题状态失败:', error);
  }
}

/**
 * 初始化应用
 * 在应用启动时调用，设置必要的监听器和配置
 */
export function initializeApp() {
  console.log('[AppInit] Initializing application...');

  try {
    // 初始化原生事件监听器
    nativeEventListener.init();

    console.log('[AppInit] Application initialized successfully');
  } catch (error) {
    console.error('[AppInit] Failed to initialize application:', error);
  }
}

/**
 * 初始化RN页面
 * 在RN页面加载时调用，确保主题状态同步
 */
export async function initializeRNPage(pageName: string): Promise<void> {
  console.log(`[AppInit] 初始化RN页面: ${pageName}`);

  try {
    // 主动同步主题状态
    await syncThemeFromNative();
    console.log(`[AppInit] ✅ RN页面 ${pageName} 初始化完成`);
  } catch (error) {
    console.error(`[AppInit] ❌ RN页面 ${pageName} 初始化失败:`, error);
  }
}

/**
 * 清理应用资源
 * 在应用卸载时调用
 */
export function cleanupApp() {
  console.log('[AppInit] Cleaning up application...');

  try {
    // 清理原生事件监听器
    nativeEventListener.cleanup();

    // 清理页面状态缓存
    Object.keys(pageStateCache).forEach(key => {
      delete pageStateCache[key];
    });

    console.log('[AppInit] Application cleaned up successfully');
  } catch (error) {
    console.error('[AppInit] Failed to cleanup application:', error);
  }
}

// 导入RN页面组件，确保它们被注册
import '../page/SettingsPage/settingspage/SettingsPageComponent';
import '../page/SettingsPage/TimeSwitchPage/TimedSwitchPageComponent';
import '../page/SettingsPage/helpsupportPage/HelpSupportPageComponent';
import '../page/SettingsPage/privacypolicyPage/PrivacyPolicyPageComponent';
import '../page/ScrollBox/HistoryPage/HistoryPageComponent';
