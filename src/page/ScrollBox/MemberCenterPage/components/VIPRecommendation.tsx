import React, { useCallback } from 'react';
import { Alert, View, Text, TouchableOpacity, Image } from 'react-native';
import { VIPRecommendation as VIPRecommendationType } from '../types';

interface VIPRecommendationProps {
  styles: any;
  recommendations: VIPRecommendationType[];
  cardType: string;
}

export const VIPRecommendation: React.FC<VIPRecommendationProps> = React.memo(({
  styles,
  recommendations,
  cardType,
}) => {
  const handleRecommendationPress = useCallback((id: string) => {
    console.log(`VIP推荐点击: ${id}`);
    Alert.alert('VIP推荐功能开发中...');
  }, []);

  // 免广告VIP不显示推荐内容
  if (cardType === 'adfree' || recommendations.length === 0) {
    return null;
  }

  const getTitle = () => {
    switch (cardType) {
      case 'member':
        return '会员专属推荐';
      case 'svip':
        return 'SVIP专属推荐';
      default:
        return 'VIP专属推荐';
    }
  };

  return (
    <View style={styles.recommendationContainer}>
      <Text style={styles.recommendationTitle}>{getTitle()}</Text>

      <View style={styles.recommendationGrid}>
        {recommendations.map((item) => (
          <TouchableOpacity
            key={item.id}
            style={styles.recommendationItem}
            onPress={() => handleRecommendationPress(item.id)}
            activeOpacity={0.8}
          >
            <Image
              source={{ uri: item.coverUrl }}
              style={styles.recommendationCover}
              resizeMode="cover"
            />
            <Text style={styles.recommendationItemTitle} numberOfLines={1}>
              {item.title}
            </Text>
            <Text style={styles.recommendationDescription} numberOfLines={2}>
              {item.description}
            </Text>
          </TouchableOpacity>
        ))}
      </View>
    </View>
  );
});
