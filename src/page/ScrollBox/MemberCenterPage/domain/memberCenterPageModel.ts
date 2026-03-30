type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapMemberCenterPageDeps = {
  loadInitialData: () => Promise<void>;
  logger?: Logger;
};

type CreateMemberCenterPageHandlersDeps = {
  navigateBack: () => void;
  setCurrentCard: (index: number) => void;
  selectPricePackage: (packageId: string) => void;
  handlePurchase: () => void;
  handlePrivacyPress: () => void;
  handleTermsPress: () => void;
  handleTaskPress: (taskId: string) => void;
};

const defaultLogger: Logger = console;

export const bootstrapMemberCenterPage = async ({
  loadInitialData,
  logger = defaultLogger,
}: BootstrapMemberCenterPageDeps): Promise<void> => {
  try {
    await loadInitialData();
  } catch (error) {
    logger.error('[MemberCenterPage] Failed to bootstrap page data', error);
  }
};

export const createMemberCenterPageHandlers = ({
  navigateBack,
  setCurrentCard,
  selectPricePackage,
  handlePurchase,
  handlePrivacyPress,
  handleTermsPress,
  handleTaskPress,
}: CreateMemberCenterPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleCardChange: (index: number) => {
    setCurrentCard(index);
  },

  handlePackageSelect: (packageId: string) => {
    selectPricePackage(packageId);
  },

  handlePurchasePress: () => {
    handlePurchase();
  },

  handlePrivacyLinkPress: () => {
    handlePrivacyPress();
  },

  handleTermsLinkPress: () => {
    handleTermsPress();
  },

  handleTaskCardPress: (taskId: string) => {
    handleTaskPress(taskId);
  },
});
