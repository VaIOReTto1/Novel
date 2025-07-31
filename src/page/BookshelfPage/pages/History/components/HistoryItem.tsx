import React from 'react';
import { View, Text, TouchableOpacity, Image } from 'react-native';
import { HistoryItemProps } from '../types';

export const HistoryItem: React.FC<HistoryItemProps> = React.memo(({
  styles,
  item,
  viewType,
  isEditing,
  isSelected,
  onPress,
  onSelect,
  onAddToShelf,
}) => {
  const handlePress = () => {
    if (isEditing) {
      onSelect();
    } else {
      onPress();
    }
  };

  const formatTime = (timestamp: number) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    
    if (days === 0) {
      return '今天';
    } else if (days === 1) {
      return '昨天';
    } else if (days < 7) {
      return `${days}天前`;
    } else {
      return date.toLocaleDateString();
    }
  };

  const getProgressText = (progress: number) => {
    if (progress >= 1) {
      return '已完结';
    }
    return `${Math.floor(progress * 100)}%`;
  };

  if (viewType === 'grid') {
    return (
      <TouchableOpacity
        style={[
          styles.gridItem,
          isSelected && styles.selectedItem,
        ]}
        onPress={handlePress}
        activeOpacity={0.7}
      >
        {isEditing && (
          <View style={styles.selectOverlay}>
            <View style={[
              styles.selectCheckbox,
              isSelected && styles.selectCheckboxSelected,
            ]}>
              {isSelected && (
                <Text style={styles.selectCheckIcon}>✓</Text>
              )}
            </View>
          </View>
        )}
        
        <View style={styles.gridCover}>
          <Image
            source={{ uri: item.cover || 'https://via.placeholder.com/120x160' }}
            style={styles.gridCoverImage}
            resizeMode="cover"
          />
          <View style={styles.gridProgress}>
            <View style={[
              styles.gridProgressBar,
              { width: `${Math.floor(item.progress * 100)}%` }
            ]} />
          </View>
        </View>
        
        <View style={styles.gridInfo}>
          <Text style={styles.gridTitle} numberOfLines={2}>
            {item.title}
          </Text>
          <Text style={styles.gridProgress}>
            {getProgressText(item.progress)}
          </Text>
          <TouchableOpacity 
            style={styles.gridAddToShelfButton}
            onPress={() => onAddToShelf?.(item)}
          >
            <Text style={[
              styles.gridAddToShelfText,
              item.isInShelf && styles.gridAddedToShelfText
            ]}>
              {item.isInShelf ? '已加入' : '加入书架'}
            </Text>
          </TouchableOpacity>
        </View>
      </TouchableOpacity>
    );
  }

  return (
    <TouchableOpacity
      style={[
        styles.listItem,
        isSelected && styles.selectedItem,
      ]}
      onPress={handlePress}
      activeOpacity={0.7}
    >
      {isEditing && (
        <View style={styles.listSelectArea}>
          <View style={[
            styles.selectCheckbox,
            isSelected && styles.selectCheckboxSelected,
          ]}>
            {isSelected && (
              <Text style={styles.selectCheckIcon}>✓</Text>
            )}
          </View>
        </View>
      )}
      
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
            onPress={() => onAddToShelf?.(item)}
          >
            <Text style={[
              styles.addToShelfText,
              item.isInShelf && styles.addedToShelfText
            ]}>
              {item.isInShelf ? '已加入' : '加入书架'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );
});