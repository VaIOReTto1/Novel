import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

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

const VIEW_OPTIONS = [
  { type: 'grid' as ViewType, icon: 'grid-view', label: '网格' },
  { type: 'list' as ViewType, icon: 'view-list', label: '列表' },
  { type: 'waterfall' as ViewType, icon: 'view-quilt', label: '双列' },
];

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
  const currentViewOption = VIEW_OPTIONS.find((option) => option.type === currentView);

  const handleViewPress = () => {
    if (isTransitioning) {
      return;
    }

    const currentIndex = VIEW_OPTIONS.findIndex((option) => option.type === currentView);
    const nextIndex = (currentIndex + 1) % VIEW_OPTIONS.length;
    onViewChange(VIEW_OPTIONS[nextIndex].type);
  };

  return (
    <View style={styles.topBarContainer}>
      <View style={styles.topBarLeft}>
        <TouchableOpacity style={styles.adBanner} activeOpacity={0.8}>
          <Text style={styles.adBannerText}>广告位</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.topBarCenter} />

      <View style={styles.topBarRight}>
        <View style={styles.topBarActionsRow}>
          <TouchableOpacity
            style={styles.topBarActionButton}
            onPress={handleViewPress}
            activeOpacity={0.7}
            disabled={isTransitioning}>
            <MaterialIcons
              name={currentViewOption?.icon || 'grid-view'}
              size={18}
              color={isTransitioning ? colors.novelTextGray : colors.novelText}
            />
            <Text
              style={[
                styles.topBarActionLabel,
                isTransitioning && styles.topBarActionLabelDisabled,
              ]}>
              {currentViewOption?.label || '网格'}
            </Text>
          </TouchableOpacity>

          <View style={styles.verticalDivider} />

          <TouchableOpacity
            style={styles.topBarActionButton}
            onPress={onFilterPress}
            activeOpacity={0.7}>
            <Text style={styles.topBarActionLabel}>筛选</Text>
          </TouchableOpacity>

          <View style={styles.verticalDivider} />

          <TouchableOpacity
            style={styles.topBarActionButton}
            onPress={onEditPress}
            activeOpacity={0.7}>
            <Text
              style={[
                styles.topBarActionLabel,
                isEditMode && styles.activeTopBarActionLabel,
              ]}>
              {isEditMode ? '完成' : '编辑'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};
