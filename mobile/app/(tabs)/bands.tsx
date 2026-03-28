import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '@/constants/theme';

export default function BandsScreen() {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.screenTitle}>Bands & Jams</Text>

      <ScrollView showsVerticalScrollIndicator={false}>
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>My Bands</Text>
          <View style={styles.emptyCard}>
            <Ionicons name="people-outline" size={48} color={theme.colors.textSecondary} />
            <Text style={styles.emptyText}>No bands yet</Text>
            <Text style={styles.emptySubtext}>
              Match with musicians and create a band from the chat screen
            </Text>
          </View>
        </View>

        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Upcoming Jams</Text>
          <View style={styles.emptyCard}>
            <Ionicons name="calendar-outline" size={48} color={theme.colors.textSecondary} />
            <Text style={styles.emptyText}>No jam sessions</Text>
            <Text style={styles.emptySubtext}>
              Create a jam session to rehearse with your matches
            </Text>
          </View>
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
});
