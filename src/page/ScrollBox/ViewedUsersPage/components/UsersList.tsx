import React, { useCallback } from 'react';
import { FlatList, Image, Text, TouchableOpacity, View } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';

import { RecommendUser } from '../types';

interface UsersListProps {
  styles: any;
  users: RecommendUser[];
  onUserPress: (userId: string) => void;
  onFollowPress: (userId: string) => void;
}

const UserItem: React.FC<{
  user: RecommendUser;
  styles: any;
  isLast: boolean;
  onUserPress: (userId: string) => void;
  onFollowPress: (userId: string) => void;
}> = React.memo(({ user, styles, isLast, onUserPress, onFollowPress }) => {
  const handleUserPress = useCallback(() => {
    onUserPress(user.id);
  }, [user.id, onUserPress]);

  const handleFollowPress = useCallback(() => {
    onFollowPress(user.id);
  }, [user.id, onFollowPress]);

  const getTagGradient = (tagType: string) => {
    switch (tagType) {
      case 'official':
        return ['#3491ff', '#1e6fff'];
      case 'author':
        return ['#ff6b35', '#ff4500'];
      case 'gold':
        return ['#ffd700', '#ffb347'];
      case 'hall':
        return ['#9370db', '#6a5acd'];
      case 'level':
        return ['#32cd32', '#228b22'];
      case 'vip':
        return ['#ff6b35', '#ff4500'];
      case 'new':
        return ['#00c851', '#00a040'];
      case 'hot':
        return ['#ff3547', '#dc143c'];
      default:
        return ['#ffd700', '#ffb347'];
    }
  };

  const renderTags = () => (
    <View style={styles.tagsContainer}>
      {user.tags.map((tag, index) => (
        <LinearGradient
          key={index}
          colors={getTagGradient(tag.type)}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
          style={styles.userTag}>
          <Text style={styles.userTagText}>{tag.text}</Text>
        </LinearGradient>
      ))}
    </View>
  );

  return (
    <TouchableOpacity
      style={[styles.userItem, isLast && styles.lastUserItem]}
      onPress={handleUserPress}
      activeOpacity={0.7}>
      <View style={styles.avatarContainer}>
        <Image source={{ uri: user.avatar }} style={styles.avatar} />
        {user.hasVBadge ? (
          <View style={styles.vBadge}>
            <Text style={styles.vBadgeText}>V</Text>
          </View>
        ) : null}
      </View>

      <View style={styles.userInfo}>
        <View style={styles.userNameRow}>
          <Text style={styles.userName}>{user.name}</Text>
          {renderTags()}
        </View>
        <Text style={styles.userDescription} numberOfLines={1}>
          {user.description}
        </Text>
      </View>

      <TouchableOpacity
        style={[styles.followButton, user.isFollowed && styles.followedButton]}
        onPress={handleFollowPress}
        activeOpacity={0.7}>
        <Text
          style={[
            styles.followButtonText,
            user.isFollowed && styles.followedButtonText,
          ]}>
          {user.isFollowed ? '已关注' : '+ 关注'}
        </Text>
      </TouchableOpacity>
    </TouchableOpacity>
  );
});

export const UsersList: React.FC<UsersListProps> = React.memo(({
  styles,
  users,
  onUserPress,
  onFollowPress,
}) => {
  const renderItem = useCallback(
    ({ item, index }: { item: RecommendUser; index: number }) => (
      <UserItem
        user={item}
        styles={styles}
        isLast={index === users.length - 1}
        onUserPress={onUserPress}
        onFollowPress={onFollowPress}
      />
    ),
    [styles, users.length, onUserPress, onFollowPress],
  );

  const keyExtractor = useCallback((item: RecommendUser) => item.id, []);

  return (
    <FlatList
      data={users}
      renderItem={renderItem}
      keyExtractor={keyExtractor}
      style={styles.usersList}
      showsVerticalScrollIndicator={false}
      bounces
    />
  );
});
