import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
}

export const TopBar: React.FC<TopBarComponentProps> = ({
  styles,
  onBackPress,
  onMarkAllReadPress,
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
        <Text style={styles.title}>我的消息</Text>
      </View>

      <TouchableOpacity
        style={styles.markAllButton}
        onPress={onMarkAllReadPress}
        activeOpacity={0.7}>
        <Text style={styles.markAllIcon}>全部已读</Text>
      </TouchableOpacity>
    </View>
  );
};
