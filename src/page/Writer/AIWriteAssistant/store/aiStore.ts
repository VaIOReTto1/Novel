import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { ChatMessage } from '../types';

export interface AIState {
  messages: ChatMessage[];
  input: string;
  sending: boolean;
  dailyRemaining: number; // “今日剩余：5次”
}

export interface AIActions {
  setInput: (v: string) => void;
  send: () => Promise<void>;
  reset: () => void;
}

type AIStore = AIState & AIActions;

const initialIntro: ChatMessage = {
  id: 'intro',
  role: 'assistant',
  text:
    'Hi，我是写作助手，会一直陪伴你在番茄的创作之旅！可以回答一些关于小说创作的问题，例如：\n\n' +
    '• 如何在不直接描写角色外貌的情况下，让读者在脑海中形成清晰的人物形象？\n' +
    '• 如果主角突然获得一种与自身性格完全相悖的超能力，这会如何影响故事的核心冲突发展？\n' +
    '• 在创作历史背景小说时，如何平衡真实历史事件的准确性与虚构情节的戏剧张力？',
  timestamp: Date.now(),
};

export const useAIStore = create<AIStore>()(
  immer((set, get) => ({
    messages: [initialIntro],
    input: '',
    sending: false,
    dailyRemaining: 5,

    setInput: (v) => set((s) => { s.input = v; }),

    send: async () => {
      const { input, sending, dailyRemaining } = get();
      if (sending || !input.trim()) { return; }
      if (dailyRemaining <= 0) { return; }

      const newUser: ChatMessage = {
        id: 'user-' + Date.now(),
        role: 'user',
        text: input,
        timestamp: Date.now(),
      };

      set((s) => {
        s.messages.push(newUser);
        s.input = '';
        s.sending = true;
        s.dailyRemaining = Math.max(0, s.dailyRemaining - 1);
      });

      // 这里接入真实 AI 接口；先用假回复模拟
      await new Promise((r) => setTimeout(r, 600));
      const reply: ChatMessage = {
        id: 'ai-' + Date.now(),
        role: 'assistant',
        text: '这是示例回答：请进一步补充人物动机、冲突目标与场景约束，我会帮你细化桥段。',
        timestamp: Date.now(),
      };
      set((s) => { s.messages.push(reply); s.sending = false; });
    },

    reset: () => set((s) => { s.messages = [initialIntro]; s.input = ''; s.sending = false; s.dailyRemaining = 5; }),
  }))
);


