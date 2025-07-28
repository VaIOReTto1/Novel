import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { EmptyStateData } from '../types';

interface EmptyStateProps {
  styles: any;
  data: EmptyStateData;
  onButtonPress?: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = React.memo(({
  styles,
  data,
  onButtonPress,
}) => {
  return (
    <View style={styles.emptyContainer}>
      <View style={styles.emptyIcon}>
        <Text style={styles.emptyIconText}>{data.icon}</Text>
      </View>
      <Text style={styles.emptyTitle}>{data.title}</Text>
      {data.subtitle && (
        <Text style={styles.emptyTitle}>{data.subtitle}</Text>
      )}
      {data.buttonText && onButtonPress && (
        <TouchableOpacity
          style={styles.emptyButton}
          onPress={onButtonPress}
          activeOpacity={0.7}
        >
          <Text style={styles.emptyButtonText}>{data.buttonText}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
});
