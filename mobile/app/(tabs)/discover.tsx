import React, { useEffect, useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Dimensions,
  Animated,
  PanResponder,
  Image,
  ActivityIndicator,
  Modal,
  TouchableOpacity,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';
import { useDiscoveryStore } from '@/stores/discoveryStore';
import Chip from '@/components/Chip';
import Button from '@/components/Button';
import { theme } from '@/constants/theme';
import type { DiscoveryProfile } from '@/types';

const { width: SCREEN_WIDTH } = Dimensions.get('window');
const SWIPE_THRESHOLD = SCREEN_WIDTH * 0.25;

export default function DiscoverScreen() {
  const {
    profiles,
    currentIndex,
    isLoading,
    fetchFeed,
    swipeLeft,
    swipeRight,
  } = useDiscoveryStore();
  const [showMatch, setShowMatch] = useState(false);
  const pan = useRef(new Animated.ValueXY()).current;

  useEffect(() => {
    fetchFeed();
  }, []);

  const currentProfile: DiscoveryProfile | undefined = profiles[currentIndex];

  const panResponder = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onPanResponderMove: Animated.event([null, { dx: pan.x, dy: pan.y }], {
        useNativeDriver: false,
      }),
      onPanResponderRelease: (_, gesture) => {
        if (gesture.dx > SWIPE_THRESHOLD) {
          swipeOff('RIGHT');
        } else if (gesture.dx < -SWIPE_THRESHOLD) {
          swipeOff('LEFT');
        } else {
          Animated.spring(pan, {
            toValue: { x: 0, y: 0 },
            useNativeDriver: false,
          }).start();
        }
      },
    }),
  ).current;

  const swipeOff = async (direction: 'LEFT' | 'RIGHT') => {
    const toX = direction === 'RIGHT' ? SCREEN_WIDTH + 100 : -SCREEN_WIDTH - 100;
    Animated.timing(pan, {
      toValue: { x: toX, y: 0 },
      duration: 250,
      useNativeDriver: false,
    }).start(async () => {
      pan.setValue({ x: 0, y: 0 });
      if (!currentProfile) return;
      if (direction === 'LEFT') {
        await swipeLeft(currentProfile.userId);
      } else {
        const result = await swipeRight(currentProfile.userId);
        if (result.matched) {
          setShowMatch(true);
        }
      }
    });
  };

  const rotate = pan.x.interpolate({
    inputRange: [-SCREEN_WIDTH, 0, SCREEN_WIDTH],
    outputRange: ['-15deg', '0deg', '15deg'],
  });

  if (isLoading) {
    return (
      <SafeAreaView style={styles.centered}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </SafeAreaView>
    );
  }

  if (!currentProfile) {
    return (
      <SafeAreaView style={styles.centered}>
        <Ionicons name="search" size={64} color={theme.colors.textSecondary} />
        <Text style={styles.emptyTitle}>No More Profiles</Text>
        <Text style={styles.emptySubtitle}>Check back later for new musicians nearby</Text>
        <View style={{ marginTop: theme.spacing.lg, width: '60%' }}>
          <Button title="Refresh" onPress={fetchFeed} variant="outline" />
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.screenTitle}>Discover</Text>

      <Animated.View
        style={[
          styles.card,
          { transform: [{ translateX: pan.x }, { translateY: pan.y }, { rotate }] },
        ]}
        {...panResponder.panHandlers}
      >
        {currentProfile.photoUrl ? (
          <Image source={{ uri: currentProfile.photoUrl }} style={styles.cardImage} />
        ) : (
          <View style={[styles.cardImage, styles.cardImagePlaceholder]}>
            <Ionicons name="person" size={80} color={theme.colors.textSecondary} />
          </View>
        )}
        <View style={styles.cardInfo}>
          <View style={styles.nameRow}>
            <Text style={styles.cardName}>{currentProfile.displayName}</Text>
            <View style={styles.distanceBadge}>
              <Ionicons name="location" size={14} color={theme.colors.accent} />
              <Text style={styles.distanceText}>
                {currentProfile.distanceMiles.toFixed(1)} mi
              </Text>
            </View>
          </View>
          <View style={styles.skillBadge}>
            <Text style={styles.skillText}>{currentProfile.skillLevel}</Text>
          </View>
          <View style={styles.chipRow}>
            {currentProfile.instruments.slice(0, 3).map((inst) => (
              <View key={inst.id} style={styles.tag}>
                <Text style={styles.tagText}>{inst.icon} {inst.name}</Text>
              </View>
            ))}
          </View>
          <View style={styles.chipRow}>
            {currentProfile.genres.slice(0, 3).map((g) => (
              <View key={g.id} style={[styles.tag, styles.genreTag]}>
                <Text style={styles.tagText}>{g.icon} {g.name}</Text>
              </View>
            ))}
          </View>
          {currentProfile.bio ? (
            <Text style={styles.bio} numberOfLines={2}>
              {currentProfile.bio}
            </Text>
          ) : null}
        </View>
      </Animated.View>

      <View style={styles.actions}>
        <TouchableOpacity
          style={[styles.actionBtn, styles.passBtn]}
          onPress={() => swipeOff('LEFT')}
        >
          <Ionicons name="close" size={32} color={theme.colors.error} />
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.actionBtn, styles.likeBtn]}
          onPress={() => swipeOff('RIGHT')}
        >
          <Ionicons name="musical-notes" size={32} color={theme.colors.success} />
        </TouchableOpacity>
      </View>

      <Modal visible={showMatch} transparent animationType="fade">
        <View style={styles.matchOverlay}>
          <View style={styles.matchContent}>
            <Ionicons name="musical-notes" size={64} color={theme.colors.primary} />
            <Text style={styles.matchTitle}>It's a Match!</Text>
            <Text style={styles.matchSubtitle}>
              You and {currentProfile?.displayName} want to jam together
            </Text>
            <Button title="Keep Swiping" onPress={() => setShowMatch(false)} />
          </View>
        </View>
      </Modal>
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
  card: {
    flex: 1,
    backgroundColor: theme.colors.card,
    borderRadius: theme.borderRadius.xl,
    overflow: 'hidden',
    marginBottom: theme.spacing.md,
  },
  cardImage: {
    width: '100%',
    height: '55%',
    backgroundColor: theme.colors.surface,
  },
  cardImagePlaceholder: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  cardInfo: {
    padding: theme.spacing.lg,
    flex: 1,
  },
  nameRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: theme.spacing.sm,
  },
  cardName: {
    fontSize: theme.fontSize.xl,
    fontWeight: '800',
    color: theme.colors.text,
    flex: 1,
  },
  distanceBadge: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: theme.colors.surface,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: theme.spacing.xs,
    borderRadius: theme.borderRadius.full,
  },
  distanceText: {
    color: theme.colors.accent,
    fontSize: theme.fontSize.xs,
    fontWeight: '600',
    marginLeft: 4,
  },
  skillBadge: {
    alignSelf: 'flex-start',
    backgroundColor: theme.colors.primary + '30',
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: 2,
    borderRadius: theme.borderRadius.sm,
    marginBottom: theme.spacing.sm,
  },
  skillText: {
    color: theme.colors.primary,
    fontSize: theme.fontSize.xs,
    fontWeight: '600',
  },
  chipRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginBottom: theme.spacing.xs,
  },
  tag: {
    backgroundColor: theme.colors.surface,
    paddingHorizontal: theme.spacing.sm,
    paddingVertical: theme.spacing.xs,
    borderRadius: theme.borderRadius.full,
    marginRight: theme.spacing.xs,
    marginBottom: theme.spacing.xs,
  },
  genreTag: {
    backgroundColor: theme.colors.secondary + '20',
  },
  tagText: {
    color: theme.colors.text,
    fontSize: theme.fontSize.xs,
  },
  bio: {
    color: theme.colors.textSecondary,
    fontSize: theme.fontSize.sm,
    marginTop: theme.spacing.xs,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'center',
    gap: 40,
    paddingBottom: theme.spacing.md,
  },
  actionBtn: {
    width: 64,
    height: 64,
    borderRadius: 32,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 2,
  },
  passBtn: {
    borderColor: theme.colors.error,
    backgroundColor: theme.colors.error + '15',
  },
  likeBtn: {
    borderColor: theme.colors.success,
    backgroundColor: theme.colors.success + '15',
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
  matchOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.8)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: theme.spacing.xl,
  },
  matchContent: {
    backgroundColor: theme.colors.card,
    borderRadius: theme.borderRadius.xl,
    padding: theme.spacing.xl,
    alignItems: 'center',
    width: '100%',
  },
  matchTitle: {
    fontSize: theme.fontSize.xxl,
    fontWeight: '800',
    color: theme.colors.primary,
    marginTop: theme.spacing.md,
  },
  matchSubtitle: {
    fontSize: theme.fontSize.md,
    color: theme.colors.textSecondary,
    textAlign: 'center',
    marginVertical: theme.spacing.lg,
  },
});
