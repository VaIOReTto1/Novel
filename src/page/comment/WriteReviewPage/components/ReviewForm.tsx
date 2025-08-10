import React, { useRef, useEffect } from 'react';
import { View, TextInput, Text } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createWriteReviewPageStyles } from '../styles/WriteReviewPageStyles';
import { REVIEW_CONSTANTS } from '../types';

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
  contentLength,
  onContentChange,
  onContentFocus,
  onContentBlur,
  autoFocus = false
}) => {
  const colors = useNovelColors();
  const styles = createWriteReviewPageStyles(colors);
  const textInputRef = useRef<TextInput>(null);

  useEffect(() => {
    if (autoFocus && textInputRef.current) {
      // 延迟聚焦，确保组件已完全渲染
      setTimeout(() => {
        textInputRef.current?.focus();
      }, 100);
    }
  }, [autoFocus]);

  return (
    <View style={styles.formContainer}>
      {/* 分割线 */}
      <View style={styles.divider} />
      
      {/* 内容输入 */}
      <View style={styles.inputSection}>
        <TextInput
          ref={textInputRef}
          style={[
            styles.contentInput,
            contentError && styles.inputError
          ]}
          value={content}
          onChangeText={onContentChange}
          onFocus={onContentFocus}
          onBlur={onContentBlur}
          placeholder="真实客观、多维描述、结构清晰、内容充实的书评，可以帮助到更多书友"
          placeholderTextColor={colors.novelTextGray + '80'}
          maxLength={REVIEW_CONSTANTS.CONTENT_MAX_LENGTH}
          multiline
          textAlignVertical="top"
          returnKeyType="default"
          autoFocus={autoFocus}
        />
        
        {contentError ? (
          <Text style={styles.errorText}>{contentError}</Text>
        ) : null}
      </View>
    </View>
  );
};