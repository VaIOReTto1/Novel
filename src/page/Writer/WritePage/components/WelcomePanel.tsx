import React, { useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createWritePageStyles } from '../styles/WritePageStyles';

export const WelcomePanel: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);
  const [closed, setClosed] = useState(false);
  if (closed) return null;
  return (
    <View style={styles.welcomePanel}>
      <TouchableOpacity style={styles.closeBtn} onPress={() => setClosed(true)}>
        <Text style={styles.closeText}>✕</Text>
      </TouchableOpacity>
      <Text style={styles.welcomeTitle}>欢迎成为 番茄作家</Text>
      <View style={styles.welcomeRow}>
        <View style={styles.welcomeItem}>
          <Text>📝</Text>
          <Text>写作福利</Text>
          <Text style={styles.welcomeSub}>独家分成签约</Text>
        </View>
        <View style={styles.welcomeItem}>
          <Text>📣</Text>
          <Text>创作活动</Text>
          <Text style={styles.welcomeSub}>多重活动激励</Text>
        </View>
        <View style={styles.welcomeItem}>
          <Text>🎁</Text>
          <Text>有奖任务</Text>
          <Text style={styles.welcomeSub}>赚作家番茄籽</Text>
        </View>
      </View>
    </View>
  );
};


