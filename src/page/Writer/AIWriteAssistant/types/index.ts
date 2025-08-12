export type ChatRole = 'assistant' | 'user' | 'system';

export interface ChatMessage {
  id: string;
  role: ChatRole;
  text: string;
  timestamp: number;
  thinking?: string;
  model?: string;
  usage?: unknown;
  latencyMs?: number;
  memoryMB?: number;
  done?: boolean;
}

export type SuggestionType = 'deepThink' | 'idea';


