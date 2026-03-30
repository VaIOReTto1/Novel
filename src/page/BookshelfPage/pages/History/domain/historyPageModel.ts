type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapBookshelfHistoryPageDeps = {
  loadHistoryItems: () => Promise<void>;
  logger?: Logger;
};

type MinimalHistoryItem = {
  id: string;
  title?: string;
};

type CreateHistoryPageHandlersDeps = {
  setCurrentTab: (tabId: 'all' | 'book' | 'listening' | 'drama') => void;
  clearSelection: () => void;
  setEditing: (editing: boolean) => void;
  removeSelectedItems: () => Promise<void>;
  toggleShelfStatus: (item: MinimalHistoryItem) => Promise<void>;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapBookshelfHistoryPage = async ({
  loadHistoryItems,
  logger = defaultLogger,
}: BootstrapBookshelfHistoryPageDeps): Promise<void> => {
  try {
    await loadHistoryItems();
  } catch (error) {
    logger.error('[BookshelfHistoryPage] Failed to bootstrap history data', error);
  }
};

export const createHistoryPageHandlers = ({
  setCurrentTab,
  clearSelection,
  setEditing,
  removeSelectedItems,
  toggleShelfStatus,
  logger = defaultLogger,
}: CreateHistoryPageHandlersDeps) => ({
  handleTabPress: (tabId: 'all' | 'book' | 'listening' | 'drama') => {
    logger.log('[BookshelfHistoryPage] Tab changed', tabId);
    setCurrentTab(tabId);
    clearSelection();
  },

  handleEditToggle: (isEditing: boolean) => {
    const nextEditingState = !isEditing;
    logger.log('[BookshelfHistoryPage] Edit mode toggled', nextEditingState);
    setEditing(nextEditingState);
    if (!nextEditingState) {
      clearSelection();
    }
  },

  handleRemoveSelected: async () => {
    await removeSelectedItems();
    setEditing(false);
  },

  handleAddToShelf: async (item: MinimalHistoryItem) => {
    try {
      await toggleShelfStatus(item);
    } catch (error) {
      logger.error('[BookshelfHistoryPage] Failed to toggle shelf status', error);
    }
  },
});
