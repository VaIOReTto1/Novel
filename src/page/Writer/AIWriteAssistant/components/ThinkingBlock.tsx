import React, { useMemo, useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';
import Icon from 'react-native-vector-icons/MaterialIcons';

interface Props {
  thinking?: string;
  memoryMB?: number;
  latencyMs?: number;
  done?: boolean;
}

export const ThinkingBlock: React.FC<Props> = ({ thinking, done }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  const [collapsed, setCollapsed] = useState(false);
  const safeThinking = thinking ?? '';
  const quoteText = useMemo(() => safeThinking.trim(), [safeThinking]);

  return (
    <View style={styles.thinkingContainer}>
      <View style={styles.thinkingHeaderRow}>
        <Text style={styles.thinkingTitle}>{done ? '思考结束' : '思考中…'}</Text>
        <TouchableOpacity onPress={() => setCollapsed(v => !v)} activeOpacity={0.7}>
          {collapsed ? <Icon name="keyboard-arrow-down" size={18} color={colors.novelTextGray} /> : <Icon name="keyboard-arrow-up" size={18} color={colors.novelTextGray} />}
        </TouchableOpacity>
      </View>
      {!collapsed && quoteText.length > 0 && (
        <View style={styles.thinkingQuoteBox}>
          <Text style={styles.thinkingQuoteText}>{quoteText}</Text>
        </View>
      )}
    </View>
  );
};


