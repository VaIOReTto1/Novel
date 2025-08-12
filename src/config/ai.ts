// DeepSeek Chat Completions API 配置（OpenAI 兼容）

export const SILICONFLOW_API_BASE = 'https://api.deepseek.com/chat/completions';

// 警告：为了演示方便，密钥被硬编码。生产环境请改为环境变量或安全存储。
export const SILICONFLOW_API_KEY = 'sk-dfae21af65724f0b805202d23c3ffa67';

export const DEFAULT_SYSTEM_PROMPT = '你是一个写作助手，擅长生成创意内容，回答时尽量使用清晰的小标题与条目组织内容。';

export type OpenAIStyleMessage = {
  role: 'system' | 'user' | 'assistant' | 'tool';
  content: string;
};

export type StreamCallbacks = {
  onContentDelta?: (text: string) => void;
  onReasoningDelta?: (text: string) => void;
  onUsage?: (usage: unknown) => void;
  onDone?: () => void;
  onError?: (err: Error) => void;
};



