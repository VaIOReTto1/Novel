import React, { useCallback } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

interface TabsSectionProps {
  styles: any;
  selectedTab: 'viewed' | 'recommend' | 'following' | 'fans';
  onTabChange: (tab: 'viewed' | 'recommend' | 'following' | 'fans') => void;
}

const TAB_DATA = [
  { key: 'viewed', name: '看过的人' },
  { key: 'recommend', name: '推荐' },
  { key: 'following', name: '关注' },
  { key: 'fans', name: '粉丝' },
] as const;

export const TabsSection: React.FC<TabsSectionProps> = React.memo(({
  styles,
  selectedTab,
  onTabChange,
}) => {
  const handleTabPress = useCallback((tab: 'viewed' | 'recommend' | 'following' | 'fans') => {
    onTabChange(tab);
  }, [onTabChange]);

  return (
    <View style={styles.tabsContainer}>
      {TAB_DATA.map((tab) => (
        <TouchableOpacity
          key={tab.key}
          style={styles.tab}
          onPress={() => handleTabPress(tab.key)}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.tabText,
            selectedTab === tab.key && styles.activeTabText,
          ]}>
            {tab.name}
          </Text>
        </TouchableOpacity>
      ))}
    </View>
  );
}); 