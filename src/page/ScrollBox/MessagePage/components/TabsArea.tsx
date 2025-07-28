import React from 'react';
import { View, ScrollView, TouchableOpacity, Text } from 'react-native';
import { TabData } from '../types';

interface TabsAreaProps {
  styles: any;
  tabs: TabData[];
  selectedTab: string;
  onTabPress: (tabId: string) => void;
}

export const TabsArea: React.FC<TabsAreaProps> = ({
  styles,
  tabs,
  selectedTab,
  onTabPress,
}) => {
  return (
    <>
      <View style={styles.tabsContainer}>
        <ScrollView
          horizontal
          showsHorizontalScrollIndicator={false}
          contentContainerStyle={styles.tabsScroll}
          scrollEventThrottle={16}
        >
          {tabs.map((tab) => (
            <TouchableOpacity
              key={tab.id}
              style={[
                styles.tab,
                selectedTab === tab.type && styles.activeTab,
              ]}
              onPress={() => onTabPress(tab.id)}
              activeOpacity={0.7}
            >
              <Text
                style={[
                  styles.tabText,
                  selectedTab === tab.type && styles.activeTabText,
                ]}
              >
                {tab.name}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>

      {/* 添加水平分割线 */}
      <View style={styles.tabsDivider} />
    </>
  );
};
