import React, { useEffect } from 'react';
import {
  View,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
  TouchableWithoutFeedback,
  Keyboard,
  BackHandler,
} from 'react-native';

import { useNovelColors } from '../../../utils/theme/colors';
import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { createWriteReviewPageStyles } from './styles/WriteReviewPageStyles';
import { useWriteReview } from './hooks/useWriteReview';
import { WriteReviewPageProps } from './types';
import {
  TopBar,
  RatingInput,
  ReviewForm,
} from './components';

const WriteReviewPage: React.FC<WriteReviewPageProps> = ({ bookId, source, initialRating }) => {
  const colors = useNovelColors();
  const styles = createWriteReviewPageStyles(colors);
  
  const {
    rating,
    content,
    contentError,
    contentLength,
    isSubmitting,
    showTips,
    starAnimations,
    setRating,
    setContent,
    toggleTips,
    submitReview,
    clearErrors,
  } = useWriteReview(initialRating);

  // Android硬件返回按钮处理
  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => {
      console.log('[WriteReviewPage] Android硬件返回按钮被按下');
      NavigationBridge.navigateBack('WriteReviewPageComponent');
      return true; // 阻止默认行为
    });

    return () => backHandler.remove();
  }, []);

  // 页面初始化
  useEffect(() => {
    if (!bookId) {
      console.warn('WriteReviewPage: bookId is required');
      NavigationBridge.navigateBack();
    }
  }, [bookId]);

  // 处理返回
  const handleBackPress = () => {
    NavigationBridge.navigateBack('WriteReviewPageComponent');
  };

  // 处理提交
  const handleSubmit = async () => {
    if (!bookId) return;
    
    await submitReview(bookId);
  };

  // 处理键盘收起
  const handleDismissKeyboard = () => {
    Keyboard.dismiss();
  };

  // 处理输入框焦点
  const handleInputFocus = () => {
    clearErrors();
  };

  // 检查是否可以提交
  const canSubmit = rating > 0 && content.trim().length >= 10 && !isSubmitting;

  if (!bookId) {
    return null;
  }

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      keyboardVerticalOffset={Platform.OS === 'ios' ? 0 : 20}
    >
      <TouchableWithoutFeedback onPress={handleDismissKeyboard}>
        <View style={styles.container}>
          {/* 顶部导航栏 */}
          <TopBar 
            onBackPress={handleBackPress} 
            canSubmit={canSubmit}
            onSubmit={handleSubmit}
          />

          {/* 滚动内容区域 */}
          <ScrollView
            style={styles.scrollContainer}
            showsVerticalScrollIndicator={false}
            keyboardShouldPersistTaps="handled"
          >
            <View style={styles.contentContainer}>
              {/* 评分区域 */}
              <RatingInput
                rating={rating}
                onRatingChange={setRating}
                starAnimations={starAnimations}
              />

              {/* 表单区域 */}
              <ReviewForm
                content={content}
                contentError={contentError}
                contentLength={contentLength}
                onContentChange={setContent}
                onContentFocus={handleInputFocus}
                autoFocus={true}
              />
            </View>
          </ScrollView>


        </View>
      </TouchableWithoutFeedback>
    </KeyboardAvoidingView>
  );
};

export default WriteReviewPage;