type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapScrollBoxHistoryPageDeps = {
  loadHistoryItems: (refresh?: boolean) => Promise<void>;
  logger?: Logger;
};

type CreateScrollBoxHistoryPageHandlersDeps = {
  navigateBack: () => void;
  setSelectedTab: (tab: string) => void;
  setViewType: (type: 'grid' | 'list') => void;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapScrollBoxHistoryPage = async ({
  loadHistoryItems,
  logger = defaultLogger,
}: BootstrapScrollBoxHistoryPageDeps): Promise<void> => {
  try {
    await loadHistoryItems();
  } catch (error) {
    logger.error('[ScrollBoxHistoryPage] Failed to bootstrap page data', error);
  }
};

export const createScrollBoxHistoryPageHandlers = ({
  navigateBack,
  setSelectedTab,
  setViewType,
}: CreateScrollBoxHistoryPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleTabPress: (tabType: string) => {
    setSelectedTab(tabType);
  },

  handleViewTypeChange: (type: 'grid' | 'list') => {
    setViewType(type);
  },
});
