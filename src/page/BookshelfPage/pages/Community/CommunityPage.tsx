import React from 'react';
import { SafeAreaView } from 'react-native';

import { useNovelColors } from '../../../../utils/theme';
import { FloatingButton } from './components/FloatingButton';
import { PostList } from './components/PostList';
import { TopBar } from './components/TopBar';
import { useCommunity } from './hooks/useCommunity';
import { createCommunityPageStyles } from './styles/CommunityPageStyles';

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
      <TopBar />
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
      <FloatingButton onPress={handlePublish} />
    </SafeAreaView>
  );
};

export default CommunityPage;
