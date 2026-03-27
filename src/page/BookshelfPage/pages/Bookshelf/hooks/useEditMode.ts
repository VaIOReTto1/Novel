import { useState, useCallback } from 'react';
import { useBookshelfStore } from '../store/bookshelfStore';

export const useEditMode = () => {
  const [isEditMode, setIsEditMode] = useState(false);
  const { selectedItems, toggleItemSelection, selectAllItems, clearSelection, removeBookshelfItems } = useBookshelfStore();

  const enterEditMode = useCallback(() => {
    setIsEditMode(true);
    clearSelection();
  }, [clearSelection]);

  const exitEditMode = useCallback(() => {
    setIsEditMode(false);
    clearSelection();
  }, [clearSelection]);

  const handleToggleItemSelection = useCallback((itemId: string) => {
    toggleItemSelection(itemId);
  }, [toggleItemSelection]);

  const handleSelectAllItems = useCallback(() => {
    selectAllItems();
  }, [selectAllItems]);

  const deleteSelectedItems = useCallback(async () => {
    if (selectedItems.size === 0) {return;}

    try {
      const itemIds = Array.from(selectedItems);
      await removeBookshelfItems(itemIds);
      exitEditMode();
    } catch (error) {
      console.error('Delete items failed:', error);
    }
  }, [selectedItems, removeBookshelfItems, exitEditMode]);

  const hasSelectedItems = selectedItems.size > 0;
  const selectedCount = selectedItems.size;

  return {
    isEditMode,
    hasSelectedItems,
    selectedCount,
    selectedItems,
    enterEditMode,
    exitEditMode,
    toggleItemSelection: handleToggleItemSelection,
    selectAllItems: handleSelectAllItems,
    deleteSelectedItems,
  };
};
