import React, { useCallback, useMemo } from 'react';
import { View, Text } from 'react-native';
import { HistoryItem } from './HistoryItem';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { HistoryItem as HistoryItemType } from '../types';

interface ContentAreaProps {
  styles: any;
  items: HistoryItemType[];
  viewType: 'grid' | 'list';
  loading: boolean;
  hasMore: boolean;
  onItemPress: (item: HistoryItemType) => void;
}

export const ContentArea: React.FC<ContentAreaProps> = React.memo(({
  styles,
  items,
  viewType,
  loading,
  hasMore,
  onItemPress,
}) => {
  // 所有hooks必须在条件返回之前调用
  const handleItemPress = useCallback((item: HistoryItemType) => {
    console.log('History item pressed:', item.title);
    onItemPress(item);
  }, [onItemPress]);

  // 网格视图 - 使用useMemo优化三列数据分布计算
  const { leftColumnItems, middleColumnItems, rightColumnItems } = useMemo(() => {
    const left: HistoryItemType[] = [];
    const middle: HistoryItemType[] = [];
    const right: HistoryItemType[] = [];

    items.forEach((item, index) => {
      const columnIndex = index % 3;
      if (columnIndex === 0) {
        left.push(item);
      } else if (columnIndex === 1) {
        middle.push(item);
      } else {
        right.push(item);
      }
    });

    return {
      leftColumnItems: left,
      middleColumnItems: middle,
      rightColumnItems: right,
    };
  }, [items]);

  // 条件渲染逻辑
  if (items.length === 0 && !loading) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyText}>暂无浏览历史</Text>
      </View>
    );
  }

  if (viewType === 'list') {
    return (
      <View style={styles.listContainer}>
        {items.map((item, index) => (
          <HistoryItem
            key={`list-${item.id}`}
            item={item}
            index={index}
            viewType="list"
            onPress={() => handleItemPress(item)}
            styles={styles}
          />
        ))}

        <LoadMoreIndicator
          loading={loading}
          hasMore={hasMore}
          styles={styles}
        />
      </View>
    );
  }

  return (
    <View style={styles.gridContainer}>
      <View style={styles.gridColumns}>
        {/* 左列 */}
        <View style={styles.gridColumnLeft}>
          {leftColumnItems.map((item, index) => (
            <View key={`left-${item.id}`} style={styles.gridColumnItem}>
              <HistoryItem
                item={item}
                index={index * 3}
                viewType="grid"
                onPress={() => handleItemPress(item)}
                styles={styles}
              />
            </View>
          ))}
        </View>

        {/* 中列 */}
        <View style={styles.gridColumnMiddle}>
          {middleColumnItems.map((item, index) => (
            <View key={`middle-${item.id}`} style={styles.gridColumnItem}>
              <HistoryItem
                item={item}
                index={index * 3 + 1}
                viewType="grid"
                onPress={() => handleItemPress(item)}
                styles={styles}
              />
            </View>
          ))}
        </View>

        {/* 右列 */}
        <View style={styles.gridColumnRight}>
          {rightColumnItems.map((item, index) => (
            <View key={`right-${item.id}`} style={styles.gridColumnItem}>
              <HistoryItem
                item={item}
                index={index * 3 + 2}
                viewType="grid"
                onPress={() => handleItemPress(item)}
                styles={styles}
              />
            </View>
          ))}
        </View>
      </View>

      <LoadMoreIndicator
        loading={loading}
        hasMore={hasMore}
        styles={styles}
      />
    </View>
  );
});
