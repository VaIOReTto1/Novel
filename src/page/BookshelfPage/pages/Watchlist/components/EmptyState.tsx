import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { createWatchlistPageStyles } from '../styles/WatchlistPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface EmptyStateProps {
  onFindDramas: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = ({ onFindDramas }) => {
  const colors = useNovelColors();
  const styles = createWatchlistPageStyles(colors);

  return (
    <View style={styles.emptyStateContainer}>
      <Text style={styles.emptyStateIcon}>📺</Text>
      <Text style={styles.emptyStateTitle}>追剧列表暂无内容</Text>
      <Text style={styles.emptyStateDescription}>
        快去发现喜欢的短剧吧
      </Text>
      <TouchableOpacity
        style={styles.emptyStateButton}
        onPress={onFindDramas}
        activeOpacity={0.8}
      >
        <Text style={styles.emptyStateButtonText}>找短剧</Text>
      </TouchableOpacity>
    </View>
  );
};
