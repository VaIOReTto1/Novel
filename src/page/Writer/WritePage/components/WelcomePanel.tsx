import React, { useState } from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createWritePageStyles } from '../styles/WritePageStyles';

const ITEMS = [
  {
    id: 'benefits',
    badge: '01',
    title: '创作福利',
    description: '签约、分成与作者权益都在这里集中查看。',
  },
  {
    id: 'activities',
    badge: '02',
    title: '创作活动',
    description: '跟进主题征文、征稿和阶段性创作计划。',
  },
  {
    id: 'tasks',
    badge: '03',
    title: '任务奖励',
    description: '把日常写作节奏转成可追踪的成长反馈。',
  },
];

export const WelcomePanel: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);
  const [closed, setClosed] = useState(false);

  if (closed) {
    return null;
  }

  return (
    <View style={styles.welcomePanel}>
      <View style={styles.welcomeHeader}>
        <Text style={styles.welcomeTitle}>欢迎进入创作工作台</Text>
        <TouchableOpacity style={styles.closeBtn} onPress={() => setClosed(true)}>
          <Text style={styles.closeText}>关闭</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.welcomeRow}>
        {ITEMS.map((item) => (
          <View key={item.id} style={styles.welcomeItem}>
            <View style={styles.welcomeBadge}>
              <Text style={styles.welcomeBadgeText}>{item.badge}</Text>
            </View>
            <Text style={styles.welcomeItemTitle}>{item.title}</Text>
            <Text style={styles.welcomeSub}>{item.description}</Text>
          </View>
        ))}
      </View>
    </View>
  );
};
