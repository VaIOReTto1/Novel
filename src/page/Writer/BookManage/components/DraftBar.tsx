import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { DraftInfo } from '../types';
import { createBookManageStyles } from '../styles/bookManageStyles';

export const DraftBar: React.FC<{ draft: DraftInfo; onContinue: () => void }> = ({
  draft,
  onContinue,
}) => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  if (!draft.exists) {
    return null;
  }

  return (
    <View style={styles.draft}>
      <Text style={styles.draftText}>检测到上一次未完成的编辑内容，可以直接续写。</Text>
      <TouchableOpacity onPress={onContinue}>
        <Text style={styles.draftAction}>继续编辑</Text>
      </TouchableOpacity>
    </View>
  );
};
