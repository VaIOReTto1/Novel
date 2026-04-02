import React from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';

import NavigationBridge from '../../../../../utils/bridge/NavigationBridge';
import { HistoryItemProps } from '../types';

export const HistoryItem: React.FC<HistoryItemProps> = React.memo(({
  styles,
  item,
  viewType,
  isEditing,
  isSelected,
  onSelect,
  onAddToShelf,
}) => {
  const handlePress = () => {
    if (isEditing) {
      onSelect();
      return;
    }

    NavigationBridge.navigateToReader(item.id);
  };

  const getProgressText = (progress: number) => {
    if (progress >= 1) {
      return '已完结';
    }
    return `${Math.floor(progress * 100)}%`;
  };

  const shelfLabel = item.isInShelf ? '已在书架' : '加入书架';

  if (viewType === 'grid') {
    return (
      <TouchableOpacity
        style={[styles.gridItem, isSelected && styles.selectedItem]}
        onPress={handlePress}
        activeOpacity={0.7}>
        {isEditing ? (
          <View style={styles.selectOverlay}>
            <View
              style={[
                styles.selectCheckbox,
                isSelected && styles.selectCheckboxSelected,
              ]}>
              {isSelected ? <Text style={styles.selectCheckIcon}>已选</Text> : null}
            </View>
          </View>
        ) : null}

        <View style={styles.gridCover}>
          <Image
            source={{ uri: item.cover || 'https://via.placeholder.com/120x160' }}
            style={styles.gridCoverImage}
            resizeMode="cover"
          />
          <View style={styles.gridProgress}>
            <View
              style={[
                styles.gridProgressBar,
                { width: `${Math.floor(item.progress * 100)}%` },
              ]}
            />
          </View>
        </View>

        <View style={styles.gridInfo}>
          <Text style={styles.gridTitle} numberOfLines={2}>
            {item.title}
          </Text>
          <Text style={styles.gridProgress}>{getProgressText(item.progress)}</Text>
          <TouchableOpacity
            style={styles.gridAddToShelfButton}
            onPress={() => onAddToShelf?.(item)}>
            <Text
              style={[
                styles.gridAddToShelfText,
                item.isInShelf && styles.gridAddedToShelfText,
              ]}>
              {shelfLabel}
            </Text>
          </TouchableOpacity>
        </View>
      </TouchableOpacity>
    );
  }

  return (
    <TouchableOpacity
      style={[styles.listItem, isSelected && styles.selectedItem]}
      onPress={handlePress}
      activeOpacity={0.7}>
      {isEditing ? (
        <View style={styles.listSelectArea}>
          <View
            style={[
              styles.selectCheckbox,
              isSelected && styles.selectCheckboxSelected,
            ]}>
            {isSelected ? <Text style={styles.selectCheckIcon}>已选</Text> : null}
          </View>
        </View>
      ) : null}

      <View style={styles.listCover}>
        <Image
          source={{ uri: item.cover || 'https://via.placeholder.com/60x80' }}
          style={styles.listCoverImage}
          resizeMode="cover"
        />
      </View>

      <View style={styles.listContent}>
        <Text style={styles.listTitle} numberOfLines={1}>
          {item.title}
        </Text>

        <View style={styles.listFooter}>
          <Text style={styles.listChapter} numberOfLines={1}>
            {item.lastChapter}
          </Text>
          <TouchableOpacity
            style={styles.addToShelfButton}
            onPress={() => onAddToShelf?.(item)}>
            <Text
              style={[
                styles.addToShelfText,
                item.isInShelf && styles.addedToShelfText,
              ]}>
              {shelfLabel}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
});
