import React, { useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import { QuestionListProps } from '../types';
import { useFeedbackHelpStore } from '../store/feedbackHelpStore';

export const QuestionList: React.FC<QuestionListProps> = React.memo(({
  styles,
  questions,
  category,
  onQuestionPress,
  onBack: _onBack,
}) => {
  const { consultCategories, getCategoryQuestions } = useFeedbackHelpStore();
  const [selectedFilter, setSelectedFilter] = useState<string>(category);

  const getCategoryIcon = (categoryId: string) => {
    const categoryIcons: { [key: string]: string } = {
      'member': '👑',
      'account': '👤',
      'subscription': '💳',
      'benefits': '🎁',
      'reading': '📖',
      'listening': '🎧',
      'other': 'ℹ️',
    };
    return categoryIcons[categoryId] || 'ℹ️';
  };

  const handleCategoryFilter = (categoryId: string) => {
    setSelectedFilter(categoryId);
  };

  const getFilteredQuestions = () => {
    if (selectedFilter === 'all') {
      return questions;
    }
    return getCategoryQuestions(selectedFilter);
  };

  const filteredQuestions = getFilteredQuestions();

  return (
    <View style={styles.questionListContainer}>
      <View style={styles.questionListContent}>
        {/* 左侧分类筛选 */}
        <View style={styles.categoryFilterContainer}>
          <ScrollView style={styles.categoryFilterList} showsVerticalScrollIndicator={false}>
            <TouchableOpacity
              style={[
                styles.categoryFilterItem,
                selectedFilter === 'all' && styles.categoryFilterItemActive,
              ]}
              onPress={() => handleCategoryFilter('all')}
              activeOpacity={0.7}
            >
              <Text style={[
                styles.categoryFilterIcon,
                selectedFilter === 'all' && styles.categoryFilterIconActive,
              ]}>📋</Text>
              <Text style={[
                styles.categoryFilterText,
                selectedFilter === 'all' && styles.categoryFilterTextActive,
              ]}>全部问题</Text>
            </TouchableOpacity>

            {consultCategories.map((cat) => (
              <TouchableOpacity
                key={cat.id}
                style={[
                  styles.categoryFilterItem,
                  selectedFilter === cat.id && styles.categoryFilterItemActive,
                ]}
                onPress={() => handleCategoryFilter(cat.id)}
                activeOpacity={0.7}
              >
                <Text style={[
                  styles.categoryFilterIcon,
                  selectedFilter === cat.id && styles.categoryFilterIconActive,
                ]}>{cat.icon}</Text>
                <Text style={[
                  styles.categoryFilterText,
                  selectedFilter === cat.id && styles.categoryFilterTextActive,
                ]}>{cat.title}</Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>

        {/* 右侧问题列表 */}
        <View style={styles.questionListRight}>
          <ScrollView style={styles.questionsList} showsVerticalScrollIndicator={false}>
            {filteredQuestions.map((question) => (
              <TouchableOpacity
                key={question.id}
                style={styles.questionItem}
                onPress={() => onQuestionPress(question.id)}
                activeOpacity={0.7}
              >
                <View style={styles.questionItemContent}>
                  <View style={styles.questionItemIcon}>
                    <Text style={styles.questionItemIconText}>
                      {getCategoryIcon(question.category)}
                    </Text>
                  </View>

                  <View style={styles.questionItemLeft}>
                    <Text style={styles.questionItemTitle}>
                      {question.title}
                    </Text>
                  </View>

                  <Text style={styles.questionItemArrow}>›</Text>
                </View>
              </TouchableOpacity>
            ))}

            {filteredQuestions.length === 0 && (
              <View style={styles.emptyState}>
                <Text style={styles.emptyStateIcon}>🤔</Text>
                <Text style={styles.emptyStateTitle}>暂无相关问题</Text>
                <Text style={styles.emptyStateDescription}>
                  没有找到相关问题，请尝试其他咨询分类或联系客服
                </Text>
              </View>
            )}
          </ScrollView>
        </View>
      </View>
    </View>
  );
});
