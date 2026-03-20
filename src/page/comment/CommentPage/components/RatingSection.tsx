import React, { memo, useEffect } from 'react';
import { View, Text, TouchableOpacity, Keyboard } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createCommentPageStyles } from '../styles/CommentPageStyles';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';

interface RatingSectionProps {
  overallRating?: number;
  totalReviews?: number;
  onWriteReview?: () => void;
  bookId?: string;
}

const HeartRating = memo(({ size = 35, onStarPress }: { size?: number; onStarPress?: (rating: number) => void }) => {
  const colors = useNovelColors();
  const [selectedRating, setSelectedRating] = React.useState(0);
  
  // 组件挂载时重置评分并关闭键盘
  useEffect(() => {
    // 组件挂载时重置评分为0并关闭键盘
    setSelectedRating(0);
    Keyboard.dismiss();
  }, []);
  
  const handleStarPress = (rating: number) => {
    setSelectedRating(rating);
    onStarPress?.(rating);
  };
  
  return (
    <View style={{ flexDirection: 'row', gap: 2 }}>
      {[1, 2, 3, 4, 5].map((star) => (
        <TouchableOpacity
          key={star}
          onPress={() => handleStarPress(star)}
          hitSlop={{ top: 10, bottom: 10, left: 5, right: 5 }}
        >
          <Icon
            name={star <= selectedRating ? "star" : "star-border"}
            size={size}
            color={colors.novelMain}
          />
        </TouchableOpacity>
      ))}
    </View>
  );
});

export const RatingSection = memo(({ 
  overallRating = 4.2, 
  totalReviews = 1234,
  onWriteReview: _onWriteReview,
  bookId 
}: RatingSectionProps) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  
  const handleStarPress = (rating: number) => {
    // 导航到发表评论页面，传递bookId和评分
    NavigationBridge.navigateToWriteReview(bookId, rating);
  };
  
  return (
    <View style={styles.ratingSection}>
      {/* 上行：十分制评分和可点击星星 */}
      <View style={styles.ratingTopRow}>
        <Text style={styles.ratingScoreText}>{overallRating.toFixed(1)}</Text>
        <Text style={styles.ratingScoreSubText}>分</Text>
        <HeartRating size={25} onStarPress={handleStarPress} />
      </View>
      
      {/* 下行：评分人数和点击星星进行评分 */}
      <View style={styles.ratingBottomRow}>
        <Text style={styles.ratingCountText}>{totalReviews}人评分</Text>
        <Text style={styles.ratingTipText}>点击星星进行评分</Text>
      </View>
    </View>
  );
});
