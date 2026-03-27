import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import AsyncStorage from '@react-native-async-storage/async-storage';

export interface UserState {
  uid: string | null;
  token: string | null;
  nickname: string | null;
  photo: string | null;
  sex: string | null; // 0/1 字符串
  isLoggedIn: boolean;
  balance: number;
  coins: number;
  isAuthor: boolean;
}

interface UserActions {
  loginSuccess: (userData: {
    uid: string;
    token: string;
    nickname: string;
    photo: string;
    sex?: string;
  }) => void;
  handleNativeUserData: (userData: {
    uid: string;
    token: string;
    nickname: string;
    photo: string;
    sex?: string;
  }) => void;
  logout: () => void;
  initializeFromCache: () => Promise<void>;
  setAuthorStatus: (isAuthor: boolean) => void;
  setBalance: (balance: number) => void;
  setCoins: (coins: number) => void;
}

type UserStore = UserState & UserActions;

const initialState: UserState = {
  uid: null,
  token: null,
  nickname: null,
  photo: null,
  sex: null,
  isLoggedIn: false,
  balance: 0.00,
  coins: 0,
  isAuthor: false,
};

const persistUserCache = (operation: Promise<unknown>) => {
  operation.catch(() => undefined);
};

export const useUserStore = create<UserStore>()(
  immer((set) => ({
    ...initialState,

    loginSuccess: (userData) => set((state) => {
      state.uid = userData.uid;
      state.token = userData.token;
      state.nickname = userData.nickname;
      state.photo = userData.photo;
      state.sex = userData.sex || null;
      state.isLoggedIn = true;
      // 异步缓存
      persistUserCache(AsyncStorage.setItem('NOVEL_USER_CACHE', JSON.stringify({
        uid: state.uid,
        token: state.token,
        nickname: state.nickname,
        photo: state.photo,
        sex: state.sex,
        isLoggedIn: state.isLoggedIn,
        balance: state.balance,
        coins: state.coins,
        isAuthor: state.isAuthor,
      })));
    }),

    handleNativeUserData: (userData) => set((state) => {
      state.uid = userData.uid;
      state.token = userData.token;
      state.nickname = userData.nickname;
      state.photo = userData.photo;
      state.sex = userData.sex || null;
      state.isLoggedIn = true;
      // 异步缓存
      persistUserCache(AsyncStorage.setItem('NOVEL_USER_CACHE', JSON.stringify({
        uid: state.uid,
        token: state.token,
        nickname: state.nickname,
        photo: state.photo,
        sex: state.sex,
        isLoggedIn: state.isLoggedIn,
        balance: state.balance,
        coins: state.coins,
        isAuthor: state.isAuthor,
      })));
    }),

    logout: () => set((state) => {
      state.uid = null;
      state.token = null;
      state.nickname = null;
      state.photo = null;
      state.sex = null;
      state.isLoggedIn = false;
      state.balance = 0.00;
      state.coins = 0;
      state.isAuthor = false;
      persistUserCache(AsyncStorage.removeItem('NOVEL_USER_CACHE'));
    }),

    initializeFromCache: async () => {
      try {
        const raw = await AsyncStorage.getItem('NOVEL_USER_CACHE');
        if (raw) {
          const cached = JSON.parse(raw);
          set((state) => {
            state.uid = cached.uid ?? state.uid;
            state.token = cached.token ?? state.token;
            state.nickname = cached.nickname ?? state.nickname;
            state.photo = cached.photo ?? state.photo;
            state.sex = cached.sex ?? state.sex;
            state.isLoggedIn = cached.isLoggedIn ?? state.isLoggedIn;
            state.balance = typeof cached.balance === 'number' ? cached.balance : state.balance;
            state.coins = typeof cached.coins === 'number' ? cached.coins : state.coins;
            state.isAuthor = !!cached.isAuthor;
          });
        }
      } catch (e) {
        // ignore cache errors
      }
    },

    setAuthorStatus: (isAuthor) => set((state) => {
      state.isAuthor = isAuthor;
      // 更新缓存
      persistUserCache(AsyncStorage.mergeItem('NOVEL_USER_CACHE', JSON.stringify({ isAuthor })));
    }),

    setBalance: (balance) => set((state) => {
      state.balance = balance;
      persistUserCache(AsyncStorage.mergeItem('NOVEL_USER_CACHE', JSON.stringify({ balance })));
    }),

    setCoins: (coins) => set((state) => {
      state.coins = coins;
      persistUserCache(AsyncStorage.mergeItem('NOVEL_USER_CACHE', JSON.stringify({ coins })));
    }),
  }))
);
