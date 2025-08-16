import React from 'react';
import { View, Text, TouchableOpacity, Image } from 'react-native';
import { HistoryItemProps } from '../types';
import NavigationBridge from '../../../../utils/bridge/NavigationBridge';

interface HistoryItemComponentProps extends HistoryItemProps {
  styles: any;
}

export const HistoryItem: React.FC<HistoryItemComponentProps> = React.memo(({
  item,
  onPress: _onPress,
  index: _index,
  viewType,
  styles,
}) => {
  const formatProgress = (progress: number): string => {
    return `已读 ${progress}%`;
  };

  const handlePress = () => {
    console.log('[ScrollBox/HistoryPage] Book pressed:', item.title);
    NavigationBridge.navigateToReader(item.id.toString());
  };

  if (viewType === 'list') {
    return (
      <TouchableOpacity
        style={styles.listItem}
        onPress={handlePress}
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
      onPress={handlePress}
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
