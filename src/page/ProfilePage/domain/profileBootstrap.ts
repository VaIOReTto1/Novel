type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapProfilePageDataDeps = {
  isThemeInitialized: boolean;
  initializeTheme: () => Promise<void>;
  loadHomeRecommendBooks: () => Promise<void>;
  logger?: Logger;
};

type BootstrapProfileUserStateDeps = {
  initializeFromCache: () => Promise<void>;
  getCurrentUserData: () => Promise<unknown>;
  getUserBalance: () => Promise<unknown>;
  getUserCoins: () => Promise<unknown>;
  getAuthorStatus: () => Promise<{ isAuthor?: boolean } | null | undefined>;
  handleNativeUserData: (userData: unknown) => void;
  setBalance: (balance: number) => void;
  setCoins: (coins: number) => void;
  setAuthorStatus: (isAuthor: boolean) => void;
};

const defaultLogger: Logger = console;

export const bootstrapProfilePageData = async ({
  isThemeInitialized,
  initializeTheme,
  loadHomeRecommendBooks,
  logger = defaultLogger,
}: BootstrapProfilePageDataDeps): Promise<void> => {
  try {
    if (!isThemeInitialized) {
      logger.log('[ProfilePage] Initializing theme from native');
      await initializeTheme();
    } else {
      logger.log('[ProfilePage] Theme already initialized');
    }

    await loadHomeRecommendBooks();
  } catch (error) {
    logger.error('[ProfilePage] Page bootstrap failed', error);
    await loadHomeRecommendBooks();
  }
};

export const bootstrapProfileUserState = async ({
  initializeFromCache,
  getCurrentUserData,
  getUserBalance,
  getUserCoins,
  getAuthorStatus,
  handleNativeUserData,
  setBalance,
  setCoins,
  setAuthorStatus,
}: BootstrapProfileUserStateDeps): Promise<void> => {
  try {
    await initializeFromCache();

    const [userData, balance, coins] = await Promise.all([
      getCurrentUserData().catch(() => null),
      getUserBalance().catch(() => 0),
      getUserCoins().catch(() => 0),
    ]);

    if (userData) {
      handleNativeUserData(userData);
    }

    if (typeof balance === 'number') {
      setBalance(balance);
    }

    if (typeof coins === 'number') {
      setCoins(coins);
    }

    try {
      const status = await getAuthorStatus();
      if (status && typeof status.isAuthor === 'boolean') {
        setAuthorStatus(status.isAuthor);
      }
    } catch {
      // ignore author status bootstrap failures
    }
  } catch {
    // ignore profile bootstrap failures to keep page usable
  }
};
