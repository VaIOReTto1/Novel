import React from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';

import NavigationBridge from '../../../../utils/bridge/NavigationBridge';
import { HistoryItemProps } from '../types';

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
  const formatProgress = (progress: number): string => `已读 ${progress}%`;

  const handlePress = () => {
    NavigationBridge.navigateToReader(item.id.toString());
  };

  if (viewType === 'list') {
    return (
      <TouchableOpacity
        style={styles.listItem}
        onPress={handlePress}
        activeOpacity={0.7}>
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

  return (
    <TouchableOpacity
      style={styles.gridItem}
      onPress={handlePress}
      activeOpacity={0.7}>
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
