import React, { memo } from 'react';
import {
  View,
  Text,
  FlatList,
  TouchableOpacity,
  Image,
  ActivityIndicator,
} from 'react-native';
import { Comment } from '../types';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createCommentPageStyles } from '../styles/CommentPageStyles';
import { useCommentStore } from '../store/commentStore';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';
import { parseNewsDate } from '../../../../utils/time/timeUtils';
import { NativeSyntheticEvent } from 'react-native';
import { TextLayoutEventData } from 'react-native';


interface CommentListProps {
  onEndReached?: () => void;
  refreshControl?: React.ReactElement;
  ListHeaderComponent?: React.ComponentType<any> | React.ReactElement | null;
}

const StarRating = memo(({ rating }: { rating?: number }) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);

  if (!rating) {return null;}

  return (
    <View style={styles.starRating}>
      {[1, 2, 3, 4, 5].map((star) => (
        <Icon
          key={star}
          name="star"
          size={14}
          color={star <= rating ? colors.novelText + 'a0' : colors.novelTextGray + 'a0'}
        />
      ))}
    </View>
  );
});

const CommentItem = memo(({ item, bookInfo }: { item: Comment; bookInfo?: { bookId: string; bookName: string; authorName: string; picUrl: string } }) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const { likeComment } = useCommentStore();

  const handleLike = () => {
    likeComment(item.id);
  };

  const handleCommentPress = () => {
    try {
      const commentData = JSON.stringify({ ...item, bookInfo });
      NavigationBridge.navigateToReviewDetail(commentData);
    } catch (error) {
      console.error('Failed to serialize comment data:', error);
    }
  };

  // fullLines：存储文字被拆成的每一行文本
  const [fullLines, setFullLines] = React.useState<string[]>([]);
  const [truncatedText, setTruncatedText] = React.useState<string>('');
  const [isExpanded, setIsExpanded] = React.useState(false);
  const [showExpandButton, setShowExpandButton] = React.useState(false);

  // 1) 测量全文文本分行
  const onTextLayout = (e: NativeSyntheticEvent<TextLayoutEventData>) => {
    if (fullLines.length === 0) {
      const lines = e.nativeEvent.lines.map((l: { text: any; }) => l.text);
      setFullLines(lines);
    }
  };

  // 2) 根据 fullLines 生成 truncatedText
  React.useEffect(() => {
    if (fullLines.length > 3) {
      const third = fullLines[2];
      // 去掉最后两个字符
      const trimmed = third.slice(0, -3);
      const txt = `${fullLines[0]}\n${fullLines[1]}\n${trimmed}...`;
      setTruncatedText(txt);
      setShowExpandButton(true);
    }
  }, [fullLines]);

  return (
    <View style={styles.commentItem}>
      {/* 第一行：头像、名字、时间、关注按钮、菜单键 */}
      <View style={styles.commentHeader}>
        <View style={styles.commentAvatar}>
          {item.commentUserPhoto ? (
            <Image
              source={{ uri: item.commentUserPhoto }}
              style={styles.commentAvatarImage}
            />
          ) : (
            <View style={[styles.commentAvatarImage, { backgroundColor: colors.novelBookBackground }]}>
              <Icon name="person" size={20} color={colors.novelTextGray} />
            </View>
          )}
        </View>

        <View style={styles.commentUserInfo}>
          <View style={styles.commentUserNameRow}>
            <Text style={styles.commentUserName}>{item.commentUser}</Text>
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
          <Text style={styles.commentTime}>{parseNewsDate(item.commentTime)}</Text>
        </View>

        <View style={styles.commentHeaderActions}>
          <TouchableOpacity style={styles.followButton} activeOpacity={0.7}>
            <Text style={styles.followButtonText}>+ 关注</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.menuButton} activeOpacity={0.7}>
            <Icon name="more-vert" size={20} color={colors.novelTextGray} />
          </TouchableOpacity>
        </View>
      </View>

      {/* 评论内容 - 最多三行，超出显示展开按钮 */}
      <View style={styles.commentContentContainer}>
        {/* 隐形测量用 */}
        {fullLines.length === 0 && (
          <Text
            style={[styles.commentContent, { position: 'absolute', opacity: 0 }]}
            onTextLayout={onTextLayout}
          >
            {item.commentContent}
          </Text>
        )}

        {/* 折叠态：手动截断 */}
        {!isExpanded && showExpandButton && truncatedText !== '' ? (
          <Text style={styles.commentContent}>
            {truncatedText}
          </Text>
        ) : (
          // 展开态或不超行：展示完整内容
          <Text style={styles.commentContent}>
            {item.commentContent}
          </Text>
        )}

        {/* 浮动“展开”按钮 */}
        {!isExpanded && showExpandButton && (
          <TouchableOpacity
            style={styles.expandButtonFloating}
            onPress={() => setIsExpanded(true)}
            activeOpacity={0.7}
          >
            <Text style={styles.expandButtonText}>展开</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* 收起按钮 - 在评论内容下一行 */}
      {isExpanded && (
        <TouchableOpacity
          style={styles.collapseButton}
          onPress={() => setIsExpanded(false)}
          activeOpacity={0.7}
        >
          <Text style={styles.expandButtonText}>收起</Text>
        </TouchableOpacity>
      )}

      {/* 评分和阅读时间 */}
      <View style={styles.commentMeta}>
        <View style={styles.commentRatingContainer}>
          <StarRating rating={item.rating} />
          <Text style={styles.readingTimeText}>阅读2小时后点评</Text>
        </View>

        {/* 点赞和踩 */}
        <View style={styles.commentActions}>
          <TouchableOpacity
            style={styles.commentAction}
            onPress={handleLike}
            activeOpacity={0.7}
          >
            <Icon
              name={item.isLiked ? 'favorite' : 'favorite-border'}
              size={16}
              color={item.isLiked ? colors.novelMain : colors.novelTextGray}
            />
            <Text
              style={[
                styles.commentActionText,
                item.isLiked && styles.commentActionTextActive,
              ]}
            >
              {String(item.likeCount || 0)}
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.commentAction}
            activeOpacity={0.7}
          >
            <Icon
              name="thumb-down"
              size={16}
              color={colors.novelTextGray}
            />
            <Text style={styles.commentActionText}>0</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* 查看评论 */}
      <TouchableOpacity onPress={handleCommentPress} style={styles.viewCommentButton} activeOpacity={0.7}>
        <Text style={styles.viewCommentText}>查看评论 &gt; </Text>
      </TouchableOpacity>

      {/* 分割线 */}
      <View style={styles.commentDivider} />
    </View>
  );
});

const EmptyComponent = memo(() => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);

  return (
    <View style={styles.emptyContainer}>
      <Icon name="chat-bubble-outline" size={48} color={colors.novelTextGray} />
      <Text style={styles.emptyText}>暂无评论</Text>
      <Text style={styles.emptySubText}>快来发表第一条评论吧</Text>
    </View>
  );
});

const LoadingComponent = memo(() => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);

  return (
    <View style={styles.loadingContainer}>
      <ActivityIndicator size="small" color={colors.novelMain} />
      <Text style={styles.loadingText}>加载中...</Text>
    </View>
  );
});

const FooterComponent = memo(() => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const { loading, hasMore, comments } = useCommentStore();

  if (comments.length === 0) {return null;}

  if (loading) {
    return (
      <View style={styles.loadMoreContainer}>
        <ActivityIndicator size="small" color={colors.novelMain} />
        <Text style={styles.loadMoreText}>加载更多...</Text>
      </View>
    );
  }

  if (!hasMore) {
    return (
      <View style={styles.endContainer}>
        <View style={styles.endLine} />
        <Text style={styles.endText}>没有更多了</Text>
        <View style={styles.endLine} />
      </View>
    );
  }

  return null;
});

export const CommentList = memo(({ onEndReached, refreshControl, ListHeaderComponent, bookInfo }: CommentListProps & { bookInfo?: { bookId: string; bookName: string; authorName: string; picUrl: string } }) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const { comments, loading } = useCommentStore();

  const renderItem = ({ item }: { item: Comment }) => (
    <CommentItem item={item} bookInfo={bookInfo} />
  );

  if (loading && comments.length === 0) {
    return <LoadingComponent />;
  }

  return (
    <FlatList
      data={comments}
      renderItem={renderItem}
      keyExtractor={(item: { id: any; }) => item.id}
      contentContainerStyle={styles.commentList}
      showsVerticalScrollIndicator={false}
      onEndReached={onEndReached}
      onEndReachedThreshold={0.3}
      refreshControl={refreshControl}
      ListHeaderComponent={ListHeaderComponent}
      ListEmptyComponent={EmptyComponent}
      ListFooterComponent={FooterComponent}
      removeClippedSubviews={true}
      maxToRenderPerBatch={10}
      windowSize={10}
      initialNumToRender={10}
      getItemLayout={(data: any, index: number) => ({
        length: 150, // 估算的item高度
        offset: 150 * index,
        index,
      })}
    />
  );
});
