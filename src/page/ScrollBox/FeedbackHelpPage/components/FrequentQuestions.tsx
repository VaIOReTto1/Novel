import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { FrequentQuestionsProps } from '../types';

export const FrequentQuestions: React.FC<FrequentQuestionsProps> = React.memo(({
  styles,
  questions,
  onQuestionPress,
}) => {
  // 定义不同的编号颜色
  const numberColors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4'];
  
  return (
    <View style={styles.frequentSection}>
      <View style={styles.frequentTitleContainer}>
        <Text style={styles.frequentTitle}>大家都在问</Text>
        <View style={styles.frequentTitleUnderline} />
      </View>
      
      <View style={styles.frequentList}>
        {questions.slice(0, 4).map((question, index) => (
          <TouchableOpacity
            key={question.id}
            style={styles.frequentItem}
            onPress={() => onQuestionPress(question.id)}
            activeOpacity={0.7}
          >
            <Text style={[styles.frequentNumberText, { color: numberColors[index] }]}>{index + 1}</Text>
            
            <View style={styles.frequentContent}>
              <Text style={styles.frequentQuestionTitle}>
                {question.title}
              </Text>
            </View>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
});