import React from 'react';
import { View, Text, ActivityIndicator, TouchableOpacity } from 'react-native';

import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface LoadMoreIndicatorProps {
  isLoading?: boolean;
  hasError?: boolean;
  onLoadMore?: () => void;
  onRetry?: () => void;
}

export const LoadMoreIndicator: React.FC<LoadMoreIndicatorProps> = ({
  isLoading = false,
  hasError = false,
  onLoadMore,
  onRetry,
}) => {
  const colors = useNovelColors();
  const ui = createNovelDesignUI(colors as any);
  const styles = createBookshelfPageStyles(colors);

  if (hasError) {
    return (
      <View style={styles.loadMoreContainer}>
        <Text style={styles.loadMoreErrorText}>加载失败</Text>
        <TouchableOpacity
          style={styles.retryButton}
          onPress={onRetry || onLoadMore}
          activeOpacity={0.7}>
          <Text style={styles.retryButtonText}>重试</Text>
        </TouchableOpacity>
      </View>
    );
  }

  if (isLoading) {
    return (
      <View style={styles.loadMoreContainer}>
        <ActivityIndicator
          size="small"
          color={ui.color.brand.primary}
          style={styles.loadMoreSpinner}
        />
        <Text style={styles.loadMoreText}>加载中...</Text>
      </View>
    );
  }

  return null;
};
