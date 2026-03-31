import React from 'react';
import { StyleSheet, View } from 'react-native';

import { stage7IconRegistry } from './generated/stage7IconRegistry';

export interface Stage7IconProps {
  name: string;
  width?: number;
  height?: number;
  color?: string;
}

export const Stage7Icon: React.FC<Stage7IconProps> = ({
  name,
  width = 24,
  height = 24,
  color = '#333333',
}) => {
  const IconAsset = stage7IconRegistry[name];

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
