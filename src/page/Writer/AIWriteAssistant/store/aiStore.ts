import {create} from 'zustand';
import {immer} from 'zustand/middleware/immer';
import {ChatMessage} from '../types';
import {OpenAIStyleMessage, DEFAULT_SYSTEM_PROMPT} from '../../../../config/ai';
import {streamChatCompletion} from '../../../../utils/ai/siliconflow';

export interface AIState {
  messages: ChatMessage[];
  input: string;
  sending: boolean;
  dailyRemaining: number; // “今日剩余：5次”
  deepThinkEnabled: boolean;
}

export interface AIActions {
  setInput: (v: string) => void;
  send: () => Promise<void>;
  reset: () => void;
  toggleDeepThink: () => void;
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
    deepThinkEnabled: true,

    setInput: v =>
      set(s => {
        s.input = v;
      }),

    send: async () => {
      const {input, sending, dailyRemaining, deepThinkEnabled} = get();
      console.log('[AIStore] send called', {
        sending,
        inputLength: input?.length,
        dailyRemaining,
        deepThinkEnabled,
      });
      if (sending || !input.trim()) {
        console.log('[AIStore] blocked: sending or empty input');
        return;
      }
      if (dailyRemaining <= 0) {
        console.log('[AIStore] blocked: no quota');
        return;
      }

      const userMsg: ChatMessage = {
        id: 'user-' + Date.now(),
        role: 'user',
        text: input,
        timestamp: Date.now(),
      };

      // 预创建一个 assistant 容器用于流式增量渲染
      const assistantId = 'ai-' + Date.now();
      const start = Date.now();

      set(s => {
        s.messages.push(userMsg);
        s.messages.push({
          id: assistantId,
          role: 'assistant',
          text: '',
          thinking: '',
          timestamp: Date.now(),
          model: deepThinkEnabled
            ? 'deepseek-ai/DeepSeek-R1'
            : 'deepseek-ai/DeepSeek-V3',
        });
        s.input = '';
        s.sending = true;
        s.dailyRemaining = Math.max(0, s.dailyRemaining - 1);
      });

      const history: OpenAIStyleMessage[] = [];
      // 将历史消息（不含初始引导气泡）转为 OpenAI 风格上下文
      for (const m of get().messages) {
        if (m.id === 'intro') {
          continue;
        }
        history.push({role: m.role as any, content: m.text});
      }
      if (!history.some(m => m.role === 'system')) {
        history.unshift({role: 'system', content: DEFAULT_SYSTEM_PROMPT});
      }
      console.log(
        '[AIStore] streaming with history',
        history.map(h => h.role).join(','),
      );

      try {
        await streamChatCompletion({
          messages: history,
          deepThinkEnabled,
          onContentDelta: (t: string) => {
            set(s => {
              const target = s.messages.find(mm => mm.id === assistantId);
              if (target) {
                target.text += t;
              }
            });
          },
          onReasoningDelta: (t: string) => {
            set(s => {
              const target = s.messages.find(mm => mm.id === assistantId);
              if (target) {
                target.thinking = (target.thinking ?? '') + t;
              }
            });
          },
          onUsage: (u: unknown) => {
            set(s => {
              const target = s.messages.find(mm => mm.id === assistantId);
              if (target) {
                target.usage = u;
              }
            });
          },
          onDone: () => {
            const latency = Date.now() - start;
            set(s => {
              const target = s.messages.find(mm => mm.id === assistantId);
              if (target) {
                target.latencyMs = latency;
                target.done = true;
              }
              s.sending = false;
            });
          },
          onError: (err: Error) => {
            set(s => {
              const target = s.messages.find(mm => mm.id === assistantId);
              if (target) {
                target.text =
                  (target.text || '') + `\n\n(错误：${err.message})`;
                target.done = true;
              }
              s.sending = false;
            });
          },
        });
      } catch (e) {
        set(s => {
          s.sending = false;
        });
      }
    },

    reset: () =>
      set(s => {
        s.messages = [initialIntro];
        s.input = '';
        s.sending = false;
        s.dailyRemaining = 5;
        s.deepThinkEnabled = true;
      }),

    toggleDeepThink: () =>
      set(s => {
        s.deepThinkEnabled = !s.deepThinkEnabled;
      }),
  })),
);
