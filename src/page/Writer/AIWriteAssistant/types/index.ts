export type ChatRole = 'assistant' | 'user' | 'system';

export interface ChatMessage {
  id: string;
  role: ChatRole;
  text: string;
  timestamp: number;
}

export type SuggestionType = 'deepThink' | 'idea';


