const readingHistoryFixture = require('../fixtures/bridge/navigation-reading-history.json');

const loadNavigationBridgeModule = (nativeModule?: Record<string, unknown>) => {
  jest.resetModules();
  jest.doMock('react-native', () => ({
    NativeModules: {
      NavigationBridge: nativeModule,
    },
  }));

  return require('../../src/utils/bridge/NavigationBridge') as typeof import('../../src/utils/bridge/NavigationBridge');
};

describe('NavigationBridge contract', () => {
  afterEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('keeps getReadingHistory response shape stable for RN consumers', async () => {
    const getReadingHistory = jest
      .fn()
      .mockResolvedValue(readingHistoryFixture.nativeResponse);

    const { NavigationBridge } = loadNavigationBridgeModule({
      getReadingHistory,
    });

    await expect(NavigationBridge.getReadingHistory()).resolves.toEqual(
      readingHistoryFixture.nativeResponse,
    );
    expect(getReadingHistory).toHaveBeenCalledTimes(1);
  });

  it('provides a safe default getReadingHistory payload when native bridge is absent', async () => {
    const { NavigationBridge } = loadNavigationBridgeModule(undefined);

    await expect(NavigationBridge.getReadingHistory()).resolves.toEqual({
      historyItems: [],
      success: false,
    });
  });

  it('rounds AI numeric parameters before crossing the bridge', async () => {
    const aiExpand = jest.fn().mockResolvedValue('expanded');
    const aiCondense = jest.fn().mockResolvedValue('condensed');
    const aiContinue = jest.fn().mockResolvedValue('continued');

    const { NavigationBridge } = loadNavigationBridgeModule({
      aiExpand,
      aiCondense,
      aiContinue,
    });

    await expect(NavigationBridge.aiExpand('text', 149.6)).resolves.toBe('expanded');
    await expect(NavigationBridge.aiCondense('text', 2.4)).resolves.toBe('condensed');
    await expect(NavigationBridge.aiContinue('text', 199.5)).resolves.toBe('continued');

    expect(aiExpand).toHaveBeenCalledWith('text', 150);
    expect(aiCondense).toHaveBeenCalledWith('text', 2);
    expect(aiContinue).toHaveBeenCalledWith('text', 200);
  });

  it('uses stable default pagination for getAuthorBooks', async () => {
    const getAuthorBooks = jest.fn().mockResolvedValue({ list: [] });
    const { NavigationBridge } = loadNavigationBridgeModule({
      getAuthorBooks,
    });

    await expect(NavigationBridge.getAuthorBooks()).resolves.toEqual({ list: [] });
    expect(getAuthorBooks).toHaveBeenCalledWith(1, 50);
  });

  it('passes search navigation through the bridge when available', () => {
    const navigateToSearch = jest.fn();
    const { NavigationBridge } = loadNavigationBridgeModule({
      navigateToSearch,
    });

    NavigationBridge.navigateToSearch('');

    expect(navigateToSearch).toHaveBeenCalledWith('');
  });

  it('falls back to the legacy become-writer bridge when the new flag API is absent', () => {
    const navigateToBecomeWriterWithStatus = jest.fn();
    const { NavigationBridge } = loadNavigationBridgeModule({
      navigateToBecomeWriterWithStatus,
    });

    NavigationBridge.navigateToBecomeWriterWithFlag(true);

    expect(navigateToBecomeWriterWithStatus).toHaveBeenCalledTimes(1);
  });

  it('fails fast when getAuthorStatus bridge method is missing', async () => {
    const { NavigationBridge } = loadNavigationBridgeModule({});

    await expect(NavigationBridge.getAuthorStatus()).rejects.toBe(
      'getAuthorStatus not available',
    );
  });
});
