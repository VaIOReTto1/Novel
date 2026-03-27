import React from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import { QuestionDetailProps } from '../types';

export const QuestionDetail: React.FC<QuestionDetailProps> = React.memo(({
  styles,
  detail,
  onResolve,
  onRelatedQuestionPress,
}) => {
  const handleResolve = (isResolved: boolean) => {
    onResolve(isResolved);
  };

  return (
    <View style={styles.detailContainer}>
      <View style={styles.detailHeader}>
        <Text style={styles.detailTitle}>{detail.title}</Text>

        <View style={styles.detailMeta}>
          {detail.tags.map((tag, index) => (
            <View key={index} style={styles.detailTag}>
              <Text style={styles.detailTagText}>{tag}</Text>
            </View>
          ))}
        </View>
      </View>

      <ScrollView style={styles.detailContent} showsVerticalScrollIndicator={false}>
        <Text style={styles.detailText}>{detail.content}</Text>

        {detail.relatedQuestions && detail.relatedQuestions.length > 0 && (
          <View style={styles.relatedSection}>
            <Text style={styles.relatedTitle}>相关问题</Text>
            {detail.relatedQuestions.map((questionId) => (
              <TouchableOpacity
                key={questionId}
                style={styles.relatedItem}
                onPress={() => onRelatedQuestionPress(questionId)}
              >
                <Text style={styles.relatedItemText}>
                  查看相关问题 #{questionId}
                </Text>
                <Text style={styles.relatedItemArrow}>›</Text>
              </TouchableOpacity>
            ))}
          </View>
        )}
      </ScrollView>

      <View style={styles.resolutionSection}>
        <View style={styles.resolutionButtons}>
          <TouchableOpacity
            style={[styles.resolutionButton, styles.resolvedButton]}
            onPress={() => handleResolve(true)}
            activeOpacity={0.7}
          >
            <Text style={[styles.resolutionButtonIcon, styles.resolvedButtonIcon]}>
              👍
            </Text>
            <Text style={[styles.resolutionButtonText, styles.resolvedButtonText]}>
              已解决
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.resolutionButton, styles.unresolvedButton]}
            onPress={() => handleResolve(false)}
            activeOpacity={0.7}
          >
            <Text style={[styles.resolutionButtonIcon, styles.unresolvedButtonIcon]}>
              👎
            </Text>
            <Text style={[styles.resolutionButtonText, styles.unresolvedButtonText]}>
              未解决
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
});
