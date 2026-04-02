import React from 'react';
import { Text, View } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';

export const EmptyChapter: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <View style={styles.emptyCard}>
      <Text style={styles.emptyText}>还没有章节，先从第一章开始搭建作品结构。</Text>
    </View>
  );
};
