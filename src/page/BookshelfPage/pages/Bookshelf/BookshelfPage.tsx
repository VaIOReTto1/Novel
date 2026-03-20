import React, { useEffect, useCallback } from 'react';
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

export const BookshelfPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);

  // Store hooks
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

  // Custom hooks
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

  // 初始化数据
  useEffect(() => {
    loadBookshelfItems();
    loadRecommendations();
  }, [loadBookshelfItems, loadRecommendations]);

  // 处理书籍点击
  const handleBookPress = useCallback((item: BookshelfItem) => {
    if (isEditMode) {
      toggleItemSelection(item.id);
    } else {
      // 跳转到阅读页面
      console.log('Open book:', item.title);
    }
  }, [isEditMode, toggleItemSelection]);

  // 处理书籍长按
  const handleBookLongPress = useCallback((item: BookshelfItem) => {
    if (!isEditMode) {
      enterEditMode();
      toggleItemSelection(item.id);
    }
  }, [isEditMode, enterEditMode, toggleItemSelection]);

  // 处理推荐书籍点击
  const handleRecommendationPress = useCallback((item: RecommendationItem) => {
    // 跳转到书籍详情页面
    console.log('Open recommendation:', item.title);
  }, []);

  // 处理菜单按钮点击
  const handleMenuPress = useCallback((item: BookshelfItem) => {
    console.log('Menu pressed for book:', item.title);
    // TODO: 实现菜单功能
  }, []);

  // 处理筛选按钮点击
  const handleFilterPress = useCallback(() => {
    console.log('Filter pressed');
    // TODO: 实现筛选功能
  }, []);

  // 处理编辑按钮点击
  const handleEditPress = useCallback(() => {
    if (isEditMode) {
      exitEditMode();
    } else {
      enterEditMode();
    }
  }, [isEditMode, exitEditMode, enterEditMode]);


  return (
    <SafeAreaView style={styles.container}>
      {/* TopBar */}
      <TopBar
        currentView={currentView}
        onViewChange={switchView}
        onFilterPress={handleFilterPress}
        onEditPress={handleEditPress}
        isTransitioning={isTransitioning}
        isEditMode={isEditMode}
      />

      {/* 编辑工具栏 */}
      {isEditMode && (
        <EditToolbar
          isEditMode={isEditMode}
          selectedCount={selectedCount}
          totalCount={bookshelfItems.length}
          onEnterEdit={enterEditMode}
          onExitEdit={exitEditMode}
          onSelectAll={selectAllItems}
          onDelete={deleteSelectedItems}
        />
      )}

      {/* 统一滚动视图 */}
      <UnifiedScrollView
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
