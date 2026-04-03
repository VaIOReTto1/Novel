import React, { useState } from 'react';
import { ScrollView, Text, TouchableOpacity, View } from 'react-native';

import { useFeedbackHelpStore } from '../store/feedbackHelpStore';
import { QuestionListProps } from '../types';

export const QuestionList: React.FC<QuestionListProps> = React.memo(({
  styles,
  questions,
  category,
  onQuestionPress,
}) => {
  const { consultCategories, getCategoryQuestions } = useFeedbackHelpStore();
  const [selectedFilter, setSelectedFilter] = useState<string>(category);

  const getCategoryIcon = (categoryId: string) => {
    const categoryIcons: { [key: string]: string } = {
      member: '会员',
      account: '账号',
      subscription: '订阅',
      benefits: '权益',
      reading: '阅读',
      listening: '听书',
      other: '其他',
    };
    return categoryIcons[categoryId] || '其他';
  };

  const handleCategoryFilter = (categoryId: string) => {
    setSelectedFilter(categoryId);
  };

  const filteredQuestions =
    selectedFilter === 'all' ? questions : getCategoryQuestions(selectedFilter);

  return (
    <View style={styles.questionListContainer}>
      <View style={styles.questionListContent}>
        <View style={styles.categoryFilterContainer}>
          <ScrollView style={styles.categoryFilterList} showsVerticalScrollIndicator={false}>
            <TouchableOpacity
              style={[
                styles.categoryFilterItem,
                selectedFilter === 'all' && styles.categoryFilterItemActive,
              ]}
              onPress={() => handleCategoryFilter('all')}
              activeOpacity={0.7}>
              <Text
                style={[
                  styles.categoryFilterIcon,
                  selectedFilter === 'all' && styles.categoryFilterIconActive,
                ]}>
                全部
              </Text>
              <Text
                style={[
                  styles.categoryFilterText,
                  selectedFilter === 'all' && styles.categoryFilterTextActive,
                ]}>
                全部问题
              </Text>
            </TouchableOpacity>

            {consultCategories.map((cat) => (
              <TouchableOpacity
                key={cat.id}
                style={[
                  styles.categoryFilterItem,
                  selectedFilter === cat.id && styles.categoryFilterItemActive,
                ]}
                onPress={() => handleCategoryFilter(cat.id)}
                activeOpacity={0.7}>
                <Text
                  style={[
                    styles.categoryFilterIcon,
                    selectedFilter === cat.id && styles.categoryFilterIconActive,
                  ]}>
                  {cat.icon}
                </Text>
                <Text
                  style={[
                    styles.categoryFilterText,
                    selectedFilter === cat.id && styles.categoryFilterTextActive,
                  ]}>
                  {cat.title}
                </Text>
              </TouchableOpacity>
            ))}
          </ScrollView>
        </View>

        <View style={styles.questionListRight}>
          <ScrollView style={styles.questionsList} showsVerticalScrollIndicator={false}>
            {filteredQuestions.map((question) => (
              <TouchableOpacity
                key={question.id}
                style={styles.questionItem}
                onPress={() => onQuestionPress(question.id)}
                activeOpacity={0.7}>
                <View style={styles.questionItemContent}>
                  <View style={styles.questionItemIcon}>
                    <Text style={styles.questionItemIconText}>
                      {getCategoryIcon(question.category)}
                    </Text>
                  </View>

                  <View style={styles.questionItemLeft}>
                    <Text style={styles.questionItemTitle}>{question.title}</Text>
                  </View>

                  <Text style={styles.questionItemArrow}>{'>'}</Text>
                </View>
              </TouchableOpacity>
            ))}

            {filteredQuestions.length === 0 ? (
              <View style={styles.emptyState}>
                <Text style={styles.emptyStateIcon}>问题</Text>
                <Text style={styles.emptyStateTitle}>暂无相关问题</Text>
                <Text style={styles.emptyStateDescription}>
                  再试试其他咨询分类或联系客服
                </Text>
              </View>
            ) : null}
          </ScrollView>
        </View>
      </View>
    </View>
  );
});
