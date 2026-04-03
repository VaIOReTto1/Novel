import React from 'react';
import { Text, View } from 'react-native';
import { EmptyStateProps } from '../types';

interface EmptyStateComponentProps extends EmptyStateProps {
  styles: any;
}

export const EmptyState: React.FC<EmptyStateComponentProps> = ({
  styles,
  message,
  icon = '消息',
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
