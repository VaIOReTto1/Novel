import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import Icon from 'react-native-vector-icons/MaterialIcons';

export const Header: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <View style={styles.header}>
      <TouchableOpacity
        style={styles.headerAction}
        onPress={() => NavigationBridge.navigateBack?.('BookManagePageComponent')}>
        <Icon name="arrow-back-ios" size={24} color={colors.novelText} />
      </TouchableOpacity>
      <Text style={styles.title}>绔犺妭绠＄悊</Text>
    </View>
  );
};
