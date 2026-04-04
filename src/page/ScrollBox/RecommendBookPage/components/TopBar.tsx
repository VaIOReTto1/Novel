import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
}

export const TopBar: React.FC<TopBarComponentProps> = ({ styles, onBackPress }) => {
  return (
    <View style={styles.topBar}>
      <TouchableOpacity
        activeOpacity={0.7}
        onPress={onBackPress}
        style={styles.backButton}>
        <Text style={styles.backArrow}>{'<'}</Text>
      </TouchableOpacity>

      <View style={styles.titleContainer}>
        <Text style={styles.title}>推书中心</Text>
      </View>

      <View style={styles.topBarSpacer} />
    </View>
  );
};
