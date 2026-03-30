const mockInitializeTheme = jest.fn().mockResolvedValue(jest.fn());
const mockInitNativeEventListener = jest.fn();
const mockCleanupNativeEventListener = jest.fn();
const mockPreloadUserData = jest.fn().mockResolvedValue(undefined);
const mockPreloadSettings = jest.fn().mockResolvedValue(undefined);

jest.mock('../../src/utils/theme/themeStore', () => ({
  initializeTheme: () => mockInitializeTheme(),
}));

jest.mock('../../src/utils/nativeEventListener', () => ({
  nativeEventListener: {
    init: () => mockInitNativeEventListener(),
    cleanup: () => mockCleanupNativeEventListener(),
  },
}));

jest.mock('../../src/utils/runtime/preload', () => ({
  preloadUserData: () => mockPreloadUserData(),
  preloadSettings: () => mockPreloadSettings(),
}));

describe('RN runtime coordinator', () => {
  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('initializes native listeners, theme, and preloads exactly once', async () => {
    const {
      initializeAppRuntime,
      cleanupAppRuntime,
    } = require('../../src/utils/runtime/runtimeCoordinator') as typeof import('../../src/utils/runtime/runtimeCoordinator');

    await initializeAppRuntime();
    await initializeAppRuntime();

    expect(mockInitNativeEventListener).toHaveBeenCalledTimes(1);
    expect(mockInitializeTheme).toHaveBeenCalledTimes(1);
    expect(mockPreloadUserData).toHaveBeenCalledTimes(1);
    expect(mockPreloadSettings).toHaveBeenCalledTimes(1);

    cleanupAppRuntime();
    expect(mockCleanupNativeEventListener).toHaveBeenCalledTimes(1);
  });
});
