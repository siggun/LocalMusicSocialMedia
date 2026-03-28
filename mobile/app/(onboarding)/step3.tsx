import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useWizardStore } from '@/stores/wizardStore';
import Button from '@/components/Button';
import Input from '@/components/Input';
import { theme } from '@/constants/theme';

export default function Step3Media() {
  const router = useRouter();
  const store = useWizardStore();
  const [soundcloud, setSoundcloud] = useState(store.soundcloudUrl);
  const [spotify, setSpotify] = useState(store.spotifyUrl);
  const [youtube, setYoutube] = useState(store.youtubeUrl);
  const [bandcamp, setBandcamp] = useState(store.bandcampUrl);

  const handleNext = () => {
    store.setMedia(soundcloud.trim(), spotify.trim(), youtube.trim(), bandcamp.trim());
    router.push('/(onboarding)/step4');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.step}>Step 3 of 4</Text>
        <Text style={styles.title}>Your Links</Text>
        <Text style={styles.subtitle}>Share your music (all optional)</Text>
      </View>

      <View style={styles.form}>
        <Input
          label="SoundCloud"
          placeholder="soundcloud.com/your-profile"
          value={soundcloud}
          onChangeText={setSoundcloud}
          icon="link-outline"
          autoCapitalize="none"
          keyboardType="url"
        />
        <Input
          label="Spotify"
          placeholder="open.spotify.com/artist/..."
          value={spotify}
          onChangeText={setSpotify}
          icon="link-outline"
          autoCapitalize="none"
          keyboardType="url"
        />
        <Input
          label="YouTube"
          placeholder="youtube.com/@your-channel"
          value={youtube}
          onChangeText={setYoutube}
          icon="link-outline"
          autoCapitalize="none"
          keyboardType="url"
        />
        <Input
          label="Bandcamp"
          placeholder="your-band.bandcamp.com"
          value={bandcamp}
          onChangeText={setBandcamp}
          icon="link-outline"
          autoCapitalize="none"
          keyboardType="url"
        />
      </View>

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
  form: {
    flex: 1,
  },
});
