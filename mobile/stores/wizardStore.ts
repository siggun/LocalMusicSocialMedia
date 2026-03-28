import { create } from 'zustand';

interface WizardState {
  displayName: string;
  bio: string;
  photoUri: string | null;
  instrumentIds: number[];
  genreIds: number[];
  skillLevel: string;
  availability: string[];
  soundcloudUrl: string;
  spotifyUrl: string;
  youtubeUrl: string;
  bandcampUrl: string;
  latitude: number | null;
  longitude: number | null;
  maxDistanceMiles: number;

  setBasics: (displayName: string, bio: string, photoUri: string | null) => void;
  setMusic: (instrumentIds: number[], genreIds: number[], skillLevel: string) => void;
  setMedia: (soundcloudUrl: string, spotifyUrl: string, youtubeUrl: string, bandcampUrl: string) => void;
  setPreferences: (availability: string[], maxDistanceMiles: number, latitude: number | null, longitude: number | null) => void;
  reset: () => void;
}

const initialState = {
  displayName: '',
  bio: '',
  photoUri: null as string | null,
  instrumentIds: [] as number[],
  genreIds: [] as number[],
  skillLevel: 'INTERMEDIATE',
  availability: [] as string[],
  soundcloudUrl: '',
  spotifyUrl: '',
  youtubeUrl: '',
  bandcampUrl: '',
  latitude: null as number | null,
  longitude: null as number | null,
  maxDistanceMiles: 25,
};

export const useWizardStore = create<WizardState>((set) => ({
  ...initialState,

  setBasics: (displayName, bio, photoUri) =>
    set({ displayName, bio, photoUri }),

  setMusic: (instrumentIds, genreIds, skillLevel) =>
    set({ instrumentIds, genreIds, skillLevel }),

  setMedia: (soundcloudUrl, spotifyUrl, youtubeUrl, bandcampUrl) =>
    set({ soundcloudUrl, spotifyUrl, youtubeUrl, bandcampUrl }),

  setPreferences: (availability, maxDistanceMiles, latitude, longitude) =>
    set({ availability, maxDistanceMiles, latitude, longitude }),

  reset: () => set(initialState),
}));
