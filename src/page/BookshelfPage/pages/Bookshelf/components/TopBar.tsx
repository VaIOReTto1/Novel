import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { ViewType } from '../types';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface TopBarProps {
  currentView: ViewType;
  onViewChange: (view: ViewType) => void;
  onFilterPress: () => void;
  onEditPress: () => void;
  isTransitioning?: boolean;
  isEditMode?: boolean;
}

export const TopBar: React.FC<TopBarProps> = ({
  currentView,
  onViewChange,
  onFilterPress,
  onEditPress,
  isTransitioning = false,
  isEditMode = false,
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  
  const viewOptions = [
    { type: 'grid' as ViewType, icon: '⊞', label: '网格' },
    { type: 'list' as ViewType, icon: '☰', label: '列表' },
    { type: 'waterfall' as ViewType, icon: '⋮', label: '双列' },
  ];

  const currentViewOption = viewOptions.find(option => option.type === currentView);

  const handleViewPress = () => {
    if (isTransitioning) return;
    
    const currentIndex = viewOptions.findIndex(option => option.type === currentView);
    const nextIndex = (currentIndex + 1) % viewOptions.length;
    onViewChange(viewOptions[nextIndex].type);
  };

  return (
    <View style={styles.topBarContainer}>
      {/* 左侧广告区域 */}
      <View style={styles.topBarLeft}>
        <TouchableOpacity style={styles.adBanner} activeOpacity={0.8}>
          <Text style={styles.adBannerText}>广告位</Text>
        </TouchableOpacity>
      </View>

      {/* 中间空白区域 */}
      <View style={styles.topBarCenter} />

      {/* 右侧功能按钮 */}
      <View style={styles.topBarRight}>
        <View style={{ flexDirection: 'row', gap: 8 }}>
          {/* 视图切换按钮 */}
          <TouchableOpacity
            style={styles.topBarActionButton}
            onPress={handleViewPress}
            activeOpacity={0.7}
            disabled={isTransitioning}
          >
            <Text style={[
              styles.topBarActionLabel,
              isTransitioning && { opacity: 0.3 }
            ]}>
              {currentViewOption?.label || '网格'}
            </Text>
          </TouchableOpacity>

          <View style={styles.verticalDivider} />

          {/* 筛选按钮 */}
          <TouchableOpacity
            style={styles.topBarActionButton}
            onPress={onFilterPress}
            activeOpacity={0.7}
          >
            <Text style={styles.topBarActionLabel}>筛选</Text>
          </TouchableOpacity>

          <View style={styles.verticalDivider} />

          {/* 编辑按钮 */}
          <TouchableOpacity
            style={styles.topBarActionButton}
            onPress={onEditPress}
            activeOpacity={0.7}
          >
            <Text style={[
              styles.topBarActionLabel,
              isEditMode && styles.activeTopBarActionLabel
            ]}>
              {isEditMode ? '完成' : '编辑'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};