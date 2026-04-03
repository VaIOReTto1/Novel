import React, { useCallback } from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import { ReservationItem } from '../types';

interface ReservationGridProps {
  styles: any;
  items: ReservationItem[];
  onItemPress: (itemId: string) => void;
  onReservePress: (itemId: string) => void;
}

const ReservationCard: React.FC<{
  item: ReservationItem;
  styles: any;
  onItemPress: (itemId: string) => void;
  onReservePress: (itemId: string) => void;
}> = React.memo(({ item, styles, onItemPress, onReservePress }) => {
  const handleItemPress = useCallback(() => {
    onItemPress(item.id);
  }, [item.id, onItemPress]);

  const handleReservePress = useCallback((e: any) => {
    e.stopPropagation();
    onReservePress(item.id);
  }, [item.id, onReservePress]);

  return (
    <TouchableOpacity
      style={styles.gridItem}
      onPress={handleItemPress}
      activeOpacity={0.8}
    >
      <View style={styles.reservationCard}>
        <Image
          source={{ uri: item.coverUrl }}
          style={styles.cardImage}
          resizeMode="cover"
        />

        <View style={styles.cardBadge}>
          <Text style={styles.cardBadgeText}>即将上线</Text>
        </View>

        <LinearGradient
          colors={['transparent', 'rgba(0,0,0,0.85)']}
          style={styles.cardGradient}
        />

        <View style={styles.cardContent}>
          <Text style={styles.cardTitle} numberOfLines={1}>
            {item.title}
          </Text>
          <Text style={styles.cardSubtitle}>
            {item.reserveCount}
          </Text>
          <TouchableOpacity
            style={[
              styles.cardButton,
              item.isReserved && styles.reservedButton,
            ]}
            onPress={handleReservePress}
            activeOpacity={0.7}
          >
            <Text style={[
              styles.cardButtonText,
              item.isReserved && styles.reservedButtonText,
            ]}>
              {item.isReserved ? '已预约' : '免费预约'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
});

export const ReservationGrid: React.FC<ReservationGridProps> = React.memo(({
  styles,
  items,
  onItemPress,
  onReservePress,
}) => {
  const groupedItems = [];
  for (let i = 0; i < items.length; i += 2) {
    groupedItems.push(items.slice(i, i + 2));
  }

  return (
    <View style={styles.gridContainer}>
      {groupedItems.map((row, rowIndex) => (
        <View key={rowIndex} style={styles.gridRow}>
          {row.map((item) => (
            <ReservationCard
              key={item.id}
              item={item}
              styles={styles}
            onItemPress={onItemPress}
            onReservePress={onReservePress}
          />
        ))}
          {row.length === 1 && <View style={styles.gridItem} />}
        </View>
      ))}
    </View>
  );
});
