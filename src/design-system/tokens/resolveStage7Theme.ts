import { stage7DarkTheme, stage7LightTheme } from './stage7Tokens';

export const resolveStage7Theme = (novelBackground: string) => {
  return novelBackground === '#000000' ? stage7DarkTheme : stage7LightTheme;
};
