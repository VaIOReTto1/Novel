type SelectionMenuAction = 'polish' | 'expand' | 'condense' | 'continue' | undefined;

type SelectionMenuPayload = {
  action?: SelectionMenuAction;
  selectedText?: string;
  start?: number;
  end?: number;
};

type CreateWritePageHandlersDeps = {
  updateSelection: (selectedText: string, start: number, end: number) => void;
  dismissKeyboard: () => void;
  onPolish: () => void;
  onShowParamModal: (
    type: 'expand' | 'condense' | 'continue',
    hint: string,
  ) => void;
};

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

export const createWritePageHandlers = ({
  updateSelection,
  dismissKeyboard,
  onPolish,
  onShowParamModal,
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
    if (action === 'expand') {
      onShowParamModal('expand', '请输入扩写比例（百分比），如 150 表示约 150%');
      return;
    }
    if (action === 'condense') {
      onShowParamModal('condense', '请输入缩写比例（分母），如 2 表示约 1/2');
      return;
    }
    if (action === 'continue') {
      onShowParamModal('continue', '请输入续写目标字数，如 200 表示约 200 字');
    }
  },
});
