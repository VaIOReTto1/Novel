import React from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';
import { CommunityCategory, CommunitySortType } from '../types';

interface FilterBarProps {
  categories: CommunityCategory[];
  sortTypes: { key: CommunitySortType; label: string }[];
  selectedCategory: string;
  selectedSort: CommunitySortType;
  onCategoryChange: (categoryId: string) => void;
  onSortChange: (sortType: CommunitySortType) => void;
}

export const FilterBar: React.FC<FilterBarProps> = ({
  categories,
  sortTypes,
  selectedCategory,
  selectedSort,
  onCategoryChange,
  onSortChange,
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <View style={styles.filterContainer}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.filterScrollView}
      >
        {/* 分类筛选 */}
        {categories.map((category) => {
          const isActive = category.id === selectedCategory;
          return (
            <TouchableOpacity
              key={category.id}
              style={[
                styles.filterItem,
                isActive && styles.activeFilterItem,
              ]}
              onPress={() => onCategoryChange(category.id)}
            >
              <Text style={[
                styles.filterText,
                isActive && styles.activeFilterText,
              ]}>
                {category.name}
              </Text>
            </TouchableOpacity>
          );
        })}

        {/* 分隔符 */}
        <View style={{ width: 16 }} />

        {/* 排序筛选 */}
        {sortTypes.map((sortType) => {
          const isActive = sortType.key === selectedSort;
          return (
            <TouchableOpacity
              key={sortType.key}
              style={[
                styles.filterItem,
                isActive && styles.activeFilterItem,
              ]}
              onPress={() => onSortChange(sortType.key)}
            >
              <Text style={[
                styles.filterText,
                isActive && styles.activeFilterText,
              ]}>
                {sortType.label}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>
    </View>
  );
};
