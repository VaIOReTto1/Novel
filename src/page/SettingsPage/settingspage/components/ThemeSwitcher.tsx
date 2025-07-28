// ThemeSwitcher.tsx
import React, { useEffect, memo } from 'react';
import { Pressable, PressableProps } from 'react-native';
import Svg, {
  Defs,
  Mask,
  Circle,
  G,
  Line,
  RadialGradient,
  Stop,
} from 'react-native-svg';
import Animated, {
  useSharedValue,
  useAnimatedProps,
  withTiming,
  interpolate,
} from 'react-native-reanimated';

// 包装 SVG 元素
const AnimatedCircle = Animated.createAnimatedComponent(Circle);
const AnimatedG = Animated.createAnimatedComponent(G);
const AnimatedLine = Animated.createAnimatedComponent(Line);

export type ThemeSwitcherProps = {
  /** true = 月亮；false = 太阳 */
  isDark: boolean;
  onToggle: () => void;
  size?: number; // 图标正方形边长，默认 28
} & Omit<PressableProps, 'onPress'>;

const ThemeSwitcher = memo(function ThemeSwitcher({
  isDark,
  onToggle,
  size = 28,
  accessibilityLabel = 'Toggle theme',
  ...pressableProps
}: ThemeSwitcherProps) {
  // 0 → 太阳；1 → 月亮
  const progress = useSharedValue(isDark ? 1 : 0);

  useEffect(() => {
    progress.value = withTiming(isDark ? 1 : 0, { duration: 450 });
  }, [isDark, progress]);

  // 基础几何
  const cx = size / 2;
  const cy = size / 2;
  const baseR = size * 0.25;

  // mask 用的“主圆”和“咬合圆”
  const mainCircleProps = useAnimatedProps(() => ({
    r: baseR + progress.value * (size * 0.2),
  }));
  const biteCircleProps = useAnimatedProps(() => {
    // 与 Compose 原文同样的起始位置：
    // initialOffset = center - Offset(radius * 2.3f, radius * 2.3f)
    const start = baseR * 2.3;
  
    // “咬合”圆沿对角线移动，但系数调成 1.9，确保最终位置落在 0.4 * size
    const offset = baseR * 1.65 * progress.value;
  
    // 计算中心点
    const bx = cx - start + offset;
    const by = cy - start + offset;
    return { cx: bx, cy: by, r: baseR * 1.55 };
  });
  

  // 光芒：黄色直线，矩阵旋转 + 轻微缩放
  const raysProps = useAnimatedProps(() => {
    const visible = progress.value < 0.55 ? 1 : 0;
    const deg = 180 * (1 - progress.value);
    const rad = (deg * Math.PI) / 180;
    const k = 1 - Math.max(0, progress.value) * 0.15;
    const cos = Math.cos(rad),
      sin = Math.sin(rad);
    const a = k * cos,
      b = k * sin,
      c = -k * sin,
      d = k * cos;
    const e = cx - a * cx - c * cy;
    const f = cy - b * cx - d * cy;
    return { opacity: visible, matrix: [a, b, c, d, e, f] } as any;
  });

  // 交叉淡入/淡出：太阳 vs 月亮 主体填充
  const sunFillProps = useAnimatedProps(() => ({
    r: baseR + progress.value * (size * 0.2),
    opacity: 1 - progress.value,
  }));
  const moonFillProps = useAnimatedProps(() => ({
    r: baseR + progress.value * (size * 0.2),
    opacity: progress.value,
  }));

  // 月亮阶段出现的两颗小星
  const star1Props = useAnimatedProps(() => {
    const p = progress.value > 0.8 ? (progress.value - 0.8) / 0.2 : 0;
    return { r: size * 0.05 * p, opacity: p };
  });
  const star2Props = useAnimatedProps(() => {
    const p = progress.value > 0.8 ? (progress.value - 0.8) / 0.2 : 0;
    return { r: size * 0.1 * p, opacity: p };
  });

  // 光芒几何（8 条直线）
  const rays = Array.from({ length: 8 }).map((_, i) => {
    const angle = (2 * Math.PI * i) / 8;
    const startR = size * 0.38;
    const rayLen = size * 0.08;
    return {
      x1: cx + startR * Math.cos(angle),
      y1: cy + startR * Math.sin(angle),
      x2: cx + (startR + rayLen) * Math.cos(angle),
      y2: cy + (startR + rayLen) * Math.sin(angle),
      key: i,
    };
  });

  return (
    <Pressable
      {...pressableProps}
      onPress={onToggle}
      accessibilityRole="switch"
      accessibilityState={{ checked: isDark }}
      accessibilityLabel={accessibilityLabel}
      hitSlop={8}
      style={[
        { width: size + 12, height: size + 12, alignItems: 'center', justifyContent: 'center' },
        // @ts-ignore
        pressableProps.style,
      ]}
    >
      <Svg width={size} height={size}>
        <Defs>
          {/* 太阳的径向渐变 */}
          <RadialGradient id="sunGrad" cx="50%" cy="50%" r="50%">
            <Stop offset="0%" stopColor="#FFD700" />
            <Stop offset="100%" stopColor="#FF8C00" />
          </RadialGradient>

          {/* 月亮的径向渐变 */}
          <RadialGradient id="moonGrad" cx="50%" cy="50%" r="50%">
            <Stop offset="0%" stopColor="#F0F8FF" />
            <Stop offset="100%" stopColor="#A9CCE3" />
          </RadialGradient>

          {/* 遮罩：白色为可见，黑色为遮挡 */}
          <Mask id="moonMask">
            <Circle cx={cx} cy={cy} r={0} fill="black" />
            <AnimatedCircle cx={cx} cy={cy} animatedProps={mainCircleProps} fill="white" />
            <AnimatedCircle animatedProps={biteCircleProps} fill="black" />
          </Mask>
        </Defs>

        {/* 太阳主体（mask + 渐变 + 反向淡出） */}
        <AnimatedCircle
          mask="url(#moonMask)"
          cx={cx}
          cy={cy}
          fill="url(#sunGrad)"
          animatedProps={sunFillProps}
        />

        {/* 月亮主体（mask + 渐变 + 正向淡入） */}
        <AnimatedCircle
          mask="url(#moonMask)"
          cx={cx}
          cy={cy}
          fill="url(#moonGrad)"
          animatedProps={moonFillProps}
        />

        {/* 光芒（黄色直线 + 动画矩阵） */}
        <AnimatedG animatedProps={raysProps}>
          {rays.map(({ x1, y1, x2, y2, key }) => (
            <AnimatedLine
              key={key}
              x1={x1}
              y1={y1}
              x2={x2}
              y2={y2}
              strokeWidth={size * 0.06}
              strokeLinecap="round"
              stroke="#FFD700"
            />
          ))}
        </AnimatedG>

        {/* 星星（只在接近月亮时显示） */}
        <AnimatedCircle
          cx={size * 0.4}
          cy={size * 0.4}
          fill="#FFFFFF"
          animatedProps={star1Props}
        />
        <AnimatedCircle
          cx={size * 0.2}
          cy={size * 0.2}
          fill="#FFFFFF"
          animatedProps={star2Props}
        />
      </Svg>
    </Pressable>
  );
});

export default ThemeSwitcher;
