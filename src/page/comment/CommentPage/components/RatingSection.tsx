import React, { memo, useEffect } from 'react';
import { Keyboard, Text, TouchableOpacity, View } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createCommentPageStyles } from '../styles/CommentPageStyles';

interface RatingSectionProps {
  overallRating?: number;
  totalReviews?: number;
  onWriteReview?: () => void;
  bookId?: string;
}

const HeartRating = memo(
  ({
    size = 35,
    onStarPress,
  }: {
    size?: number;
    onStarPress?: (rating: number) => void;
  }) => {
    const colors = useNovelColors();
    const styles = createCommentPageStyles(colors);
    const [selectedRating, setSelectedRating] = React.useState(0);

    useEffect(() => {
      setSelectedRating(0);
      Keyboard.dismiss();
    }, []);

    const handleStarPress = (rating: number) => {
      setSelectedRating(rating);
      onStarPress?.(rating);
    };

    return (
      <View style={styles.heartRating}>
        {[1, 2, 3, 4, 5].map((star) => (
          <TouchableOpacity
            key={star}
            onPress={() => handleStarPress(star)}
            hitSlop={{ top: 10, bottom: 10, left: 5, right: 5 }}>
            <Icon
              name={star <= selectedRating ? 'star' : 'star-border'}
              size={size}
              color={colors.novelMain}
            />
          </TouchableOpacity>
        ))}
      </View>
    );
  },
);

export const RatingSection = memo(({
  overallRating = 4.2,
  totalReviews = 1234,
  onWriteReview: _onWriteReview,
  bookId,
}: RatingSectionProps) => {
  const styles = createCommentPageStyles(useNovelColors());

  const handleStarPress = (rating: number) => {
    NavigationBridge.navigateToWriteReview(bookId, rating);
  };

  return (
    <View style={styles.ratingSection}>
      <View style={styles.ratingTopRow}>
        <Text style={styles.ratingScoreText}>{overallRating.toFixed(1)}</Text>
        <Text style={styles.ratingScoreSubText}>分</Text>
        <HeartRating size={25} onStarPress={handleStarPress} />
      </View>

      <View style={styles.ratingBottomRow}>
        <Text style={styles.ratingCountText}>{`${totalReviews}人评分`}</Text>
        <Text style={styles.ratingTipText}>点击星星进行评分</Text>
      </View>
    </View>
  );
});
