import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Image, Alert } from 'react-native';
import { useRouter } from 'expo-router';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import * as ImagePicker from 'expo-image-picker';
import { useWizardStore } from '@/stores/wizardStore';
import Button from '@/components/Button';
import Input from '@/components/Input';
import { theme } from '@/constants/theme';

export default function Step1Basics() {
  const router = useRouter();
  const { displayName, bio, photoUri, setBasics } = useWizardStore();
  const [name, setName] = useState(displayName);
  const [bioText, setBioText] = useState(bio);
  const [photo, setPhoto] = useState<string | null>(photoUri);

  const pickImage = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ['images'],
      allowsEditing: true,
      aspect: [1, 1],
      quality: 0.8,
    });
    if (!result.canceled) {
      setPhoto(result.assets[0].uri);
    }
  };

  const handleNext = () => {
    if (!name.trim()) {
      Alert.alert('Required', 'Please enter a display name');
      return;
    }
    setBasics(name.trim(), bioText.trim(), photo);
    router.push('/(onboarding)/step2');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.step}>Step 1 of 4</Text>
        <Text style={styles.title}>The Basics</Text>
        <Text style={styles.subtitle}>Tell us who you are</Text>
      </View>

      <View style={styles.form}>
        <TouchableOpacity style={styles.photoButton} onPress={pickImage}>
          {photo ? (
            <Image source={{ uri: photo }} style={styles.photo} />
          ) : (
            <View style={styles.photoPlaceholder}>
              <Ionicons name="camera" size={32} color={theme.colors.textSecondary} />
              <Text style={styles.photoText}>Add Photo</Text>
            </View>
          )}
        </TouchableOpacity>

        <Input
          label="Display Name"
          placeholder="Your stage name"
          value={name}
          onChangeText={setName}
          maxLength={50}
        />
        <Input
          label="Bio"
          placeholder="Tell musicians about yourself..."
          value={bioText}
          onChangeText={setBioText}
          multiline
          maxLength={500}
          numberOfLines={4}
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
  photoButton: {
    alignSelf: 'center',
    marginBottom: theme.spacing.lg,
  },
  photo: {
    width: 120,
    height: 120,
    borderRadius: 60,
  },
  photoPlaceholder: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: theme.colors.surface,
    borderWidth: 2,
    borderColor: theme.colors.border,
    borderStyle: 'dashed',
    justifyContent: 'center',
    alignItems: 'center',
  },
  photoText: {
    color: theme.colors.textSecondary,
    fontSize: theme.fontSize.xs,
    marginTop: theme.spacing.xs,
  },
});
