type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapBookManagePageDeps = {
  load: () => Promise<void>;
  logger?: Logger;
};

type CreateBookManagePageHandlersDeps = {
  navigateBack: () => void;
  navigateToWritePage: () => void;
  navigateToBookManage: () => void;
};

const defaultLogger: Logger = console;

export const bootstrapBookManagePage = async ({
  load,
  logger = defaultLogger,
}: BootstrapBookManagePageDeps): Promise<void> => {
  try {
    await load();
  } catch (error) {
    logger.error('[BookManagePage] Failed to load page data', error);
  }
};

export const createBookManagePageHandlers = ({
  navigateBack,
  navigateToWritePage,
  navigateToBookManage,
}: CreateBookManagePageHandlersDeps) => ({
  handleBack: () => {
    navigateBack();
  },

  handleContinueDraft: () => {
    navigateToWritePage();
  },

  handleOpenBookManage: () => {
    navigateToBookManage();
  },

  handleCreateChapter: () => {
    navigateToWritePage();
  },
});
