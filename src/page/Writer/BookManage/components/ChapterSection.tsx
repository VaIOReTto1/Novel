import React from 'react';
import { View, Text } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';
import { ChapterItem } from '../types';

export const ChapterSection: React.FC<{ chapters: ChapterItem[] }> = ({ chapters }) => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <>
      <View style={styles.sectionHeaderRow}>
        <Text style={styles.sectionTitle}>绔犺妭绠＄悊</Text>
      </View>
      {chapters.length === 0 ? (
        <View />
      ) : (
        <View style={styles.chapterList}>
          {chapters.map((c) => (
            <View key={c.id} style={styles.chapterCard}>
              <Text style={styles.chapterTitle}>{c.title}</Text>
              <Text style={styles.chapterMeta}>{`${c.words} 字`}</Text>
            </View>
          ))}
        </View>
      )}
    </>
  );
};
