import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';

export const Footer: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);

  return (
    <View style={styles.footer}>
      <TouchableOpacity
        style={styles.createBtn}
        onPress={() => NavigationBridge.navigateToWritePage()}>
        <Text style={styles.createText}>创建章节</Text>
      </TouchableOpacity>
      <Text style={styles.tip}>作品资料与分卷信息可在作者端继续完善。</Text>
    </View>
  );
};
