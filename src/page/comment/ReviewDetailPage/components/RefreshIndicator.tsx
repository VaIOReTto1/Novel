import React from 'react';
import { RefreshControl } from 'react-native';

interface RefreshIndicatorProps {
  refreshing: boolean;
  onRefresh: () => void;
  colors: string[];
  progressBackgroundColor: string;
}

export const RefreshIndicator: React.FC<RefreshIndicatorProps> = ({
  refreshing,
  onRefresh,
  colors,
  progressBackgroundColor,
}) => {
  return (
    <RefreshControl
      refreshing={refreshing}
      onRefresh={onRefresh}
      colors={colors}
      progressBackgroundColor={progressBackgroundColor}
      tintColor={colors[0]}
    />
  );
};