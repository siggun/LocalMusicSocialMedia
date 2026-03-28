export const theme = {
  colors: {
    primary: '#6C63FF',
    secondary: '#FF6584',
    accent: '#00D9FF',
    background: '#1a1a2e',
    surface: '#16213e',
    card: '#0f3460',
    text: '#FFFFFF',
    textSecondary: '#A0A0B0',
    success: '#4CAF50',
    error: '#FF5252',
    warning: '#FFC107',
    border: '#2a2a4e',
  },
  spacing: {
    xs: 4,
    sm: 8,
    md: 16,
    lg: 24,
    xl: 32,
  },
  borderRadius: {
    sm: 8,
    md: 12,
    lg: 16,
    xl: 24,
    full: 9999,
  },
  fontSize: {
    xs: 12,
    sm: 14,
    md: 16,
    lg: 18,
    xl: 24,
    xxl: 32,
  },
} as const;

export type Theme = typeof theme;
export default theme;
