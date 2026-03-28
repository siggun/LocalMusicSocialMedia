export const API_BASE_URL =
  process.env.EXPO_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1';

export const INSTRUMENTS: { id: number; name: string; icon: string }[] = [
  { id: 1, name: 'Electric Guitar', icon: '🎸' },
  { id: 2, name: 'Acoustic Guitar', icon: '🎸' },
  { id: 3, name: 'Bass Guitar', icon: '🎸' },
  { id: 4, name: 'Drums', icon: '🥁' },
  { id: 5, name: 'Piano', icon: '🎹' },
  { id: 6, name: 'Keyboard', icon: '🎹' },
  { id: 7, name: 'Vocals', icon: '🎤' },
  { id: 8, name: 'Violin', icon: '🎻' },
  { id: 9, name: 'Cello', icon: '🎻' },
  { id: 10, name: 'Saxophone', icon: '🎷' },
  { id: 11, name: 'Trumpet', icon: '🎺' },
  { id: 12, name: 'Flute', icon: '🪈' },
  { id: 13, name: 'Harmonica', icon: '🎵' },
  { id: 14, name: 'Banjo', icon: '🪕' },
  { id: 15, name: 'Ukulele', icon: '🪕' },
  { id: 16, name: 'Mandolin', icon: '🪕' },
  { id: 17, name: 'Trombone', icon: '🎺' },
  { id: 18, name: 'Clarinet', icon: '🎵' },
  { id: 19, name: 'DJ/Turntables', icon: '🎧' },
  { id: 20, name: 'Synthesizer', icon: '🎛️' },
];

export const GENRES: { id: number; name: string; icon: string }[] = [
  { id: 1, name: 'Rock', icon: '🎸' },
  { id: 2, name: 'Jazz', icon: '🎷' },
  { id: 3, name: 'Blues', icon: '🎵' },
  { id: 4, name: 'Classical', icon: '🎻' },
  { id: 5, name: 'Hip Hop', icon: '🎤' },
  { id: 6, name: 'R&B', icon: '🎶' },
  { id: 7, name: 'Country', icon: '🤠' },
  { id: 8, name: 'Electronic', icon: '🎛️' },
  { id: 9, name: 'Folk', icon: '🪕' },
  { id: 10, name: 'Metal', icon: '🤘' },
  { id: 11, name: 'Punk', icon: '💀' },
  { id: 12, name: 'Reggae', icon: '🏝️' },
  { id: 13, name: 'Latin', icon: '💃' },
  { id: 14, name: 'Pop', icon: '🎵' },
  { id: 15, name: 'Funk', icon: '🕺' },
  { id: 16, name: 'Soul', icon: '❤️' },
  { id: 17, name: 'Indie', icon: '🎹' },
  { id: 18, name: 'Alternative', icon: '🔊' },
  { id: 19, name: 'World', icon: '🌍' },
  { id: 20, name: 'Experimental', icon: '🔬' },
];

export const SKILL_LEVELS = ['BEGINNER', 'INTERMEDIATE', 'PRO'] as const;
export type SkillLevel = (typeof SKILL_LEVELS)[number];

export const AVAILABILITY_OPTIONS = [
  'WEEKDAY_MORNINGS',
  'WEEKDAY_EVENINGS',
  'WEEKENDS',
  'ANYTIME',
] as const;
export type AvailabilityOption = (typeof AVAILABILITY_OPTIONS)[number];
