type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapWatchlistPageDeps = {
  loadWatchlistItems: () => Promise<void>;
  logger?: Logger;
};

type WatchlistItemLike = {
  id: string;
  title?: string;
};

type CreateWatchlistPageHandlersDeps = {
  isEditMode: boolean;
  enterEditMode: () => void;
  exitEditMode: () => void;
  selectAllItems: () => void;
  deleteSelectedItems: () => Promise<void>;
  toggleItemSelection: (itemId: string) => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapWatchlistPage = async ({
  loadWatchlistItems,
  logger = defaultLogger,
}: BootstrapWatchlistPageDeps): Promise<void> => {
  try {
    await loadWatchlistItems();
  } catch (error) {
    logger.error('[WatchlistPage] Failed to bootstrap watchlist data', error);
  }
};

export const createWatchlistPageHandlers = ({
  isEditMode,
  enterEditMode,
  exitEditMode,
  selectAllItems,
  deleteSelectedItems,
  toggleItemSelection,
  logger = defaultLogger,
}: CreateWatchlistPageHandlersDeps) => ({
  handleEditPress: () => {
    if (isEditMode) {
      exitEditMode();
      return;
    }
    enterEditMode();
  },

  handleSelectAll: () => {
    selectAllItems();
  },

  handleDelete: async () => {
    await deleteSelectedItems();
  },

  handleItemSelect: (item: WatchlistItemLike) => {
    toggleItemSelection(item.id);
  },

  handleItemPress: (item: WatchlistItemLike) => {
    logger.log('[WatchlistPage] Play drama', item.title);
  },

  handleFindDramas: () => {
    logger.log('[WatchlistPage] Navigate to discover page');
  },
});
