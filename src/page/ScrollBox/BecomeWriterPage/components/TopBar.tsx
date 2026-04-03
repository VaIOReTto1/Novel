import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
  showAI?: boolean;
  onAIPress?: () => void;
}

export const TopBar: React.FC<TopBarComponentProps> = ({
  styles,
  onBackPress,
  showAI,
  onAIPress,
}) => {
  return (
    <View style={styles.topBar}>
      <TouchableOpacity
        style={styles.backButton}
        onPress={onBackPress}
        activeOpacity={0.7}>
        <Text style={styles.backArrow}>{'<'}</Text>
      </TouchableOpacity>

      <View style={styles.titleContainer}>
        <Text style={styles.title}>成为作家</Text>
      </View>

      {showAI ? (
        <TouchableOpacity
          style={styles.backButton}
          onPress={onAIPress}
          activeOpacity={0.7}>
          <Text style={styles.aiIcon}>AI</Text>
        </TouchableOpacity>
      ) : (
        <View style={styles.backButton} />
      )}
    </View>
  );
};
