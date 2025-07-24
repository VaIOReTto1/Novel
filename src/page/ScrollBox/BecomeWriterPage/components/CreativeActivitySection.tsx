import React, { useCallback } from 'react';
import { View, Text, TouchableOpacity, Image } from 'react-native';
import { ActivityItem } from '../types';

interface CreativeActivitySectionProps {
  styles: any;
  selectedTab: 'novel' | 'short';
  activities: Record<'novel' | 'short', ActivityItem[]>;
  onTabChange: (tab: 'novel' | 'short') => void;
  onMorePress: () => void;
}

export const CreativeActivitySection: React.FC<CreativeActivitySectionProps> = React.memo(({
  styles,
  selectedTab,
  activities,
  onTabChange,
  onMorePress,
}) => {
  const handleTabPress = useCallback((tab: 'novel' | 'short') => {
    onTabChange(tab);
  }, [onTabChange]);

  const currentActivities = activities[selectedTab];

  return (
    <View style={styles.section}>
      {/* Header */}
      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>创作活动</Text>
        <TouchableOpacity onPress={onMorePress}>
          <Text style={styles.moreLink}>更多 ›</Text>
        </TouchableOpacity>
      </View>

      {/* Tab 切换 */}
      <View style={styles.subtabsContainer}>
        <TouchableOpacity
          style={[styles.subtab, selectedTab === 'novel' && styles.activeSubtab]}
          onPress={() => handleTabPress('novel')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.subtabText,
            selectedTab === 'novel' && styles.activeSubtabText
          ]}>
            小说
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.subtab, selectedTab === 'short' && styles.activeSubtab]}
          onPress={() => handleTabPress('short')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.subtabText,
            selectedTab === 'short' && styles.activeSubtabText
          ]}>
            短故事
          </Text>
        </TouchableOpacity>
      </View>

      {/* 活动列表 */}
      <View style={styles.activityList}>
        {currentActivities.map((activity, idx) => (
          <React.Fragment key={activity.id}>
            <View style={styles.activityItem}>
              <Image
                source={{ uri: activity.coverUrl }}
                style={styles.activityCover}
              />
              <View style={styles.activityInfo}>
                <Text style={styles.activityTitle} numberOfLines={2}>
                  {activity.title}
                </Text>
                <Text style={styles.activityTime}>{activity.time}</Text>
              </View>
              <TouchableOpacity style={styles.activityButton} activeOpacity={0.7}>
                <Text style={styles.activityButtonText}>去查看</Text>
              </TouchableOpacity>
            </View>
            {/* 如果不是最后一项，就渲染分割线 */}
            {idx < currentActivities.length - 1 && (
              <View style={styles.activityDivider} />
            )}
          </React.Fragment>
        ))}
      </View>
    </View>
  );
});
