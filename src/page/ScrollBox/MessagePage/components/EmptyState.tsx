import React from 'react';
import { View, Text } from 'react-native';
import { EmptyStateProps } from '../types';

interface EmptyStateComponentProps extends EmptyStateProps {
  styles: any;
}

export const EmptyState: React.FC<EmptyStateComponentProps> = ({
  styles,
  message,
  icon = '📦',
}) => {
  return (
    <View style={styles.emptyContainer}>
      <View style={styles.emptyImage}>
        <Text style={styles.emptyImageText}>{icon}</Text>
      </View>
      <Text style={styles.emptyText}>{message}</Text>
    </View>
  );
};
