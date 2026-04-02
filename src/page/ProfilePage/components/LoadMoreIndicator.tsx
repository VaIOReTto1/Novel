import React from 'react';
import { ActivityIndicator, Text, View } from 'react-native';
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
          <ActivityIndicator size="small" color="#C96A34" />
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
