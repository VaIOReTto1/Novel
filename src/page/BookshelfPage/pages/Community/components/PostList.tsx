import React from 'react';
import {
  FlatList,
  ListRenderItem,
  RefreshControl,
  Text,
  View,
} from 'react-native';

import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';
import { CommunityCircle, CommunityPost } from '../types';
import { EmptyState } from './EmptyState';
import { LoadingIndicator } from './LoadingIndicator';
import { PostItem } from './PostItem';
import { TabBar } from './TabBar';

interface PostListProps {
  posts: CommunityPost[];
  loading: boolean;
  refreshing: boolean;
  hasMore: boolean;
  onRefresh: () => void;
  onLoadMore: () => void;
  onLike: (postId: string) => void;
  onComment: (postId: string) => void;
  onShare: (postId: string) => void;
  onMore: (postId: string) => void;
  onUserPress: (userId: string) => void;
  onSubscribe: (userId: string) => void;
  circles: CommunityCircle[];
  selectedCircle: string;
  onCircleChange: (circleId: string) => void;
}

export const PostList: React.FC<PostListProps> = ({
  posts,
  loading,
  refreshing,
  hasMore,
  onRefresh,
  onLoadMore,
  onLike,
  onComment,
  onShare,
  onMore,
  onUserPress,
  onSubscribe,
  circles,
  selectedCircle,
  onCircleChange,
}) => {
  const colors = useNovelColors();
  const ui = createNovelDesignUI(colors as any);
  const styles = createCommunityPageStyles(colors);

  const renderPost: ListRenderItem<CommunityPost> = ({ item }) => (
    <PostItem
      post={item}
      onLike={onLike}
      onComment={onComment}
      onShare={onShare}
      onMore={onMore}
      onUserPress={onUserPress}
      onSubscribe={onSubscribe}
    />
  );

  const renderHeader = () => (
    <View style={styles.postListHeader}>
      <TabBar
        circles={circles}
        selectedCircle={selectedCircle}
        onCircleChange={onCircleChange}
      />

      <View style={styles.topDivider} />

      <View style={styles.hotCommentHeader}>
        <Text style={styles.hotCommentEyebrow}>EDITOR&apos;S PICK</Text>
        <Text style={styles.hotCommentTitle}>热门讨论</Text>
        <Text style={styles.hotCommentSubtitle}>
          关注此时此刻最受讨论的阅读话题
        </Text>
      </View>
    </View>
  );

  const renderFooter = () => {
    if (!hasMore) {
      return null;
    }

    return <LoadingIndicator />;
  };

  const renderEmpty = () => {
    if (loading) {
      return <LoadingIndicator />;
    }

    return (
      <EmptyState
        title="暂无帖子"
        description="这里还没有新的讨论，先去发起第一条分享吧。"
        buttonText="发布帖子"
        onButtonPress={() => {}}
      />
    );
  };

  return (
    <FlatList
      style={styles.postList}
      contentContainerStyle={styles.postListContent}
      data={posts}
      renderItem={renderPost}
      keyExtractor={(item) => item.id}
      ListHeaderComponent={renderHeader}
      refreshControl={
        <RefreshControl
          refreshing={refreshing}
          onRefresh={onRefresh}
          colors={[ui.color.brand.primary]}
          tintColor={ui.color.brand.primary}
        />
      }
      onEndReached={onLoadMore}
      onEndReachedThreshold={0.1}
      ListFooterComponent={renderFooter}
      ListEmptyComponent={renderEmpty}
      showsVerticalScrollIndicator={false}
      removeClippedSubviews
      maxToRenderPerBatch={10}
      windowSize={10}
      initialNumToRender={10}
    />
  );
};
