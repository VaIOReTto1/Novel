import React from 'react';
import { View, TouchableOpacity, Text, Animated } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createWriteReviewPageStyles } from '../styles/WriteReviewPageStyles';

interface RatingInputProps {
  rating: number;
  onRatingChange: (rating: number) => void;
  starAnimations?: Animated.Value[];
}

const RATING_LABELS = {
  1: '太差了',
  2: '有点差',
  3: '一般般',
  4: '优秀',
  5: '很优秀',
};

export const RatingInput: React.FC<RatingInputProps> = ({
  rating,
  onRatingChange,
  starAnimations = [],
}) => {
  const colors = useNovelColors();
  const styles = createWriteReviewPageStyles(colors);

  const handleStarPress = (selectedRating: number) => {
    onRatingChange(selectedRating);
  };

  const renderStar = (index: number) => {
    const starNumber = index + 1;
    const isSelected = starNumber <= rating;
    const animation = starAnimations[index];

    const starStyle = [
      styles.starButton,
      isSelected && styles.starButtonSelected,
      animation && {
        transform: [{
          scale: animation,
        }],
      },
    ];

    return (
      <TouchableOpacity
        key={index}
        style={starStyle}
        onPress={() => handleStarPress(starNumber)}
        activeOpacity={0.7}
      >
        <Text style={[
          styles.starText,
          isSelected && styles.starTextSelected,
        ]}>
          ★
        </Text>
      </TouchableOpacity>
    );
  };

  return (
    <View style={styles.ratingContainer}>
      <View style={styles.starContainer}>
        {[0, 1, 2, 3, 4].map(renderStar)}
      </View>

      {rating > 0 && (
        <Text style={styles.ratingLabel}>
          {RATING_LABELS[rating as keyof typeof RATING_LABELS]}
        </Text>
      )}
    </View>
  );
};
