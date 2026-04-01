import { novelDesignDarkTheme, novelDesignLightTheme } from './novelDesignTokens';

export const resolveNovelDesignTheme = (novelBackground: string) => {
  return novelBackground === '#000000' ? novelDesignDarkTheme : novelDesignLightTheme;
};
