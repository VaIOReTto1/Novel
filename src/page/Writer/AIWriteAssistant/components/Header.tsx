import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

import { createAIStyles } from '../styles/aiStyles';
import { useNovelColors } from '../../../../utils/theme/colors';
import Icon from 'react-native-vector-icons/MaterialIcons';

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
      <TouchableOpacity style={styles.headerAction} onPress={onBack}>
        <Icon name="arrow-back-ios" size={24} color={colors.novelText} />
      </TouchableOpacity>
      <View style={styles.headTitleRow}>
        <Text style={styles.headTitle}>鍐欎綔鍔╂墜</Text>
        <Text style={styles.quota}>{quotaText}</Text>
      </View>
      <TouchableOpacity style={styles.headerAction} onPress={onMenu}>
        <Icon name="more-vert" size={24} color={colors.novelText} />
      </TouchableOpacity>
    </View>
  );
};
