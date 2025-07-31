// React Native 动态主题色实现，基于全局主题Store状态管理
// 兼容RN 0.79版本，使用Zustand状态管理

import { useThemeStore } from './themeStore';

/**
 * 返回当前系统模式下的颜色值
 * @param light 浅色模式下使用的十六进制颜色字符串
 * @param dark 深色模式下使用的十六进制颜色字符串
 * @returns 当前模式对应的颜色值
 */
export function useDynamicColor(light: string, dark: string): string {
  const { isDarkMode } = useThemeStore();
  return isDarkMode ? dark : light;
}

/**
 * 动态主题Hook - 从全局状态管理获取
 */
export function useDynamicTheme() {
  const { isDarkMode } = useThemeStore();
  return isDarkMode ? 'dark' : 'light';
}

export interface NovelColors {
  novelMain: string;
  novelMainLight: string;
  novelBookBackground: string;
  novelBackground: string;
  novelDivider: string;
  novelSecondaryBackground: string;
  novelText: string;
  novelTextGray: string;
  novelLightGray: string;
  novelChipBackground: string;
  novelError: string;
  outline: string;
}

const lightColors: NovelColors = {
  novelMain: '#FF995D',
  novelMainLight: '#F86827',
  novelBookBackground: '#E8E3CF',
  novelBackground: '#FFFFFF',
  novelDivider: '#F7F7F8',
  novelSecondaryBackground: '#F7F7F8',
  novelText: '#000000',
  novelTextGray: '#7F7F7F',
  novelLightGray: '#DDDDDD',
  novelChipBackground: '#EBEDF0',
  novelError: '#FF995D',
  outline: '#E0E0E0',
};

const darkColors: NovelColors = {
  novelMain: '#FF995D',
  novelMainLight: '#F86827',
  novelBookBackground: '#E8E3CF',
  novelBackground: '#000000',
  novelDivider: '#1C1C1E',
  novelSecondaryBackground: '#1C1C1E',
  novelText: '#FFFFFF',
  novelTextGray: '#97989F',
  novelLightGray: '#1C1C1E',
  novelChipBackground: '#23242B',
  novelError: '#FF0000',
  outline: '#3C3C3E',
};


export const useNovelColors = (): NovelColors => {
  const scheme = useDynamicTheme(); // 使用改进的主题Hook
  return scheme === 'dark' ? darkColors : lightColors;
};
