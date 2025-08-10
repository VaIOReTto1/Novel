import React, { useState } from 'react';
import { View, Text, TextInput, FlatList, Image, TouchableOpacity } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useReviewDetailPageStyles } from '../hooks/useReviewDetailPageStyles';
import { useReviewDetailStore } from '../store/reviewDetailStore';
import { parseNewsDate } from '../../../../utils/time/timeUtils';
import { Comment } from '../types/reviewDetailTypes';
import { wp } from '../../../../utils/theme';

interface CommentListProps {
  onInputFocus?: () => void;
  onViewMoreReplies?: (comment: Comment) => void;
  onLike?: (commentId: string) => void;
  onDislike?: (commentId: string) => void;
  onReply?: (commentId: string) => void;
}

export const CommentList: React.FC<CommentListProps> = ({ 
  onInputFocus, 
  onViewMoreReplies, 
  onLike, 
  onDislike, 
  onReply 
}) => {
  const { colors, styles } = useReviewDetailPageStyles();
  const { hasMoreComments, isLoadingMore, toggleCommentLike, toggleCommentDislike } = useReviewDetailStore();

  // 使用store中的comments数据（这些是对当前评论的回复）
  // commentData是当前评论的详情，comments是对这个评论的回复
  const { comments } = useReviewDetailStore();

  console.log('[CommentList] comments from store:', comments);
  const [commentText, setCommentText] = useState('');

  const handleLikePress = (commentId: string) => {
    toggleCommentLike(commentId);
  };

  const handleDislikePress = (commentId: string) => {
    toggleCommentDislike(commentId);
  };

  const handleSendComment = () => {
    if (commentText.trim()) {
      // TODO: 实现发送评论功能
      console.log('发送评论:', commentText);
      setCommentText('');
    }
  };

  const renderCommentInput = () => {
    return (
      <View style={styles.commentInputContainer}>
        {/* 用户头像 */}
        <View style={styles.commentInputAvatar}>
          <Icon name="person" size={20} color={colors.novelTextGray} />
        </View>

        {/* 输入框 */}
        <TouchableOpacity
          style={styles.commentInputBox}
          onPress={onInputFocus}
          activeOpacity={0.8}
        >
          <Text style={[styles.commentInput, { color: colors.novelTextGray }]}>
            写下你的评论...
          </Text>
        </TouchableOpacity>
      </View>
    );
  };

  const renderCommentItem = ({ item, isReply = false, isFirst = false }: { item: Comment; isReply?: boolean; isFirst?: boolean }) => {
    // 如果是二级评论，使用简化显示 - userName和content在同一个Text中，颜色不同
    if (isReply) {
      return (
        <View style={styles.replyCommentItem}>
          <Text style={styles.replyContent}>
            <Text style={styles.replyUserName}>{item.userName}: </Text>
            <Text style={styles.replyContentText}>{item.content}</Text>
          </Text>
        </View>
      );
    }

    return (
      <View style={styles.commentItem}>
        {/* 用户信息 */}
        <View style={styles.commentUserInfo}>
          <View style={styles.commentAvatar}>
            {item.userAvatar ? (
              <Image
                source={{ uri: item.userAvatar }}
                style={styles.commentAvatarImage}
              />
            ) : (
              <Icon name="person" size={20} color={colors.novelTextGray} />
            )}
          </View>
          <View style={styles.commentUserDetails}>
            <View style={styles.commentUserNameRow}>
              <View style={styles.commentUserNameContainer}>
                <Text style={styles.commentUserName}>{item.userName}</Text>
                {item.tag && (
                  <View style={[styles.commentTag, styles[`commentTag_${item.tag}`]]}>
                    <Text style={styles.commentTagText}>
                      {item.tag === 'first_comment' ? '首评' : 
                       item.tag === 'true_fan' ? '真爱粉' : 
                       item.tag === 'vip' ? 'VIP' : ''}
                    </Text>
                  </View>
                )}
              </View>
              <TouchableOpacity style={styles.commentMenuButton} activeOpacity={0.7}>
                <Icon name="more-vert" size={20} color={colors.novelTextGray} />
              </TouchableOpacity>
            </View>
            
            {/* 评论内容 */}
            <Text style={styles.commentContent}>{item.content}</Text>

            {/* 操作按钮 */}
            <View style={styles.commentActions}>
              <View style={styles.commentLeftActions}>
                <Text style={styles.commentTime}>{parseNewsDate(item.createTime)}</Text>
                <TouchableOpacity style={styles.commentActionButton}>
                  <Text style={styles.commentActionText}>回复</Text>
                </TouchableOpacity>
              </View>

              <View style={styles.commentRightActions}>
                <TouchableOpacity
                  style={styles.commentActionButton}
                  onPress={() => handleLikePress(item.id)}
                >
                  <Icon
                    name={item.isLiked ? 'favorite' : 'favorite-border'}
                    size={20}
                    color={item.isLiked ? colors.novelMain : colors.novelTextGray}
                  />
                  <Text
                    style={[
                      styles.commentActionText,
                      item.isLiked && { color: colors.novelMain },
                    ]}
                  >
                    {item.likeCount}
                  </Text>
                </TouchableOpacity>

                <TouchableOpacity
                  style={styles.commentActionButton}
                  onPress={() => handleDislikePress(item.id)}
                >
                  <Icon
                    name={item.isDisliked ? 'thumb-down' : 'thumb-down-off-alt'}
                    size={20}
                    color={item.isDisliked ? colors.novelMain : colors.novelTextGray}
                  />
                </TouchableOpacity>
              </View>
            </View>

            {/* 二级评论 */}
            {item.replies && item.replies.length > 0 && (
              <View style={styles.repliesContainer}>
                {item.replies.slice(0, 2).map((reply) => (
                  <View key={reply.id}>
                    {renderCommentItem({ item: reply, isReply: true })}
                  </View>
                ))}
                {item.replies.length > 2 && (
                  <TouchableOpacity
                    style={styles.viewMoreRepliesButton}
                    onPress={() => onViewMoreReplies?.(item)}
                  >
                    <Text style={styles.viewMoreRepliesText}>
                      查看全部评论 ({item.replies.length})
                    </Text>
                    <Icon name="keyboard-arrow-right" size={20} color={colors.novelMain} />
                  </TouchableOpacity>
                )}
              </View>
            )}
          </View>
        </View>
      </View>
    );
  };

  const renderFooter = () => {
    if (isLoadingMore) {
      return (
        <View style={styles.loadingFooter}>
          <Text style={styles.loadingText}>加载中...</Text>
        </View>
      );
    }

    if (!hasMoreComments && comments.length > 0) {
      return (
        <View style={styles.noMoreFooter}>
          <Text style={styles.noMoreText}>没有更多评论了</Text>
        </View>
      );
    }

    return null;
  };

  const renderEmpty = () => {
    return (
      <View style={styles.emptyContainer}>
        <Icon name="comment" size={48} color={colors.novelDivider} />
        <Text style={styles.emptyText}>暂无评论</Text>
        <Text style={styles.emptySubText}>快来发表第一条评论吧</Text>
      </View>
    );
  };

  return (
    <View style={styles.commentListContainer}>
      <View style={styles.commentListHeader}>
        <Text style={styles.commentListTitle}>全部评论</Text>
        <Text style={styles.commentCount}>{comments.length}</Text>
      </View>

      {renderCommentInput()}

      {comments.length === 0 ? (
        renderEmpty()
      ) : (
        <>
          {comments.map((item, index) => (
            <View key={item.id}>
              {renderCommentItem({ item, isFirst: index === 0 })}
            </View>
          ))}
          {renderFooter()}
        </>
      )}
    </View>
  );
};