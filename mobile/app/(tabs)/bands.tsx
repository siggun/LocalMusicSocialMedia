import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import * as api from '@/services/api';
import type { BandResponse, JamEventResponse } from '@/types';
import { theme } from '@/constants/theme';

export default function BandsScreen() {
  const router = useRouter();
  const [bands, setBands] = useState<BandResponse[]>([]);
  const [jams, setJams] = useState<JamEventResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [bandsData, jamsData] = await Promise.all([
        api.getBands(),
        api.getJams(),
      ]);
      setBands(bandsData);
      setJams(jamsData);
    } catch {
      // Silently handle - empty state will show
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <SafeAreaView style={styles.centered}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.screenTitle}>Bands & Jams</Text>

      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>My Bands</Text>
          {bands.length === 0 ? (
            <View style={styles.emptyCard}>
              <Ionicons name="people-outline" size={48} color={theme.colors.textSecondary} />
              <Text style={styles.emptyText}>No bands yet</Text>
              <Text style={styles.emptySubtext}>
                Match with musicians and create a band from the chat screen
              </Text>
            </View>
          ) : (
            bands.map((band) => (
              <TouchableOpacity
                key={band.id}
                style={styles.card}
                onPress={() => router.push(`/band/${band.id}`)}
                activeOpacity={0.7}
              >
                <View style={styles.cardIcon}>
                  <Ionicons name="people" size={24} color={theme.colors.primary} />
                </View>
                <View style={styles.cardInfo}>
                  <Text style={styles.cardTitle}>{band.name}</Text>
                  <Text style={styles.cardSub}>
                    {band.members.length} member{band.members.length !== 1 ? 's' : ''}
                    {band.genre ? ` · ${band.genre}` : ''}
                  </Text>
                </View>
                <Ionicons name="chevron-forward" size={20} color={theme.colors.textSecondary} />
              </TouchableOpacity>
            ))
          )}
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Upcoming Jams</Text>
          {jams.length === 0 ? (
            <View style={styles.emptyCard}>
              <Ionicons name="calendar-outline" size={48} color={theme.colors.textSecondary} />
              <Text style={styles.emptyText}>No jam sessions</Text>
              <Text style={styles.emptySubtext}>
                Create a jam session to rehearse with your matches
              </Text>
            </View>
          ) : (
            jams.map((jam) => (
              <TouchableOpacity
                key={jam.id}
                style={styles.card}
                onPress={() => router.push(`/jam/${jam.id}`)}
                activeOpacity={0.7}
              >
                <View style={[styles.cardIcon, styles.jamIcon]}>
                  <Ionicons name="calendar" size={24} color={theme.colors.secondary} />
                </View>
                <View style={styles.cardInfo}>
                  <Text style={styles.cardTitle}>{jam.title}</Text>
                  <Text style={styles.cardSub}>
                    {new Date(jam.eventDate).toLocaleDateString(undefined, {
                      weekday: 'short',
                      month: 'short',
                      day: 'numeric',
                      hour: 'numeric',
                      minute: '2-digit',
                    })}
                    {jam.locationName ? ` · ${jam.locationName}` : ''}
                  </Text>
                  <Text style={styles.attendeeCount}>
                    {jam.attendees.filter((a) => a.status === 'GOING').length} going
                  </Text>
                </View>
                <Ionicons name="chevron-forward" size={20} color={theme.colors.textSecondary} />
              </TouchableOpacity>
            ))
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
    paddingHorizontal: theme.spacing.md,
  },
  centered: {
    flex: 1,
    backgroundColor: theme.colors.background,
    justifyContent: 'center',
    alignItems: 'center',
  },
  screenTitle: {
    fontSize: theme.fontSize.xl,
    fontWeight: '800',
    color: theme.colors.text,
    marginBottom: theme.spacing.md,
    marginTop: theme.spacing.sm,
  },
  section: {
    marginBottom: theme.spacing.xl,
  },
  sectionTitle: {
    fontSize: theme.fontSize.lg,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: theme.spacing.md,
  },
  emptyCard: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.lg,
    padding: theme.spacing.xl,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderStyle: 'dashed',
  },
  emptyText: {
    fontSize: theme.fontSize.md,
    fontWeight: '600',
    color: theme.colors.text,
    marginTop: theme.spacing.md,
  },
  emptySubtext: {
    fontSize: theme.fontSize.sm,
    color: theme.colors.textSecondary,
    textAlign: 'center',
    marginTop: theme.spacing.xs,
  },
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    padding: theme.spacing.md,
    borderRadius: theme.borderRadius.lg,
    marginBottom: theme.spacing.sm,
  },
  cardIcon: {
    width: 48,
    height: 48,
    borderRadius: 24,
    backgroundColor: theme.colors.primary + '20',
    justifyContent: 'center',
    alignItems: 'center',
  },
  jamIcon: {
    backgroundColor: theme.colors.secondary + '20',
  },
  cardInfo: {
    flex: 1,
    marginLeft: theme.spacing.md,
  },
  cardTitle: {
    fontSize: theme.fontSize.md,
    fontWeight: '700',
    color: theme.colors.text,
  },
  cardSub: {
    fontSize: theme.fontSize.sm,
    color: theme.colors.textSecondary,
    marginTop: 2,
  },
  attendeeCount: {
    fontSize: theme.fontSize.xs,
    color: theme.colors.success,
    fontWeight: '600',
    marginTop: 2,
  },
});
