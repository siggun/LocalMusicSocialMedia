import React, { useState } from 'react';
import { View, Text, StyleSheet, Alert, KeyboardAvoidingView, Platform } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useAuthStore } from '@/stores/authStore';
import Button from '@/components/Button';
import Input from '@/components/Input';
import { theme } from '@/constants/theme';

export default function SignupScreen() {
  const router = useRouter();
  const { register, isLoading } = useAuthStore();
  const [displayName, setDisplayName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleSignup = async () => {
    if (!displayName.trim() || !email.trim() || !password) {
      Alert.alert('Error', 'Please fill in all fields');
      return;
    }
    if (password !== confirmPassword) {
      Alert.alert('Error', 'Passwords do not match');
      return;
    }
    if (password.length < 8) {
      Alert.alert('Error', 'Password must be at least 8 characters');
      return;
    }
    try {
      await register(email.trim(), password, displayName.trim());
    } catch (error: any) {
      Alert.alert('Signup Failed', error?.response?.data?.message || 'Could not create account');
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        style={styles.inner}
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      >
        <Text style={styles.title}>Join Lyra</Text>
        <Text style={styles.subtitle}>Create your account to start swiping</Text>

        <View style={styles.form}>
          <Input
            label="Display Name"
            placeholder="Your stage name"
            value={displayName}
            onChangeText={setDisplayName}
            icon="person-outline"
          />
          <Input
            label="Email"
            placeholder="you@example.com"
            value={email}
            onChangeText={setEmail}
            icon="mail-outline"
            keyboardType="email-address"
            autoCapitalize="none"
          />
          <Input
            label="Password"
            placeholder="At least 8 characters"
            value={password}
            onChangeText={setPassword}
            icon="lock-closed-outline"
            secureTextEntry
          />
          <Input
            label="Confirm Password"
            placeholder="Re-enter password"
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            icon="lock-closed-outline"
            secureTextEntry
          />
        </View>

        <Button title="Sign Up" onPress={handleSignup} loading={isLoading} />

        <View style={styles.footer}>
          <Text style={styles.footerText}>Already have an account? </Text>
          <Text
            style={styles.footerLink}
            onPress={() => router.replace('/(auth)/login')}
          >
            Log In
          </Text>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: theme.colors.background,
  },
  inner: {
    flex: 1,
    paddingHorizontal: theme.spacing.xl,
    justifyContent: 'center',
  },
  title: {
    fontSize: theme.fontSize.xxl,
    fontWeight: '800',
    color: theme.colors.text,
    marginBottom: theme.spacing.xs,
  },
  subtitle: {
    fontSize: theme.fontSize.md,
    color: theme.colors.textSecondary,
    marginBottom: theme.spacing.xl,
  },
  form: {
    marginBottom: theme.spacing.lg,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginTop: theme.spacing.lg,
  },
  footerText: {
    color: theme.colors.textSecondary,
    fontSize: theme.fontSize.sm,
  },
  footerLink: {
    color: theme.colors.primary,
    fontSize: theme.fontSize.sm,
    fontWeight: '600',
  },
});
