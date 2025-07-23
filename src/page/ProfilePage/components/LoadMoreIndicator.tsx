import React from 'react';
import { View, Text } from 'react-native';
import Animated from 'react-native-reanimated';
import { LoadMoreIndicatorProps } from '../types';

interface LoadMoreIndicatorComponentProps extends LoadMoreIndicatorProps {
  styles: any;
}

export const LoadMoreIndicator: React.FC<LoadMoreIndicatorComponentProps> = React.memo(({
  loading,
  hasMore,
  styles,
}) => {
  if (loading) {
    return (
      <View style={styles.waterfallLoadingContainer}>
        <View style={styles.loadingSpinner}>
          <Animated.Text style={[styles.spinnerText]}>⟳</Animated.Text>
        </View>
        <Text style={styles.waterfallLoadingText}>加载中...</Text>
      </View>
    );
  }

  if (!hasMore) {
    return (
      <View style={styles.waterfallLoadingContainer}>
        <View style={styles.waterfallEndLine} />
        <Text style={styles.waterfallEndText}>已加载全部</Text>
        <View style={styles.waterfallEndLine} />
      </View>
    );
  }

  return null;
});
