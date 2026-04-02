import React, { useEffect, useRef } from 'react';
import { Text, TextInput, View } from 'react-native';

import { useNovelColors } from '../../../../utils/theme/colors';
import { REVIEW_CONSTANTS } from '../types';
import { createWriteReviewPageStyles } from '../styles/WriteReviewPageStyles';

interface ReviewFormProps {
  content: string;
  contentError: string;
  contentLength: number;
  onContentChange: (content: string) => void;
  onContentFocus?: () => void;
  onContentBlur?: () => void;
  autoFocus?: boolean;
}

export const ReviewForm: React.FC<ReviewFormProps> = ({
  content,
  contentError,
  onContentChange,
  onContentFocus,
  onContentBlur,
  autoFocus = false,
}) => {
  const colors = useNovelColors();
  const styles = createWriteReviewPageStyles(colors);
  const textInputRef = useRef<TextInput>(null);

  useEffect(() => {
    if (autoFocus && textInputRef.current) {
      setTimeout(() => {
        textInputRef.current?.focus();
      }, 100);
    }
  }, [autoFocus]);

  return (
    <View style={styles.formContainer}>
      <View style={styles.divider} />

      <View style={styles.inputSection}>
        <TextInput
          ref={textInputRef}
          style={[styles.contentInput, contentError && styles.inputError]}
          value={content}
          onChangeText={onContentChange}
          onFocus={onContentFocus}
          onBlur={onContentBlur}
          placeholder="写下真实、清晰、有帮助的书评内容"
          placeholderTextColor={colors.novelTextGray + '80'}
          maxLength={REVIEW_CONSTANTS.CONTENT_MAX_LENGTH}
          multiline
          textAlignVertical="top"
          returnKeyType="default"
          autoFocus={autoFocus}
        />

        {contentError ? <Text style={styles.errorText}>{contentError}</Text> : null}
      </View>
    </View>
  );
};
