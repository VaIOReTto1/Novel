import React from 'react';
import { View, Text } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';
import { BookBasic } from '../types';

export const Banner: React.FC<{ book: BookBasic | null }> = ({ book }) => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);
  return (
    <View style={styles.banner}>
      <View style={styles.bannerRow}>
        <View style={styles.cover} />
        <View style={styles.bannerInfo}>
          <Text style={styles.bookTitle}>{book?.title ?? ''}</Text>
          <Text style={styles.author}>{book?.authorName ?? ''}</Text>
          <Text style={styles.status}>{book?.statusText ?? ''}</Text>
        </View>
      </View>
    </View>
  );
};


