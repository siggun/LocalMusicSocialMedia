import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useWizardStore } from '@/stores/wizardStore';
import * as api from '@/services/api';
import type { InstrumentDto, GenreDto } from '@/types';
import Chip from '@/components/Chip';
import Button from '@/components/Button';
import { theme } from '@/constants/theme';

const SKILL_LEVELS = ['BEGINNER', 'INTERMEDIATE', 'PRO'];

export default function Step2Music() {
  const router = useRouter();
  const { instrumentIds, genreIds, skillLevel, setMusic } = useWizardStore();
  const [instruments, setInstruments] = useState<InstrumentDto[]>([]);
  const [genres, setGenres] = useState<GenreDto[]>([]);
  const [selectedInstruments, setSelectedInstruments] = useState<number[]>(instrumentIds);
  const [selectedGenres, setSelectedGenres] = useState<number[]>(genreIds);
  const [selectedSkill, setSelectedSkill] = useState(skillLevel);

  useEffect(() => {
    Promise.all([api.getInstruments(), api.getGenres()])
      .then(([inst, gen]) => {
        setInstruments(inst);
        setGenres(gen);
      })
      .catch(() => Alert.alert('Error', 'Failed to load reference data'));
  }, []);

  const toggleInstrument = (id: number) => {
    setSelectedInstruments((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id],
    );
  };

  const toggleGenre = (id: number) => {
    setSelectedGenres((prev) =>
      prev.includes(id) ? prev.filter((g) => g !== id) : [...prev, id],
    );
  };

  const handleNext = () => {
    if (selectedInstruments.length === 0) {
      Alert.alert('Required', 'Select at least one instrument');
      return;
    }
    if (selectedGenres.length === 0) {
      Alert.alert('Required', 'Select at least one genre');
      return;
    }
    setMusic(selectedInstruments, selectedGenres, selectedSkill);
    router.push('/(onboarding)/step3');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.step}>Step 2 of 4</Text>
        <Text style={styles.title}>Your Music</Text>
        <Text style={styles.subtitle}>What do you play?</Text>
      </View>

      <ScrollView style={styles.scroll} showsVerticalScrollIndicator={false}>
        <Text style={styles.sectionTitle}>Instruments</Text>
        <View style={styles.chipGrid}>
          {instruments.map((inst) => (
            <Chip
              key={inst.id}
              label={`${inst.icon} ${inst.name}`}
              selected={selectedInstruments.includes(inst.id)}
              onPress={() => toggleInstrument(inst.id)}
            />
          ))}
        </View>

        <Text style={styles.sectionTitle}>Genres</Text>
        <View style={styles.chipGrid}>
          {genres.map((genre) => (
            <Chip
              key={genre.id}
              label={`${genre.icon} ${genre.name}`}
              selected={selectedGenres.includes(genre.id)}
              onPress={() => toggleGenre(genre.id)}
            />
          ))}
        </View>

        <Text style={styles.sectionTitle}>Skill Level</Text>
        <View style={styles.chipGrid}>
          {SKILL_LEVELS.map((level) => (
            <Chip
              key={level}
              label={level.charAt(0) + level.slice(1).toLowerCase()}
              selected={selectedSkill === level}
              onPress={() => setSelectedSkill(level)}
            />
          ))}
        </View>
      </ScrollView>

      <Button title="Next" onPress={handleNext} />
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
    marginBottom: theme.spacing.lg,
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
  scroll: {
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
});
