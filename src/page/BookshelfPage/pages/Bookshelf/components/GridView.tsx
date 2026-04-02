import React from 'react';
import { FlatList } from 'react-native';
import { BookshelfItem } from '../types';
import { BookItem } from './BookItem';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { EmptyState } from './EmptyState';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';
import { GRID_COLUMNS } from '../utils/constants';

interface GridViewProps {
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

export const GridView: React.FC<GridViewProps> = ({
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
  const renderItem = ({ item }: { item: BookshelfItem }) => (
    <BookItem
      item={item}
      onPress={onBookPress}
      onLongPress={onBookLongPress}
      onMenuPress={onMenuPress}
      isSelected={selectedItems.includes(item.id)}
      isEditMode={isEditMode}
      viewType="grid"
    />
  );

  const renderFooter = () => {
    if (hasMore || isLoading) {
      return (
        <LoadMoreIndicator
          isLoading={isLoading}
          onLoadMore={onLoadMore}
        />
      );
    }
    return null;
  };

  const renderEmpty = () => {
    if (isLoading || isRefreshing) {return null;}
    return <EmptyState type="bookshelf" />;
  };

  return (
    <FlatList
      data={data}
      renderItem={renderItem}
      keyExtractor={(item: { id: any; }) => item.id}
      numColumns={GRID_COLUMNS}
      contentContainerStyle={styles.gridContainer}
      columnWrapperStyle={GRID_COLUMNS > 1 ? styles.gridRow : undefined}
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
      initialNumToRender={6}
    />
  );
};
