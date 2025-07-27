import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

export interface UserState {
  uid: string | null;
  token: string | null;
  nickname: string | null;
  photo: string | null;
  sex: string | null;
  isLoggedIn: boolean;
  balance: number;
  coins: number;
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
    }),

    handleNativeUserData: (userData) => set((state) => {
      state.uid = userData.uid;
      state.token = userData.token;
      state.nickname = userData.nickname;
      state.photo = userData.photo;
      state.sex = userData.sex || null;
      state.isLoggedIn = true;
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
    }),
  }))
);
