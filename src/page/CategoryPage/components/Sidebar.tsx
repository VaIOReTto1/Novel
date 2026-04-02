import React from 'react';
import { ScrollView, Text, TouchableOpacity } from 'react-native';

import { useNovelColors } from '../../../utils/theme';
import { CategoryItem } from '../store/categoryStore';
import { createCategoryPageStyles } from '../styles/CategoryPageStyles';

interface SidebarProps {
  items: CategoryItem[];
  activeId: number | null;
  onSelect: (id: number) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  items,
  activeId,
  onSelect,
}) => {
  const colors = useNovelColors();
  const styles = createCategoryPageStyles(colors);

  return (
    <ScrollView style={styles.sidebar}>
      {items.map((category) => (
        <TouchableOpacity
          key={category.id}
          onPress={() => onSelect(category.id)}
          style={styles.sidebarItem}>
          <Text
            style={[
              styles.sidebarText,
              activeId === category.id && styles.sidebarItemActive,
            ]}>
            {category.name}
          </Text>
        </TouchableOpacity>
      ))}
    </ScrollView>
  );
};
