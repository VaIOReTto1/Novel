import React from 'react';
import { FlatList, RefreshControl, ListRenderItem, View, Text } from 'react-native';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';
import { CommunityPost, CommunityCircle } from '../types';
import { PostItem } from './PostItem';
import { EmptyState } from './EmptyState';
import { LoadingIndicator } from './LoadingIndicator';
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
  const styles = createCommunityPageStyles(colors);

  const renderPost: ListRenderItem<CommunityPost> = ({ item }: { item: CommunityPost }) => (
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

  const renderHeader = () => {
    return (
      <View>
        {/* TabBar */}
        <TabBar
          circles={circles}
          selectedCircle={selectedCircle}
          onCircleChange={onCircleChange}
        />

        {/* 与TabBar的间距 */}
        <View style={styles.topDivider} />

        {/* 最热评论标题 */}
        <View style={styles.hotCommentHeader}>
          <Text style={styles.hotCommentTitle}>最热评论</Text>
        </View>
      </View>
    );
  };

  const renderFooter = () => {
    if (!hasMore) {return null;}
    return <LoadingIndicator />;
  };

  const renderEmpty = () => {
    if (loading) {return <LoadingIndicator />;}
    return (
      <EmptyState
        title="暂无帖子"
        description="这里还没有任何帖子，快来发布第一个吧！"
        buttonText="发布帖子"
        onButtonPress={() => {}}
      />
    );
  };

  return (
    <FlatList
      style={styles.postList}
      data={posts}
      renderItem={renderPost}
      keyExtractor={(item: { id: any; }) => item.id}
      ListHeaderComponent={renderHeader}
      refreshControl={
        <RefreshControl
          refreshing={refreshing}
          onRefresh={onRefresh}
          colors={[colors.novelMain]}
          tintColor={colors.novelMain}
        />
      }
      onEndReached={onLoadMore}
      onEndReachedThreshold={0.1}
      ListFooterComponent={renderFooter}
      ListEmptyComponent={renderEmpty}
      showsVerticalScrollIndicator={false}
      removeClippedSubviews={true}
      maxToRenderPerBatch={10}
      windowSize={10}
      initialNumToRender={10}
    />
  );
};
