import React, { useEffect, useMemo } from 'react';
import { View, SafeAreaView } from 'react-native';
import { createWatchlistPageStyles } from './styles/WatchlistPageStyles';
import { useNovelColors } from '../../../../utils/theme';
import { TopBar } from './components/TopBar';
import { WatchlistGrid } from './components/WatchlistGrid';
import { EmptyState } from './components/EmptyState';
import { EditToolbar } from './components/EditToolbar';
import { useWatchlistStore } from './store/watchlistStore';
import { useEditMode } from './hooks/useEditMode';
import {
  bootstrapWatchlistPage,
  createWatchlistPageHandlers,
} from './domain/watchlistPageModel';

export const WatchlistPage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWatchlistPageStyles(colors);

  const {
    watchlistItems,
    isLoading,
    loadWatchlistItems,
  } = useWatchlistStore();

  const {
    isEditMode,
    selectedItems,
    enterEditMode,
    exitEditMode,
    toggleItemSelection,
    selectAllItems,
    deleteSelectedItems,
    isAllSelected,
  } = useEditMode();

  useEffect(() => {
    bootstrapWatchlistPage({
      loadWatchlistItems,
    });
  }, [loadWatchlistItems]);

  const watchlistHandlers = useMemo(
    () =>
      createWatchlistPageHandlers({
        isEditMode,
        enterEditMode,
        exitEditMode,
        selectAllItems,
        deleteSelectedItems,
        toggleItemSelection,
      }),
    [
      deleteSelectedItems,
      enterEditMode,
      exitEditMode,
      isEditMode,
      selectAllItems,
      toggleItemSelection,
    ],
  );

  return (
    <SafeAreaView style={styles.container}>
      <TopBar
        onEditPress={watchlistHandlers.handleEditPress}
        isEditMode={isEditMode}
      />

      {isEditMode && (
        <EditToolbar
          selectedCount={selectedItems.size}
          totalCount={watchlistItems.length}
          onSelectAll={watchlistHandlers.handleSelectAll}
          onDelete={watchlistHandlers.handleDelete}
          isAllSelected={isAllSelected}
        />
      )}

      <View style={styles.content}>
        {watchlistItems.length === 0 ? (
          <EmptyState onFindDramas={watchlistHandlers.handleFindDramas} />
        ) : (
          <WatchlistGrid
            data={watchlistItems}
            isEditMode={isEditMode}
            selectedItems={selectedItems}
            onItemPress={watchlistHandlers.handleItemPress}
            onItemSelect={watchlistHandlers.handleItemSelect}
            loading={isLoading}
          />
        )}
      </View>
    </SafeAreaView>
  );
};

console.log('[WatchlistPage] Component loaded');
