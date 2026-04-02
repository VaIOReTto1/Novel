import React, { useEffect, useCallback, useMemo } from 'react';
import { SafeAreaView } from 'react-native';
import { useBookshelfStore, useRecommendationStore } from './store/bookshelfStore';
import { useViewMode } from './hooks/useViewMode';
import { useEditMode } from './hooks/useEditMode';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { TopBar } from './components/TopBar';
import { EditToolbar } from './components/EditToolbar';
import { UnifiedScrollView } from './components/UnifiedScrollView';
import { createBookshelfPageStyles } from './styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../utils/theme';
import { BookshelfItem, RecommendationItem } from './types';
import {
  bootstrapBookshelfPage,
  createBookshelfPageHandlers,
} from './domain/bookshelfPageModel';

export const BookshelfPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);

  const {
    bookshelfItems,
    isLoading: isBookshelfLoading,
    hasMore: hasMoreBookshelf,
    selectedItems,
    loadBookshelfItems,
  } = useBookshelfStore();

  const {
    recommendations,
    isLoading: isRecommendationLoading,
    hasMore: hasMoreRecommendations,
    loadRecommendations,
  } = useRecommendationStore();

  const {
    currentView,
    isTransitioning,
    switchView,
  } = useViewMode('grid');

  const {
    isEditMode,
    selectedCount,
    enterEditMode,
    exitEditMode,
    toggleItemSelection,
    selectAllItems,
    deleteSelectedItems,
  } = useEditMode();

  const {
    isRefreshing,
    onRefresh,
    onLoadMore,
    onLoadMoreRecommendations,
  } = useRefreshLogic();

  useEffect(() => {
    bootstrapBookshelfPage({
      loadBookshelfItems,
      loadRecommendations,
    });
  }, [loadBookshelfItems, loadRecommendations]);

  const bookshelfHandlers = useMemo(
    () =>
      createBookshelfPageHandlers({
        isEditMode,
        enterEditMode,
        exitEditMode,
        toggleItemSelection,
      }),
    [enterEditMode, exitEditMode, isEditMode, toggleItemSelection],
  );

  const handleBookPress = useCallback((item: BookshelfItem) => {
    bookshelfHandlers.handleBookPress(item);
  }, [bookshelfHandlers]);

  const handleBookLongPress = useCallback((item: BookshelfItem) => {
    bookshelfHandlers.handleBookLongPress(item);
  }, [bookshelfHandlers]);

  const handleRecommendationPress = useCallback((item: RecommendationItem) => {
    bookshelfHandlers.handleRecommendationPress(item);
  }, [bookshelfHandlers]);

  const handleMenuPress = useCallback((item: BookshelfItem) => {
    bookshelfHandlers.handleMenuPress(item);
  }, [bookshelfHandlers]);

  const handleFilterPress = useCallback(() => {
    bookshelfHandlers.handleFilterPress();
  }, [bookshelfHandlers]);

  const handleEditPress = useCallback(() => {
    bookshelfHandlers.handleEditPress();
  }, [bookshelfHandlers]);

  return (
    <SafeAreaView style={styles.container}>
      <TopBar
        styles={styles}
        currentView={currentView}
        onViewChange={switchView}
        onFilterPress={handleFilterPress}
        onEditPress={handleEditPress}
        isTransitioning={isTransitioning}
        isEditMode={isEditMode}
      />

      {isEditMode && (
        <EditToolbar
          styles={styles}
          isEditMode={isEditMode}
          selectedCount={selectedCount}
          totalCount={bookshelfItems.length}
          onEnterEdit={enterEditMode}
          onExitEdit={exitEditMode}
          onSelectAll={selectAllItems}
          onDelete={deleteSelectedItems}
        />
      )}

      <UnifiedScrollView
        styles={styles}
        bookshelfData={bookshelfItems}
        recommendations={recommendations}
        currentView={currentView}
        isEditMode={isEditMode}
        selectedItems={Array.from(selectedItems)}
        onBookPress={handleBookPress}
        onBookLongPress={handleBookLongPress}
        onMenuPress={handleMenuPress}
        onRecommendationPress={handleRecommendationPress}
        onLoadMore={onLoadMore}
        hasMore={hasMoreBookshelf}
        isLoading={isBookshelfLoading}
        onLoadMoreRecommendations={onLoadMoreRecommendations}
        hasMoreRecommendations={hasMoreRecommendations}
        isRecommendationLoading={isRecommendationLoading}
        isRefreshing={isRefreshing}
        onRefresh={onRefresh}
      />
    </SafeAreaView>
  );
};
