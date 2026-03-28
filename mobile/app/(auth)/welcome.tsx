import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import Button from '@/components/Button';
import { theme } from '@/constants/theme';

export default function WelcomeScreen() {
  const router = useRouter();

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.hero}>
        <View style={styles.iconWrapper}>
          <Ionicons name="musical-notes" size={64} color={theme.colors.primary} />
        </View>
        <Text style={styles.title}>Lyra</Text>
        <Text style={styles.tagline}>Find your sound. Find your band.</Text>
      </View>

      <View style={styles.actions}>
        <Button title="Sign Up" onPress={() => router.push('/(auth)/signup')} />
        <View style={styles.spacer} />
        <Button
          title="Log In"
          variant="outline"
          onPress={() => router.push('/(auth)/login')}
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
    justifyContent: 'space-between',
    paddingHorizontal: theme.spacing.xl,
    paddingBottom: theme.spacing.xl,
  },
  hero: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  iconWrapper: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: theme.colors.surface,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: theme.spacing.lg,
  },
  title: {
    fontSize: 42,
    fontWeight: '800',
    color: theme.colors.text,
    marginBottom: theme.spacing.sm,
  },
  tagline: {
    fontSize: theme.fontSize.lg,
    color: theme.colors.textSecondary,
    textAlign: 'center',
  },
  actions: {
    width: '100%',
  },
  spacer: {
    height: theme.spacing.md,
  },
});
