import React, { useCallback, useRef } from 'react';
import { View, Text, Dimensions, FlatList } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Animated, {
  Extrapolate,
  interpolate,
  runOnJS,
  useAnimatedScrollHandler,
  useAnimatedStyle,
  useSharedValue,
} from 'react-native-reanimated';
import { VIPCard } from '../types';
import { wp } from '../../../../utils/theme/dimensions';

/**
 * -------------------------------------------------------------
 * VIP卡片无限循环轮播组件
 * 
 * 核心实现原理：
 * 1. 【首尾克隆】：在原始数据前后各添加CLONE个哨兵元素，形成 [尾部克隆...原始数据...头部克隆] 结构
 * 2. 【无缝跳转】：当滚动到边界克隆区域时，利用scrollTo瞬间跳转到对应的真实位置，实现视觉上的无限循环
 * 3. 【精确居中】：修正contentContainerStyle的paddingHorizontal为(screenWidth - CARD_WIDTH) / 2，确保卡片完美居中
 * 4. 【边界优化】：改进索引计算逻辑，处理边界情况下的索引映射，避免跳跃和闪烁
 * 5. 【3D动效】：左右卡片采用±35°旋转、0.75倍缩放、透明度渐变，突出中心卡片的1.1倍放大效果
 * 6. 【性能优化】：使用getItemLayout预计算布局，snapToInterval确保精确对齐，decelerationRate="fast"提升响应速度
 * 
 * 关键参数：
 * - CLONE = 2：每侧克隆数量，平衡性能与无限循环效果
 * - CARD_TOTAL = CARD_WIDTH + CARD_SPACING：单个卡片占用的总宽度
 * - SIDE_CARD_SCALE = 0.75：侧边卡片缩放比例
 */

const { width: screenWidth } = Dimensions.get('window');
const CARD_WIDTH = wp(220);     // ← 与样式保持一致
const CARD_SPACING = wp(0);
const CARD_TOTAL = CARD_WIDTH + CARD_SPACING;
const SIDE_CARD_SCALE = 0.95;   // 侧卡更小，突出中心
const CLONE = 2;                // 每侧克隆 2 张哨兵

interface VIPCardCarouselProps {
  styles: any;
  cards: VIPCard[];
  currentIndex: number;
  onCardChange: (index: number) => void;
}

const AnimatedFlatList = Animated.createAnimatedComponent(FlatList);

// 单张卡片
const VIPCardItem = React.memo(({
  card,
  styles,
  dataIndex,
  scrollX,
  isActive,
}: {
  card: VIPCard;
  styles: any;
  dataIndex: number;
  scrollX: Animated.SharedValue<number>;
  isActive: boolean;
}) => {
  const animatedStyle = useAnimatedStyle(() => {
    const inputRange = [
      (dataIndex - 1) * CARD_TOTAL,
      dataIndex * CARD_TOTAL,
      (dataIndex + 1) * CARD_TOTAL,
    ];

    const rotateY = interpolate(scrollX.value, inputRange, [-35, 0, 35], Extrapolate.CLAMP);
    const translateX = interpolate(
      scrollX.value,
      inputRange,
      [-CARD_SPACING, 0, CARD_SPACING],
      Extrapolate.CLAMP,
    );
    const scale = interpolate(scrollX.value, inputRange, [SIDE_CARD_SCALE, 1.0, SIDE_CARD_SCALE], Extrapolate.CLAMP); // 中心保持原始大小
    const opacity = interpolate(scrollX.value, inputRange, [0.8, 1, 0.8], Extrapolate.CLAMP);

    return {
      transform: [
        { perspective: 1000 },
        { translateX },
        { rotateY: `${rotateY}deg` },
        { scale },
      ],
      opacity,
      zIndex: isActive ? 20 : 10,
    };
  }, [dataIndex, isActive]);

  return (
    <Animated.View style={[styles.cardWrapper, animatedStyle, { marginHorizontal: CARD_SPACING / 2 }]}>
        <LinearGradient
          colors={card.bgGradient}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
          style={[
            styles.card
          ]}
        >
          <View>
            <Text style={styles.cardTitle}>{card.title}</Text>
            <Text style={styles.cardSubtitle}>{card.subtitle}</Text>
          </View>
          <View style={styles.cardBottomIcon}>
            <Text style={{ fontSize: 20, color: 'white' }}>👑</Text>
          </View>
        </LinearGradient>
    </Animated.View>
  );
});

export const VIPCardCarousel: React.FC<VIPCardCarouselProps> = React.memo(({ styles, cards, currentIndex, onCardChange }) => {
  const listRef = useRef<FlatList>(null);
  const scrollX = useSharedValue((currentIndex + CLONE) * CARD_TOTAL);
  const lastIndexRef = useRef(currentIndex);

  /** JS 线程执行：无动画跳转到 offset */
  const jumpToOffset = useCallback((offset: number) => {
    listRef.current?.scrollToOffset({ offset, animated: false });
  }, []);

  // 滚动事件 / 无限循环逻辑
  const scrollHandler = useAnimatedScrollHandler({
    onScroll: (event) => {
      const x = event.contentOffset.x;
      scrollX.value = x;

      // ⌈ 计算真实索引并回调 JS 层 - 优化边界处理 ⌉
      const idx = Math.round(x / CARD_TOTAL);
      let actual;
      
      // 处理边界情况，确保索引计算的准确性
      if (idx < CLONE) {
        actual = cards.length - (CLONE - idx);
      } else if (idx >= cards.length + CLONE) {
        actual = idx - cards.length - CLONE;
      } else {
        actual = idx - CLONE;
      }
      
      // 确保索引在有效范围内
      actual = ((actual % cards.length) + cards.length) % cards.length;
      
      if (actual !== lastIndexRef.current) {
        lastIndexRef.current = actual;
        runOnJS(onCardChange)(actual);
      }
    },

    onMomentumEnd: (event) => {
      const x = event.contentOffset.x;
      const idx = Math.round(x / CARD_TOTAL);
      
      // 安全的边界检测，避免崩溃
      if (cards.length === 0) return;
      
      // ▸ 如果滑到"伪首/伪尾"，瞬间跳回真实位置
      if (idx < CLONE) {
        const target = (cards.length + idx) * CARD_TOTAL;   // 伪首 → 真尾
        scrollX.value = target;
        runOnJS(jumpToOffset)(target);
      } else if (idx >= cards.length + CLONE) {
        const target = (idx - cards.length) * CARD_TOTAL;   // 伪尾 → 真首
        scrollX.value = target;
        runOnJS(jumpToOffset)(target);
      }
    },
  });

  // 渲染单项
  const renderItem = useCallback(({ item, index }: { item: VIPCard | null; index: number }) => {
    if (!item || typeof item.id === 'undefined') {
      return <View style={styles.cardWrapper} />;
    }
    let displayIdx = (index - CLONE + cards.length) % cards.length;
    const isActive = displayIdx === currentIndex;

    return (
      <VIPCardItem
        card={item}
        styles={styles}
        dataIndex={index}
        scrollX={scrollX}
        isActive={isActive}
      />
    );
  }, [styles, cards.length, currentIndex, scrollX]);

  const keyExtractor = useCallback((item: VIPCard | null, index: number) => item && item.id !== undefined ? `${item.id}-${index}` : `placeholder-${index}` , []);

  const getItemLayout = useCallback((_data: any, index: number) => ({
    length: CARD_TOTAL,
    offset: CARD_TOTAL * index,
    index,
  }), []);

  if (!cards || cards.length === 0) return null;

  // ➜ 组装无限循环数据
  const data = [
    ...cards.slice(-CLONE),    // 尾部克隆
    ...cards,
    ...cards.slice(0, CLONE),  // 头部克隆
  ];

  return (
    <View style={styles.cardCarouselContainer}>
      <AnimatedFlatList
        ref={listRef as any}
        data={data}
        renderItem={renderItem}
        keyExtractor={keyExtractor}
        horizontal
        showsHorizontalScrollIndicator={false}
        onScroll={scrollHandler}
        scrollEventThrottle={2}
        snapToInterval={CARD_TOTAL}
        snapToAlignment="start"
        decelerationRate="fast"
        bounces={false}
        getItemLayout={getItemLayout}
        initialScrollIndex={currentIndex + CLONE}
        style={{ overflow: 'visible' }}
        contentContainerStyle={{
          paddingHorizontal: (screenWidth - CARD_WIDTH) / 2 - CARD_SPACING / 2,
          overflow: 'visible',
        }}
      />

      {/* 指示器 */}
      <View style={styles.cardIndicatorContainer}>
        {cards.map((_, idx) => (
          <View
            key={idx}
            style={[styles.cardIndicator, idx === currentIndex && styles.cardIndicatorActive]}
          />
        ))}
      </View>
    </View>
  );
});
