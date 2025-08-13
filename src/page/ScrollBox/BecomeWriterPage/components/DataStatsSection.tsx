import React, { useCallback } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { DataStats } from '../types';

interface DataStatsSectionProps {
  styles: any;
  selectedTab: 'novel' | 'short';
  dataStats: Record<'novel' | 'short', DataStats>;
  isExpanded: boolean;
  onTabChange: (tab: 'novel' | 'short') => void;
  onToggleExpanded: () => void;
  works?: { id: string; title: string; words: number }[];
  onPressWork?: (id: string) => void;
  onCreateChapter?: () => void;
  isAuthor?: boolean;
}

export const DataStatsSection: React.FC<DataStatsSectionProps> = React.memo(({
  styles,
  selectedTab,
  dataStats,
  isExpanded,
  onTabChange,
  onToggleExpanded,
  works,
  onPressWork,
  onCreateChapter,
  isAuthor,
}) => {
  const handleTabPress = useCallback((tab: 'novel' | 'short') => {
    onTabChange(tab);
  }, [onTabChange]);

  const currentStats = dataStats[selectedTab];

  return (
    <View style={styles.section}>
      <View style={styles.sectionHeader}>
        <Text style={styles.sectionTitle}>作品数据</Text>
        <Text style={styles.moreLink}>数据说明 ⓘ</Text>
      </View>

      {/* Tabs */}
      <View style={styles.tabsContainer}>
        <TouchableOpacity
          style={[styles.tab, selectedTab === 'novel' && styles.activeTab]}
          onPress={() => handleTabPress('novel')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.tabText,
            selectedTab === 'novel' && styles.activeTabText,
          ]}>
            小说
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.tab, selectedTab === 'short' && styles.activeTab]}
          onPress={() => handleTabPress('short')}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.tabText,
            selectedTab === 'short' && styles.activeTabText,
          ]}>
            短故事
          </Text>
        </TouchableOpacity>
      </View>

      {/* 作品卡片或空状态 */}
      {isAuthor && works && works.length > 0 ? (
        <View>
          {works.map((w) => (
            <TouchableOpacity key={w.id} style={styles.activityItem} onPress={() => onPressWork?.(w.id)}>
              <View style={styles.activityCover} />
              <View style={styles.activityInfo}>
                <Text style={styles.activityTitle} numberOfLines={1}>{w.title}</Text>
                <Text style={styles.activityTime}>已写 {w.words} 字</Text>
              </View>
              <TouchableOpacity style={styles.activityButton} onPress={onCreateChapter} activeOpacity={0.7}>
                <Text style={styles.activityButtonText}>创建章节</Text>
              </TouchableOpacity>
            </TouchableOpacity>
          ))}
        </View>
      ) : (
        <View style={styles.emptyStateContainer}>
          <View style={styles.emptyStateIcon} />
          <View style={styles.emptyStateTextGroup}>
            <Text style={styles.emptyStateTitle}>{isAuthor ? '暂未创建作品' : '成为番茄作家，开始创作'}</Text>
            <Text style={styles.emptyStateSubtitle}>{isAuthor ? '期待你在番茄小说写出好故事' : '加入作家行列，享受更多创作福利'}</Text>
          </View>
        </View>
      )}

      {/* 展开时显示数据统计 */}
      {isExpanded && (
        <View style={styles.statsContainer}>
          <View style={styles.statItem}>
            <Text style={styles.statLabel}>码字数</Text>
            <Text style={styles.statNumber}>{currentStats.wordCount}</Text>
            <Text style={styles.statDash}>-</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statLabel}>阅读人数</Text>
            <Text style={styles.statNumber}>{currentStats.readers}</Text>
            <Text style={styles.statDash}>-</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statLabel}>催更人数</Text>
            <Text style={styles.statNumber}>{currentStats.urgers}</Text>
            <Text style={styles.statDash}>-</Text>
          </View>
          <View style={styles.statItem}>
            <Text style={styles.statLabel}>每日收益（元）</Text>
            <Text style={styles.statNumber}>{currentStats.dailyIncome}</Text>
            <Text style={styles.statDash}>-</Text>
          </View>
        </View>
      )}

      {/* 展开/收起按钮 */}
      <TouchableOpacity onPress={onToggleExpanded} style={styles.toggleButton}>
        <Text style={styles.toggleButtonText}>
          {isExpanded ? '收起数据 ▲' : '展开查看数据 ▼'}
        </Text>
      </TouchableOpacity>
    </View>
  );
});
