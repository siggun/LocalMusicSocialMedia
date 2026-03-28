import axios, { AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { API_BASE_URL } from '../constants/config';
import type {
  AuthResponse,
  MusicianProfile,
  CreateProfileRequest,
  DiscoveryProfile,
  SwipeResponse,
  MatchResponse,
  InstrumentDto,
  GenreDto,
  NotificationItem,
  ApiResponse,
  Page,
} from '../types';

let getToken: () => string | null = () => null;
let onUnauthorized: () => void = () => {};

export function setAuthInterceptors(
  tokenGetter: () => string | null,
  unauthorizedHandler: () => void,
) {
  getToken = tokenGetter;
  onUnauthorized = unauthorizedHandler;
}

const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken();
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      onUnauthorized();
    }
    return Promise.reject(error);
  },
);

function unwrap<T>(response: { data: ApiResponse<T> }): T {
  return response.data.data;
}

// Auth
export async function register(
  email: string,
  password: string,
  displayName: string,
): Promise<AuthResponse> {
  return unwrap(
    await api.post<ApiResponse<AuthResponse>>('/auth/register', {
      email,
      password,
      displayName,
    }),
  );
}

export async function login(
  email: string,
  password: string,
): Promise<AuthResponse> {
  return unwrap(
    await api.post<ApiResponse<AuthResponse>>('/auth/login', {
      email,
      password,
    }),
  );
}

// Profile
export async function getMyProfile(): Promise<MusicianProfile> {
  return unwrap(
    await api.get<ApiResponse<MusicianProfile>>('/profiles/me'),
  );
}

export async function createProfile(
  data: CreateProfileRequest,
): Promise<MusicianProfile> {
  return unwrap(
    await api.post<ApiResponse<MusicianProfile>>('/profiles', data),
  );
}

export async function updateProfile(
  data: Partial<CreateProfileRequest>,
): Promise<MusicianProfile> {
  return unwrap(
    await api.put<ApiResponse<MusicianProfile>>('/profiles/me', data),
  );
}

export async function uploadPhoto(
  formData: FormData,
): Promise<MusicianProfile> {
  return unwrap(
    await api.post<ApiResponse<MusicianProfile>>(
      '/profiles/me/photo',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } },
    ),
  );
}

export async function uploadAudio(
  formData: FormData,
): Promise<MusicianProfile> {
  return unwrap(
    await api.post<ApiResponse<MusicianProfile>>(
      '/profiles/me/audio',
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } },
    ),
  );
}

export async function updateLocation(
  latitude: number,
  longitude: number,
): Promise<MusicianProfile> {
  return unwrap(
    await api.put<ApiResponse<MusicianProfile>>('/profiles/me/location', {
      latitude,
      longitude,
    }),
  );
}

// Discovery
export async function getDiscoveryFeed(params?: {
  maxDistance?: number;
  genres?: number[];
  instruments?: number[];
  page?: number;
  size?: number;
}): Promise<Page<DiscoveryProfile>> {
  const queryParams: Record<string, string> = {};
  if (params?.maxDistance != null)
    queryParams.maxDistance = String(params.maxDistance);
  if (params?.genres?.length)
    queryParams.genres = params.genres.join(',');
  if (params?.instruments?.length)
    queryParams.instruments = params.instruments.join(',');
  if (params?.page != null) queryParams.page = String(params.page);
  if (params?.size != null) queryParams.size = String(params.size);

  return unwrap(
    await api.get<ApiResponse<Page<DiscoveryProfile>>>('/discovery', {
      params: queryParams,
    }),
  );
}

// Swipe
export async function swipe(
  targetUserId: string,
  direction: 'LEFT' | 'RIGHT',
): Promise<SwipeResponse> {
  return unwrap(
    await api.post<ApiResponse<SwipeResponse>>('/swipes', {
      targetUserId,
      direction,
    }),
  );
}

// Matches
export async function getMatches(): Promise<MatchResponse[]> {
  return unwrap(
    await api.get<ApiResponse<MatchResponse[]>>('/matches'),
  );
}

export async function unmatch(matchId: string): Promise<void> {
  await api.delete(`/matches/${matchId}`);
}

// Reference data
export async function getInstruments(): Promise<InstrumentDto[]> {
  return unwrap(
    await api.get<ApiResponse<InstrumentDto[]>>('/instruments'),
  );
}

export async function getGenres(): Promise<GenreDto[]> {
  return unwrap(await api.get<ApiResponse<GenreDto[]>>('/genres'));
}

// Notifications
export async function getNotifications(): Promise<NotificationItem[]> {
  return unwrap(
    await api.get<ApiResponse<NotificationItem[]>>('/notifications'),
  );
}

export async function getUnreadCount(): Promise<{ count: number }> {
  return unwrap(
    await api.get<ApiResponse<{ count: number }>>(
      '/notifications/unread-count',
    ),
  );
}

export async function markAllRead(): Promise<void> {
  await api.put('/notifications/read-all');
}

export default api;
