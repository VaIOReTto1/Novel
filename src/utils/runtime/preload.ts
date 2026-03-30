import { useThemeStore } from '../theme/themeStore';
import { getCurrentUserData } from '../bridge/UserBridge';

export const syncThemeFromNative = async (): Promise<void> => {
  try {
    console.log('[RuntimePreload] Syncing theme from native');

    const themeStore = useThemeStore.getState();
    if (themeStore.initializeFromNative) {
      await themeStore.initializeFromNative();
    }
  } catch (error) {
    console.error('[RuntimePreload] Failed to sync theme from native:', error);
  }
};

export const preloadUserData = async (): Promise<void> => {
  try {
    console.log('[RuntimePreload] Preloading user data');

    const { useUserStore } = await import('../../page/ProfilePage/store/userStore');
    const currentUserState = useUserStore.getState();

    if (currentUserState.isLoggedIn && currentUserState.uid) {
      return;
    }

    const userData = await getCurrentUserData();
    if (userData) {
      useUserStore.getState().handleNativeUserData(userData);
    }
  } catch (error) {
    console.error('[RuntimePreload] Failed to preload user data:', error);
  }
};

export const preloadSettings = async (): Promise<void> => {
  try {
    console.log('[RuntimePreload] Preloading settings');

    const { useSettingsStore } = await import('../../page/SettingsPage/settingspage/store/settingsStore');
    const { loadFollowSystemTheme, loadAutoSwitchNightMode } = useSettingsStore.getState();

    await Promise.all([
      loadFollowSystemTheme(),
      loadAutoSwitchNightMode(),
    ]);
  } catch (error) {
    console.error('[RuntimePreload] Failed to preload settings:', error);
  }
};
