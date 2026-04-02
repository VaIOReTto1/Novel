import React from 'react';
import { Text, View } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { ChapterItem } from '../types';
import { createBookManageStyles } from '../styles/bookManageStyles';

export const ChapterSection: React.FC<{ chapters: ChapterItem[] }> = ({ chapters }) => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <>
      <View style={styles.sectionHeaderRow}>
        <Text style={styles.sectionTitle}>章节列表</Text>
      </View>
      {chapters.length === 0 ? null : (
        <View style={styles.chapterList}>
          {chapters.map((chapter) => (
            <View key={chapter.id} style={styles.chapterCard}>
              <Text style={styles.chapterTitle}>{chapter.title}</Text>
              <Text style={styles.chapterMeta}>{`${chapter.words} 字`}</Text>
            </View>
          ))}
        </View>
      )}
    </>
  );
};
