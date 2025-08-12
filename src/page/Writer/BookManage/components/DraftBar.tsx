import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';
import { DraftInfo } from '../types';

export const DraftBar: React.FC<{ draft: DraftInfo; onContinue: () => void }> = ({ draft, onContinue }) => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);
  if (!draft.exists) return null;
  return (
    <View style={styles.draft}>
      <Text style={styles.draftText}>存在上一次未编辑完的内容</Text>
      <TouchableOpacity onPress={onContinue}>
        <Text style={styles.draftAction}>继续编辑</Text>
      </TouchableOpacity>
    </View>
  );
};


