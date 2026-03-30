import {
  bootstrapCommunityPage,
  createCommunityPageHandlers,
} from '../../src/page/BookshelfPage/pages/Community/domain/communityPageModel';

describe('community page model helpers', () => {
  test('bootstraps community page by loading posts once', async () => {
    const loadPosts = jest.fn().mockResolvedValue(undefined);

    await bootstrapCommunityPage({
      loadPosts,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(loadPosts).toHaveBeenCalledTimes(1);
  });

  test('delegates filter and sort actions to store setters', () => {
    const setActiveTab = jest.fn();
    const setSelectedCategory = jest.fn();
    const setSelectedCircle = jest.fn();
    const setSortType = jest.fn();
    const handlers = createCommunityPageHandlers({
      loading: false,
      hasMore: true,
      setActiveTab,
      setSelectedCategory,
      setSelectedCircle,
      setSortType,
      refreshPosts: jest.fn(),
      loadMorePosts: jest.fn(),
      likePosts: jest.fn(),
      clearError: jest.fn(),
      loadPosts: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleTabChange('following');
    handlers.handleCategoryChange('discussion');
    handlers.handleCircleChange('circle-1');
    handlers.handleSortChange('latest');

    expect(setActiveTab).toHaveBeenCalledWith('following');
    expect(setSelectedCategory).toHaveBeenCalledWith('discussion');
    expect(setSelectedCircle).toHaveBeenCalledWith('circle-1');
    expect(setSortType).toHaveBeenCalledWith('latest');
  });

  test('refresh and retry delegate to refreshPosts and clearError/loadPosts', () => {
    const refreshPosts = jest.fn();
    const clearError = jest.fn();
    const loadPosts = jest.fn();
    const handlers = createCommunityPageHandlers({
      loading: false,
      hasMore: true,
      setActiveTab: jest.fn(),
      setSelectedCategory: jest.fn(),
      setSelectedCircle: jest.fn(),
      setSortType: jest.fn(),
      refreshPosts,
      loadMorePosts: jest.fn(),
      likePosts: jest.fn(),
      clearError,
      loadPosts,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleRefresh();
    handlers.handleRetry();

    expect(refreshPosts).toHaveBeenCalledTimes(1);
    expect(clearError).toHaveBeenCalledTimes(1);
    expect(loadPosts).toHaveBeenCalledTimes(1);
  });

  test('load more only runs when not loading and more pages exist', () => {
    const loadMorePosts = jest.fn();
    const readyHandlers = createCommunityPageHandlers({
      loading: false,
      hasMore: true,
      setActiveTab: jest.fn(),
      setSelectedCategory: jest.fn(),
      setSelectedCircle: jest.fn(),
      setSortType: jest.fn(),
      refreshPosts: jest.fn(),
      loadMorePosts,
      likePosts: jest.fn(),
      clearError: jest.fn(),
      loadPosts: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });
    const blockedByLoadingHandlers = createCommunityPageHandlers({
      loading: true,
      hasMore: true,
      setActiveTab: jest.fn(),
      setSelectedCategory: jest.fn(),
      setSelectedCircle: jest.fn(),
      setSortType: jest.fn(),
      refreshPosts: jest.fn(),
      loadMorePosts,
      likePosts: jest.fn(),
      clearError: jest.fn(),
      loadPosts: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });
    const blockedByHasMoreHandlers = createCommunityPageHandlers({
      loading: false,
      hasMore: false,
      setActiveTab: jest.fn(),
      setSelectedCategory: jest.fn(),
      setSelectedCircle: jest.fn(),
      setSortType: jest.fn(),
      refreshPosts: jest.fn(),
      loadMorePosts,
      likePosts: jest.fn(),
      clearError: jest.fn(),
      loadPosts: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    readyHandlers.handleLoadMore();
    blockedByLoadingHandlers.handleLoadMore();
    blockedByHasMoreHandlers.handleLoadMore();

    expect(loadMorePosts).toHaveBeenCalledTimes(1);
  });

  test('like handler delegates to store action', () => {
    const likePosts = jest.fn();
    const handlers = createCommunityPageHandlers({
      loading: false,
      hasMore: true,
      setActiveTab: jest.fn(),
      setSelectedCategory: jest.fn(),
      setSelectedCircle: jest.fn(),
      setSortType: jest.fn(),
      refreshPosts: jest.fn(),
      loadMorePosts: jest.fn(),
      likePosts,
      clearError: jest.fn(),
      loadPosts: jest.fn(),
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    handlers.handleLike('post-1');

    expect(likePosts).toHaveBeenCalledWith('post-1');
  });
});
