import React from 'react';
import { StyleSheet, View } from 'react-native';

import { novelDesignIconRegistry } from './generated/novelDesignIconRegistry';

export interface NovelDesignIconProps {
  name: string;
  width?: number;
  height?: number;
  color?: string;
}

export const NovelDesignIcon: React.FC<NovelDesignIconProps> = ({
  name,
  width = 24,
  height = 24,
  color = '#333333',
}) => {
  const IconAsset = novelDesignIconRegistry[name];

  if (!IconAsset) {
    return <View style={[styles.placeholder, { width, height }]} />;
  }

  return (
    <IconAsset
      width={width}
      height={height}
      fill={color}
      color={color}
    />
  );
};

const styles = StyleSheet.create({
  placeholder: {
    backgroundColor: '#cccccc',
    borderRadius: 4,
  },
});
