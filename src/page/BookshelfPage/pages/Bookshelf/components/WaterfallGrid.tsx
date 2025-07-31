import React, { useMemo } from 'react';
import { View, FlatList, ScrollView } from 'react-native';
import { BookshelfItem } from '../types';
import { BookItem } from './BookItem';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { EmptyState } from './EmptyState';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';
import { WATERFALL_COLUMNS } from '../utils/constants';

interface WaterfallGridProps {
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
}

export const WaterfallGrid: React.FC<WaterfallGridProps> = ({
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
  // 将数据分配到两列
  const { leftColumnBooks, rightColumnBooks } = useMemo(() => {
    const left: BookshelfItem[] = [];
    const right: BookshelfItem[] = [];
    
    data.forEach((item, index) => {
      if (index % WATERFALL_COLUMNS === 0) {
        left.push(item);
      } else {
        right.push(item);
      }
    });
    
    return { leftColumnBooks: left, rightColumnBooks: right };
  }, [data]);

  const handleBookPress = (item: BookshelfItem) => {
    onBookPress?.(item);
  };

  const handleBookLongPress = (item: BookshelfItem) => {
    onBookLongPress?.(item);
  };

  if (data.length === 0 && !isLoading) {
    return <EmptyState type="bookshelf" />;
  }

  const renderColumn = (books: BookshelfItem[]) => (
    <View style={styles.waterfallColumn}>
      {books.map((item) => (
        <BookItem
          key={item.id}
          item={item}
          onPress={handleBookPress}
          onLongPress={handleBookLongPress}
          onMenuPress={onMenuPress}
          isSelected={selectedItems.includes(item.id)}
          isEditMode={isEditMode}
          viewType="waterfall"
        />
      ))}
    </View>
  );

  return (
    <ScrollView style={styles.waterfallContainer} showsVerticalScrollIndicator={false}>
      <View style={styles.waterfallGrid}>
        {renderColumn(leftColumnBooks)}
        {renderColumn(rightColumnBooks)}
      </View>
      
      {(hasMore || isLoading) && (
        <LoadMoreIndicator
          isLoading={isLoading}
          onLoadMore={onLoadMore}
        />
      )}
    </ScrollView>
  );
};