const createScrollBoxHistoryItem = (overrides = {}) => ({
  id: 1,
  title: 'Real scroll history item',
  author: 'Author',
  description: 'Description',
  coverUrl: 'https://example.com/cover.png',
  lastReadTime: new Date('2026-03-20T10:00:00.000Z').toISOString(),
  readProgress: 50,
  type: 'book',
  categoryId: 1,
  readCount: 1,
  rating: 4.5,
  ...overrides,
});

const createBookshelfHistoryItem = (overrides = {}) => ({
  id: 'item-1',
  title: 'Real bookshelf history item',
  author: 'Author',
  description: 'Description',
  cover: 'https://example.com/cover.png',
  coverUrl: 'https://example.com/cover.png',
  lastReadTime: Date.parse('2026-03-20T10:00:00.000Z'),
  lastChapter: 'Chapter 1',
  readProgress: 50,
  progress: 50,
  type: 'book',
  categoryId: 1,
  readCount: 1,
  rating: 4.5,
  tags: [],
  isFinished: false,
  updateStatus: 'ongoing',
  isInShelf: false,
  ...overrides,
});

const loadScrollBoxHistoryStore = () => {
  jest.resetModules();
  jest.doMock('../../src/utils/bridge/NavigationBridge', () => ({
    NavigationBridge: {
      getReadingHistory: jest.fn().mockResolvedValue({
        historyItems: [],
        success: true,
      }),
    },
  }));

  return require('../../src/page/ScrollBox/HistoryPage/store/historyStore') as typeof import('../../src/page/ScrollBox/HistoryPage/store/historyStore');
};

const loadBookshelfHistoryStore = () => {
  jest.resetModules();
  jest.doMock('../../src/utils/bridge/NavigationBridge', () => ({
    NavigationBridge: {
      getReadingHistory: jest.fn().mockResolvedValue({
        historyItems: [],
        success: true,
      }),
    },
  }));

  return require('../../src/page/BookshelfPage/pages/History/store/historyStore') as typeof import('../../src/page/BookshelfPage/pages/History/store/historyStore');
};

describe('history stores mock closure', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.useRealTimers();
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('keeps ScrollBox history loadMore as a deterministic no-op once only real items remain', async () => {
    const { useHistoryStore } = loadScrollBoxHistoryStore();
    const existingItems = [createScrollBoxHistoryItem()];

    useHistoryStore.setState({
      historyItems: existingItems,
      cachedHistoryItems: existingItems,
      currentPage: 1,
      pageSize: 20,
      hasMore: true,
      loading: false,
      error: null,
      selectedTab: 'all',
    });

    const loadMorePromise = useHistoryStore.getState().loadMoreHistory();

    await jest.runAllTimersAsync();
    await loadMorePromise;

    expect(useHistoryStore.getState().historyItems).toEqual(existingItems);
    expect(useHistoryStore.getState().cachedHistoryItems).toEqual(existingItems);
    expect(useHistoryStore.getState().hasMore).toBe(false);
    expect(useHistoryStore.getState().currentPage).toBe(1);
  });

  it('keeps Bookshelf history loadMore as a deterministic no-op once only real items remain', async () => {
    const { useHistoryStore } = loadBookshelfHistoryStore();
    const existingItems = [createBookshelfHistoryItem()];

    useHistoryStore.setState({
      historyItems: existingItems,
      cachedHistoryItems: existingItems,
      currentPage: 1,
      pageSize: 20,
      hasMore: true,
      isLoadingMore: false,
      error: null,
      currentTab: 'all',
      sortType: 'lastRead',
    });

    const loadMorePromise = useHistoryStore.getState().loadMoreHistory();

    await jest.runAllTimersAsync();
    await loadMorePromise;

    expect(useHistoryStore.getState().historyItems).toEqual(existingItems);
    expect(useHistoryStore.getState().cachedHistoryItems).toEqual(existingItems);
    expect(useHistoryStore.getState().hasMore).toBe(false);
    expect(useHistoryStore.getState().currentPage).toBe(1);
  });
});
