import {create} from 'zustand';
import {immer} from 'zustand/middleware/immer';
import NavigationBridge from '../../../../utils/bridge/NavigationBridge';

import {HistoryEntry} from '../types';

// 统一成功提示（Android 用 Toast，iOS 回退到 Alert）
const notifySuccess = () => {
  try {

    const RN: any = require('react-native');
    const Platform = RN.Platform;
    const ToastAndroid = RN.ToastAndroid;
    const Alert = RN.Alert;
    if (Platform?.OS === 'android' && ToastAndroid?.show) {
      ToastAndroid.show('获取成功', ToastAndroid.SHORT);
    } else if (Alert?.alert) {
      Alert.alert('获取成功');
    }
  } catch (_e) {
    // ignore
  }
};

interface WriteState {
  title: string;
  content: string;
  history: HistoryEntry[];
  future: HistoryEntry[];
  isToolbarVisible: boolean;
  selectedText: string;
  selectionRange: {start: number; end: number} | null;
  holdSelection: boolean;
  overlayLoading: boolean;
  modal: {
    visible: boolean;
    type?: 'expand' | 'condense' | 'continue';
    hint?: string;
  };
  clipboardText: string;
  errorModal?: {visible: boolean; message: string};
  lastOp?: {
    action: 'polish' | 'expand' | 'condense' | 'continue';
    param?: number;
    snapshot: {selectedText: string};
  };
  // 与页面协作：请求让正文获得焦点但不弹键盘
  focusRequestNonce: number;
  suppressKeyboard: boolean;
}

interface WriteActions {
  setTitle: (v: string) => void;
  setContent: (v: string) => void;
  publish: () => void;
  undo: () => void;
  redo: () => void;
  // selection
  updateSelection: (selectedText: string, start: number, end: number) => void;
  hideToolbar: () => void;
  releaseSelectionHold: () => void;
  // clipboard
  selectAll: () => void;
  copySelected: () => void;
  cutSelected: () => void;
  pasteAtSelection: (text: string) => void;
  // AI actions wrapper
  polishSelected: () => Promise<void>;
  expandSelected: (ratio: number) => Promise<void>;
  condenseSelected: (ratio: number) => Promise<void>;
  continueSelected: (length: number) => Promise<void>;
  // overlay & modal
  setOverlay: (loading: boolean) => void;
  showParamModal: (
    type: 'expand' | 'condense' | 'continue',
    hint: string,
  ) => void;
  hideParamModal: () => void;
  // error & retry
  showError: (message: string) => void;
  hideError: () => void;
  retryLastOperation: () => Promise<void>;
  // focus request
  requestFocusWithoutKeyboard: () => void;
}

type WriteStore = WriteState & WriteActions;

export const useWriteStore = create<WriteStore>()(
  immer((set, get) => ({
    title: '',
    content: '',
    history: [],
    future: [],
    isToolbarVisible: false,
    selectedText: '',
    selectionRange: null,
    holdSelection: false,
    overlayLoading: false,
    modal: {visible: false},
    clipboardText: '',
    focusRequestNonce: 0,
    suppressKeyboard: false,

    setTitle: v =>
      set(s => {
        s.history.push({
          title: s.title,
          content: s.content,
          timestamp: Date.now(),
        });
        s.title = v;
        s.future = [];
      }),

    setContent: v =>
      set(s => {
        s.history.push({
          title: s.title,
          content: s.content,
          timestamp: Date.now(),
        });
        s.content = v;
        s.future = [];
      }),

    publish: () => {
      const {title, content} = get();
      if (!title.trim() && !content.trim()) {
        console.warn('[WriteStore] 请输入标题或正文');
        return;
      }
      console.log('[WriteStore] 发布成功（模拟）');
    },

    undo: () =>
      set(s => {
        const last = s.history.pop();
        if (!last) {
          return;
        }
        s.future.push({
          title: s.title,
          content: s.content,
          timestamp: Date.now(),
        });
        s.title = last.title;
        s.content = last.content;
      }),

    redo: () =>
      set(s => {
        const next = s.future.pop();
        if (!next) {
          return;
        }
        s.history.push({
          title: s.title,
          content: s.content,
          timestamp: Date.now(),
        });
        s.title = next.title;
        s.content = next.content;
      }),

    updateSelection: (txt, start, end) =>
      set(s => {
        if (end > start) {
          s.selectedText = txt;
          s.selectionRange = {start, end};
          s.isToolbarVisible = true;
          s.holdSelection = true;
        } else {
          // 系统上报零选区时，仅当未保持时才关闭
          if (!s.holdSelection) {
            s.selectedText = '';
            s.selectionRange = null;
            s.isToolbarVisible = false;
          }
        }
      }),
    hideToolbar: () =>
      set(s => {
        s.isToolbarVisible = false;
      }),
    releaseSelectionHold: () =>
      set(s => {
        s.holdSelection = false;
        s.isToolbarVisible = false;
        s.selectedText = '';
        s.selectionRange = null;
        s.suppressKeyboard = false;
      }),

    requestFocusWithoutKeyboard: () =>
      set(s => {
        s.suppressKeyboard = true;
        s.focusRequestNonce = (s.focusRequestNonce || 0) + 1;
      }),

    selectAll: () =>
      set(s => {
        s.selectedText = s.content;
        s.selectionRange = {start: 0, end: s.content.length};
        s.isToolbarVisible = true;
        s.holdSelection = true;
      }),
    copySelected: () =>
      set(s => {
        if (!s.selectedText) {
          return;
        }
        try {

          const Clipboard =
            require('@react-native-clipboard/clipboard').default;
          Clipboard.setString(s.selectedText);
        } catch (_e) {
          s.clipboardText = s.selectedText;
        }
      }),
    cutSelected: () =>
      set(s => {
        if (!s.selectedText) {
          return;
        }
        const start =
          s.selectionRange?.start ?? s.content.indexOf(s.selectedText);
        const end =
          s.selectionRange?.end ??
          (start >= 0 ? start + s.selectedText.length : -1);
        if (start < 0 || end < 0) {
          return;
        }
        try {
          const Clipboard =
            require('@react-native-clipboard/clipboard').default;
          Clipboard.setString(s.selectedText);
        } catch (_e) {
          s.clipboardText = s.selectedText;
        }
        s.history.push({
          title: s.title,
          content: s.content,
          timestamp: Date.now(),
        });
        s.content = s.content.slice(0, start) + s.content.slice(end);
        s.isToolbarVisible = false;
        s.holdSelection = false;
        s.selectedText = '';
        s.selectionRange = null;
      }),
    pasteAtSelection: text => {
      // 若已传入文本，直接同步粘贴；否则尝试从系统剪贴板异步读取
      if (text) {
        set(s => {
          const start = s.selectionRange?.start;
          const end = s.selectionRange?.end;
          s.history.push({
            title: s.title,
            content: s.content,
            timestamp: Date.now(),
          });
          if (start != null && end != null && end > start) {
            s.content = s.content.slice(0, start) + text + s.content.slice(end);
            s.isToolbarVisible = false;
            s.holdSelection = false;
            s.selectedText = '';
            s.selectionRange = null;
          } else {
            const idx = s.content.length;
            s.content = s.content.slice(0, idx) + text + s.content.slice(idx);
          }
        });
        return;
      }
      try {

        const Clipboard: any =
          require('@react-native-clipboard/clipboard').default;
        const maybe = Clipboard?.getString?.();
        if (maybe && typeof maybe.then === 'function') {
          // 异步路径
          (maybe as Promise<string>)
            .then((str: string) => {
              const insert = str || get().clipboardText;
              if (!insert) {
                return;
              }
              set(s => {
                const start = s.selectionRange?.start;
                const end = s.selectionRange?.end;
                s.history.push({
                  title: s.title,
                  content: s.content,
                  timestamp: Date.now(),
                });
                if (start != null && end != null && end > start) {
                  s.content =
                    s.content.slice(0, start) + insert + s.content.slice(end);
                  s.isToolbarVisible = false;
                  s.holdSelection = false;
                  s.selectedText = '';
                  s.selectionRange = null;
                } else {
                  const idx = s.content.length;
                  s.content =
                    s.content.slice(0, idx) + insert + s.content.slice(idx);
                }
              });
            })
            .catch(() => {
              const fallback = get().clipboardText;
              if (!fallback) {
                return;
              }
              set(s => {
                const start = s.selectionRange?.start;
                const end = s.selectionRange?.end;
                s.history.push({
                  title: s.title,
                  content: s.content,
                  timestamp: Date.now(),
                });
                if (start != null && end != null && end > start) {
                  s.content =
                    s.content.slice(0, start) + fallback + s.content.slice(end);
                  s.isToolbarVisible = false;
                  s.holdSelection = false;
                  s.selectedText = '';
                  s.selectionRange = null;
                } else {
                  const idx = s.content.length;
                  s.content =
                    s.content.slice(0, idx) + fallback + s.content.slice(idx);
                }
              });
            });
        } else {
          // 同步路径（某些环境可能返回同步字符串）
          const insert = (
            typeof maybe === 'string' ? maybe : get().clipboardText
          ) as string;
          if (!insert) {
            return;
          }
          set(s => {
            const start = s.selectionRange?.start;
            const end = s.selectionRange?.end;
            s.history.push({
              title: s.title,
              content: s.content,
              timestamp: Date.now(),
            });
            if (start != null && end != null && end > start) {
              s.content =
                s.content.slice(0, start) + insert + s.content.slice(end);
              s.isToolbarVisible = false;
              s.holdSelection = false;
              s.selectedText = '';
              s.selectionRange = null;
            } else {
              const idx = s.content.length;
              s.content =
                s.content.slice(0, idx) + insert + s.content.slice(idx);
            }
          });
        }
      } catch (_e) {
        const fallback = get().clipboardText;
        if (!fallback) {
          return;
        }
        set(s => {
          const start = s.selectionRange?.start;
          const end = s.selectionRange?.end;
          s.history.push({
            title: s.title,
            content: s.content,
            timestamp: Date.now(),
          });
          if (start != null && end != null && end > start) {
            s.content =
              s.content.slice(0, start) + fallback + s.content.slice(end);
            s.isToolbarVisible = false;
            s.holdSelection = false;
            s.selectedText = '';
            s.selectionRange = null;
          } else {
            const idx = s.content.length;
            s.content =
              s.content.slice(0, idx) + fallback + s.content.slice(idx);
          }
        });
      }
    },

    setOverlay: loading =>
      set(s => {
        s.overlayLoading = loading;
        if (loading) {
          // 触发遮罩时关闭选择框
        }
      }),
    showParamModal: (type, hint) =>
      set(s => {
        s.modal = {visible: true, type, hint};
      }),
    hideParamModal: () =>
      set(s => {
        s.modal = {visible: false};
      }),

    showError: message =>
      set(s => {
        s.errorModal = {visible: true, message};
      }),
    hideError: () =>
      set(s => {
        s.errorModal = {visible: false, message: ''};
      }),

    polishSelected: async () => {
      const {selectedText} = get();
      if (!selectedText) {
        return;
      }
      set(s => {
        s.overlayLoading = true;
        s.lastOp = {action: 'polish', snapshot: {selectedText}};
      });
      try {
        const data = await NavigationBridge.aiPolish(selectedText);
        set(s => {
          const start = s.selectionRange?.start;
          const end = s.selectionRange?.end;
          if (start != null && end != null && end > start) {
            s.content = s.content.slice(0, start) + data + s.content.slice(end);
          } else {
            const idx = s.content.indexOf(s.selectedText);
            if (idx >= 0) {
              s.content =
                s.content.slice(0, idx) +
                data +
                s.content.slice(idx + s.selectedText.length);
            }
          }
          s.overlayLoading = false;
          s.isToolbarVisible = false;
          s.selectedText = '';
          s.holdSelection = false;
          s.selectionRange = null;
        });
        notifySuccess();
      } catch (e) {
        set(s => {
          s.overlayLoading = false;
          const msg = e instanceof Error ? e.message : 'AI 调用失败';
          s.errorModal = {visible: true, message: msg};
        });
        throw e;
      }
    },

    expandSelected: async ratio => {
      const {selectedText} = get();
      if (!selectedText) {
        return;
      }
      set(s => {
        s.overlayLoading = true;
        s.lastOp = {
          action: 'expand',
          param: Math.round(ratio),
          snapshot: {selectedText},
        };
      });
      try {
        const intRatio = Math.round(ratio);
        const data = await NavigationBridge.aiExpand(selectedText, intRatio);
        set(s => {
          const start = s.selectionRange?.start;
          const end = s.selectionRange?.end;
          if (start != null && end != null && end > start) {
            s.content = s.content.slice(0, start) + data + s.content.slice(end);
          } else {
            const idx = s.content.indexOf(s.selectedText);
            if (idx >= 0) {
              s.content =
                s.content.slice(0, idx) +
                data +
                s.content.slice(idx + s.selectedText.length);
            }
          }
          s.overlayLoading = false;
          s.isToolbarVisible = false;
          s.selectedText = '';
          s.holdSelection = false;
          s.selectionRange = null;
        });
        notifySuccess();
      } catch (e) {
        set(s => {
          s.overlayLoading = false;
          s.errorModal = {
            visible: true,
            message: e instanceof Error ? e.message : 'AI 调用失败',
          };
        });
        throw e;
      }
    },

    condenseSelected: async ratio => {
      const {selectedText} = get();
      if (!selectedText) {
        return;
      }
      set(s => {
        s.overlayLoading = true;
        s.lastOp = {
          action: 'condense',
          param: Math.round(ratio),
          snapshot: {selectedText},
        };
      });
      try {
        const intRatio = Math.round(ratio);
        const data = await NavigationBridge.aiCondense(selectedText, intRatio);
        set(s => {
          const start = s.selectionRange?.start;
          const end = s.selectionRange?.end;
          if (start != null && end != null && end > start) {
            s.content = s.content.slice(0, start) + data + s.content.slice(end);
          } else {
            const idx = s.content.indexOf(s.selectedText);
            if (idx >= 0) {
              s.content =
                s.content.slice(0, idx) +
                data +
                s.content.slice(idx + s.selectedText.length);
            }
          }
          s.overlayLoading = false;
          s.isToolbarVisible = false;
          s.selectedText = '';
          s.holdSelection = false;
          s.selectionRange = null;
        });
        notifySuccess();
      } catch (e) {
        set(s => {
          s.overlayLoading = false;
          s.errorModal = {
            visible: true,
            message: e instanceof Error ? e.message : 'AI 调用失败',
          };
        });
        throw e;
      }
    },

    continueSelected: async length => {
      const {selectedText} = get();
      if (!selectedText) {
        return;
      }
      set(s => {
        s.overlayLoading = true;
        s.lastOp = {
          action: 'continue',
          param: Math.round(length),
          snapshot: {selectedText},
        };
      });
      try {
        const intLen = Math.round(length);
        const data = await NavigationBridge.aiContinue(selectedText, intLen);
        set(s => {
          const start = s.selectionRange?.start;
          const end = s.selectionRange?.end;
          if (start != null && end != null && end > start) {
            s.content = s.content.slice(0, end) + data + s.content.slice(end);
          } else {
            const idx = s.content.indexOf(s.selectedText);
            if (idx >= 0) {
              s.content =
                s.content.slice(0, idx + s.selectedText.length) +
                data +
                s.content.slice(idx + s.selectedText.length);
            }
          }
          s.overlayLoading = false;
          s.isToolbarVisible = false;
          s.selectedText = '';
          s.holdSelection = false;
          s.selectionRange = null;
        });
        notifySuccess();
      } catch (e) {
        set(s => {
          s.overlayLoading = false;
          s.errorModal = {
            visible: true,
            message: e instanceof Error ? e.message : 'AI 调用失败',
          };
        });
        throw e;
      }
    },

    retryLastOperation: async () => {
      const {lastOp} = get();
      if (!lastOp) {
        return;
      }
      const text = lastOp.snapshot.selectedText;
      if (!text) {
        return;
      }
      if (lastOp.action === 'polish') {
        return get().polishSelected();
      }
      if (lastOp.action === 'expand') {
        return get().expandSelected(lastOp.param || 150);
      }
      if (lastOp.action === 'condense') {
        return get().condenseSelected(lastOp.param || 2);
      }
      if (lastOp.action === 'continue') {
        return get().continueSelected(lastOp.param || 200);
      }
    },
  })),
);
