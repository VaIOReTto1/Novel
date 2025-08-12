export interface WriteDocumentState {
  title: string;
  content: string;
}

export interface HistoryEntry extends WriteDocumentState {
  timestamp: number;
}


