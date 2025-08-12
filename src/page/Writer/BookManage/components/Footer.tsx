import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createBookManageStyles } from '../styles/bookManageStyles';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';

export const Footer: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookManageStyles(colors);
  return (
    <View style={styles.footer}>
      <TouchableOpacity style={styles.createBtn} onPress={() => NavigationBridge.navigateToWritePage()}>
        <Text style={styles.createText}>创建章节</Text>
      </TouchableOpacity>
      <Text style={styles.tip}>修改作品及分卷信息请下载「番茄作家助手APP」</Text>
    </View>
  );
};


