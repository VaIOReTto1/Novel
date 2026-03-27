import React, { useEffect } from 'react';
import { View, SafeAreaView } from 'react-native';
import { createWatchlistPageStyles } from './styles/WatchlistPageStyles';
import { useNovelColors } from '../../../../utils/theme';
import { TopBar } from './components/TopBar';
import { WatchlistGrid } from './components/WatchlistGrid';
import { EmptyState } from './components/EmptyState';
import { EditToolbar } from './components/EditToolbar';
import { useWatchlistStore } from './store/watchlistStore';
import { useEditMode } from './hooks/useEditMode';

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
    loadWatchlistItems();
  }, [loadWatchlistItems]);

  const handleItemPress = (item: any) => {
    // TODO: 导航到播放页面
    console.log('Play drama:', item.title);
  };

  const handleFindDramas = () => {
    // TODO: 导航到发现页面
    console.log('Navigate to discover page');
  };

  const handleEditPress = () => {
    if (isEditMode) {
      exitEditMode();
    } else {
      enterEditMode();
    }
  };

  const handleSelectAll = () => {
    selectAllItems();
  };

  const handleDelete = async () => {
    await deleteSelectedItems();
  };

  return (
    <SafeAreaView style={styles.container}>
      <TopBar
        onEditPress={handleEditPress}
        isEditMode={isEditMode}
      />

      {isEditMode && (
        <EditToolbar
          selectedCount={selectedItems.size}
          totalCount={watchlistItems.length}
          onSelectAll={handleSelectAll}
          onDelete={handleDelete}
          isAllSelected={isAllSelected}
        />
      )}

      <View style={{ flex: 1 }}>
        {watchlistItems.length === 0 ? (
          <EmptyState onFindDramas={handleFindDramas} />
        ) : (
          <WatchlistGrid
            data={watchlistItems}
            isEditMode={isEditMode}
            selectedItems={selectedItems}
            onItemPress={handleItemPress}
            onItemSelect={(item) => toggleItemSelection(item.id)}
            loading={isLoading}
          />
        )}
      </View>
    </SafeAreaView>
  );
};

console.log('[WatchlistPage] Component loaded');
