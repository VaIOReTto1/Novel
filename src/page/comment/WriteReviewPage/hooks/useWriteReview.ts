import { useState, useCallback, useRef } from 'react';
import { Animated, Alert } from 'react-native';
import { WriteReviewState, WriteReviewActions, REVIEW_CONSTANTS, CommentSubmitRequest } from '../types';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';

// 模拟API调用 - 实际项目中应该从API服务中导入
const submitComment = async (data: CommentSubmitRequest): Promise<boolean> => {
  try {
    // 这里应该调用实际的API
    console.log('提交评论:', data);
    
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // 模拟成功率（90%成功）
    if (Math.random() > 0.1) {
      return true;
    } else {
      throw new Error('网络错误，请重试');
    }
  } catch (error) {
    console.error('提交评论失败:', error);
    throw error;
  }
};

const initialState: WriteReviewState = {
  rating: 0,
  content: '',
  isSubmitting: false,
  showTips: false,
  contentError: '',
  contentLength: 0,
};

export const useWriteReview = (initialRating?: number) => {
  const [state, setState] = useState<WriteReviewState>({
    ...initialState,
    rating: initialRating || 0
  });
  const starAnimations = useRef([...Array(5)].map(() => new Animated.Value(1))).current;

  const updateState = useCallback((updates: Partial<WriteReviewState>) => {
    setState(prev => ({ ...prev, ...updates }));
  }, []);

  const setRating = useCallback((rating: number) => {
    updateState({ rating });
    
    // 星星动画效果
    starAnimations.forEach((animation, index) => {
      if (index < rating) {
        Animated.sequence([
          Animated.timing(animation, {
            toValue: 1.2,
            duration: 100,
            useNativeDriver: true,
          }),
          Animated.timing(animation, {
            toValue: 1,
            duration: 100,
            useNativeDriver: true,
          }),
        ]).start();
      }
    });
  }, [updateState, starAnimations]);



  const setContent = useCallback((content: string) => {
    updateState({ 
      content, 
      contentLength: content.length,
      contentError: '' // 清除错误
    });
  }, [updateState]);

  const setSubmitting = useCallback((isSubmitting: boolean) => {
    updateState({ isSubmitting });
  }, [updateState]);

  const toggleTips = useCallback(() => {
    updateState({ showTips: !state.showTips });
  }, [state.showTips, updateState]);

  const validateForm = useCallback((): boolean => {
    let isValid = true;
    const errors: Partial<Pick<WriteReviewState, 'titleError' | 'contentError'>> = {};

    // 验证评分
    if (state.rating < REVIEW_CONSTANTS.RATING_MIN) {
      Alert.alert('提示', '请选择评分');
      return false;
    }



    // 验证内容
    if (state.content.trim().length < REVIEW_CONSTANTS.CONTENT_MIN_LENGTH) {
      errors.contentError = `评论内容至少需要${REVIEW_CONSTANTS.CONTENT_MIN_LENGTH}个字符`;
      isValid = false;
    }

    if (state.content.length > REVIEW_CONSTANTS.CONTENT_MAX_LENGTH) {
      errors.contentError = `评论内容不能超过${REVIEW_CONSTANTS.CONTENT_MAX_LENGTH}个字符`;
      isValid = false;
    }

    // 更新错误状态
    updateState(errors);

    return isValid;
  }, [state.rating, state.content, updateState]);

  const clearErrors = useCallback(() => {
    updateState({ contentError: '' });
  }, [updateState]);

  const submitReview = useCallback(async (bookId: string): Promise<boolean> => {
    if (!validateForm()) {
      return false;
    }

    setSubmitting(true);

    try {
      const submitData: CommentSubmitRequest = {
        bookId: parseInt(bookId, 10),
        commentContent: state.content.trim(),
        // userId 可以从用户状态中获取，这里暂时不传
      };

      const success = await submitComment(submitData);
      
      if (success) {
        Alert.alert(
          '提交成功',
          '您的评论已提交，感谢您的分享！',
          [
            {
              text: '确定',
              onPress: () => {
                // 返回上一页
                NavigationBridge.navigateBack();
              }
            }
          ]
        );
        return true;
      }
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : '提交失败，请重试';
      Alert.alert('提交失败', errorMessage);
    } finally {
      setSubmitting(false);
    }

    return false;
  }, [state.content, validateForm, setSubmitting]);

  const reset = useCallback(() => {
    setState(initialState);
    // 重置动画
    starAnimations.forEach(animation => {
      animation.setValue(1);
    });
  }, [starAnimations]);

  const actions: WriteReviewActions = {
    setRating,
    setContent,
    setSubmitting,
    toggleTips,
    validateForm,
    clearErrors,
    submitReview,
    reset,
  };

  return {
    ...state,
    ...actions,
    starAnimations,
  };
};