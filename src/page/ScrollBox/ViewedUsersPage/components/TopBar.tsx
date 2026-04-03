import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
  userName: string;
}

export const TopBar: React.FC<TopBarComponentProps> = ({
  styles,
  userName,
  onBackPress,
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
        <Text style={styles.title}>{userName}</Text>
      </View>

      <View style={styles.backButton} />
    </View>
  );
};
