const userBridgeFixture = require('../fixtures/bridge/user-bridge-current-user-data.json');

const loadUserBridgeModule = (nativeModule?: Record<string, unknown>) => {
  jest.resetModules();
  jest.doMock('react-native', () => ({
    NativeModules: {
      UserBridge: nativeModule,
    },
  }));

  return require('../../src/utils/bridge/UserBridge') as typeof import('../../src/utils/bridge/UserBridge');
};

describe('UserBridge contract', () => {
  afterEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('maps getCurrentUserData native payload to the RN contract shape', async () => {
    const getCurrentUserData = jest
      .fn()
      .mockResolvedValue(userBridgeFixture.nativeResponse);

    const { getCurrentUserData: loadCurrentUserData } = loadUserBridgeModule({
      getCurrentUserData,
    });

    await expect(loadCurrentUserData()).resolves.toEqual(
      userBridgeFixture.expectedJsResponse,
    );
    expect(getCurrentUserData).toHaveBeenCalledTimes(1);
  });

  it('fails closed when the native bridge is unavailable', async () => {
    const {
      getCurrentUserData,
      isUserLoggedIn,
      getUserBalance,
      getUserCoins,
    } = loadUserBridgeModule(undefined);

    await expect(getCurrentUserData()).resolves.toBeNull();
    await expect(isUserLoggedIn()).resolves.toBe(false);
    await expect(getUserBalance()).resolves.toBe(0);
    await expect(getUserCoins()).resolves.toBe(0);
  });

  it('reads balance and coins from the shared native balance payload', async () => {
    const getUserBalance = jest.fn().mockResolvedValue({
      balance: 25.5,
      coins: 12,
    });

    const {
      getUserBalance: loadUserBalance,
      getUserCoins,
    } = loadUserBridgeModule({
      getUserBalance,
    });

    await expect(loadUserBalance()).resolves.toBe(25.5);
    await expect(getUserCoins()).resolves.toBe(12);
    expect(getUserBalance).toHaveBeenCalledTimes(2);
  });
});
