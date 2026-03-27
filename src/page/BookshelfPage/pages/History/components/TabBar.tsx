import React, { useCallback } from 'react';
import { View, ScrollView, TouchableOpacity, Text } from 'react-native';
import { HistoryTabBarProps } from '../types';

export const TabBar: React.FC<HistoryTabBarProps> = React.memo(({
  styles,
  tabs,
  selectedTab,
  onTabPress,
  viewType,
  onViewTypeChange,
  sortType,
  onSortTypeChange,
  isEditing,
  onEditToggle,
}) => {
  const handleSortPress = useCallback(() => {
    // 循环切换排序方式
    const sortOptions: Array<'lastRead' | 'addTime' | 'title' | 'progress'> =
      ['lastRead', 'addTime', 'title', 'progress'];
    const currentIndex = sortOptions.indexOf(sortType);
    const nextIndex = (currentIndex + 1) % sortOptions.length;
    onSortTypeChange(sortOptions[nextIndex]);
  }, [sortType, onSortTypeChange]);

  const getSortText = () => {
    switch (sortType) {
      case 'lastRead': return '最近阅读';
      case 'addTime': return '添加时间';
      case 'title': return '书名';
      case 'progress': return '进度';
      default: return '排序';
    }
  };

  return (
    <View style={styles.tabBarContainer}>
      <View style={styles.tabBarContent}>
        {/* 左侧Tab区域 */}
        <View style={styles.tabsContainer}>
          <ScrollView
            style={styles.tabScrollView}
            contentContainerStyle={styles.tabsScroll}
            horizontal
            showsHorizontalScrollIndicator={false}
          >
            {tabs.map((tab) => {
              const isActive = selectedTab === tab.id;
              return (
                <TouchableOpacity
                  key={tab.id}
                  style={[
                    styles.tab,
                    isActive && styles.activeTab,
                  ]}
                  onPress={() => onTabPress(tab.id)}
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
                  {tab.count !== undefined && tab.count > 0 && (
                    <View style={styles.tabBadge}>
                      <Text style={styles.tabBadgeText}>
                        {tab.count > 99 ? '99+' : tab.count}
                      </Text>
                    </View>
                  )}
                </TouchableOpacity>
              );
            })}
          </ScrollView>
        </View>

        {/* 右侧控制区域 */}
        <View style={styles.viewControls}>
          {!isEditing && (
            <>
              {/* 视图切换按钮 */}
              <TouchableOpacity
                style={styles.viewToggleButton}
                onPress={() => onViewTypeChange(viewType === 'grid' ? 'list' : 'grid')}
                activeOpacity={0.7}
              >
                <Text style={styles.viewToggleText}>
                  {viewType === 'grid' ? '列表' : '宫格'}
                </Text>
              </TouchableOpacity>

              <View style={styles.verticalDivider} />

              {/* 排序按钮 */}
              <TouchableOpacity
                style={styles.staticButton}
                onPress={handleSortPress}
                activeOpacity={0.7}
              >
                <Text style={styles.staticButtonText}>{getSortText()}</Text>
              </TouchableOpacity>
            </>
          )}

          {/* 编辑按钮 */}
          <TouchableOpacity
            style={styles.staticButton}
            onPress={onEditToggle}
            activeOpacity={0.7}
          >
            <Text style={styles.staticButtonText}>
              {isEditing ? '完成' : '编辑'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
});
