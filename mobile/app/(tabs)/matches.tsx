import React, { useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  Image,
  ActivityIndicator,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { useMatchStore } from '@/stores/matchStore';
import { theme } from '@/constants/theme';
import type { MatchResponse } from '@/types';

export default function MatchesScreen() {
  const router = useRouter();
  const { matches, isLoading, fetchMatches } = useMatchStore();

  useEffect(() => {
    fetchMatches();
  }, []);

  const renderMatch = ({ item }: { item: MatchResponse }) => {
    const profile = item.matchedUser;
    return (
      <TouchableOpacity
        style={styles.matchItem}
        onPress={() => router.push(`/chat/${item.matchId}`)}
        activeOpacity={0.7}
      >
        {profile.photoUrl ? (
          <Image source={{ uri: profile.photoUrl }} style={styles.avatar} />
        ) : (
          <View style={[styles.avatar, styles.avatarPlaceholder]}>
            <Ionicons name="person" size={24} color={theme.colors.textSecondary} />
          </View>
        )}
        <View style={styles.matchInfo}>
          <Text style={styles.matchName}>{profile.displayName}</Text>
          <View style={styles.tagRow}>
            {profile.instruments.slice(0, 2).map((inst) => (
              <Text key={inst.id} style={styles.miniTag}>
                {inst.icon} {inst.name}
              </Text>
            ))}
          </View>
        </View>
        <Text style={styles.matchDate}>
          {new Date(item.matchedAt).toLocaleDateString()}
        </Text>
      </TouchableOpacity>
    );
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
      <Text style={styles.screenTitle}>Matches</Text>

      {matches.length === 0 ? (
        <View style={styles.centered}>
          <Ionicons name="chatbubbles-outline" size={64} color={theme.colors.textSecondary} />
          <Text style={styles.emptyTitle}>No Matches Yet</Text>
          <Text style={styles.emptySubtitle}>
            Keep swiping to find musicians who want to jam with you
          </Text>
        </View>
      ) : (
        <FlatList
          data={matches}
          keyExtractor={(item) => item.matchId}
          renderItem={renderMatch}
          contentContainerStyle={styles.list}
          showsVerticalScrollIndicator={false}
        />
      )}
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
    paddingHorizontal: theme.spacing.xl,
  },
  screenTitle: {
    fontSize: theme.fontSize.xl,
    fontWeight: '800',
    color: theme.colors.text,
    marginBottom: theme.spacing.md,
    marginTop: theme.spacing.sm,
  },
  list: {
    paddingBottom: theme.spacing.xl,
  },
  matchItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    padding: theme.spacing.md,
    borderRadius: theme.borderRadius.lg,
    marginBottom: theme.spacing.sm,
  },
  avatar: {
    width: 56,
    height: 56,
    borderRadius: 28,
  },
  avatarPlaceholder: {
    backgroundColor: theme.colors.card,
    justifyContent: 'center',
    alignItems: 'center',
  },
  matchInfo: {
    flex: 1,
    marginLeft: theme.spacing.md,
  },
  matchName: {
    fontSize: theme.fontSize.md,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: 4,
  },
  tagRow: {
    flexDirection: 'row',
    gap: 6,
  },
  miniTag: {
    fontSize: theme.fontSize.xs,
    color: theme.colors.textSecondary,
    backgroundColor: theme.colors.card,
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: theme.borderRadius.full,
  },
  matchDate: {
    fontSize: theme.fontSize.xs,
    color: theme.colors.textSecondary,
  },
  emptyTitle: {
    fontSize: theme.fontSize.xl,
    fontWeight: '700',
    color: theme.colors.text,
    marginTop: theme.spacing.lg,
  },
  emptySubtitle: {
    fontSize: theme.fontSize.md,
    color: theme.colors.textSecondary,
    textAlign: 'center',
    marginTop: theme.spacing.sm,
  },
});
