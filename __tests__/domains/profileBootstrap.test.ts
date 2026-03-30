import {
  bootstrapProfilePageData,
  bootstrapProfileUserState,
} from '../../src/page/ProfilePage/domain/profileBootstrap';

describe('profileBootstrap domain helpers', () => {
  test('initializes theme before loading home data when theme is not initialized', async () => {
    const calls: string[] = [];

    await bootstrapProfilePageData({
      isThemeInitialized: false,
      initializeTheme: jest.fn(async () => {
        calls.push('theme');
      }),
      loadHomeRecommendBooks: jest.fn(async () => {
        calls.push('home');
      }),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(calls).toEqual(['theme', 'home']);
  });

  test('falls back to loading home data when theme init fails', async () => {
    const loadHomeRecommendBooks = jest.fn().mockResolvedValue(undefined);

    await bootstrapProfilePageData({
      isThemeInitialized: false,
      initializeTheme: jest.fn().mockRejectedValue(new Error('theme failed')),
      loadHomeRecommendBooks,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadHomeRecommendBooks).toHaveBeenCalledTimes(1);
  });

  test('hydrates cached user state and native user payloads into the user domain', async () => {
    const handleNativeUserData = jest.fn();
    const setBalance = jest.fn();
    const setCoins = jest.fn();
    const setAuthorStatus = jest.fn();

    await bootstrapProfileUserState({
      initializeFromCache: jest.fn().mockResolvedValue(undefined),
      getCurrentUserData: jest.fn().mockResolvedValue({ uid: '1', nickname: 'tester' }),
      getUserBalance: jest.fn().mockResolvedValue(12),
      getUserCoins: jest.fn().mockResolvedValue(34),
      getAuthorStatus: jest.fn().mockResolvedValue({ isAuthor: true }),
      handleNativeUserData,
      setBalance,
      setCoins,
      setAuthorStatus,
    });

    expect(handleNativeUserData).toHaveBeenCalledWith({ uid: '1', nickname: 'tester' });
    expect(setBalance).toHaveBeenCalledWith(12);
    expect(setCoins).toHaveBeenCalledWith(34);
    expect(setAuthorStatus).toHaveBeenCalledWith(true);
  });
});
