type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapViewedUsersPageDeps = {
  loadInitialData: () => Promise<void>;
  logger?: Logger;
};

type CreateViewedUsersPageHandlersDeps = {
  navigateBack: () => void;
  setSelectedTab: (tab: 'viewed' | 'recommend' | 'following' | 'fans') => void;
  handleFollowUser: (userId: string) => void;
  handleViewMoreRecommend: () => void;
  handleUserPress: (userId: string) => void;
};

type GetViewedUsersCurrentUsersArgs = {
  selectedTab: 'viewed' | 'recommend' | 'following' | 'fans';
  viewedUsers: Array<{ id: string }>;
  recommendUsers: Array<{ id: string }>;
  followingUsers: Array<{ id: string }>;
  fansUsers: Array<{ id: string }>;
};

const defaultLogger: Logger = console;

export const bootstrapViewedUsersPage = async ({
  loadInitialData,
  logger = defaultLogger,
}: BootstrapViewedUsersPageDeps): Promise<void> => {
  try {
    await loadInitialData();
  } catch (error) {
    logger.error('[ViewedUsersPage] Failed to bootstrap page data', error);
  }
};

export const createViewedUsersPageHandlers = ({
  navigateBack,
  setSelectedTab,
  handleFollowUser,
  handleViewMoreRecommend,
  handleUserPress,
}: CreateViewedUsersPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleTabChange: (tab: 'viewed' | 'recommend' | 'following' | 'fans') => {
    setSelectedTab(tab);
  },

  handleFollowPress: (userId: string) => {
    handleFollowUser(userId);
  },

  handleUserItemPress: (userId: string) => {
    handleUserPress(userId);
  },

  handleViewMorePress: () => {
    handleViewMoreRecommend();
  },
});

export const getViewedUsersCurrentUsers = ({
  selectedTab,
  viewedUsers,
  recommendUsers,
  followingUsers,
  fansUsers,
}: GetViewedUsersCurrentUsersArgs): Array<{ id: string }> => {
  switch (selectedTab) {
    case 'viewed':
      return viewedUsers;
    case 'recommend':
      return recommendUsers;
    case 'following':
      return followingUsers;
    case 'fans':
      return fansUsers;
    default:
      return [];
  }
};
