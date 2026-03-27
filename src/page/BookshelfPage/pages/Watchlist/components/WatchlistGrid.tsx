import React from 'react';
import { View, Text, TouchableOpacity, FlatList, Image, ActivityIndicator } from 'react-native';
import { createWatchlistPageStyles } from '../styles/WatchlistPageStyles';
import { useNovelColors } from '../../../../../utils/theme';
import { WatchlistItem } from '../types';

interface WatchlistGridProps {
  data: WatchlistItem[];
  isEditMode: boolean;
  selectedItems: Set<string>;
  onItemPress: (item: WatchlistItem) => void;
  onItemSelect: (item: WatchlistItem) => void;
  loading: boolean;
}

export const WatchlistGrid: React.FC<WatchlistGridProps> = ({
  data,
  isEditMode,
  selectedItems,
  onItemPress,
  onItemSelect,
  loading,
}) => {
  const colors = useNovelColors();
  const styles = createWatchlistPageStyles(colors);

  const renderItem = ({ item }: { item: WatchlistItem }) => {
    const isSelected = selectedItems.has(item.id);
    const coverImage = item.cover || item.coverUrl || 'https://via.placeholder.com/150x200';

    return (
      <TouchableOpacity
        style={[
          styles.gridItem,
          isSelected && styles.selectedGridItem,
        ]}
        onPress={() => {
          if (isEditMode) {
            onItemSelect(item);
          } else {
            onItemPress(item);
          }
        }}
        activeOpacity={0.8}
      >
        <View style={styles.gridItemImageContainer}>
          <Image
            source={{ uri: coverImage }}
            style={styles.gridItemImage}
            resizeMode="cover"
          />

          {/* 选择状态 */}
          {isEditMode && (
            <View style={styles.selectionCheckbox}>
              <Text style={styles.checkboxIcon}>
                {isSelected ? '✓' : ''}
              </Text>
            </View>
          )}

          {/* 更新提示 */}
          {!item.isFinished && item.lastUpdate && (
            <View style={styles.updateBadge}>
              <Text style={styles.updateBadgeText}>更新</Text>
            </View>
          )}
        </View>

        <View style={styles.gridItemContent}>
          <Text style={styles.gridItemTitle} numberOfLines={2}>
            {item.title}
          </Text>
          <Text style={styles.gridItemSubtitle} numberOfLines={1}>
            {item.episodeCount ? `${item.currentEpisode}集/${item.episodeCount}集` : (item.isFinished ? '已完结' : '连载中')}
          </Text>
        </View>
      </TouchableOpacity>
    );
  };

  const renderFooter = () => {
    if (!loading) {return null;}

    return (
      <View style={styles.loadingFooter}>
        <ActivityIndicator size="small" color={colors.novelBackground} />
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  };

  return (
    <FlatList
      data={data}
      renderItem={renderItem}
      keyExtractor={(item: { id: any; }) => item.id}
      numColumns={3}
      columnWrapperStyle={styles.gridColumnWrapper}
      contentContainerStyle={styles.gridContainer}
      showsVerticalScrollIndicator={false}
      ListFooterComponent={renderFooter}
    />
  );
};
