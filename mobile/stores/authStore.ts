import { create } from 'zustand';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as api from '../services/api';
import { setAuthInterceptors } from '../services/api';

const TOKEN_KEY = 'lyra_token';
const USER_ID_KEY = 'lyra_user_id';
const EMAIL_KEY = 'lyra_email';

interface AuthState {
  token: string | null;
  userId: string | null;
  email: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

interface AuthActions {
  login: (email: string, password: string) => Promise<void>;
  register: (
    email: string,
    password: string,
    displayName: string,
  ) => Promise<void>;
  logout: () => Promise<void>;
  loadToken: () => Promise<void>;
  setToken: (token: string, userId: string, email: string) => void;
}

export const useAuthStore = create<AuthState & AuthActions>((set, get) => {
  // Wire up interceptors so the API layer can read the token and handle 401s
  setAuthInterceptors(
    () => get().token,
    () => {
      get().logout();
    },
  );

  return {
    token: null,
    userId: null,
    email: null,
    isAuthenticated: false,
    isLoading: true,

    login: async (email: string, password: string) => {
      set({ isLoading: true });
      try {
        const response = await api.login(email, password);
        await AsyncStorage.multiSet([
          [TOKEN_KEY, response.token],
          [USER_ID_KEY, response.userId],
          [EMAIL_KEY, response.email],
        ]);
        set({
          token: response.token,
          userId: response.userId,
          email: response.email,
          isAuthenticated: true,
          isLoading: false,
        });
      } catch (error) {
        set({ isLoading: false });
        throw error;
      }
    },

    register: async (
      email: string,
      password: string,
      displayName: string,
    ) => {
      set({ isLoading: true });
      try {
        const response = await api.register(email, password, displayName);
        await AsyncStorage.multiSet([
          [TOKEN_KEY, response.token],
          [USER_ID_KEY, response.userId],
          [EMAIL_KEY, response.email],
        ]);
        set({
          token: response.token,
          userId: response.userId,
          email: response.email,
          isAuthenticated: true,
          isLoading: false,
        });
      } catch (error) {
        set({ isLoading: false });
        throw error;
      }
    },

    logout: async () => {
      await AsyncStorage.multiRemove([TOKEN_KEY, USER_ID_KEY, EMAIL_KEY]);
      set({
        token: null,
        userId: null,
        email: null,
        isAuthenticated: false,
        isLoading: false,
      });
    },

    loadToken: async () => {
      set({ isLoading: true });
      try {
        const values = await AsyncStorage.multiGet([
          TOKEN_KEY,
          USER_ID_KEY,
          EMAIL_KEY,
        ]);
        const token = values[0][1];
        const userId = values[1][1];
        const email = values[2][1];

        if (token && userId) {
          set({
            token,
            userId,
            email,
            isAuthenticated: true,
            isLoading: false,
          });
        } else {
          set({ isLoading: false });
        }
      } catch {
        set({ isLoading: false });
      }
    },

    setToken: (token: string, userId: string, email: string) => {
      AsyncStorage.multiSet([
        [TOKEN_KEY, token],
        [USER_ID_KEY, userId],
        [EMAIL_KEY, email],
      ]);
      set({
        token,
        userId,
        email,
        isAuthenticated: true,
        isLoading: false,
      });
    },
  };
});
