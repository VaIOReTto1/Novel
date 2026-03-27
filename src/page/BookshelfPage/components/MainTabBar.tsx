import React, { useCallback } from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { MainTabBarProps } from '../types';

export const MainTabBar: React.FC<MainTabBarProps> = React.memo(({
  styles,
  tabs,
  selectedTab,
  onTabPress,
}) => {
  const handleTabPress = useCallback((tabId: string) => {
    console.log('[MainTabBar] Tab pressed:', tabId);
    onTabPress(tabId);
  }, [onTabPress]);

  return (
    <View style={styles.tabBarContainer}>
      <View style={styles.tabsContainer}>
        {tabs.map((tab) => {
          const isActive = selectedTab === tab.id;
          return (
            <TouchableOpacity
              key={tab.id}
              style={[
                styles.tab,
                isActive && styles.activeTab,
              ]}
              onPress={() => handleTabPress(tab.id)}
              activeOpacity={0.7}
            >
              <Text
                style={[
                  styles.tabText,
                  isActive && styles.activeTabText,
                ]}
              >
                {tab.name}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );
});
