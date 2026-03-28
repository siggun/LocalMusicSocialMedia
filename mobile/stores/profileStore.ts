import { create } from 'zustand';
import * as api from '../services/api';
import type { MusicianProfile, CreateProfileRequest } from '../types';

interface ProfileState {
  profile: MusicianProfile | null;
  isLoading: boolean;
  hasProfile: boolean;
}

interface ProfileActions {
  fetchProfile: () => Promise<void>;
  createProfile: (data: CreateProfileRequest) => Promise<void>;
  updateProfile: (data: Partial<CreateProfileRequest>) => Promise<void>;
  clearProfile: () => void;
}

export const useProfileStore = create<ProfileState & ProfileActions>(
  (set) => ({
    profile: null,
    isLoading: false,
    hasProfile: false,

    fetchProfile: async () => {
      set({ isLoading: true });
      try {
        const profile = await api.getMyProfile();
        set({ profile, hasProfile: true, isLoading: false });
      } catch (error: any) {
        if (error?.response?.status === 404) {
          set({ profile: null, hasProfile: false, isLoading: false });
        } else {
          set({ isLoading: false });
          throw error;
        }
      }
    },

    createProfile: async (data: CreateProfileRequest) => {
      set({ isLoading: true });
      try {
        const profile = await api.createProfile(data);
        set({ profile, hasProfile: true, isLoading: false });
      } catch (error) {
        set({ isLoading: false });
        throw error;
      }
    },

    updateProfile: async (data: Partial<CreateProfileRequest>) => {
      set({ isLoading: true });
      try {
        const profile = await api.updateProfile(data);
        set({ profile, hasProfile: true, isLoading: false });
      } catch (error) {
        set({ isLoading: false });
        throw error;
      }
    },

    clearProfile: () => {
      set({ profile: null, hasProfile: false, isLoading: false });
    },
  }),
);
