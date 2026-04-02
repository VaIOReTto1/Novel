import * as ReactNative from 'react-native';

type SharedValue<T> = { value: T };

const Animated = {
  View: ReactNative.View,
  Text: ReactNative.Text,
  ScrollView: ReactNative.ScrollView,
  FlatList: ReactNative.FlatList,
  createAnimatedComponent: <T,>(component: T): T => component,
};

export const Extrapolate = {
  CLAMP: 'clamp',
};

export const useSharedValue = <T,>(value: T): SharedValue<T> => ({ value });

export const useAnimatedStyle = <T,>(updater: () => T): T => updater();

export const useAnimatedProps = <T,>(updater: () => T): T => updater();

export const withTiming = <T,>(
  value: T,
  _config?: unknown,
  callback?: (finished: boolean) => void,
): T => {
  callback?.(true);
  return value;
};

export const interpolate = (
  value: number,
  input: number[],
  output: number[],
): number => {
  if (input.length === 0 || output.length === 0) {
    return value;
  }
  if (value <= input[0]) {
    return output[0];
  }
  if (value >= input[input.length - 1]) {
    return output[output.length - 1];
  }

  for (let index = 0; index < input.length - 1; index += 1) {
    const start = input[index];
    const end = input[index + 1];
    if (value >= start && value <= end) {
      const ratio = (value - start) / (end - start);
      return output[index] + ratio * (output[index + 1] - output[index]);
    }
  }

  return output[0];
};

export const runOnUI =
  <T extends (...args: any[]) => any>(fn: T) =>
  (...args: Parameters<T>): ReturnType<T> =>
    fn(...args);

export const runOnJS =
  <T extends (...args: any[]) => any>(fn: T) =>
  (...args: Parameters<T>): ReturnType<T> =>
    fn(...args);

export const useAnimatedScrollHandler = (_handlers?: unknown) => () => undefined;

export default Animated;
