import { useSharedValue, useAnimatedStyle, withTiming, runOnUI } from 'react-native-reanimated';
import { useEffect } from 'react';
import { PULL_THRESHOLD } from '../utils/constants';

export const useHistoryAnimations = (
  isRefreshing: boolean,
  isPullingDown: boolean,
  pullDistance: number
) => {
  const spinValue = useSharedValue(0);
  const isSpinning = useSharedValue(false);

  // 转圈动画控制
  useEffect(() => {
    const shouldSpin = isRefreshing || (isPullingDown && pullDistance > PULL_THRESHOLD);

    if (shouldSpin && !isSpinning.value) {
      runOnUI(() => {
        'worklet';
        isSpinning.value = true;
        const startRotation = () => {
          if (isSpinning.value) {
            spinValue.value = withTiming(360, { duration: 1000 }, (finished) => {
              if (finished && isSpinning.value) {
                spinValue.value = 0;
                startRotation();
              }
            });
          }
        };
        startRotation();
      })();
    } else if (!shouldSpin && isSpinning.value) {
      runOnUI(() => {
        'worklet';
        isSpinning.value = false;
        spinValue.value = withTiming(0, { duration: 200 });
      })();
    }
  }, [isRefreshing, isPullingDown, pullDistance, spinValue, isSpinning]);

  // 创建旋转动画样式
  const spinStyle = useAnimatedStyle(() => {
    return {
      transform: [{ rotate: `${spinValue.value}deg` }],
    };
  });

  return {
    spinStyle,
  };
};
