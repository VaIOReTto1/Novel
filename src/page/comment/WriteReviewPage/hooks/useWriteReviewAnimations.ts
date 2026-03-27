import { useRef, useEffect } from 'react';
import { Animated } from 'react-native';

export const useWriteReviewAnimations = () => {
  const fadeAnim = useRef(new Animated.Value(0)).current;
  const scaleAnim = useRef(new Animated.Value(0.95)).current;
  const slideAnim = useRef(new Animated.Value(50)).current;

  useEffect(() => {
    // 页面进入动画
    Animated.parallel([
      Animated.timing(fadeAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
      Animated.timing(scaleAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: true,
      }),
      Animated.timing(slideAnim, {
        toValue: 0,
        duration: 300,
        useNativeDriver: true,
      }),
    ]).start();
  }, [fadeAnim, scaleAnim, slideAnim]);

  // 星级评分动画
  const createStarAnimation = (rating: number) => {
    const starAnims = Array.from({ length: 5 }, () => new Animated.Value(1));

    const animateStars = () => {
      const animations = starAnims.map((anim, index) => {
        if (index < rating) {
          return Animated.sequence([
            Animated.timing(anim, {
              toValue: 1.2,
              duration: 100,
              useNativeDriver: true,
            }),
            Animated.timing(anim, {
              toValue: 1,
              duration: 100,
              useNativeDriver: true,
            }),
          ]);
        }
        return Animated.timing(anim, {
          toValue: 1,
          duration: 0,
          useNativeDriver: true,
        });
      });

      Animated.stagger(50, animations).start();
    };

    return { starAnims, animateStars };
  };

  // 提交按钮动画
  const submitButtonAnim = useRef(new Animated.Value(1)).current;

  const animateSubmitButton = (isSubmitting: boolean) => {
    if (isSubmitting) {
      Animated.loop(
        Animated.sequence([
          Animated.timing(submitButtonAnim, {
            toValue: 0.95,
            duration: 500,
            useNativeDriver: true,
          }),
          Animated.timing(submitButtonAnim, {
            toValue: 1,
            duration: 500,
            useNativeDriver: true,
          }),
        ])
      ).start();
    } else {
      submitButtonAnim.stopAnimation();
      Animated.timing(submitButtonAnim, {
        toValue: 1,
        duration: 200,
        useNativeDriver: true,
      }).start();
    }
  };

  return {
    fadeAnim,
    scaleAnim,
    slideAnim,
    createStarAnimation,
    submitButtonAnim,
    animateSubmitButton,
  };
};
