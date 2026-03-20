import React from 'react';
import { SafeAreaView } from 'react-native';
import { useNovelColors } from '../../../../utils/theme';
import { createCommunityPageStyles } from './styles/CommunityPageStyles';
import { useCommunity } from './hooks/useCommunity';
import { PostList } from './components/PostList';
import { FloatingButton } from './components/FloatingButton';

const CommunityPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  const {
    posts,
    circles,
    selectedCircle,
    loading,
    refreshing,
    hasMore,
    handleCircleChange,
    handleRefresh,
    handleLoadMore,
    handleLike,
    handleComment,
    handleShare,
    handleMore,
    handleUserPress,
    handleSubscribe,
    handlePublish,
  } = useCommunity();

  return (
    <SafeAreaView style={styles.container}>
      {/* 帖子列表 - 包含TabBar在滑动轴内 */}
      <PostList
        posts={posts}
        loading={loading}
        refreshing={refreshing}
        hasMore={hasMore}
        onRefresh={handleRefresh}
        onLoadMore={handleLoadMore}
        onLike={handleLike}
        onComment={handleComment}
        onShare={handleShare}
        onMore={handleMore}
        onUserPress={handleUserPress}
        onSubscribe={handleSubscribe}
        circles={circles}
        selectedCircle={selectedCircle}
        onCircleChange={handleCircleChange}
      />

      {/* 浮动发布按钮 */}
      <FloatingButton onPress={handlePublish} />
    </SafeAreaView>
  );
};

export default CommunityPage;
