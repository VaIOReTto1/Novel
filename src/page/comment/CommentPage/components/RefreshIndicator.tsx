import React, { memo, useEffect, useRef } from 'react';
import {
  View,
  Text,
  Animated,
  ActivityIndicator,
} from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createCommentPageStyles } from '../styles/CommentPageStyles';

interface RefreshIndicatorProps {
  isRefreshing: boolean;
  refreshText?: string;
}

export const RefreshIndicator = memo(({ 
  isRefreshing, 
  refreshText = '正在刷新...' 
}: RefreshIndicatorProps) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const scaleAnim = useRef(new Animated.Value(0.8)).current;
  
  useEffect(() => {
    if (isRefreshing) {
      Animated.parallel([
        Animated.timing(fadeAnim, {
          toValue: 1,
          duration: 200,
          useNativeDriver: true,
        }),
        Animated.spring(scaleAnim, {
          toValue: 1,
          tension: 100,
          friction: 8,
          useNativeDriver: true,
        }),
      ]).start();
    } else {
      Animated.parallel([
        Animated.timing(fadeAnim, {
          toValue: 0,
          duration: 200,
          useNativeDriver: true,
        }),
        Animated.timing(scaleAnim, {
          toValue: 0.8,
          duration: 200,
          useNativeDriver: true,
        }),
      ]).start();
    }
  }, [isRefreshing, fadeAnim, scaleAnim]);
  
  if (!isRefreshing) return null;
  
  return (
    <Animated.View
      style={[
        styles.refreshIndicator,
        {
          opacity: fadeAnim,
          transform: [{ scale: scaleAnim }],
        },
      ]}
    >
      <View style={styles.refreshContent}>
        <ActivityIndicator
          size="small"
          color={colors.novelMain}
          style={styles.loadingSpinner}
        />
        <Text style={styles.refreshText}>{refreshText}</Text>
      </View>
    </Animated.View>
  );
});