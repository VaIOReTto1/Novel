import React, { useCallback } from 'react';
import { View, ScrollView, RefreshControl } from 'react-native';
import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { BookshelfItem, RecommendationItem, ViewType } from '../types';
import { GridView } from './GridView';
import { ListView } from './ListView';
import { WaterfallGrid } from './WaterfallGrid';
import { RecommendationFlow } from './RecommendationFlow';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface UnifiedScrollViewProps {
  // 书架数据
  bookshelfData: BookshelfItem[];
  onBookPress: (item: BookshelfItem) => void;
  onBookLongPress: (item: BookshelfItem) => void;
  onMenuPress?: (item: BookshelfItem) => void;
  onLoadMore: () => void;
  hasMore: boolean;
  isLoading: boolean;

  // 推荐数据
  recommendations: RecommendationItem[];
  onRecommendationPress: (item: RecommendationItem) => void;
  onLoadMoreRecommendations: () => void;
  hasMoreRecommendations: boolean;
  isRecommendationLoading: boolean;

  // 视图状态
  currentView: ViewType;
  isEditMode: boolean;
  selectedItems: string[];

  // 刷新控制
  isRefreshing: boolean;
  onRefresh: () => void;
}

export const UnifiedScrollView: React.FC<UnifiedScrollViewProps> = ({
  bookshelfData,
  onBookPress,
  onBookLongPress,
  onMenuPress,
  onLoadMore,
  hasMore,
  isLoading,
  recommendations,
  onRecommendationPress,
  onLoadMoreRecommendations,
  hasMoreRecommendations,
  isRecommendationLoading,
  currentView,
  isEditMode,
  selectedItems,
  isRefreshing,
  onRefresh,
}) => {
  const colors = useNovelColors();
  const ui = createNovelDesignUI(colors as any);
  const styles = createBookshelfPageStyles(colors);

  // 渲染书架内容视图
  const renderBookshelfContent = useCallback(() => {
    const baseProps = {
      data: bookshelfData,
      onBookPress,
      onBookLongPress,
      onMenuPress,
      onLoadMore,
      hasMore,
      isLoading,
      isEditMode,
      selectedItems,
      // 禁用内部的刷新控制，由外层统一控制
      isRefreshing: false,
      onRefresh: undefined,
    };

    switch (currentView) {
      case 'grid':
        return <GridView {...baseProps} />;
      case 'list':
        return <ListView {...baseProps} />;
      case 'waterfall':
        return <WaterfallGrid {...baseProps} />;
      default:
        return <GridView {...baseProps} />;
    }
  }, [
    bookshelfData,
    onBookPress,
    onBookLongPress,
    onMenuPress,
    onLoadMore,
    hasMore,
    isLoading,
    isEditMode,
    selectedItems,
    currentView,
  ]);

  return (
    <ScrollView
      style={styles.unifiedScrollContainer}
      showsVerticalScrollIndicator={false}
      refreshControl={
        <RefreshControl
          refreshing={isRefreshing}
          onRefresh={onRefresh}
          colors={[ui.color.brand.primary]}
          tintColor={ui.color.brand.primary}
        />
      }
    >
      {/* 书架内容区域 */}
      <View style={styles.bookshelfContentWrapper}>
        {renderBookshelfContent()}
      </View>

      {/* 推荐流区域 */}
      <View style={styles.recommendationWrapper}>
        <RecommendationFlow
          data={recommendations}
          onBookPress={onRecommendationPress}
          onLoadMore={onLoadMoreRecommendations}
          hasMore={hasMoreRecommendations}
          isLoading={isRecommendationLoading}
          title="为你推荐"
        />
      </View>
    </ScrollView>
  );
};
