import { nativeEventListener } from '../nativeEventListener';
import { initializeTheme } from '../theme/themeStore';
import {
  preloadSettings,
  preloadUserData,
  syncThemeFromNative,
} from './preload';
import {
  clearAllPageState,
  clearPageState,
  getPageState,
  savePageState,
} from './pageStateCache';

let appInitialized = false;
let themeCleanup: (() => void) | null = null;

export const initializeAppRuntime = async (): Promise<void> => {
  if (appInitialized) {
    return;
  }

  console.log('[RuntimeCoordinator] Initializing app runtime');

  nativeEventListener.init();
  themeCleanup = await initializeTheme();
  await preloadUserData();
  await preloadSettings();

  appInitialized = true;
};

export const initializeRNRuntimePage = async (pageName: string): Promise<void> => {
  console.log(`[RuntimeCoordinator] Initializing RN page: ${pageName}`);

  if (!appInitialized) {
    await initializeAppRuntime();
  }

  await syncThemeFromNative();
};

export const cleanupAppRuntime = (): void => {
  console.log('[RuntimeCoordinator] Cleaning up app runtime');

  themeCleanup?.();
  themeCleanup = null;
  nativeEventListener.cleanup();
  clearAllPageState();
  appInitialized = false;
};

export {
  clearPageState,
  getPageState,
  savePageState,
};
