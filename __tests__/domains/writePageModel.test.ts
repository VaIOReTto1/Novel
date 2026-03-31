import {
  appendToSelectedText,
  createWritePageHandlers,
  getWritePageModalPlaceholder,
  getWritePageSelection,
  runWritePageFocusSync,
  replaceSelectedText,
} from '../../src/page/Writer/WritePage/domain/writePageModel';

describe('write page domain helpers', () => {
  test('replaces selected text when selection exists in content', () => {
    expect(replaceSelectedText('hello world', 'world', 'novel')).toBe('hello novel');
  });

  test('returns original content when selected text is missing', () => {
    expect(replaceSelectedText('hello world', 'missing', 'novel')).toBe('hello world');
  });

  test('appends to selected text when selection exists in content', () => {
    expect(appendToSelectedText('hello world', 'world', '!')).toBe('hello world!');
  });

  test('routes polish action to immediate callback', () => {
    const updateSelection = jest.fn();
    const dismissKeyboard = jest.fn();
    const onPolish = jest.fn();
    const onShowParamModal = jest.fn();
    const handlers = createWritePageHandlers({
      updateSelection,
      dismissKeyboard,
      onPolish,
      onShowParamModal,
    });

    handlers.handleSelectionMenuAction({
      action: 'polish',
      selectedText: 'abc',
      start: 1,
      end: 4,
    });

    expect(updateSelection).toHaveBeenCalledWith('abc', 1, 4);
    expect(dismissKeyboard).toHaveBeenCalledTimes(1);
    expect(onPolish).toHaveBeenCalledTimes(1);
    expect(onShowParamModal).not.toHaveBeenCalled();
  });

  test('routes expand and continue actions to parameter modal', () => {
    const onShowParamModal = jest.fn();
    const handlers = createWritePageHandlers({
      updateSelection: jest.fn(),
      dismissKeyboard: jest.fn(),
      onPolish: jest.fn(),
      onShowParamModal,
    });

    handlers.handleSelectionMenuAction({
      action: 'expand',
      selectedText: 'abc',
      start: 1,
      end: 4,
    });
    handlers.handleSelectionMenuAction({
      action: 'continue',
      selectedText: 'abc',
      start: 1,
      end: 4,
    });

    expect(onShowParamModal).toHaveBeenNthCalledWith(
      1,
      'expand',
      '请输入扩写比例（百分比），如 150 表示约 150%',
    );
    expect(onShowParamModal).toHaveBeenNthCalledWith(
      2,
      'continue',
      '请输入续写目标字数，如 200 表示约 200 字',
    );
  });

  test('ignores invalid selection menu payloads', () => {
    const updateSelection = jest.fn();
    const dismissKeyboard = jest.fn();
    const onPolish = jest.fn();
    const onShowParamModal = jest.fn();
    const handlers = createWritePageHandlers({
      updateSelection,
      dismissKeyboard,
      onPolish,
      onShowParamModal,
    });

    handlers.handleSelectionMenuAction({
      action: undefined,
      selectedText: '',
      start: 0,
      end: 0,
    });

    expect(updateSelection).not.toHaveBeenCalled();
    expect(dismissKeyboard).not.toHaveBeenCalled();
    expect(onPolish).not.toHaveBeenCalled();
    expect(onShowParamModal).not.toHaveBeenCalled();
  });

  test('extracts selection text and range from native event', () => {
    expect(
      getWritePageSelection('hello world', {
        nativeEvent: {
          selection: { start: 6, end: 11 },
        },
      }),
    ).toEqual({
      selectedText: 'world',
      start: 6,
      end: 11,
    });
  });

  test('returns null when selection event is missing or collapsed', () => {
    expect(getWritePageSelection('hello', {})).toBeNull();
    expect(
      getWritePageSelection('hello', {
        nativeEvent: {
          selection: { start: 2, end: 2 },
        },
      }),
    ).toBeNull();
  });

  test('returns parameter modal placeholders by operation type', () => {
    expect(getWritePageModalPlaceholder('continue')).toBe('长度(字数)');
    expect(getWritePageModalPlaceholder('expand')).toBe('比例');
    expect(getWritePageModalPlaceholder(undefined)).toBe('比例');
  });

  test('submits parameter modal to the matching action and resets input', () => {
    const hideParamModal = jest.fn();
    const setParamInput = jest.fn();
    const expandSelected = jest.fn();
    const condenseSelected = jest.fn();
    const continueSelected = jest.fn();

    const handlers = createWritePageHandlers({
      updateSelection: jest.fn(),
      dismissKeyboard: jest.fn(),
      onPolish: jest.fn(),
      onShowParamModal: jest.fn(),
      hideParamModal,
      setParamInput,
      expandSelected,
      condenseSelected,
      continueSelected,
    });

    handlers.handleConfirmParamModal('12', 'expand');
    handlers.handleConfirmParamModal('8', 'condense');
    handlers.handleConfirmParamModal('200', 'continue');

    expect(hideParamModal).toHaveBeenCalledTimes(3);
    expect(setParamInput).toHaveBeenCalledTimes(3);
    expect(expandSelected).toHaveBeenCalledWith(12);
    expect(condenseSelected).toHaveBeenCalledWith(8);
    expect(continueSelected).toHaveBeenCalledWith(200);
  });

  test('ignores invalid param modal values', () => {
    const expandSelected = jest.fn();
    const handlers = createWritePageHandlers({
      updateSelection: jest.fn(),
      dismissKeyboard: jest.fn(),
      onPolish: jest.fn(),
      onShowParamModal: jest.fn(),
      hideParamModal: jest.fn(),
      setParamInput: jest.fn(),
      expandSelected,
      condenseSelected: jest.fn(),
      continueSelected: jest.fn(),
    });

    handlers.handleConfirmParamModal('', 'expand');
    handlers.handleConfirmParamModal('abc', 'expand');
    handlers.handleConfirmParamModal('0', 'expand');

    expect(expandSelected).not.toHaveBeenCalled();
  });

  test('focus sync focuses editor and dismisses keyboard', () => {
    const focus = jest.fn();
    const dismissKeyboard = jest.fn();

    runWritePageFocusSync({
      focus,
      dismissKeyboard,
    });

    expect(focus).toHaveBeenCalledTimes(1);
    expect(dismissKeyboard).toHaveBeenCalledTimes(1);
  });
});
