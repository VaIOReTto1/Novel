import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { createAIStyles } from '../styles/aiStyles';
import { useNovelColors } from '../../../../utils/theme/colors';

interface HeaderProps {
  onBack: () => void;
  onMenu: () => void;
  quotaText: string;
}

export const Header: React.FC<HeaderProps> = ({ onBack, onMenu, quotaText }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  return (
    <View style={styles.header}>
      <TouchableOpacity onPress={onBack}>
        <Text style={styles.headBack}>‹</Text>
      </TouchableOpacity>
      <View style={styles.headTitleRow}>
        <Text style={styles.headTitle}>写作助手</Text>
        <Text style={styles.quota}>{quotaText}</Text>
      </View>
      <TouchableOpacity onPress={onMenu}>
        <Text style={styles.headBack}>⋯</Text>
      </TouchableOpacity>
    </View>
  );
};


