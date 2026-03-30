export {
  clearPageState,
  getPageState,
  savePageState,
} from './runtime/pageStateCache';
export {
  preloadSettings,
  preloadUserData,
  syncThemeFromNative,
} from './runtime/preload';
export const initializeApp = async () => {
  const { initializeAppRuntime } = await import('./runtime/runtimeCoordinator');
  return initializeAppRuntime();
};
export const initializeRNPage = async (pageName: string): Promise<void> => {
  const { initializeRNRuntimePage } = await import('./runtime/runtimeCoordinator');
  return initializeRNRuntimePage(pageName);
};
export const cleanupApp = (): void => {
  const { cleanupAppRuntime } = require('./runtime/runtimeCoordinator') as typeof import('./runtime/runtimeCoordinator');
  cleanupAppRuntime();
};
