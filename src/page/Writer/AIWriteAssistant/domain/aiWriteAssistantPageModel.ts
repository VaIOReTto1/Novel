type Logger = Pick<Console, 'log' | 'error'>;

type BootstrapAIWriteAssistantPageDeps = {
  rehydrate: () => Promise<void>;
  logger?: Logger;
};

type CreateAIWriteAssistantHandlersDeps = {
  navigateBack: () => void;
  setIdeaVisible: (visible: boolean) => void;
  setIdeaPromptActive: (active: boolean) => void;
  setIdeaCategory: (category: string) => void;
  scrollToEnd: () => void;
};

const defaultLogger: Logger = console;

export const bootstrapAIWriteAssistantPage = async ({
  rehydrate,
  logger = defaultLogger,
}: BootstrapAIWriteAssistantPageDeps): Promise<void> => {
  try {
    await rehydrate();
  } catch (error) {
    logger.error('[AIWriteAssistant] Failed to rehydrate', error);
  }
};

export const createAIWriteAssistantHandlers = ({
  navigateBack,
  setIdeaVisible,
  setIdeaPromptActive,
  setIdeaCategory,
  scrollToEnd,
}: CreateAIWriteAssistantHandlersDeps) => ({
  handleBack: () => {
    navigateBack();
  },

  handleToggleIdea: (currentVisible: boolean) => {
    const nextVisible = !currentVisible;
    setIdeaVisible(nextVisible);
    setIdeaPromptActive(nextVisible);
  },

  handleCloseIdea: () => {
    setIdeaVisible(false);
    setIdeaPromptActive(false);
  },

  handleSelectIdea: (category: string) => {
    setIdeaCategory(category);
  },

  handleInputFocus: () => {
    scrollToEnd();
  },
});
