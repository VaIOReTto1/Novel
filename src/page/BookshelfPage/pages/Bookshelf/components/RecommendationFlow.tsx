import React from 'react';
import {
  View,
  Text,
  ScrollView,
  NativeSyntheticEvent,
  NativeScrollEvent,
  TouchableOpacity,
  Image,
} from 'react-native';

import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { RecommendationItem } from '../types';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

const recommendationCoverImageStyle = { width: '100%', height: '100%' } as const;

interface RecommendationFlowProps {
  data: RecommendationItem[];
  onBookPress?: (item: RecommendationItem) => void;
  onLoadMore?: () => void;
  hasMore?: boolean;
  isLoading?: boolean;
  title?: string;
  styles?: ReturnType<typeof createBookshelfPageStyles>;
}

interface RecommendationItemCardProps {
  item: RecommendationItem;
  styles: ReturnType<typeof createBookshelfPageStyles>;
  onBookPress?: (item: RecommendationItem) => void;
}

const RecommendationItemCard: React.FC<RecommendationItemCardProps> = ({
  item,
  styles,
  onBookPress,
}) => {
  const colors = useNovelColors();
  const ui = createNovelDesignUI(colors as any);

  return (
    <TouchableOpacity
      style={styles.recommendationItem}
      onPress={() => onBookPress?.(item)}>
      <View style={styles.recommendationItemCover}>
        {(item.cover || item.coverUrl) ? (
          <Image
            source={{ uri: item.cover || item.coverUrl }}
            style={recommendationCoverImageStyle}
            resizeMode="cover"
          />
        ) : (
          <View
            style={[
              recommendationCoverImageStyle,
              { backgroundColor: ui.color.bg.elevated },
            ]}
          />
        )}
      </View>

      <View style={styles.recommendationItemInfo}>
        <Text style={styles.recommendationItemTitle} numberOfLines={2}>
          {item.title}
        </Text>
        {item.description ? (
          <Text style={styles.recommendationItemDescription} numberOfLines={3}>
            {item.description}
          </Text>
        ) : null}
        {item.tags && item.tags.length > 0 ? (
          <View style={styles.recommendationItemTag}>
            <Text style={styles.recommendationItemTagText}>{item.tags[0]}</Text>
          </View>
        ) : null}
      </View>
    </TouchableOpacity>
  );
};

export const RecommendationFlow: React.FC<RecommendationFlowProps> = ({
  data,
  onBookPress,
  onLoadMore,
  hasMore = false,
  isLoading = false,
  title = '鐚滀綘鍠滄',
  styles,
}) => {
  const colors = useNovelColors();
  const resolvedStyles = styles ?? createBookshelfPageStyles(colors);

  if (data.length === 0) {
    return null;
  }

  return (
    <View style={resolvedStyles.recommendationContainer}>
      <View style={resolvedStyles.recommendationHeader}>
        <Text style={resolvedStyles.recommendationTitle}>-- {title} --</Text>
      </View>

      <ScrollView
        style={resolvedStyles.recommendationContent}
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
        scrollEventThrottle={400}>
        <View style={resolvedStyles.recommendationGrid}>
          <View style={resolvedStyles.recommendationColumn}>
            {data
              .filter((_, index) => index % 2 === 0)
              .map((item) => (
                <RecommendationItemCard
                  key={item.id}
                  item={item}
                  styles={resolvedStyles}
                  onBookPress={onBookPress}
                />
              ))}
          </View>

          <View style={resolvedStyles.recommendationColumn}>
            {data
              .filter((_, index) => index % 2 === 1)
              .map((item) => (
                <RecommendationItemCard
                  key={item.id}
                  item={item}
                  styles={resolvedStyles}
                  onBookPress={onBookPress}
                />
              ))}
          </View>
        </View>

        {(hasMore || isLoading) ? (
          <LoadMoreIndicator
            isLoading={isLoading}
            onLoadMore={onLoadMore}
          />
        ) : null}
      </ScrollView>
    </View>
  );
};
