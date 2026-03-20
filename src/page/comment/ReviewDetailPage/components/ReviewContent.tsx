import React, { memo } from 'react';
import { View, Text, TouchableOpacity, Image } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useReviewDetailPageStyles } from '../hooks/useReviewDetailPageStyles';
import { parseNewsDate } from '../../../../utils/time/timeUtils';
import { useNovelColors } from '../../../../utils/theme/colors';

const StarRating = memo(({ rating }: { rating?: number }) => {
  const colors = useNovelColors();
  const { styles } = useReviewDetailPageStyles();

  if (!rating) {
    return null;
  }

  return (
    <View style={styles.starRating}>
      {[1, 2, 3, 4, 5].map((star) => (
        <Icon
          key={star}
          name="star"
          size={18}
          color={star <= rating ? colors.novelText + 'a0' : colors.novelTextGray + 'a0'}
        />
      ))}
    </View>
  );
});

interface ReviewContentProps {
  commentData?: {
    id: string;
    commentContent: string;
    rating: number;
    commentTime: string;
    commentUser: string;
    commentUserId: number;
    commentUserPhoto?: string;
    likeCount: number;
    isLiked: boolean;
    bookInfo?: {
      bookId: string;
      bookName: string;
      authorName: string;
      picUrl: string;
    };
  };
}

export const ReviewContent: React.FC<ReviewContentProps> = ({ commentData }) => {
  const { colors, styles } = useReviewDetailPageStyles();

  if (!commentData) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  const handleLikePress = () => {
    // TODO: 实现点赞功能
    console.log('点赞评论:', commentData.id);
  };
  void handleLikePress;


  return (
    <View style={styles.reviewDetailContainer}>
      {/* 用户信息 */}
      <View style={styles.userInfoContainer}>
        <View style={styles.avatarContainer}>
          {commentData.commentUserPhoto ? (
            <Image
              source={{ uri: commentData.commentUserPhoto }}
              style={styles.avatarImage}
            />
          ) : (
            <Icon name="person" size={24} color={colors.novelTextGray} />
          )}
        </View>
        <View style={styles.userInfo}>
          <Text style={styles.userName}>{commentData.commentUser}</Text>
          <Text style={styles.commentTime}>{parseNewsDate(commentData.commentTime)}</Text>
        </View>
        <TouchableOpacity style={styles.followButton} activeOpacity={0.7}>
          <Text style={styles.followButtonText}>+ 关注</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.userMetaContainer}>
        <StarRating rating={commentData.rating} />
        <Text style={styles.readingTimeText}>阅读2小时后点评</Text>
      </View>

      {/* 评论内容 */}
      <Text style={styles.reviewContent}>{commentData.commentContent}</Text>

      {/* 书籍信息框 */}
      {commentData.bookInfo && (
        <View style={styles.bookInfoBox}>
          <Image
            source={{ uri: commentData.bookInfo.picUrl }}
            style={styles.bookCover}
          />
          <View style={styles.bookDetails}>
            <Text style={styles.bookName}>{commentData.bookInfo.bookName}</Text>
            <Text style={styles.bookAuthor}>{commentData.bookInfo.authorName}</Text>
          </View>
        </View>
      )}
    </View>
  );
};
