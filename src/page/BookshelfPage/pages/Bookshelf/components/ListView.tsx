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
  styles?: ReturnType<typeof createBookshelfPageStyles>;
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
  styles,
}) => {
  const colors = useNovelColors();
  const resolvedStyles = styles ?? createBookshelfPageStyles(colors);

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
      {index < data.length - 1 ? (
        <View style={resolvedStyles.listSeparator} />
      ) : null}
    </>
  );

  const renderFooter = () => {
    return (
      <View>
        {(hasMore || isLoading) ? (
          <LoadMoreIndicator
            isLoading={isLoading}
            onLoadMore={onLoadMore}
          />
        ) : null}
      </View>
    );
  };

  const renderEmpty = () => {
    if (isLoading || isRefreshing) {
      return null;
    }
    return <EmptyState type="bookshelf" />;
  };

  const getItemLayout = (_: any, index: number) => ({
    length: 120,
    offset: 120 * index,
    index,
  });

  return (
    <FlatList
      data={data}
      renderItem={renderItem}
      keyExtractor={(item: { id: any }) => item.id}
      contentContainerStyle={resolvedStyles.listContainer}
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
