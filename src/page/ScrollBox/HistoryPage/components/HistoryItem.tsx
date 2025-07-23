import React from 'react';
import { View, TouchableOpacity, Text, Image } from 'react-native';
import { HistoryItemProps } from '../types';
import { cleanHtml } from '../../../../utils/htmlTextUtil';

interface HistoryItemComponentProps extends HistoryItemProps {
  styles: any;
}

export const HistoryItem: React.FC<HistoryItemComponentProps> = React.memo(({
  item,
  onPress,
  viewType,
  styles,
}) => {
  const formatLastReadTime = (timeString: string): string => {
    const date = new Date(timeString);
    const now = new Date();
    const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60));
    
    if (diffInHours < 1) {
      return '刚刚阅读';
    } else if (diffInHours < 24) {
      return `${diffInHours}小时前`;
    } else {
      const diffInDays = Math.floor(diffInHours / 24);
      if (diffInDays < 30) {
        return `${diffInDays}天前`;
      } else {
        return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
      }
    }
  };

  const formatProgress = (progress: number): string => {
    return `已读 ${progress}%`;
  };

  if (viewType === 'list') {
    return (
      <TouchableOpacity
        style={styles.listItem}
        onPress={onPress}
        activeOpacity={0.7}
      >
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
        </View>

        <View style={styles.listInfo}>
          <Text style={styles.listTitle} numberOfLines={2}>
            {item.title}
          </Text>
          
          <Text style={styles.listAuthor} numberOfLines={1}>
            {item.author}
          </Text>
        </View>
      </TouchableOpacity>
    );
  }

  // 网格视图
  return (
    <TouchableOpacity
      style={styles.gridItem}
      onPress={onPress}
      activeOpacity={0.7}
    >
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
      </View>

      <View style={styles.gridInfo}>
        <Text style={styles.gridTitle} numberOfLines={2}>
          {item.title}
        </Text>
        
        <Text style={styles.gridAuthor} numberOfLines={1}>
          {item.author}
        </Text>
        
        <Text style={styles.gridProgress}>
          {formatProgress(item.readProgress)}
        </Text>
      </View>
    </TouchableOpacity>
  );
}); 