import { NativeModules } from 'react-native';

const { UserBridge } = NativeModules;

/**
 * 用户数据桥接模块
 * 提供与Android端UserBridgeModule的通信接口
 */
export interface UserData {
  uid: string;
  token: string;
  nickname: string;
  photo: string;
  sex?: string;
  isLoggedIn: boolean;
  balance: number;
  coins: number;
}

/**
 * 获取当前用户数据
 * @returns Promise<UserData | null> 用户数据
 */
export const getCurrentUserData = async (): Promise<UserData | null> => {
  return new Promise((resolve, reject) => {
    if (UserBridge?.getCurrentUserData) {
      UserBridge.getCurrentUserData()
        .then((result: any) => {
          if (result) {
            // 转换Android端返回的数据格式
            const userData: UserData = {
              uid: result.uid,
              token: result.token,
              nickname: result.nickname,
              photo: result.photo,
              sex: result.sex,
              isLoggedIn: true, // 如果有数据说明已登录
              balance: typeof result.balance === 'number' ? result.balance : 0,
              coins: typeof result.coins === 'number' ? result.coins : 0,
            };
            resolve(userData);
          } else {
            resolve(null);
          }
        })
        .catch((error: any) => {
          reject(new Error(error.message || '获取用户数据失败'));
        });
    } else {
      // 模拟数据（当在纯RN环境中运行时）
      resolve({
        uid: 'mock_uid',
        token: 'mock_token',
        nickname: '测试用户',
        photo: '',
        sex: 'unknown',
        isLoggedIn: false,
        balance: 0,
        coins: 0,
      });
    }
  });
};

/**
 * 检查用户登录状态
 * @returns Promise<boolean> 是否已登录
 */
export const isUserLoggedIn = async (): Promise<boolean> => {
  return new Promise((resolve, reject) => {
    if (UserBridge?.isUserLoggedIn) {
      UserBridge.isUserLoggedIn()
        .then((result: boolean) => {
          resolve(result);
        })
        .catch((error: any) => {
          reject(new Error(error.message || '检查登录状态失败'));
        });
    } else {
      // 模拟数据
      resolve(false);
    }
  });
};

/**
 * 获取用户余额
 * @returns Promise<number> 用户余额
 */
export const getUserBalance = async (): Promise<number> => {
  return new Promise((resolve, reject) => {
    if (UserBridge?.getUserBalance) {
      UserBridge.getUserBalance()
        .then((result: any) => {
          resolve(result.balance || 0);
        })
        .catch((error: any) => {
          reject(new Error(error.message || '获取余额失败'));
        });
    } else {
      // 模拟数据
      resolve(0);
    }
  });
};

/**
 * 获取用户金币
 * @returns Promise<number> 用户金币
 */
export const getUserCoins = async (): Promise<number> => {
  return new Promise((resolve, reject) => {
    if (UserBridge?.getUserBalance) {
      UserBridge.getUserBalance()
        .then((result: any) => {
          resolve(result.coins || 0);
        })
        .catch((error: any) => {
          reject(new Error(error.message || '获取金币失败'));
        });
    } else {
      // 模拟数据
      resolve(0);
    }
  });
};

export default {
  getCurrentUserData,
  isUserLoggedIn,
  getUserBalance,
  getUserCoins,
};
