import React, { useEffect } from 'react';
import {
  View,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
  TouchableWithoutFeedback,
  Keyboard,
} from 'react-native';

import { useNovelColors } from '../../../utils/theme/colors';
import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { createWriteReviewPageStyles } from './styles/WriteReviewPageStyles';
import { useWriteReview } from './hooks/useWriteReview';
import { WriteReviewPageProps } from './types';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  bootstrapWriteReviewPage,
  createWriteReviewPageHandlers,
  canSubmitWriteReview,
} from './domain/writeReviewPageModel';
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
    starAnimations,
    setRating,
    setContent,
    submitReview,
    clearErrors,
  } = useWriteReview(initialRating);

  const handlers = React.useMemo(
    () =>
      createWriteReviewPageHandlers({
        bookId,
        submitReview,
        clearErrors,
        dismissKeyboard: () => Keyboard.dismiss(),
        navigateBack: NavigationBridge.navigateBack,
      }),
    [bookId, submitReview, clearErrors],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      NavigationBridge.navigateBack('WriteReviewPageComponent');
      return true;
    });
  }, []);

  useEffect(() => {
    bootstrapWriteReviewPage({
      bookId,
      source,
      navigateBack: () => NavigationBridge.navigateBack(),
    });
  }, [bookId, source]);

  const handleBackPress = () => {
    handlers.handleBackPress();
  };

  const handleSubmit = async () => {
    await handlers.handleSubmit();
  };

  const handleDismissKeyboard = () => {
    handlers.handleDismissKeyboard();
  };

  const handleInputFocus = () => {
    handlers.handleInputFocus();
  };

  const canSubmit = canSubmitWriteReview({
    rating,
    content,
    isSubmitting,
  });

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
          <TopBar
            onBackPress={handleBackPress}
            canSubmit={canSubmit}
            onSubmit={handleSubmit}
          />

          <ScrollView
            style={styles.scrollContainer}
            showsVerticalScrollIndicator={false}
            keyboardShouldPersistTaps="handled"
          >
            <View style={styles.contentContainer}>
              <RatingInput
                rating={rating}
                onRatingChange={setRating}
                starAnimations={starAnimations}
              />

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
