import React from 'react';
import { Text, View } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createWritePageStyles } from '../styles/WritePageStyles';

export const VolumeBar: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);

  return (
    <View style={styles.volumeBar}>
      <Text style={styles.volumeText}>第一卷：默认卷</Text>
    </View>
  );
};
