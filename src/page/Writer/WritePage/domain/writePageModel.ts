type SelectionMenuAction = 'polish' | 'expand' | 'condense' | 'continue' | undefined;
type ParamModalType = 'expand' | 'condense' | 'continue' | undefined;

type SelectionMenuPayload = {
  action?: SelectionMenuAction;
  selectedText?: string;
  start?: number;
  end?: number;
};

type SelectionEvent = {
  nativeEvent?: {
    selection?: {
      start: number;
      end: number;
    };
  };
};

type CreateWritePageHandlersDeps = {
  updateSelection: (selectedText: string, start: number, end: number) => void;
  dismissKeyboard: () => void;
  onPolish: () => void;
  onShowParamModal: (
    type: 'expand' | 'condense' | 'continue',
    hint: string,
  ) => void;
  hideParamModal?: () => void;
  setParamInput?: (value: string) => void;
  expandSelected?: (ratio: number) => void;
  condenseSelected?: (ratio: number) => void;
  continueSelected?: (length: number) => void;
};

type FocusSyncDeps = {
  focus: () => void;
  dismissKeyboard: () => void;
};

export const EXPAND_HINT = '请输入扩写比例（百分比），如 150 表示约 150%';
export const CONDENSE_HINT = '请输入缩写比例（分母），如 2 表示约 1/2';
export const CONTINUE_HINT = '请输入续写目标字数，如 200 表示约 200 字';

export const replaceSelectedText = (
  content: string,
  selectedText: string,
  newText: string,
): string => {
  const index = content.indexOf(selectedText);
  if (index < 0) {
    return content;
  }
  return content.slice(0, index) + newText + content.slice(index + selectedText.length);
};

export const appendToSelectedText = (
  content: string,
  selectedText: string,
  newText: string,
): string => {
  const index = content.indexOf(selectedText);
  if (index < 0) {
    return content;
  }
  return (
    content.slice(0, index + selectedText.length) +
    newText +
    content.slice(index + selectedText.length)
  );
};

export const getWritePageSelection = (
  content: string,
  event: SelectionEvent,
): { selectedText: string; start: number; end: number } | null => {
  const selection = event?.nativeEvent?.selection;
  if (!selection) {
    return null;
  }
  const start = Math.min(selection.start, selection.end);
  const end = Math.max(selection.start, selection.end);
  if (end <= start) {
    return null;
  }
  return {
    selectedText: content.slice(start, end),
    start,
    end,
  };
};

export const getWritePageModalPlaceholder = (type: ParamModalType): string =>
  type === 'continue' ? '长度(字数)' : '比例';

export const runWritePageFocusSync = ({
  focus,
  dismissKeyboard,
}: FocusSyncDeps): void => {
  try {
    focus();
  } catch {}
  dismissKeyboard();
};

const getHintByAction = (action: Exclude<SelectionMenuAction, undefined>): string => {
  if (action === 'expand') {
    return EXPAND_HINT;
  }
  if (action === 'condense') {
    return CONDENSE_HINT;
  }
  return CONTINUE_HINT;
};

export const createWritePageHandlers = ({
  updateSelection,
  dismissKeyboard,
  onPolish,
  onShowParamModal,
  hideParamModal,
  setParamInput,
  expandSelected,
  condenseSelected,
  continueSelected,
}: CreateWritePageHandlersDeps) => ({
  handleSelectionMenuAction: (payload: SelectionMenuPayload) => {
    const action = payload.action;
    const selectedText = payload.selectedText;
    if (!action || !selectedText) {
      return;
    }

    updateSelection(selectedText, payload.start ?? 0, payload.end ?? 0);
    dismissKeyboard();

    if (action === 'polish') {
      onPolish();
      return;
    }

    onShowParamModal(action, getHintByAction(action));
  },

  handleConfirmParamModal: (paramInput: string, type: ParamModalType) => {
    const param = Number(paramInput);
    if (!param || Number.isNaN(param) || !type) {
      return;
    }

    hideParamModal?.();
    setParamInput?.('');

    if (type === 'expand') {
      expandSelected?.(param);
      return;
    }
    if (type === 'condense') {
      condenseSelected?.(param);
      return;
    }
    if (type === 'continue') {
      continueSelected?.(param);
    }
  },
});
