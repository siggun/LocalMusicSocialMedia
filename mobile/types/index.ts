export interface User {
  id: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  userId: string;
  email: string;
}

export interface InstrumentDto {
  id: number;
  name: string;
  icon: string;
}

export interface GenreDto {
  id: number;
  name: string;
  icon: string;
}

export interface MusicianProfile {
  id: string;
  userId: string;
  displayName: string;
  bio: string;
  skillLevel: string;
  availability: string[];
  photoUrl: string | null;
  audioIntroUrl: string | null;
  latitude: number | null;
  longitude: number | null;
  maxDistanceMiles: number | null;
  instruments: InstrumentDto[];
  genres: GenreDto[];
  soundcloudUrl: string | null;
  spotifyUrl: string | null;
  youtubeUrl: string | null;
  bandcampUrl: string | null;
  createdAt: string;
}

export interface DiscoveryProfile extends MusicianProfile {
  distanceMiles: number;
  compatibilityScore: number;
}

export interface MatchResponse {
  matchId: string;
  matchedUser: MusicianProfile;
  matchedAt: string;
}

export interface SwipeRequest {
  targetUserId: string;
  direction: 'LEFT' | 'RIGHT';
}

export interface SwipeResponse {
  matched: boolean;
  matchId: string | null;
}

export interface CreateProfileRequest {
  displayName: string;
  bio: string;
  skillLevel: string;
  availability: string[];
  instrumentIds: number[];
  genreIds: number[];
  latitude?: number;
  longitude?: number;
  maxDistanceMiles?: number;
  soundcloudUrl?: string;
  spotifyUrl?: string;
  youtubeUrl?: string;
  bandcampUrl?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string | null;
  data: T;
}

export interface NotificationItem {
  id: string;
  type: string;
  title: string;
  body: string;
  dataJson: string | null;
  isRead: boolean;
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}

export interface BandMemberInfo {
  userId: string;
  displayName: string;
  role: string;
  joinedAt: string;
}

export interface BandResponse {
  id: string;
  name: string;
  genre: string | null;
  bio: string | null;
  photoUrl: string | null;
  createdBy: string;
  members: BandMemberInfo[];
  createdAt: string;
}

export interface CreateBandRequest {
  name: string;
  genre?: string;
  bio?: string;
  memberUserIds?: string[];
}

export type RsvpStatus = 'GOING' | 'MAYBE' | 'DECLINED';

export interface JamAttendeeInfo {
  userId: string;
  displayName: string;
  status: RsvpStatus;
  respondedAt: string;
}

export interface JamEventResponse {
  id: string;
  title: string;
  description: string | null;
  locationName: string | null;
  latitude: number | null;
  longitude: number | null;
  eventDate: string;
  createdBy: string;
  createdByName: string;
  bandId: string | null;
  bandName: string | null;
  attendees: JamAttendeeInfo[];
  createdAt: string;
}

export interface CreateJamRequest {
  title: string;
  description?: string;
  locationName?: string;
  latitude?: number;
  longitude?: number;
  eventDate: string;
  bandId?: string;
}
