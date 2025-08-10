import { useRef, useEffect } from 'react';
import { Animated } from 'react-native';

/**
 * 动画效果 Hook
 * 提供页面进入时的淡入和缩放动画
 */
export   const useAnimations = () => {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const scaleAnim = useRef(new Animated.Value(0.95)).current;
  
  useEffect(() => {
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
      Animated.spring(scaleAnim, {
        toValue: 1,
        tension: 100,
        friction: 8,
        useNativeDriver: true,
      }),
    ]).start();
  }, [fadeAnim, scaleAnim]);
  
  return { fadeAnim, scaleAnim };
};