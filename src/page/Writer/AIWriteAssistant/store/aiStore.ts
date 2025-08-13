import {create} from 'zustand';
import {immer} from 'zustand/middleware/immer';
import {ChatMessage} from '../types';
import {OpenAIStyleMessage, DEFAULT_SYSTEM_PROMPT} from '../../../../config/ai';
import {streamChatCompletion} from '../../../../utils/ai/siliconflow';
import {SimpleStorage} from '../../../../utils/storage/simpleStorage';

export interface AIState {
  messages: ChatMessage[];
  input: string;
  sending: boolean;
  dailyRemaining: number; // “今日剩余：5次”
  deepThinkEnabled: boolean;
  ideaCategory?: string; // 灵感类型
  hydrated?: boolean; // 是否已从存储恢复
  ideaPromptActive?: boolean; // 仅当开启“开书灵感”浮窗时，才注入题材 System Prompt
}

export interface AIActions {
  setInput: (v: string) => void;
  send: () => Promise<void>;
  reset: () => void;
  toggleDeepThink: () => void;
  setIdeaCategory: (c: string) => void;
  rehydrate: () => Promise<void>;
  setIdeaPromptActive: (active: boolean) => void;
}

type AIStore = AIState & AIActions;

const buildIntroText = (category?: string) => {
  const genre = category ? `（当前偏好：${category}）` : '';
  return (
    'Hi，我是写作助手，会一直陪伴你在番茄的创作之旅！' +
    `${genre}\n可以回答一些关于小说创作的问题，例如：\n\n` +
    '• 如何在不直接描写角色外貌的情况下，让读者在脑海中形成清晰的人物形象？\n' +
    '• 如果主角突然获得一种与自身性格完全相悖的超能力，这会如何影响故事的核心冲突发展？\n' +
    '• 在创作历史背景小说时，如何平衡真实历史事件的准确性与虚构情节的戏剧张力？'
  );
};

const buildSystemPrompt = (category?: string) => {
  const theme = category ? `当前用户选择的创作方向/题材：${category}。` : '';
  const tail = '请在后续的所有回答、灵感、人物、情节、世界观示例中，优先贴合该方向；除非用户显式改变方向。输出中文，结构清晰，包含可直接采纳的要点与钩子。';
  return `${DEFAULT_SYSTEM_PROMPT}\n${theme}${tail}`.trim();
};

const initialIntro: ChatMessage = {
  id: 'intro',
  role: 'assistant',
  text: buildIntroText(),
  timestamp: Date.now(),
};

const STORAGE_KEY = 'novel.ai.chat.history.v1';
type PersistShape = Pick<AIState, 'messages' | 'dailyRemaining' | 'deepThinkEnabled' | 'ideaCategory'>;

export const useAIStore = create<AIStore>()(
  immer((set, get) => ({
    messages: [initialIntro],
    input: '',
    sending: false,
    dailyRemaining: 5,
    deepThinkEnabled: true,
    ideaCategory: undefined,
    hydrated: false,
    ideaPromptActive: false,

    setInput: v =>
      set(s => {
        s.input = v;
      }),

    send: async () => {
      const {input, sending, dailyRemaining, deepThinkEnabled, ideaPromptActive} = get();
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
      // 将历史消息（不含初始引导气泡、当前占位 assistant、空文本）转为 OpenAI 风格上下文
      for (const m of get().messages) {
        if (m.id === 'intro') {
          continue;
        }
        // 跳过当前请求创建的占位 assistant（空内容，会干扰多轮前缀与缓存命中）
        if (m.id === assistantId) {
          continue;
        }
        if (!m.text || !m.text.trim().length) {
          continue;
        }
        history.push({role: m.role as any, content: m.text});
      }
      // 仅在“开书灵感”开启时注入题材方向，否则使用默认 System Prompt
      const systemPrompt = ideaPromptActive
        ? buildSystemPrompt(get().ideaCategory)
        : DEFAULT_SYSTEM_PROMPT;
      const finalHistory: OpenAIStyleMessage[] = [
        {role: 'system', content: systemPrompt},
        ...history.filter(m => m.role !== 'system'),
      ];
      console.log(
        '[AIStore] streaming with history',
        finalHistory.map(h => h.role).join(','),
      );

      try {
        // 对增量写入进行微型缓冲，减少 set 次数
        let bufferedText = '';
        let bufferedThinking = '';
        let lastFlush = 0;
        const flush = () => {
          if (!bufferedText && !bufferedThinking) { return; }
          set(s => {
            const target = s.messages.find(mm => mm.id === assistantId);
            if (target) {
              if (bufferedText) {
                target.text += bufferedText;
              }
              if (bufferedThinking) {
                target.thinking = (target.thinking ?? '') + bufferedThinking;
              }
            }
          });
          bufferedText = '';
          bufferedThinking = '';
          lastFlush = Date.now();
        };

        const maybeFlush = (force = false) => {
          const now = Date.now();
          if (force || now - lastFlush > 50) {
            flush();
          }
        };

        await streamChatCompletion({
          messages: finalHistory,
          deepThinkEnabled,
          onContentDelta: (t: string) => {
            bufferedText += t;
            maybeFlush();
          },
          onReasoningDelta: (t: string) => {
            bufferedThinking += t;
            maybeFlush();
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
            flush();
            const latency = Date.now() - start;
            set(s => {
              const target = s.messages.find(mm => mm.id === assistantId);
              if (target) {
                target.latencyMs = latency;
                target.done = true;
              }
              s.sending = false;
            });
            // 仅在完整结束后进行持久化
            const {messages, dailyRemaining: daily, deepThinkEnabled: deep, ideaCategory} = get();
            const data: PersistShape = {messages, dailyRemaining: daily, deepThinkEnabled: deep, ideaCategory};
            SimpleStorage.setItem(STORAGE_KEY, JSON.stringify(data)).catch(() => {});
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
        s.ideaCategory = undefined;
        s.ideaPromptActive = false;
        // 覆盖持久化
        const data: PersistShape = {
          messages: s.messages,
          dailyRemaining: s.dailyRemaining,
          deepThinkEnabled: s.deepThinkEnabled,
          ideaCategory: s.ideaCategory,
        };
        SimpleStorage.setItem(STORAGE_KEY, JSON.stringify(data)).catch(() => {});
      }),

    toggleDeepThink: () =>
      set(s => {
        s.deepThinkEnabled = !s.deepThinkEnabled;
      }),

    setIdeaCategory: (c: string) =>
      set(s => {
        s.ideaCategory = c;
        // 同步刷新引导文案
        if (s.messages.length > 0 && s.messages[0].id === 'intro') {
          s.messages[0].text = buildIntroText(c);
        }
        // 立即持久化分类变化
        const data: PersistShape = {
          messages: s.messages,
          dailyRemaining: s.dailyRemaining,
          deepThinkEnabled: s.deepThinkEnabled,
          ideaCategory: s.ideaCategory,
        };
        SimpleStorage.setItem(STORAGE_KEY, JSON.stringify(data)).catch(() => {});
      }),

    rehydrate: async () => {
      if (get().hydrated) { return; }
      try {
        const raw = await SimpleStorage.getItem(STORAGE_KEY);
        if (raw) {
          const data = JSON.parse(raw) as PersistShape;
          set(s => {
            s.messages = Array.isArray(data.messages) && data.messages.length > 0 ? data.messages : [initialIntro];
            s.dailyRemaining = Number.isFinite(data.dailyRemaining as number) ? (data.dailyRemaining as number) : 5;
            s.deepThinkEnabled = !!data.deepThinkEnabled;
            s.ideaCategory = data.ideaCategory;
            // 确保首条 intro 按当前分类渲染
            if (s.messages.length > 0 && s.messages[0].id === 'intro') {
              s.messages[0].text = buildIntroText(s.ideaCategory);
            }
            s.hydrated = true;
            s.ideaPromptActive = false; // 打开页面默认关闭“开书灵感”
          });
          return;
        }
      } catch {}
      set(s => {
        s.hydrated = true;
        s.ideaPromptActive = false;
      });
    },

    setIdeaPromptActive: (active: boolean) => set(s => {
      s.ideaPromptActive = active;
    }),
  })),
);
