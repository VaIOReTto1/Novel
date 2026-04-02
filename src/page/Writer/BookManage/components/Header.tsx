import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';

export const Header: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <View style={styles.header}>
      <TouchableOpacity
        style={styles.headerAction}
        onPress={() => NavigationBridge.navigateBack?.('BookManagePageComponent')}>
        <Icon name="arrow-back-ios-new" size={18} color={colors.novelText} />
      </TouchableOpacity>
      <Text style={styles.title}>作品管理</Text>
    </View>
  );
};
