import React, { useState } from 'react';
import { View, ScrollView, TouchableOpacity, Text } from 'react-native';
import Animated, { useAnimatedStyle, withTiming } from 'react-native-reanimated';
import IconComponent from '../../../component/IconComponent';
import { getPageIcons, PAGE_WIDTH } from '../utils/constants';
import { IconData } from '../types';
import { wp } from '../../../utils/theme/dimensions';

interface ScrollableAreaProps {
  styles: any;
  scrollX: any;
  animatedContainerStyle: any;
  firstPageIconsStyle: any;
  secondPageIconsStyle: any;
  thirdPageIconsStyle: any;
  firstPageAdStyle: any;
  colors: any;
  onBeWriterPress?: () => void;
  isAuthor?: boolean;
}

/**
 * 动画指示器组件
 * 解决useAnimatedStyle在map回调中使用的Hook规则问题
 */
interface AnimatedDotProps {
  index: number;
  scrollX: any;
  colors: any;
  styles: any;
}

const AnimatedDot: React.FC<AnimatedDotProps> = ({ index, scrollX, colors, styles }) => {
  const animatedDotStyle = useAnimatedStyle(() => {
    const isActive = Math.round(scrollX.value / PAGE_WIDTH) === index;
    return {
      backgroundColor: withTiming(
        isActive ? colors.novelMain : colors.novelDivider,
        { duration: 200 }
      ),
      transform: [
        {
          scale: withTiming(
            isActive ? 1.2 : 1,
            { duration: 200 }
          ),
        },
      ],
    };
  });

  return (
    <Animated.View
      style={[styles.dot, animatedDotStyle]}
    />
  );
};

export const ScrollableArea: React.FC<ScrollableAreaProps> = ({
  styles,
  scrollX,
  animatedContainerStyle,
  firstPageIconsStyle,
  secondPageIconsStyle,
  thirdPageIconsStyle,
  firstPageAdStyle,
  colors,
  onBeWriterPress,
  isAuthor,
}) => {
  const [, setCurrentPage] = useState(0);
  const totalPages = 3;

  // 渲染图标
  const renderIcon = (iconData: IconData) => (
    <TouchableOpacity
      key={iconData.id}
      style={styles.iconItem}
      onPress={iconData.onPress}
    >
      <IconComponent name={iconData.icon} width={wp(25)} height={wp(25)} />
      <Text style={styles.iconText}>{iconData.name}</Text>
    </TouchableOpacity>
  );

  // 渲染广告组件
  const renderAdvertisement = () => (
    <View style={styles.advertisement}>
      <View style={styles.adBookCover} />
      <View style={styles.adContent}>
        <Text style={styles.adTitle} numberOfLines={2}>
          加饰披摩，高冷校花消不住了
        </Text>
        <Text style={styles.adAuthor} numberOfLines={1}>
          书时真
        </Text>
      </View>
      <TouchableOpacity style={styles.continueReading}>
        <Text style={styles.continueText}>继续阅读 &gt;</Text>
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={styles.scrollableContainer}>
      <Animated.View style={[styles.scrollArea, animatedContainerStyle]}>
        <ScrollView
          horizontal
          pagingEnabled
          showsHorizontalScrollIndicator={false}
          onMomentumScrollEnd={(event: any) => {
            const pageIndex = Math.round(event.nativeEvent.contentOffset.x / PAGE_WIDTH);
            setCurrentPage(pageIndex);
          }}
          onScroll={(event: any) => {
            scrollX.value = event.nativeEvent.contentOffset.x;
          }}
          scrollEventThrottle={16}
        >
          {/* 第一页：4个图标 + 广告 */}
          <View style={[styles.page, { width: PAGE_WIDTH }]}>
            <Animated.View style={[styles.firstPageIcons, firstPageIconsStyle]}>
              {getPageIcons(0)
                .map((iconData) => {
                  if (iconData.id === 'be_writer') {
                    return {
                      ...iconData,
                      name: isAuthor ? '写小说' : '成为作家',
                      onPress: onBeWriterPress || iconData.onPress,
                    } as IconData;
                  }
                  return iconData;
                })
                .map((iconData) => renderIcon(iconData))}
            </Animated.View>
            <Animated.View style={firstPageAdStyle}>
              {renderAdvertisement()}
            </Animated.View>
          </View>

          {/* 第二页：15个图标布局 */}
          <View style={[styles.page, { width: PAGE_WIDTH }]}>
            <Animated.View style={[styles.gridContainer, secondPageIconsStyle]}>
              {getPageIcons(1).map((iconData) => renderIcon(iconData))}
            </Animated.View>
          </View>

          {/* 第三页：剩余图标 */}
          <View style={[styles.page, { width: PAGE_WIDTH }]}>
            <Animated.View style={[styles.lastPageContainer, thirdPageIconsStyle]}>
              {getPageIcons(2).map((iconData) => renderIcon(iconData))}
            </Animated.View>
          </View>
        </ScrollView>
      </Animated.View>

      {/* 动画页面指示器 */}
      <View style={styles.pageIndicator}>
        {Array.from({ length: totalPages }).map((_, index) => (
          <AnimatedDot
            key={index}
            index={index}
            scrollX={scrollX}
            colors={colors}
            styles={styles}
          />
        ))}
      </View>
    </View>
  );
};
