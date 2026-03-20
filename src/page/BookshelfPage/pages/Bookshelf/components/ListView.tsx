import React from 'react';
import { FlatList, View } from 'react-native';
import { BookshelfItem } from '../types';
import { BookItem } from './BookItem';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { EmptyState } from './EmptyState';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface ListViewProps {
  data: BookshelfItem[];
  onBookPress?: (item: BookshelfItem) => void;
  onBookLongPress?: (item: BookshelfItem) => void;
  onMenuPress?: (item: BookshelfItem) => void;
  onLoadMore?: () => void;
  onRefresh?: () => void;
  hasMore?: boolean;
  isLoading?: boolean;
  isRefreshing?: boolean;
  isEditMode?: boolean;
  selectedItems?: string[];
  // 推荐流相关props
}

export const ListView: React.FC<ListViewProps> = ({
  data,
  onBookPress,
  onBookLongPress,
  onMenuPress,
  onLoadMore,
  onRefresh,
  hasMore = false,
  isLoading = false,
  isRefreshing = false,
  isEditMode = false,
  selectedItems = [],
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  const renderItem = ({ item, index }: { item: BookshelfItem; index: number }) => (
    <>
      <BookItem
        item={item}
        onPress={onBookPress}
        onLongPress={onBookLongPress}
        onMenuPress={onMenuPress}
        isSelected={selectedItems.includes(item.id)}
        isEditMode={isEditMode}
        viewType="list"
      />
      {index < data.length - 1 && (
        <View style={styles.listSeparator} />
      )}
    </>
  );

  const renderFooter = () => {
    return (
      <View>
        {/* 书架加载更多 */}
        {(hasMore || isLoading) && (
          <LoadMoreIndicator
            isLoading={isLoading}
            onLoadMore={onLoadMore}
          />
        )}
      </View>
    );
  };

  const renderEmpty = () => {
    if (isLoading || isRefreshing) {return null;}
    return <EmptyState type="bookshelf" />;
  };

  const getItemLayout = (_: any, index: number) => ({
    length: 120, // 估算的item高度
    offset: 120 * index,
    index,
  });

  return (
    <FlatList
      data={data}
      renderItem={renderItem}
      keyExtractor={(item: { id: any; }) => item.id}
      contentContainerStyle={styles.listContainer}
      onEndReached={onLoadMore}
      onEndReachedThreshold={0.1}
      onRefresh={onRefresh}
      refreshing={isRefreshing}
      ListFooterComponent={renderFooter}
      ListEmptyComponent={renderEmpty}
      showsVerticalScrollIndicator={false}
      removeClippedSubviews={true}
      maxToRenderPerBatch={10}
      windowSize={10}
      initialNumToRender={8}
      getItemLayout={getItemLayout}
    />
  );
};
