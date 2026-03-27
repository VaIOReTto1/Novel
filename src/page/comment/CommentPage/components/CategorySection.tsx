import React, { memo, useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createCommentPageStyles } from '../styles/CommentPageStyles';

interface CategorySectionProps {
  onCategoryChange?: (category: string) => void;
  selectedCategory?: string;
}

const COMMENT_CATEGORIES = [
  { id: 'all', name: '全部 1247' },
  { id: 'positive', name: '好评 1128' },
  { id: 'negative', name: '差评 2' },
  { id: 'plot', name: '剧情' },
  { id: 'character', name: '人物' },
  { id: 'writing', name: '文笔' },
  { id: 'update', name: '更新 54' },
];

export const CategorySection = memo(({
  onCategoryChange,
  selectedCategory = 'all',
}: CategorySectionProps) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const [activeCategory, setActiveCategory] = useState(selectedCategory);

  const handleCategoryPress = (categoryId: string) => {
    setActiveCategory(categoryId);
    onCategoryChange?.(categoryId);
  };

  return (
    <View style={styles.categorySection}>
      <View style={styles.categoryTabs}>
        {COMMENT_CATEGORIES.map((category) => {
          const isActive = activeCategory === category.id;
          return (
            <TouchableOpacity
              key={category.id}
              style={[
                styles.categoryTab,
                isActive && styles.categoryTabActive,
              ]}
              onPress={() => handleCategoryPress(category.id)}
              activeOpacity={0.8}
            >
              <Text style={[
                styles.categoryTabText,
                isActive && styles.categoryTabTextActive,
              ]}>
                {category.name}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );
});
