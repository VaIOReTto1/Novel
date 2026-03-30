import { DeviceEventEmitter } from 'react-native';

type ThemeChangedPayload = {
  colorScheme: string;
  currentThemeMode?: string;
  followSystem?: boolean;
};

type WritePageSelectionPayload = {
  action?: 'polish' | 'expand' | 'condense' | 'continue';
  selectedText?: string;
  start?: number;
  end?: number;
};

export const subscribeThemeChanged = (
  listener: (payload: ThemeChangedPayload) => void,
): (() => void) => {
  const subscription = DeviceEventEmitter.addListener('ThemeChanged', listener);
  return () => subscription.remove();
};

export const emitThemeChanged = (payload: ThemeChangedPayload): void => {
  DeviceEventEmitter.emit('ThemeChanged', payload);
};

export const subscribeWritePageSelectionMenuAction = (
  listener: (payload: WritePageSelectionPayload) => void,
): (() => void) => {
  const subscription = DeviceEventEmitter.addListener(
    'WritePageSelectionMenuAction',
    listener,
  );
  return () => subscription.remove();
};

export const subscribeUserDataReceived = (
  listener: (payload: unknown) => void,
): (() => void) => {
  const subscription = DeviceEventEmitter.addListener(
    'onUserDataReceived',
    listener,
  );
  return () => subscription.remove();
};

export const subscribeRecommendBooksReceived = (
  listener: (payload: unknown) => void,
): (() => void) => {
  const subscription = DeviceEventEmitter.addListener(
    'onRecommendBooksReceived',
    listener,
  );
  return () => subscription.remove();
};

export const emitUserDataReceived = (payload: unknown): void => {
  DeviceEventEmitter.emit('onUserDataReceived', payload);
};

export const emitRecommendBooksReceived = (payload: unknown): void => {
  DeviceEventEmitter.emit('onRecommendBooksReceived', payload);
};
