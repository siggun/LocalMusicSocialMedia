import React, { useEffect } from 'react';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { useAuthStore } from '@/stores/authStore';
import { useProfileStore } from '@/stores/profileStore';
import { useRouter, useSegments } from 'expo-router';
import { theme } from '@/constants/theme';

export default function RootLayout() {
  const { isAuthenticated, isLoading, loadToken } = useAuthStore();
  const { hasProfile, fetchProfile } = useProfileStore();
  const router = useRouter();
  const segments = useSegments();

  useEffect(() => {
    loadToken();
  }, []);

  useEffect(() => {
    if (isLoading) return;

    const inAuth = segments[0] === '(auth)';
    const inOnboarding = segments[0] === '(onboarding)';

    if (!isAuthenticated && !inAuth) {
      router.replace('/(auth)/welcome');
    } else if (isAuthenticated && !hasProfile && !inOnboarding) {
      fetchProfile().then(() => {
        const profile = useProfileStore.getState();
        if (!profile.hasProfile) {
          router.replace('/(onboarding)/step1');
        }
      });
    } else if (isAuthenticated && hasProfile && (inAuth || inOnboarding)) {
      router.replace('/(tabs)/discover');
    }
  }, [isAuthenticated, isLoading, hasProfile, segments]);

  return (
    <>
      <StatusBar style="light" />
      <Stack
        screenOptions={{
          headerShown: false,
          contentStyle: { backgroundColor: theme.colors.background },
          animation: 'slide_from_right',
        }}
      />
    </>
  );
}
