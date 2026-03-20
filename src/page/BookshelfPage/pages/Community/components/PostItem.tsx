import React from 'react';
import { View, Text, TouchableOpacity, Image, ScrollView } from 'react-native';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';
import { CommunityPost } from '../types';

interface PostItemProps {
  post: CommunityPost;
  onLike?: (postId: string) => void;
  onComment?: (postId: string) => void;
  onShare?: (postId: string) => void;
  onMore?: (postId: string) => void;
  onUserPress?: (userId: string) => void;
  onSubscribe?: (userId: string) => void;
}

export const PostItem: React.FC<PostItemProps> = ({
  post,
  onLike,
  onComment,
  onShare,
  onMore,
  onUserPress,
  onSubscribe,
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <View>
      <View style={styles.postItem}>
        {/* 第一行：头像、标题、来源、订阅按钮、菜单 */}
        <View style={styles.postHeader}>
          <TouchableOpacity 
            style={styles.userAvatar}
            onPress={() => onUserPress?.(post.author.id)}
          >
            {post.author.avatar ? (
              <Image 
                source={{ uri: post.author.avatar }} 
                style={styles.userAvatar}
              />
            ) : (
              <View style={styles.userAvatar} />
            )}
          </TouchableOpacity>
          
          <View style={styles.headerContent}>
            <TouchableOpacity onPress={() => onUserPress?.(post.author.id)}>
              <Text style={styles.userName}>{post.author.name}</Text>
            </TouchableOpacity>
            {post.novelName && (
              <Text style={styles.sourceText}>
                来自 {post.novelName}
              </Text>
            )}
          </View>
          
          <View style={styles.headerActions}>
            <TouchableOpacity 
              style={styles.subscribeButton}
              onPress={() => onSubscribe?.(post.author.id)}
            >
              <Text style={styles.subscribeButtonText}>订阅</Text>
            </TouchableOpacity>
            
            <TouchableOpacity 
              style={styles.moreButton}
              onPress={() => onMore?.(post.id)}
            >
              <Text style={styles.moreIcon}>⋯</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* 帖子标题 */}
        {post.title && (
          <Text style={styles.postTitle}>
            {post.title}
          </Text>
        )}
        
        {/* 评论内容 */}
        <Text style={styles.postContent} numberOfLines={6}>
          {post.content}
        </Text>

        {/* 图片 */}
        {post.images && post.images.length > 0 && (
          <ScrollView 
            horizontal
            showsHorizontalScrollIndicator={false}
            style={styles.postImages}
          >
            {post.images.map((image, index) => (
              <Image 
                key={index}
                source={{ uri: image }}
                style={styles.postImage}
              />
            ))}
          </ScrollView>
        )}

        {/* 最后一行：三个按钮 space-around */}
        <View style={styles.postActions}>
          <TouchableOpacity 
            style={styles.actionButton}
            onPress={() => onShare?.(post.id)}
          >
            <Text style={styles.actionIcon}>📤</Text>
            <Text style={styles.actionText}>{post.shareCount}</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            style={styles.actionButton}
            onPress={() => onComment?.(post.id)}
          >
            <Text style={styles.actionIcon}>💬</Text>
            <Text style={styles.actionText}>{post.commentCount}</Text>
          </TouchableOpacity>

          <TouchableOpacity 
            style={styles.actionButton}
            onPress={() => onLike?.(post.id)}
          >
            <Text style={[
              styles.actionIcon,
              post.isLiked && styles.likedIcon,
            ]}>
              {post.isLiked ? '❤️' : '🤍'}
            </Text>
            <Text style={[
              styles.actionText,
              post.isLiked && styles.likedText,
            ]}>
              {post.likeCount}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
      
      {/* 底部分割线 */}
      <View style={styles.bottomDivider} />
    </View>
  );
};