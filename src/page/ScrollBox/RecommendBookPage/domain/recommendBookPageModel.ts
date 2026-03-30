type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapRecommendBookPageDeps = {
  loadInitialData: () => Promise<void>;
  logger?: Logger;
};

type CreateRecommendBookPageHandlersDeps = {
  navigateBack: () => void;
  setSelectedTaskTab: (tab: 'recommend' | 'book') => void;
  handleViewAllTasks: () => void;
  handleServicePress: (serviceId: string) => void;
  handleTaskPress: (taskId: string) => void;
  showWithdrawAlert: () => void;
};

const defaultLogger: Logger = console;

export const bootstrapRecommendBookPage = async ({
  loadInitialData,
  logger = defaultLogger,
}: BootstrapRecommendBookPageDeps): Promise<void> => {
  try {
    await loadInitialData();
  } catch (error) {
    logger.error('[RecommendBookPage] Failed to bootstrap page data', error);
  }
};

export const createRecommendBookPageHandlers = ({
  navigateBack,
  setSelectedTaskTab,
  handleViewAllTasks,
  handleServicePress,
  handleTaskPress,
  showWithdrawAlert,
}: CreateRecommendBookPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleTaskTabChange: (tab: 'recommend' | 'book') => {
    setSelectedTaskTab(tab);
  },

  handleWithdrawPress: () => {
    showWithdrawAlert();
  },

  handleViewAllTasksPress: () => {
    handleViewAllTasks();
  },

  handleServiceItemPress: (serviceId: string) => {
    handleServicePress(serviceId);
  },

  handleTaskItemPress: (taskId: string) => {
    handleTaskPress(taskId);
  },
});
