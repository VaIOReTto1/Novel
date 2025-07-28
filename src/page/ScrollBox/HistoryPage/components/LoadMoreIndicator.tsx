import React from 'react';
import { View, Text } from 'react-native';
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
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  if (!hasMore) {
    return (
      <View style={styles.loadingContainer}>
        <View style={styles.endLine} />
        <Text style={styles.endText}>已加载全部</Text>
        <View style={styles.endLine} />
      </View>
    );
  }

  return null;
});
