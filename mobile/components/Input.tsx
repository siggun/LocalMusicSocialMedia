import React from 'react';
import { View, Text, TextInput, StyleSheet, KeyboardTypeOptions } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { theme } from '@/constants/theme';

interface InputProps {
  label?: string;
  placeholder?: string;
  value: string;
  onChangeText: (text: string) => void;
  secureTextEntry?: boolean;
  multiline?: boolean;
  maxLength?: number;
  error?: string;
  icon?: keyof typeof Ionicons.glyphMap;
  keyboardType?: KeyboardTypeOptions;
  autoCapitalize?: 'none' | 'sentences' | 'words' | 'characters';
  numberOfLines?: number;
}

export default function Input({
  label,
  placeholder,
  value,
  onChangeText,
  secureTextEntry,
  multiline,
  maxLength,
  error,
  icon,
  keyboardType,
  autoCapitalize,
  numberOfLines,
}: InputProps) {
  return (
    <View style={styles.container}>
      {label && <Text style={styles.label}>{label}</Text>}
      <View
        style={[
          styles.inputWrapper,
          multiline && styles.multilineWrapper,
          error ? styles.errorBorder : null,
        ]}
      >
        {icon && (
          <Ionicons
            name={icon}
            size={20}
            color={theme.colors.textSecondary}
            style={styles.icon}
          />
        )}
        <TextInput
          style={[
            styles.input,
            multiline && styles.multilineInput,
            icon ? styles.inputWithIcon : null,
          ]}
          placeholder={placeholder}
          placeholderTextColor={theme.colors.textSecondary}
          value={value}
          onChangeText={onChangeText}
          secureTextEntry={secureTextEntry}
          multiline={multiline}
          maxLength={maxLength}
          keyboardType={keyboardType}
          autoCapitalize={autoCapitalize}
          numberOfLines={numberOfLines}
          textAlignVertical={multiline ? 'top' : 'center'}
        />
      </View>
      <View style={styles.footer}>
        {error ? <Text style={styles.errorText}>{error}</Text> : <View />}
        {maxLength != null && (
          <Text style={styles.charCount}>
            {value.length}/{maxLength}
          </Text>
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    marginBottom: theme.spacing.md,
  },
  label: {
    color: theme.colors.text,
    fontSize: theme.fontSize.sm,
    fontWeight: '600',
    marginBottom: theme.spacing.xs,
  },
  inputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    borderWidth: 1,
    borderColor: theme.colors.border,
    borderRadius: theme.borderRadius.md,
    paddingHorizontal: theme.spacing.md,
  },
  multilineWrapper: {
    alignItems: 'flex-start',
    minHeight: 100,
  },
  errorBorder: {
    borderColor: theme.colors.error,
  },
  icon: {
    marginRight: theme.spacing.sm,
  },
  input: {
    flex: 1,
    color: theme.colors.text,
    fontSize: theme.fontSize.md,
    paddingVertical: 14,
  },
  multilineInput: {
    minHeight: 80,
    paddingTop: 14,
  },
  inputWithIcon: {
    paddingLeft: 0,
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: theme.spacing.xs,
    minHeight: 18,
  },
  errorText: {
    color: theme.colors.error,
    fontSize: theme.fontSize.xs,
    flex: 1,
  },
  charCount: {
    color: theme.colors.textSecondary,
    fontSize: theme.fontSize.xs,
  },
});
