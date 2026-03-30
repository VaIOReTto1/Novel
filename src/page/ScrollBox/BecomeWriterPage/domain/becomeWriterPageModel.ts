type Logger = Pick<Console, 'log' | 'error'>;

type NativeAuthorBook = {
  id?: string | number;
  bookName?: string;
  wordCount?: number;
};

type BootstrapBecomeWriterPageDeps = {
  isAuthor: boolean;
  loadInitialData: () => Promise<void>;
  getAuthorBooks: (pageNum?: number, pageSize?: number) => Promise<{ list?: NativeAuthorBook[] }>;
  setWorks: (works: Array<{ id: string; title: string; words: number }>) => void;
  logger?: Logger;
};

type CreateBecomeWriterPageHandlersDeps = {
  navigateBack: () => void;
  setSelectedDataTab: (tab: 'novel' | 'short') => void;
  setSelectedAuthorTab: (tab: 'benefits' | 'road' | 'platform') => void;
  setSelectedActivityTab: (tab: 'novel' | 'short') => void;
  navigateToAIPage: () => void;
  handleMorePress: (section: string) => void;
  navigateToBookManage: () => void;
  navigateToWritePage: () => void;
};

const defaultLogger: Logger = console;

const fallbackWorks = [{ id: 'm1', title: '我的新书', words: 0 }];

export const bootstrapBecomeWriterPage = async ({
  isAuthor,
  loadInitialData,
  getAuthorBooks,
  setWorks,
  logger = defaultLogger,
}: BootstrapBecomeWriterPageDeps): Promise<void> => {
  try {
    await loadInitialData();
  } catch (error) {
    logger.error('[BecomeWriterPage] Failed to bootstrap base data', error);
  }

  if (!isAuthor) {
    return;
  }

  try {
    const response = await getAuthorBooks(1, 50);
    const list = Array.isArray(response?.list) ? response.list : [];
    if (list.length > 0) {
      setWorks(
        list.map((item) => ({
          id: String(item.id ?? Date.now()),
          title: String(item.bookName ?? '未命名作品'),
          words: Number(item.wordCount ?? 0),
        })),
      );
      return;
    }
  } catch (error) {
    logger.error('[BecomeWriterPage] Failed to load author works', error);
  }

  setWorks(fallbackWorks);
};

export const createBecomeWriterPageHandlers = ({
  navigateBack,
  setSelectedDataTab,
  setSelectedAuthorTab,
  setSelectedActivityTab,
  navigateToAIPage,
  handleMorePress,
  navigateToBookManage,
  navigateToWritePage,
}: CreateBecomeWriterPageHandlersDeps) => ({
  handleBackPress: () => {
    navigateBack();
  },

  handleDataTabChange: (tab: 'novel' | 'short') => {
    setSelectedDataTab(tab);
  },

  handleAuthorTabChange: (tab: 'benefits' | 'road' | 'platform') => {
    setSelectedAuthorTab(tab);
  },

  handleActivityTabChange: (tab: 'novel' | 'short') => {
    setSelectedActivityTab(tab);
  },

  handleAIPress: () => {
    navigateToAIPage();
  },

  handleActivityMorePress: () => {
    handleMorePress('创作活动');
  },

  handleCourseMorePress: () => {
    handleMorePress('作家课堂');
  },

  handlePressWork: () => {
    navigateToBookManage();
  },

  handleCreateChapter: () => {
    navigateToWritePage();
  },
});
