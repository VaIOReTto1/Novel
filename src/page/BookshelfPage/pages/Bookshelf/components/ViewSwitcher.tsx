import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

import { ViewType } from '../types';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface ViewSwitcherProps {
  currentView: ViewType;
  onViewChange: (view: ViewType) => void;
  isTransitioning?: boolean;
}

const VIEW_OPTIONS = [
  { type: 'grid' as ViewType, icon: 'grid-view', label: '网格' },
  { type: 'list' as ViewType, icon: 'view-list', label: '列表' },
  { type: 'waterfall' as ViewType, icon: 'view-quilt', label: '瀑布流' },
];

export const ViewSwitcher: React.FC<ViewSwitcherProps> = ({
  currentView,
  onViewChange,
  isTransitioning = false,
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);

  const handleViewPress = (viewType: ViewType) => {
    if (viewType !== currentView && !isTransitioning) {
      onViewChange(viewType);
    }
  };

  return (
    <View style={styles.viewSwitcher}>
      {VIEW_OPTIONS.map((option) => {
        const isActive = option.type === currentView;

        return (
          <TouchableOpacity
            key={option.type}
            style={[
              styles.viewOption,
              isActive && styles.activeViewOption,
              isTransitioning && styles.disabledViewOption,
            ]}
            onPress={() => handleViewPress(option.type)}
            activeOpacity={0.7}
            disabled={isTransitioning}>
            <MaterialIcons
              name={option.icon}
              size={18}
              color={isActive ? colors.novelBackground : colors.novelText}
            />
            <Text
              style={[
                styles.viewOptionLabel,
                isActive && styles.activeViewOptionLabel,
              ]}>
              {option.label}
            </Text>
          </TouchableOpacity>
        );
      })}
    </View>
  );
};
