import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import Icon from 'react-native-vector-icons/Feather';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';

interface Props {
  onLike: () => void;
  onDislike: () => void;
  onCopy: () => void;
  onRetry: () => void;
}

export const ActionBar: React.FC<Props> = ({ onLike, onDislike, onCopy, onRetry }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  return (
    <View style={styles.actionBarWrap}>
      <Text style={styles.aiDisclaimer}>内容由 AI 生成，仅供参考</Text>
      <View style={styles.actionBar}>
        <TouchableOpacity onPress={onLike} style={styles.actionBtn}>
          <Icon name="heart" size={16} color={colors.novelTextGray} />
        </TouchableOpacity>
        <TouchableOpacity onPress={onDislike} style={styles.actionBtn}>
          <Icon name="thumbs-down" size={16} color={colors.novelTextGray} />
        </TouchableOpacity>
        <TouchableOpacity onPress={onCopy} style={styles.actionBtn}>
          <Icon name="copy" size={16} color={colors.novelTextGray} />
        </TouchableOpacity>
        <TouchableOpacity onPress={onRetry} style={styles.actionBtn}>
          <Icon name="rotate-cw" size={16} color={colors.novelTextGray} />
        </TouchableOpacity>
      </View>
    </View>
  );
};



