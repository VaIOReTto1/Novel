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
  bookshelfData: BookshelfItem[];
  onBookPress: (item: BookshelfItem) => void;
  onBookLongPress: (item: BookshelfItem) => void;
  onMenuPress?: (item: BookshelfItem) => void;
  onLoadMore: () => void;
  hasMore: boolean;
  isLoading: boolean;
  recommendations: RecommendationItem[];
  onRecommendationPress: (item: RecommendationItem) => void;
  onLoadMoreRecommendations: () => void;
  hasMoreRecommendations: boolean;
  isRecommendationLoading: boolean;
  currentView: ViewType;
  isEditMode: boolean;
  selectedItems: string[];
  isRefreshing: boolean;
  onRefresh: () => void;
  styles?: ReturnType<typeof createBookshelfPageStyles>;
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
  styles,
}) => {
  const colors = useNovelColors();
  const ui = createNovelDesignUI(colors as any);
  const resolvedStyles = styles ?? createBookshelfPageStyles(colors);

  const renderBookshelfContent = useCallback(() => {
    const baseProps = {
      styles: resolvedStyles,
      data: bookshelfData,
      onBookPress,
      onBookLongPress,
      onMenuPress,
      onLoadMore,
      hasMore,
      isLoading,
      isEditMode,
      selectedItems,
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
    resolvedStyles,
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
      style={resolvedStyles.unifiedScrollContainer}
      showsVerticalScrollIndicator={false}
      refreshControl={
        <RefreshControl
          refreshing={isRefreshing}
          onRefresh={onRefresh}
          colors={[ui.color.brand.primary]}
          tintColor={ui.color.brand.primary}
        />
      }>
      <View style={resolvedStyles.bookshelfContentWrapper}>
        {renderBookshelfContent()}
      </View>

      <View style={resolvedStyles.recommendationWrapper}>
        <RecommendationFlow
          styles={resolvedStyles}
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
