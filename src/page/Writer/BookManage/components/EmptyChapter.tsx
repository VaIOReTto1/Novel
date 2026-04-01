import React from 'react';
import { View, Text } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';

export const EmptyChapter: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <View style={styles.emptyCard}>
      <Text style={styles.emptyText}>鏆傛棤绔犺妭</Text>
    </View>
  );
};
