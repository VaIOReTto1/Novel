type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapBookshelfPageDeps = {
  loadBookshelfItems: () => Promise<void>;
  loadRecommendations: () => Promise<void>;
  logger?: Logger;
};

type MinimalBookshelfItem = {
  id: string;
  title?: string;
};

type MinimalRecommendationItem = {
  title?: string;
};

type CreateBookshelfPageHandlersDeps = {
  isEditMode: boolean;
  enterEditMode: () => void;
  exitEditMode: () => void;
  toggleItemSelection: (itemId: string) => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapBookshelfPage = async ({
  loadBookshelfItems,
  loadRecommendations,
  logger = defaultLogger,
}: BootstrapBookshelfPageDeps): Promise<void> => {
  try {
    await Promise.all([
      loadBookshelfItems(),
      loadRecommendations(),
    ]);
  } catch (error) {
    logger.error('[BookshelfPage] Failed to bootstrap page data', error);
  }
};

export const createBookshelfPageHandlers = ({
  isEditMode,
  enterEditMode,
  exitEditMode,
  toggleItemSelection,
  logger = defaultLogger,
}: CreateBookshelfPageHandlersDeps) => ({
  handleBookPress: (item: MinimalBookshelfItem) => {
    if (isEditMode) {
      toggleItemSelection(item.id);
      return;
    }
    logger.log('[BookshelfPage] Open book', item.title);
  },

  handleBookLongPress: (item: MinimalBookshelfItem) => {
    if (isEditMode) {
      return;
    }
    enterEditMode();
    toggleItemSelection(item.id);
  },

  handleRecommendationPress: (item: MinimalRecommendationItem) => {
    logger.log('[BookshelfPage] Open recommendation', item.title);
  },

  handleMenuPress: (item: MinimalBookshelfItem) => {
    logger.log('[BookshelfPage] Open menu', item.title);
  },

  handleFilterPress: () => {
    logger.log('[BookshelfPage] Open filter');
  },

  handleEditPress: () => {
    if (isEditMode) {
      exitEditMode();
      return;
    }
    enterEditMode();
  },
});
