import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { useNovelColors } from '../../../../../utils/theme';
import { createWatchlistPageStyles } from '../styles/WatchlistPageStyles';

interface EmptyStateProps {
  onFindDramas: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = ({ onFindDramas }) => {
  const colors = useNovelColors();
  const styles = createWatchlistPageStyles(colors);

  return (
    <View style={styles.emptyStateContainer}>
      <Text style={styles.emptyStateIcon}>待看</Text>
      <Text style={styles.emptyStateTitle}>追剧列表暂时为空</Text>
      <Text style={styles.emptyStateDescription}>
        去发现新的短剧，把感兴趣的内容收进你的案头片单。
      </Text>
      <TouchableOpacity
        style={styles.emptyStateButton}
        onPress={onFindDramas}
        activeOpacity={0.8}>
        <Text style={styles.emptyStateButtonText}>去找短剧</Text>
      </TouchableOpacity>
    </View>
  );
};
