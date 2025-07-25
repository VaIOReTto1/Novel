import React, { useEffect, useRef } from 'react';
import { View, Text, Image, TouchableOpacity, Animated, Dimensions } from 'react-native';
import { UserInfo, AnnouncementData } from '../types';

interface UserSectionProps {
  styles: any;
  userInfo: UserInfo | null;
  announcement: AnnouncementData;
}

export const UserSection: React.FC<UserSectionProps> = React.memo(({
  styles,
  userInfo,
  announcement,
}) => {
  const translateY = useRef(new Animated.Value(0)).current;
  const currentIndex = useRef(0);
  
  // 创建三条相同的公告消息
  const announcements = [
    announcement,
    announcement,
    announcement
  ];
  
  useEffect(() => {
    const startCarousel = () => {
      const animateToNext = () => {
        currentIndex.current = (currentIndex.current + 1) % announcements.length;
        
        Animated.timing(translateY, {
          toValue: -currentIndex.current * 20, // 使用wp(20)的高度
          duration: 500,
          useNativeDriver: true,
        }).start(() => {
          // 当到达最后一个时，重置到第一个
          if (currentIndex.current === announcements.length - 1) {
            setTimeout(() => {
              currentIndex.current = 0;
              translateY.setValue(0);
            }, 100);
          }
        });
      };
      
      // 每3秒切换一次
      const interval = setInterval(animateToNext, 3000);
      return interval;
    };
    
    const interval = startCarousel();
    
    return () => {
      clearInterval(interval);
    };
  }, [translateY, announcements.length]);
  
  return (
    <>
      {/* 用户信息 */}
      {userInfo && (
        <TouchableOpacity style={styles.profileSection} activeOpacity={0.7}>
          <Image
            source={{ uri: userInfo.avatar }}
            style={styles.profileAvatar}
          />
          <Text style={styles.profileName}>{userInfo.name}</Text>
          <Text style={styles.profileArrow}>›</Text>
        </TouchableOpacity>
      )}

      {/* 公告轮播 */}
      <View style={styles.announcementSection}>
        <Text style={styles.announcementTag}>{announcement.tag}</Text>
        <View style={styles.announcementCarouselContainer}>
          <Animated.View 
            style={[
              styles.announcementCarousel,
              {
                transform: [{ translateY }]
              }
            ]}
          >
            {announcements.map((item, index) => (
              <View key={index} style={styles.announcementItem}>
                <Text style={styles.announcementText} numberOfLines={1}>
                  {item.text}
                </Text>
              </View>
            ))}
          </Animated.View>
        </View>
        <Text style={styles.announcementMore}>更多  ›</Text>
      </View>
    </>
  );
});