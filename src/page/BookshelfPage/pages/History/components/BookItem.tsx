import React from 'react';
import { View, TouchableOpacity, Text, Image } from 'react-native';
import { BookItemProps } from '../../../types';

export const BookItem: React.FC<BookItemProps> = React.memo(({
  item,
  viewType,
  isEditing,
  isSelected,
  onPress,
  onSelect,
  styles,
}) => {
  const formatProgress = (progress: number): string => {
    return `已读 ${progress}%`;
  };

  const formatTime = (timeString: string): string => {
    const date = new Date(timeString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) {
      return '今天';
    } else if (diffDays === 1) {
      return '昨天';
    } else if (diffDays < 30) {
      return `${diffDays}天前`;
    } else {
      return date.toLocaleDateString();
    }
  };

  const handlePress = () => {
    if (isEditing && onSelect) {
      onSelect(item);
    } else {
      onPress(item);
    }
  };

  if (viewType === 'list') {
    return (
      <TouchableOpacity
        style={[styles.listItem, isEditing && { opacity: 0.8 }]}
        onPress={handlePress}
        activeOpacity={0.7}
      >
        {/* 编辑模式选择框 */}
        {isEditing && (
          <View style={[
            styles.editCheckbox,
            isSelected && styles.editCheckboxSelected
          ]}>
            {isSelected && (
              <Text style={styles.editCheckIcon}>✓</Text>
            )}
          </View>
        )}

        <View style={styles.listCover}>
          {item.coverUrl ? (
            <Image
              source={{ uri: item.coverUrl }}
              style={styles.listCoverImage}
              resizeMode="cover"
            />
          ) : (
            <View style={styles.placeholderCover}>
              <Text style={styles.placeholderText}>暂无封面</Text>
            </View>
          )}
          
          {/* 状态标签 */}
          {item.isFinished ? (
            <View style={[styles.bookBadge, styles.finishedBadge]}>
              <Text style={styles.badgeText}>完结</Text>
            </View>
          ) : (
            <View style={[styles.bookBadge, styles.updateBadge]}>
              <Text style={styles.badgeText}>更新</Text>
            </View>
          )}
        </View>

        <View style={styles.listInfo}>
          <Text style={styles.listTitle} numberOfLines={2}>
            {item.title}
          </Text>

          <Text style={styles.listAuthor} numberOfLines={1}>
            {item.author}
          </Text>

          <View style={styles.listMeta}>
            <Text style={styles.listProgress}>
              {formatProgress(item.progress)}
            </Text>
            <Text style={styles.listTime}>
              {formatTime(item.lastReadTime)}
            </Text>
          </View>

          {/* 进度条 */}
          <View style={styles.listProgressBar}>
            <View 
              style={[
                styles.listProgressFill,
                { width: `${item.progress}%` }
              ]} 
            />
          </View>
        </View>
      </TouchableOpacity>
    );
  }

  // 网格视图
  return (
    <TouchableOpacity
      style={[styles.gridItem, isEditing && { opacity: 0.8 }]}
      onPress={handlePress}
      activeOpacity={0.7}
    >
      {/* 编辑模式选择框 */}
      {isEditing && (
        <View style={[
          styles.editCheckbox,
          isSelected && styles.editCheckboxSelected
        ]}>
          {isSelected && (
            <Text style={styles.editCheckIcon}>✓</Text>
          )}
        </View>
      )}

      <View style={styles.gridCover}>
        {item.coverUrl ? (
          <Image
            source={{ uri: item.coverUrl }}
            style={styles.gridCoverImage}
            resizeMode="cover"
          />
        ) : (
          <View style={styles.placeholderCover}>
            <Text style={styles.placeholderText}>暂无封面</Text>
          </View>
        )}
        
        {/* 状态标签 */}
        {item.isFinished ? (
          <View style={[styles.bookBadge, styles.finishedBadge]}>
            <Text style={styles.badgeText}>完结</Text>
          </View>
        ) : (
          <View style={[styles.bookBadge, styles.updateBadge]}>
            <Text style={styles.badgeText}>更新</Text>
          </View>
        )}

        {/* 编辑模式遮罩 */}
        {isEditing && <View style={styles.editOverlay} />}
      </View>

      <View style={styles.gridInfo}>
        <Text style={styles.gridTitle} numberOfLines={2}>
          {item.title}
        </Text>

        <Text style={styles.gridAuthor} numberOfLines={1}>
          {item.author}
        </Text>

        <Text style={styles.gridProgress}>
          {formatProgress(item.progress)}
        </Text>

        {/* 进度条 */}
        <View style={styles.gridProgressBar}>
          <View 
            style={[
              styles.gridProgressFill,
              { width: `${item.progress}%` }
            ]} 
          />
        </View>
      </View>
    </TouchableOpacity>
  );
});