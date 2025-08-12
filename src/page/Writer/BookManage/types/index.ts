export interface BookBasic {
  id: string;
  title: string;
  authorName: string;
  statusText: string; // 待审核/已上架 等
  coverUrl?: string;
}

export interface DraftInfo {
  exists: boolean;
  updatedAt?: number;
}

export interface ChapterItem {
  id: string;
  title: string;
  words: number;
  updatedAt: number;
}


