import React, { useCallback } from 'react';
import { Text, View } from 'react-native';

import { Book } from '../types';
import { BookItem } from './BookItem';
import { LoadMoreIndicator } from './LoadMoreIndicator';

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
  const handleBookPress = useCallback(
    (book: Book) => {
      onBookPress(book);
    },
    [onBookPress],
  );

  if (books.length === 0 && !loading) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyText}>暂无推荐书籍</Text>
      </View>
    );
  }

  const leftColumnBooks = books.filter((_, index) => index % 2 === 0);
  const rightColumnBooks = books.filter((_, index) => index % 2 === 1);

  return (
    <View>
      <View style={styles.waterfallGrid}>
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

      <LoadMoreIndicator loading={loading} hasMore={hasMore} styles={styles} />
    </View>
  );
};
