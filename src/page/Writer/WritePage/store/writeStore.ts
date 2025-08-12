import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';

import { HistoryEntry } from '../types';

interface WriteState {
  title: string;
  content: string;
  history: HistoryEntry[];
  future: HistoryEntry[];
}

interface WriteActions {
  setTitle: (v: string) => void;
  setContent: (v: string) => void;
  publish: () => void;
  undo: () => void;
  redo: () => void;
}

type WriteStore = WriteState & WriteActions;

export const useWriteStore = create<WriteStore>()(
  immer((set, get) => ({
    title: '',
    content: '',
    history: [],
    future: [],

    setTitle: (v) => set((s) => {
      s.history.push({ title: s.title, content: s.content, timestamp: Date.now() });
      s.title = v;
      s.future = [];
    }),

    setContent: (v) => set((s) => {
      s.history.push({ title: s.title, content: s.content, timestamp: Date.now() });
      s.content = v;
      s.future = [];
    }),

    publish: () => {
      const { title, content } = get();
      if (!title.trim() && !content.trim()) {
        alert('请输入标题或正文');
        return;
      }
      alert('发布成功（模拟）');
    },

    undo: () => set((s) => {
      const last = s.history.pop();
      if (!last) return;
      s.future.push({ title: s.title, content: s.content, timestamp: Date.now() });
      s.title = last.title;
      s.content = last.content;
    }),

    redo: () => set((s) => {
      const next = s.future.pop();
      if (!next) return;
      s.history.push({ title: s.title, content: s.content, timestamp: Date.now() });
      s.title = next.title;
      s.content = next.content;
    }),
  }))
);


