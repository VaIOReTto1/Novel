type Logger = Pick<Console, 'log' | 'error'>;

type EmptyStateData = {
  icon: string;
  title: string;
  buttonText?: string;
};

type BootstrapMyReservationPageDeps = {
  loadInitialData: () => Promise<void>;
  logger?: Logger;
};

type CreateMyReservationPageHandlersDeps = {
  navigateBack: () => void;
  setSelectedTab: (tab: 'new' | 'mine') => void;
  setSelectedSubTab: (tab: 'online' | 'offline') => void;
  handleReserveItem: (itemId: string) => void;
  handleItemPress: (itemId: string) => void;
};

type GetMyReservationContentStateArgs = {
  selectedTab: 'new' | 'mine';
  selectedSubTab: 'online' | 'offline';
  newReservations: Array<{ id: string }>;
  myOnlineReservations: Array<{ id: string }>;
  myOfflineReservations: Array<{ id: string }>;
  emptyStates: Record<'online' | 'offline', EmptyStateData>;
};

const defaultLogger: Logger = console;

export const bootstrapMyReservationPage = async ({
  loadInitialData,
  logger = defaultLogger,
}: BootstrapMyReservationPageDeps): Promise<void> => {
  try {
    await loadInitialData();
  } catch (error) {
    logger.error('[MyReservationPage] Failed to bootstrap page data', error);
  }
};

export const createMyReservationPageHandlers = ({
  navigateBack,
  setSelectedTab,
  setSelectedSubTab,
  handleReserveItem,
  handleItemPress,
}: CreateMyReservationPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleMainTabChange: (tab: 'new' | 'mine') => {
    setSelectedTab(tab);
  },

  handleSubTabChange: (tab: 'online' | 'offline') => {
    setSelectedSubTab(tab);
  },

  handleReservePress: (itemId: string) => {
    handleReserveItem(itemId);
  },

  handleItemPress: (itemId: string) => {
    handleItemPress(itemId);
  },
});

export const getMyReservationContentState = ({
  selectedTab,
  selectedSubTab,
  newReservations,
  myOnlineReservations,
  myOfflineReservations,
  emptyStates,
}: GetMyReservationContentStateArgs): {
  items: Array<{ id: string }>;
  emptyData: EmptyStateData | null;
} => {
  if (selectedTab === 'new') {
    return {
      items: newReservations,
      emptyData: null,
    };
  }

  const items = selectedSubTab === 'online' ? myOnlineReservations : myOfflineReservations;
  return {
    items,
    emptyData: items.length === 0 ? emptyStates[selectedSubTab] : null,
  };
};
