import { useSharedValue, useAnimatedStyle, withTiming } from 'react-native-reanimated';
import { useEffect } from 'react';
import { PULL_THRESHOLD } from '../utils/constants';

export const useHistoryAnimations = (
  isRefreshing: boolean, 
  isPullingDown: boolean, 
  pullDistance: number
) => {
  const spinValue = useSharedValue(0);

  // 转圈动画控制
  useEffect(() => {
    const shouldSpin = isRefreshing || (isPullingDown && pullDistance > PULL_THRESHOLD);

    if (shouldSpin) {
      const startRotation = () => {
        spinValue.value = withTiming(360, { duration: 1000 }, (finished) => {
          if (finished && (isRefreshing || (isPullingDown && pullDistance > PULL_THRESHOLD))) {
            spinValue.value = 0;
            startRotation();
          }
        });
      };
      startRotation();
    } else {
      spinValue.value = 0;
    }
  }, [isRefreshing, isPullingDown, pullDistance, spinValue]);

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