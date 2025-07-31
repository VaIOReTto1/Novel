import React from 'react';
import { View, Text, ScrollView, NativeSyntheticEvent, NativeScrollEvent, TouchableOpacity, Image } from 'react-native';
import { RecommendationItem } from '../types';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface RecommendationFlowProps {
  data: RecommendationItem[];
  onBookPress?: (item: RecommendationItem) => void;
  onLoadMore?: () => void;
  hasMore?: boolean;
  isLoading?: boolean;
  title?: string;
}

export const RecommendationFlow: React.FC<RecommendationFlowProps> = ({
  data,
  onBookPress,
  onLoadMore,
  hasMore = false,
  isLoading = false,
  title = '推荐阅读',
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  const handleBookPress = (item: RecommendationItem) => {
    onBookPress?.(item);
  };

  // 如果没有推荐数据，不显示推荐流
  if (data.length === 0) {
    return null;
  }

  // 推荐项目组件
  const RecommendationItemComponent: React.FC<{ item: RecommendationItem }> = ({ item }) => (
    <TouchableOpacity
      style={styles.recommendationItem}
      onPress={() => handleBookPress(item)}
    >
      {/* 封面 */}
      <View style={styles.recommendationItemCover}>
        {(item.cover || item.coverUrl) ? (
          <Image
            source={{ uri: item.cover || item.coverUrl }}
            style={{ width: '100%', height: '100%' }}
            resizeMode="cover"
          />
        ) : (
          <View style={{ width: '100%', height: '100%', backgroundColor: '#f0f0f0' }} />
        )}
      </View>

      {/* 信息 */}
      <View style={styles.recommendationItemInfo}>
        <Text style={styles.recommendationItemTitle} numberOfLines={2}>
          {item.title}
        </Text>
        {item.description && (
          <Text style={styles.recommendationItemDescription} numberOfLines={3}>
            {item.description}
          </Text>
        )}
        {item.tags && item.tags.length > 0 && (
          <View style={styles.recommendationItemTag}>
            <Text style={styles.recommendationItemTagText}>{item.tags[0]}</Text>
          </View>
        )}
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.recommendationContainer}>
      {/* 推荐标题 */}
      <View style={styles.recommendationHeader}>
        <Text style={styles.recommendationTitle}>
          -- {title} --
        </Text>
      </View>

      {/* 推荐内容 - 瀑布流布局 */}
      <ScrollView
        style={styles.recommendationContent}
        showsVerticalScrollIndicator={false}
        onScroll={(event: NativeSyntheticEvent<NativeScrollEvent>) => {
          const { layoutMeasurement, contentOffset, contentSize } = event.nativeEvent;
          const paddingToBottom = 20;

          if (
            layoutMeasurement.height + contentOffset.y >=
            contentSize.height - paddingToBottom
          ) {
            if (hasMore && !isLoading) {
              onLoadMore?.();
            }
          }
        }}
        scrollEventThrottle={400}
      >
        <View style={styles.recommendationGrid}>
          {/* 左列 */}
          <View style={styles.recommendationColumn}>
            {data
              .filter((_, index) => index % 2 === 0)
              .map((item) => (
                <RecommendationItemComponent
                  key={item.id}
                  item={item}
                />
              ))
            }
          </View>

          {/* 右列 */}
          <View style={styles.recommendationColumn}>
            {data
              .filter((_, index) => index % 2 === 1)
              .map((item) => (
                <RecommendationItemComponent
                  key={item.id}
                  item={item}
                />
              ))
            }
          </View>
        </View>

        {/* 加载更多指示器 */}
        {(hasMore || isLoading) && (
          <LoadMoreIndicator
            isLoading={isLoading}
            onLoadMore={onLoadMore}
          />
        )}
      </ScrollView>
    </View>
  );
};