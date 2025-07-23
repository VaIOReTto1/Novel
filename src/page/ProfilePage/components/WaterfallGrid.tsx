import React, { useCallback } from 'react';
import { View, Text } from 'react-native';
import { BookItem } from './BookItem';
import { LoadMoreIndicator } from './LoadMoreIndicator';
import { Book } from '../types';

interface WaterfallGridProps {
  styles: any;
  books: Book[];
  loading: boolean;
  hasMore: boolean;
  onBookPress: (book: Book) => void;
}

export const WaterfallGrid: React.FC<WaterfallGridProps> = ({
  styles,
  books,
  loading,
  hasMore,
  onBookPress,
}) => {
  const handleBookPress = useCallback((book: Book) => {
    console.log('Book pressed:', book.title);
    onBookPress(book);
  }, [onBookPress]);

  if (books.length === 0 && !loading) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyText}>暂无推荐书籍</Text>
      </View>
    );
  }

  // 将书籍分为两列
  const leftColumnBooks = books.filter((_, index) => index % 2 === 0);
  const rightColumnBooks = books.filter((_, index) => index % 2 === 1);

  return (
    <View>
      <View style={styles.waterfallGrid}>
        {/* 左列 */}
        <View style={styles.waterfallColumn}>
          {leftColumnBooks.map((book, index) => (
            <BookItem
              key={`left-${book.id}-${index}`}
              book={book}
              index={index * 2}
              onPress={() => handleBookPress(book)}
              styles={styles}
            />
          ))}
        </View>

        {/* 右列 */}
        <View style={styles.waterfallColumn}>
          {rightColumnBooks.map((book, index) => (
            <BookItem
              key={`right-${book.id}-${index}`}
              book={book}
              index={index * 2 + 1}
              onPress={() => handleBookPress(book)}
              styles={styles}
            />
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
};
