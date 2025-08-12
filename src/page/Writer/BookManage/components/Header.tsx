import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';

export const Header: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);
  return (
    <View style={styles.header}>
      <TouchableOpacity onPress={() => NavigationBridge.navigateBack?.('BookManagePageComponent')}>
        <Text style={styles.back}>‹</Text>
      </TouchableOpacity>
      <Text style={styles.title}>章节管理</Text>
    </View>
  );
};


