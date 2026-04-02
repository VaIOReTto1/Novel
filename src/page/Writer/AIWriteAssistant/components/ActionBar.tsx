import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import Icon from 'react-native-vector-icons/Feather';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';

interface Props {
  onLike: () => void;
  onDislike: () => void;
  onCopy: () => void;
  onRetry: () => void;
}

export const ActionBar: React.FC<Props> = ({
  onLike,
  onDislike,
  onCopy,
  onRetry,
}) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  const actions = [
    { key: 'like', icon: 'heart', label: '\u6709\u7528', onPress: onLike },
    { key: 'dislike', icon: 'thumbs-down', label: '\u4e0d\u559c\u6b22', onPress: onDislike },
    { key: 'copy', icon: 'copy', label: '\u590d\u5236', onPress: onCopy },
    { key: 'retry', icon: 'rotate-cw', label: '\u91cd\u8bd5', onPress: onRetry },
  ];

  return (
    <View style={styles.actionBarWrap}>
      <Text style={styles.aiDisclaimer}>内容由 AI 生成，仅供参考</Text>
      <View style={styles.actionBar}>
        {actions.map((action) => (
          <TouchableOpacity
            key={action.key}
            onPress={action.onPress}
            style={styles.actionBtn}>
            <View style={styles.actionBtnContent}>
              <Icon name={action.icon} size={16} color={colors.novelTextGray} />
              <Text style={styles.actionText}>{action.label}</Text>
            </View>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
};
