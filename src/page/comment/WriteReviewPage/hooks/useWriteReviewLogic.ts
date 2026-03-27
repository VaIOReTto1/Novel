import { useCallback } from 'react';
import { Alert } from 'react-native';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import { useWriteReviewStore } from '../store/writeReviewStore';

export const useWriteReviewLogic = () => {
  const {
    reviewData,
    isSubmitting,
    error,
    updateRating,
    updateTitle,
    updateContent,
    submitReview,
    clearError,
  } = useWriteReviewStore();

  // 处理评分变更
  const handleRatingPress = useCallback((rating: number) => {
    console.log('[useWriteReviewLogic] 更新评分:', rating);
    updateRating(rating);
  }, [updateRating]);

  // 处理标题变更
  const handleTitleChange = useCallback((title: string) => {
    updateTitle(title);
  }, [updateTitle]);

  // 处理内容变更
  const handleContentChange = useCallback((content: string) => {
    updateContent(content);
  }, [updateContent]);

  // 处理提交
  const handleSubmit = useCallback(async () => {
    console.log('[useWriteReviewLogic] 开始提交评论');

    const success = await submitReview();

    if (success) {
      Alert.alert('成功', '评论发表成功', [
        {
          text: '确定',
          onPress: () => {
            console.log('[useWriteReviewLogic] 评论提交成功，返回上一页');
            NavigationBridge.navigateBack();
          },
        },
      ]);
    } else if (error) {
      Alert.alert('提示', error);
    }
  }, [submitReview, error]);

  // 处理返回
  const handleBack = useCallback(() => {
    console.log('[useWriteReviewLogic] 用户点击返回按钮');
    NavigationBridge.navigateBack();
  }, []);

  // 清除错误
  const handleClearError = useCallback(() => {
    clearError();
  }, [clearError]);

  return {
    // 状态
    reviewData,
    isSubmitting,
    error,

    // 操作
    handleRatingPress,
    handleTitleChange,
    handleContentChange,
    handleSubmit,
    handleBack,
    handleClearError,
  };
};
