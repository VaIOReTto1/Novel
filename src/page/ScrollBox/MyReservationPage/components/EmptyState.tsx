import React from 'react';
import { View, Text, Image } from 'react-native';
import { EmptyStateData } from '../types';

interface EmptyStateProps {
  styles: any;
  data: EmptyStateData;
}

export const EmptyState: React.FC<EmptyStateProps> = React.memo(({
  styles,
  data,
}) => {
  return (
    <View style={styles.emptyContainer}>
      <View style={styles.emptyIcon}>
        <Image
          source={{ uri: 'https://placehold.co/160' }}
          style={{ width: 80, height: 80 }}
          resizeMode="cover"
        />
      </View>
      <Text style={styles.emptyTitle}>{data.title}</Text>
    </View>
  );
});
