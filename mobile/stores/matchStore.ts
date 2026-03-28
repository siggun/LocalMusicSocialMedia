import { create } from 'zustand';
import * as api from '../services/api';
import type { MatchResponse } from '../types';

interface MatchState {
  matches: MatchResponse[];
  isLoading: boolean;
}

interface MatchActions {
  fetchMatches: () => Promise<void>;
  removeMatch: (matchId: string) => Promise<void>;
}

export const useMatchStore = create<MatchState & MatchActions>((set, get) => ({
  matches: [],
  isLoading: false,

  fetchMatches: async () => {
    set({ isLoading: true });
    try {
      const matches = await api.getMatches();
      set({ matches, isLoading: false });
    } catch (error) {
      set({ isLoading: false });
      throw error;
    }
  },

  removeMatch: async (matchId: string) => {
    try {
      await api.unmatch(matchId);
      set((state) => ({
        matches: state.matches.filter((m) => m.matchId !== matchId),
      }));
    } catch (error) {
      throw error;
    }
  },
}));
