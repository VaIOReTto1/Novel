import { useCallback } from 'react';
import { useWatchlistStore } from '../store/watchlistStore';

export const useEditMode = () => {
  const {
    isEditing,
    selectedItems,
    watchlistItems,
    setEditMode,
    toggleItemSelection,
    selectAllItems,
    clearSelection,
    removeWatchlistItems,
  } = useWatchlistStore();

  const hasSelectedItems = selectedItems.size > 0;
  const selectedCount = selectedItems.size;
  const isAllSelected = selectedItems.size === watchlistItems.length && watchlistItems.length > 0;

  const enterEditMode = useCallback(() => {
    setEditMode(true);
  }, [setEditMode]);

  const exitEditMode = useCallback(() => {
    setEditMode(false);
    clearSelection();
  }, [setEditMode, clearSelection]);

  const handleToggleItemSelection = useCallback((id: string) => {
    toggleItemSelection(id);
  }, [toggleItemSelection]);

  const handleSelectAll = useCallback(() => {
    if (isAllSelected) {
      clearSelection();
    } else {
      selectAllItems();
    }
  }, [isAllSelected, clearSelection, selectAllItems]);

  const deleteSelectedItems = useCallback(async () => {
    if (selectedItems.size === 0) return;
    
    const idsToDelete = Array.from(selectedItems);
    await removeWatchlistItems(idsToDelete);
  }, [selectedItems, removeWatchlistItems]);

  return {
    isEditMode: isEditing,
    hasSelectedItems,
    selectedCount,
    isAllSelected,
    selectedItems,
    enterEditMode,
    exitEditMode,
    toggleItemSelection: handleToggleItemSelection,
    selectAllItems: handleSelectAll,
    deleteSelectedItems,
  };
};