import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { ViewType } from '../types';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface ViewSwitcherProps {
  currentView: ViewType;
  onViewChange: (view: ViewType) => void;
  isTransitioning?: boolean;
}

export const ViewSwitcher: React.FC<ViewSwitcherProps> = ({
  currentView,
  onViewChange,
  isTransitioning = false,
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  const viewOptions = [
    { type: 'grid' as ViewType, icon: '⊞', label: '网格' },
    { type: 'list' as ViewType, icon: '☰', label: '列表' },
    { type: 'waterfall' as ViewType, icon: '⋮', label: '瀑布流' },
  ];

  const handleViewPress = (viewType: ViewType) => {
    if (viewType !== currentView && !isTransitioning) {
      onViewChange(viewType);
    }
  };

  return (
    <View style={styles.viewSwitcher}>
      {viewOptions.map((option) => {
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
            disabled={isTransitioning}
          >
            <Text style={[
              styles.viewOptionIcon,
              isActive && styles.activeViewOptionIcon,
            ]}>
              {option.icon}
            </Text>
            <Text style={[
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
