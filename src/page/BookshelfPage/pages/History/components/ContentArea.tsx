import React from 'react';
import { View, FlatList, Text } from 'react-native';
import { HistoryContentAreaProps } from '../types';
import { HistoryItem } from './HistoryItem';

export const ContentArea: React.FC<HistoryContentAreaProps> = React.memo(({
  styles,
  historyItems,
  viewType,
  isEditing,
  selectedItems,
  onItemPress,
  onItemSelect,
  onAddToShelf,
  loading,
  error,
  onRefresh,
  refreshing,
  onLoadMore,
}) => {
  if (loading && historyItems.length === 0) {
    return (
      <View style={styles.loadingContainer}>
        <Text style={styles.loadingText}>加载中...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.errorContainer}>
        <Text style={styles.errorText}>{error}</Text>
      </View>
    );
  }

  if (historyItems.length === 0) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyText}>暂无历史记录</Text>
      </View>
    );
  }

  // 网格布局需要手动分组以支持justifyContent: 'space-between'
  const renderGridRow = ({ item: rowItems }: { item: any[] }) => (
    <View style={styles.gridRow}>
      {rowItems.map((item, index) => (
        <HistoryItem
          key={item.id}
          styles={styles}
          item={item}
          viewType={viewType}
          isEditing={isEditing}
          isSelected={selectedItems.includes(item.id)}
          onPress={() => onItemPress(item)}
          onSelect={() => onItemSelect(item.id)}
          onAddToShelf={onAddToShelf}
        />
      ))}
      {/* 填充空白项以保持对齐 */}
      {Array.from({ length: 3 - rowItems.length }).map((_, index) => (
        <View key={`empty-${index}`} style={styles.gridItem} />
      ))}
    </View>
  );

  const renderListItem = ({ item }: { item: any }) => (
    <HistoryItem
      styles={styles}
      item={item}
      viewType={viewType}
      isEditing={isEditing}
      isSelected={selectedItems.includes(item.id)}
      onPress={() => onItemPress(item)}
      onSelect={() => onItemSelect(item.id)}
      onAddToShelf={onAddToShelf}
    />
  );

  // 将数据分组为每行3个项目（仅网格模式）
  const gridData = viewType === 'grid' 
    ? historyItems.reduce((rows: any[][], item: any, index: number) => {
        if (index % 3 === 0) rows.push([]);
        rows[rows.length - 1].push(item);
        return rows;
      }, [])
    : historyItems;

  const renderItem = viewType === 'grid' ? renderGridRow : renderListItem;
  const data = viewType === 'grid' ? gridData : historyItems;
  const keyExtractor = viewType === 'grid' 
    ? (item: any[], index: number) => `row-${index}`
    : (item: any) => item.id;

  return (
    <FlatList
      key={viewType}
      data={data}
      renderItem={renderItem}
      keyExtractor={keyExtractor}
      contentContainerStyle={[
        styles.contentContainer,
        viewType === 'grid' && styles.gridContainer,
      ]}
      showsVerticalScrollIndicator={false}
      onRefresh={onRefresh}
      refreshing={refreshing}
      onEndReached={onLoadMore}
      onEndReachedThreshold={0.1}
      removeClippedSubviews={true}
      maxToRenderPerBatch={10}
      windowSize={10}
      initialNumToRender={10}
      getItemLayout={viewType === 'list' ? (data: any, index: number) => ({
        length: 80,
        offset: 80 * index,
        index,
      }) : undefined}
    />
  );
});