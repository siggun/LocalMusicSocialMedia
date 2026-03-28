import React, { useState } from 'react';
import { View, Text, StyleSheet, Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import Slider from '@react-native-community/slider';
import * as Location from 'expo-location';
import { useWizardStore } from '@/stores/wizardStore';
import { useProfileStore } from '@/stores/profileStore';
import * as api from '@/services/api';
import Chip from '@/components/Chip';
import Button from '@/components/Button';
import { theme } from '@/constants/theme';

const AVAILABILITY_OPTIONS = [
  { key: 'WEEKDAY_MORNINGS', label: 'Weekday Mornings' },
  { key: 'WEEKDAY_EVENINGS', label: 'Weekday Evenings' },
  { key: 'WEEKENDS', label: 'Weekends' },
  { key: 'ANYTIME', label: 'Anytime' },
];

export default function Step4Preferences() {
  const router = useRouter();
  const wizard = useWizardStore();
  const { createProfile } = useProfileStore();
  const [availability, setAvailability] = useState<string[]>(wizard.availability);
  const [maxDistance, setMaxDistance] = useState(wizard.maxDistanceMiles);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const toggleAvailability = (key: string) => {
    setAvailability((prev) =>
      prev.includes(key) ? prev.filter((a) => a !== key) : [...prev, key],
    );
  };

  const handleFinish = async () => {
    setIsSubmitting(true);
    try {
      let lat = wizard.latitude;
      let lng = wizard.longitude;

      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status === 'granted') {
        const loc = await Location.getCurrentPositionAsync({});
        lat = loc.coords.latitude;
        lng = loc.coords.longitude;
      }

      wizard.setPreferences(availability, maxDistance, lat, lng);

      await createProfile({
        displayName: wizard.displayName,
        bio: wizard.bio,
        skillLevel: wizard.skillLevel,
        availability,
        instrumentIds: wizard.instrumentIds,
        genreIds: wizard.genreIds,
        latitude: lat ?? undefined,
        longitude: lng ?? undefined,
        maxDistanceMiles: maxDistance,
        soundcloudUrl: wizard.soundcloudUrl || undefined,
        spotifyUrl: wizard.spotifyUrl || undefined,
        youtubeUrl: wizard.youtubeUrl || undefined,
        bandcampUrl: wizard.bandcampUrl || undefined,
      });

      if (wizard.photoUri) {
        const formData = new FormData();
        formData.append('file', {
          uri: wizard.photoUri,
          name: 'photo.jpg',
          type: 'image/jpeg',
        } as any);
        await api.uploadPhoto(formData);
      }

      wizard.reset();
      router.replace('/(tabs)/discover');
    } catch (error: any) {
      Alert.alert('Error', error?.response?.data?.message || 'Failed to create profile');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.step}>Step 4 of 4</Text>
        <Text style={styles.title}>Preferences</Text>
        <Text style={styles.subtitle}>Set your availability and range</Text>
      </View>

      <View style={styles.content}>
        <Text style={styles.sectionTitle}>Availability</Text>
        <View style={styles.chipGrid}>
          {AVAILABILITY_OPTIONS.map((opt) => (
            <Chip
              key={opt.key}
              label={opt.label}
              selected={availability.includes(opt.key)}
              onPress={() => toggleAvailability(opt.key)}
            />
          ))}
        </View>

        <Text style={styles.sectionTitle}>Max Distance</Text>
        <Text style={styles.distanceValue}>{maxDistance} miles</Text>
        <Slider
          style={styles.slider}
          minimumValue={5}
          maximumValue={100}
          step={5}
          value={maxDistance}
          onValueChange={setMaxDistance}
          minimumTrackTintColor={theme.colors.primary}
          maximumTrackTintColor={theme.colors.border}
          thumbTintColor={theme.colors.primary}
        />
        <View style={styles.sliderLabels}>
          <Text style={styles.sliderLabel}>5 mi</Text>
          <Text style={styles.sliderLabel}>100 mi</Text>
        </View>
      </View>

      <Button title="Create Profile" onPress={handleFinish} loading={isSubmitting} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
    paddingHorizontal: theme.spacing.xl,
    paddingBottom: theme.spacing.xl,
  },
  header: {
    marginTop: theme.spacing.lg,
    marginBottom: theme.spacing.xl,
  },
  step: {
    fontSize: theme.fontSize.sm,
    color: theme.colors.primary,
    fontWeight: '600',
    marginBottom: theme.spacing.xs,
  },
  title: {
    fontSize: theme.fontSize.xxl,
    fontWeight: '800',
    color: theme.colors.text,
  },
  subtitle: {
    fontSize: theme.fontSize.md,
    color: theme.colors.textSecondary,
    marginTop: theme.spacing.xs,
  },
  content: {
    flex: 1,
  },
  sectionTitle: {
    fontSize: theme.fontSize.lg,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: theme.spacing.sm,
    marginTop: theme.spacing.md,
  },
  chipGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  distanceValue: {
    fontSize: theme.fontSize.xl,
    fontWeight: '700',
    color: theme.colors.primary,
    textAlign: 'center',
    marginBottom: theme.spacing.sm,
  },
  slider: {
    width: '100%',
    height: 40,
  },
  sliderLabels: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  sliderLabel: {
    color: theme.colors.textSecondary,
    fontSize: theme.fontSize.xs,
  },
});
