import React from 'react';
import {
  View,
  Text,
  Image,
  TouchableOpacity,
  ScrollView,
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { CommunityComment } from '../types';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';

interface CommentsSectionProps {
  comments: CommunityComment[];
  onCommentLike: (commentId: string) => void;
  onCommentSubscribe: (commentId: string) => void;
  onCommentShare: (commentId: string) => void;
  onCommentReply: (commentId: string) => void;
}

const CommentsSection: React.FC<CommentsSectionProps> = ({
  comments,
  onCommentLike,
  onCommentSubscribe,
  onCommentShare,
  onCommentReply,
}) => {
  const styles = createCommunityPageStyles();

  const renderComment = (comment: CommunityComment) => (
    <View key={comment.id} style={styles.commentItem}>
      {/* 评论头部 */}
      <View style={styles.commentHeader}>
        <Image source={{ uri: comment.author.avatar }} style={styles.commentAvatar} />
        <View style={styles.commentAuthorInfo}>
          <Text style={styles.commentAuthorName}>{comment.author.name}</Text>
          {comment.sourceNovel && (
            <Text style={styles.commentSource}>{comment.sourceNovel} ></Text>
          )}
        </View>
        <View style={styles.commentActions}>
          <TouchableOpacity
            style={[
              styles.subscribeButton,
              comment.isSubscribed && styles.subscribeButtonActive,
            ]}
            onPress={() => onCommentSubscribe(comment.id)}
          >
            <Text
              style={[
                styles.subscribeButtonText,
                comment.isSubscribed && styles.subscribeButtonTextActive,
              ]}
            >
              {comment.isSubscribed ? '已订阅' : '订阅'}
            </Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.menuButton}>
            <Icon name="more-vert" size={20} color="#999" />
          </TouchableOpacity>
        </View>
      </View>

      {/* 评论内容 */}
      <Text style={styles.commentContent}>{comment.content}</Text>

      {/* 评论底部操作 */}
      <View style={styles.commentFooter}>
        <TouchableOpacity
          style={styles.commentActionButton}
          onPress={() => onCommentShare(comment.id)}
        >
          <Icon name="share" size={16} color="#999" />
          <Text style={styles.commentActionText}>转发</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.commentActionButton}
          onPress={() => onCommentReply(comment.id)}
        >
          <Icon name="chat-bubble-outline" size={16} color="#999" />
          <Text style={styles.commentActionText}>评论</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.commentActionButton}
          onPress={() => onCommentLike(comment.id)}
        >
          <Icon
            name={comment.isLiked ? "favorite" : "favorite-border"}
            size={16}
            color={comment.isLiked ? "#ff6b6b" : "#999"}
          />
          <Text
            style={[
              styles.commentActionText,
              comment.isLiked && styles.commentActionTextActive,
            ]}
          >
            {comment.likeCount}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );

  return (
    <View style={styles.commentsSection}>
      {/* 评论区标题 */}
      <View style={styles.commentsHeader}>
        <Text style={styles.commentsTitle}>热门评论</Text>
      </View>

      {/* 评论列表 */}
      <ScrollView showsVerticalScrollIndicator={false}>
        {comments.map(renderComment)}
      </ScrollView>
    </View>
  );
};

export default CommentsSection;