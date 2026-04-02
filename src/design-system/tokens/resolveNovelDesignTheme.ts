import { novelDesignDarkTheme, novelDesignLightTheme } from './novelDesignTokens';

export interface NovelDesignThemeInput {
  novelBackground?: string;
  novelThemeMode?: 'light' | 'dark';
}

export const resolveNovelDesignMode = (
  input: string | NovelDesignThemeInput,
): 'light' | 'dark' => {
  if (typeof input === 'string') {
    return input === '#000000' ? 'dark' : 'light';
  }

  if (input.novelThemeMode) {
    return input.novelThemeMode;
  }

  return input.novelBackground === '#000000' ? 'dark' : 'light';
};

export const resolveNovelDesignTheme = (input: string | NovelDesignThemeInput) => {
  return resolveNovelDesignMode(input) === 'dark'
    ? novelDesignDarkTheme
    : novelDesignLightTheme;
};
