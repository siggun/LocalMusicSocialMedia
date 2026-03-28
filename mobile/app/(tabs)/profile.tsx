import React, { useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
  ActivityIndicator,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useProfileStore } from '@/stores/profileStore';
import { useAuthStore } from '@/stores/authStore';
import Button from '@/components/Button';
import { theme } from '@/constants/theme';

export default function ProfileScreen() {
  const { profile, isLoading, fetchProfile } = useProfileStore();
  const { logout, email } = useAuthStore();

  useEffect(() => {
    fetchProfile();
  }, []);

  if (isLoading || !profile) {
    return (
      <SafeAreaView style={styles.centered}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.header}>
          {profile.photoUrl ? (
            <Image source={{ uri: profile.photoUrl }} style={styles.avatar} />
          ) : (
            <View style={[styles.avatar, styles.avatarPlaceholder]}>
              <Ionicons name="person" size={48} color={theme.colors.textSecondary} />
            </View>
          )}
          <Text style={styles.name}>{profile.displayName}</Text>
          <Text style={styles.email}>{email}</Text>
          <View style={styles.skillBadge}>
            <Text style={styles.skillText}>{profile.skillLevel}</Text>
          </View>
        </View>

        {profile.bio ? (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Bio</Text>
            <Text style={styles.bioText}>{profile.bio}</Text>
          </View>
        ) : null}

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Instruments</Text>
          <View style={styles.tagRow}>
            {profile.instruments.map((inst) => (
              <View key={inst.id} style={styles.tag}>
                <Text style={styles.tagText}>{inst.icon} {inst.name}</Text>
              </View>
            ))}
          </View>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Genres</Text>
          <View style={styles.tagRow}>
            {profile.genres.map((g) => (
              <View key={g.id} style={[styles.tag, styles.genreTag]}>
                <Text style={styles.tagText}>{g.icon} {g.name}</Text>
              </View>
            ))}
          </View>
        </View>

        {profile.availability.length > 0 && (
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Availability</Text>
            <View style={styles.tagRow}>
              {profile.availability.map((a) => (
                <View key={a} style={styles.tag}>
                  <Text style={styles.tagText}>
                    {a.replace(/_/g, ' ').toLowerCase().replace(/^\w/, (c) => c.toUpperCase())}
                  </Text>
                </View>
              ))}
            </View>
          </View>
        )}

        <View style={styles.linksSection}>
          {profile.soundcloudUrl && (
            <LinkRow icon="musical-note" label="SoundCloud" />
          )}
          {profile.spotifyUrl && (
            <LinkRow icon="disc" label="Spotify" />
          )}
          {profile.youtubeUrl && (
            <LinkRow icon="play-circle" label="YouTube" />
          )}
          {profile.bandcampUrl && (
            <LinkRow icon="headset" label="Bandcamp" />
          )}
        </View>

        <View style={styles.logoutSection}>
          <Button title="Log Out" variant="outline" onPress={logout} />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

function LinkRow({ icon, label }: { icon: keyof typeof Ionicons.glyphMap; label: string }) {
  return (
    <View style={linkStyles.row}>
      <Ionicons name={icon} size={20} color={theme.colors.primary} />
      <Text style={linkStyles.text}>{label}</Text>
      <Ionicons name="open-outline" size={16} color={theme.colors.textSecondary} />
    </View>
  );
}

const linkStyles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: theme.spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: theme.colors.border,
  },
  text: {
    flex: 1,
    marginLeft: theme.spacing.md,
    color: theme.colors.text,
    fontSize: theme.fontSize.md,
  },
});

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
  header: {
    alignItems: 'center',
    paddingVertical: theme.spacing.xl,
  },
  avatar: {
    width: 100,
    height: 100,
    borderRadius: 50,
    marginBottom: theme.spacing.md,
  },
  avatarPlaceholder: {
    backgroundColor: theme.colors.surface,
    justifyContent: 'center',
    alignItems: 'center',
  },
  name: {
    fontSize: theme.fontSize.xl,
    fontWeight: '800',
    color: theme.colors.text,
  },
  email: {
    fontSize: theme.fontSize.sm,
    color: theme.colors.textSecondary,
    marginTop: theme.spacing.xs,
  },
  skillBadge: {
    backgroundColor: theme.colors.primary + '30',
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.xs,
    borderRadius: theme.borderRadius.full,
    marginTop: theme.spacing.sm,
  },
  skillText: {
    color: theme.colors.primary,
    fontSize: theme.fontSize.sm,
    fontWeight: '600',
  },
  section: {
    marginBottom: theme.spacing.lg,
  },
  sectionTitle: {
    fontSize: theme.fontSize.lg,
    fontWeight: '700',
    color: theme.colors.text,
    marginBottom: theme.spacing.sm,
  },
  bioText: {
    color: theme.colors.textSecondary,
    fontSize: theme.fontSize.md,
    lineHeight: 22,
  },
  tagRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  tag: {
    backgroundColor: theme.colors.surface,
    paddingHorizontal: theme.spacing.md,
    paddingVertical: theme.spacing.sm,
    borderRadius: theme.borderRadius.full,
    marginRight: theme.spacing.sm,
    marginBottom: theme.spacing.sm,
  },
  genreTag: {
    backgroundColor: theme.colors.secondary + '20',
  },
  tagText: {
    color: theme.colors.text,
    fontSize: theme.fontSize.sm,
  },
  linksSection: {
    backgroundColor: theme.colors.surface,
    borderRadius: theme.borderRadius.lg,
    paddingHorizontal: theme.spacing.md,
    marginBottom: theme.spacing.xl,
  },
  logoutSection: {
    paddingBottom: theme.spacing.xl,
  },
});
