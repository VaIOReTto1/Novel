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
        <Text style={styles.sectionTitle}>章节管理</Text>
      </View>
      {chapters.length === 0 ? (
        <View />
      ) : (
        <View>
          {chapters.map((c) => (
            <Text key={c.id}>{c.title}</Text>
          ))}
        </View>
      )}
    </>
  );
};


