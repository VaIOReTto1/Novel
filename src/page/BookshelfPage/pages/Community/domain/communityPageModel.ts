import { CommunitySortType } from '../types';

type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapCommunityPageDeps = {
  loadPosts: () => Promise<void>;
  logger?: Logger;
};

type CreateCommunityPageHandlersDeps = {
  loading: boolean;
  hasMore: boolean;
  setActiveTab: (tabId: string) => void;
  setSelectedCategory: (categoryId: string) => void;
  setSelectedCircle: (circleId: string) => void;
  setSortType: (sort: CommunitySortType) => void;
  refreshPosts: () => void;
  loadMorePosts: () => void;
  likePosts: (postId: string) => void;
  clearError: () => void;
  loadPosts: () => Promise<void>;
  logger?: Logger;
};

const defaultLogger: Logger = console;

export const bootstrapCommunityPage = async ({
  loadPosts,
  logger = defaultLogger,
}: BootstrapCommunityPageDeps): Promise<void> => {
  try {
    await loadPosts();
  } catch (error) {
    logger.error('[CommunityPage] Failed to bootstrap posts', error);
  }
};

export const createCommunityPageHandlers = ({
  loading,
  hasMore,
  setActiveTab,
  setSelectedCategory,
  setSelectedCircle,
  setSortType,
  refreshPosts,
  loadMorePosts,
  likePosts,
  clearError,
  loadPosts,
}: CreateCommunityPageHandlersDeps) => ({
  handleTabChange: (tabId: string) => {
    setActiveTab(tabId);
  },

  handleCategoryChange: (categoryId: string) => {
    setSelectedCategory(categoryId);
  },

  handleCircleChange: (circleId: string) => {
    setSelectedCircle(circleId);
  },

  handleSortChange: (sort: CommunitySortType) => {
    setSortType(sort);
  },

  handleRefresh: () => {
    refreshPosts();
  },

  handleLoadMore: () => {
    if (!loading && hasMore) {
      loadMorePosts();
    }
  },

  handleLike: (postId: string) => {
    likePosts(postId);
  },

  handleRetry: () => {
    clearError();
    loadPosts();
  },
});
