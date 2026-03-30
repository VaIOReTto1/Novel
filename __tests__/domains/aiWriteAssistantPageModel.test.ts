import {
  bootstrapAIWriteAssistantPage,
  createAIWriteAssistantHandlers,
} from '../../src/page/Writer/AIWriteAssistant/domain/aiWriteAssistantPageModel';

describe('AI write assistant page domain helpers', () => {
  test('bootstraps persisted AI state by rehydrating once', async () => {
    const rehydrate = jest.fn().mockResolvedValue(undefined);

    await bootstrapAIWriteAssistantPage({
      rehydrate,
      logger: {
        log: jest.fn(),
        error: jest.fn(),
      },
    });

    expect(rehydrate).toHaveBeenCalledTimes(1);
  });

  test('toggles idea prompt visibility and syncs prompt active flag', () => {
    const setIdeaVisible = jest.fn();
    const setIdeaPromptActive = jest.fn();
    const handlers = createAIWriteAssistantHandlers({
      navigateBack: jest.fn(),
      setIdeaVisible,
      setIdeaPromptActive,
      setIdeaCategory: jest.fn(),
      scrollToEnd: jest.fn(),
    });

    handlers.handleToggleIdea(false);
    handlers.handleToggleIdea(true);

    expect(setIdeaVisible).toHaveBeenNthCalledWith(1, true);
    expect(setIdeaPromptActive).toHaveBeenNthCalledWith(1, true);
    expect(setIdeaVisible).toHaveBeenNthCalledWith(2, false);
    expect(setIdeaPromptActive).toHaveBeenNthCalledWith(2, false);
  });

  test('closes idea selector and deactivates prompt flag', () => {
    const setIdeaVisible = jest.fn();
    const setIdeaPromptActive = jest.fn();
    const handlers = createAIWriteAssistantHandlers({
      navigateBack: jest.fn(),
      setIdeaVisible,
      setIdeaPromptActive,
      setIdeaCategory: jest.fn(),
      scrollToEnd: jest.fn(),
    });

    handlers.handleCloseIdea();

    expect(setIdeaVisible).toHaveBeenCalledWith(false);
    expect(setIdeaPromptActive).toHaveBeenCalledWith(false);
  });

  test('selects idea category and keeps floating selector open', () => {
    const setIdeaCategory = jest.fn();
    const handlers = createAIWriteAssistantHandlers({
      navigateBack: jest.fn(),
      setIdeaVisible: jest.fn(),
      setIdeaPromptActive: jest.fn(),
      setIdeaCategory,
      scrollToEnd: jest.fn(),
    });

    handlers.handleSelectIdea('玄幻');

    expect(setIdeaCategory).toHaveBeenCalledWith('玄幻');
  });

  test('navigates back and scrolls on input focus', () => {
    const navigateBack = jest.fn();
    const scrollToEnd = jest.fn();
    const handlers = createAIWriteAssistantHandlers({
      navigateBack,
      setIdeaVisible: jest.fn(),
      setIdeaPromptActive: jest.fn(),
      setIdeaCategory: jest.fn(),
      scrollToEnd,
    });

    handlers.handleBack();
    handlers.handleInputFocus();

    expect(navigateBack).toHaveBeenCalledTimes(1);
    expect(scrollToEnd).toHaveBeenCalledTimes(1);
  });
});
