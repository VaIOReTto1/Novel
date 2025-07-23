import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { TopBarProps } from '../types';
import { commonSizes } from '../../../../utils/theme/dimensions';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
}

export const TopBar: React.FC<TopBarComponentProps> = ({ 
  styles, 
  onBackPress, 
  onSearchPress 
}) => {
  return (
    <View style={styles.topBar}>
      <TouchableOpacity 
        style={styles.backButton} 
        onPress={onBackPress}
        activeOpacity={0.7}
      >
        <Text style={styles.backArrow}>‹</Text>
      </TouchableOpacity>
      
      <View style={styles.titleContainer}>
        <Text style={styles.title}>浏览历史</Text>
      </View>
      
      <TouchableOpacity 
        style={styles.searchButton} 
        onPress={onSearchPress}
        activeOpacity={0.7}
      >
        <Text style={{ fontSize: commonSizes.iconSize }}>🔍</Text>
      </TouchableOpacity>
    </View>
  );
};