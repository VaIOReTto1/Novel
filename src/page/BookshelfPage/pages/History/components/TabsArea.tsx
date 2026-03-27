import React, { useCallback } from 'react';
import { View, ScrollView, TouchableOpacity, Text } from 'react-native';
import { TabData } from '../types';

interface TabsAreaProps {
  styles: any;
  tabs: TabData[];
  selectedTab: string;
  onTabPress: (tabId: string) => void;
  viewType: 'grid' | 'list';
  onViewTypeChange: (type: 'grid' | 'list') => void;
}

export const TabsArea: React.FC<TabsAreaProps> = React.memo(({
  styles,
  tabs,
  selectedTab,
  onTabPress,
  viewType,
  onViewTypeChange,
}) => {
  const handleViewToggle = useCallback(() => {
    const newViewType = viewType === 'grid' ? 'list' : 'grid';
    onViewTypeChange(newViewType);
  }, [viewType, onViewTypeChange]);

  return (
    <>
      <View style={styles.tabsContainer}>
        {/* 左侧可滑动的tabs */}
        <View style={styles.tabsScrollArea}>
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

        {/* 右侧视图切换按钮 */}
        <View style={styles.viewControls}>
          <TouchableOpacity
            style={styles.viewToggleButton}
            onPress={handleViewToggle}
            activeOpacity={0.7}
          >
            <Text style={styles.viewToggleText}>
              {viewType === 'grid' ? '列表' : '宫格'}
            </Text>
          </TouchableOpacity>

          <View style={styles.verticalDivider} />

          <View style={styles.staticButton}>
            <Text style={styles.staticButtonText}>
              更多
            </Text>
          </View>
        </View>
      </View>
    </>
  );
});
