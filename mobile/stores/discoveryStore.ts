import { create } from 'zustand';
import * as api from '../services/api';
import type { DiscoveryProfile, SwipeResponse } from '../types';

interface DiscoveryFilters {
  maxDistance: number;
  genres: number[];
  instruments: number[];
  skillLevel: string | null;
}

interface DiscoveryState {
  profiles: DiscoveryProfile[];
  currentIndex: number;
  isLoading: boolean;
  filters: DiscoveryFilters;
}

interface DiscoveryActions {
  fetchFeed: () => Promise<void>;
  swipeLeft: (targetUserId: string) => Promise<void>;
  swipeRight: (targetUserId: string) => Promise<SwipeResponse>;
  setFilters: (filters: Partial<DiscoveryFilters>) => void;
  resetDeck: () => void;
}

export const useDiscoveryStore = create<DiscoveryState & DiscoveryActions>(
  (set, get) => ({
    profiles: [],
    currentIndex: 0,
    isLoading: false,
    filters: {
      maxDistance: 25,
      genres: [],
      instruments: [],
      skillLevel: null,
    },

    fetchFeed: async () => {
      set({ isLoading: true });
      try {
        const { filters } = get();
        const page = await api.getDiscoveryFeed({
          maxDistance: filters.maxDistance,
          genres: filters.genres.length > 0 ? filters.genres : undefined,
          instruments:
            filters.instruments.length > 0
              ? filters.instruments
              : undefined,
        });
        set({ profiles: page.content, currentIndex: 0, isLoading: false });
      } catch (error) {
        set({ isLoading: false });
        throw error;
      }
    },

    swipeLeft: async (targetUserId: string) => {
      try {
        await api.swipe(targetUserId, 'LEFT');
        set((state) => ({ currentIndex: state.currentIndex + 1 }));
      } catch (error) {
        throw error;
      }
    },

    swipeRight: async (targetUserId: string) => {
      try {
        const response = await api.swipe(targetUserId, 'RIGHT');
        set((state) => ({ currentIndex: state.currentIndex + 1 }));
        return response;
      } catch (error) {
        throw error;
      }
    },

    setFilters: (newFilters: Partial<DiscoveryFilters>) => {
      set((state) => ({
        filters: { ...state.filters, ...newFilters },
      }));
    },

    resetDeck: () => {
      set({ currentIndex: 0 });
    },
  }),
);
