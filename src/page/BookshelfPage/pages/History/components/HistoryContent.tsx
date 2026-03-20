import React, { useCallback, useMemo } from 'react';
import { View, Text, ScrollView } from 'react-native';
import { BookItem } from './BookItem';
import { BookshelfContentProps } from '../../../types';
import { BookItem as BookItemType } from '../../../types';
import { wp } from '../../../../../utils/theme/dimensions';

export const HistoryContent: React.FC<BookshelfContentProps> = React.memo(({
  styles,
  books,
  viewType,
  isEditing,
  selectedBooks,
  onBookPress,
  onBookSelect,
  loading,
}) => {
  // 处理书籍点击
  const handleBookPress = useCallback((item: BookItemType) => {
    console.log('Book pressed:', item.title);
    onBookPress(item);
  }, [onBookPress]);

  // 处理书籍选择
  const handleBookSelect = useCallback((item: BookItemType) => {
    console.log('Book selected:', item.title);
    onBookSelect(item);
  }, [onBookSelect]);

  // 网格视图 - 使用useMemo优化三列数据分布计算
  const { leftColumnBooks, middleColumnBooks, rightColumnBooks } = useMemo(() => {
    const left: BookItemType[] = [];
    const middle: BookItemType[] = [];
    const right: BookItemType[] = [];

    books.forEach((book: BookItemType, index: number) => {
      const columnIndex = index % 3;
      if (columnIndex === 0) {
        left.push(book);
      } else if (columnIndex === 1) {
        middle.push(book);
      } else {
        right.push(book);
      }
    });

    return {
      leftColumnBooks: left,
      middleColumnBooks: middle,
      rightColumnBooks: right,
    };
  }, [books]);

  // 条件渲染逻辑
  if (books.length === 0 && !loading) {
    return (
      <View style={styles.emptyContainer}>
        <Text style={styles.emptyIcon}>📚</Text>
        <Text style={styles.emptyText}>书架空空如也</Text>
        <Text style={styles.emptyDescription}>
          去发现更多好书，添加到书架吧~
        </Text>
      </View>
    );
  }

  if (viewType === 'list') {
    return (
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.listContainer}>
          {books.map((book: BookItemType, index: number) => (
            <BookItem
              key={`list-${book.id}`}
              item={book}
              index={index}
              viewType="list"
              isEditing={isEditing}
              isSelected={selectedBooks.includes(book.id)}
              onPress={handleBookPress}
              onSelect={handleBookSelect}
              styles={styles}
            />
          ))}

          {loading && (
            <View style={styles.loadingContainer}>
              <Text style={styles.loadingText}>加载中...</Text>
            </View>
          )}
        </View>
      </ScrollView>
    );
  }

  return (
    <ScrollView
      style={styles.scrollView}
      contentContainerStyle={styles.scrollContent}
      showsVerticalScrollIndicator={false}
    >
      <View style={styles.gridContainer}>
        <View style={{ flexDirection: 'row', justifyContent: 'space-between' }}>
          {/* 左列 */}
          <View style={{ flex: 1, marginRight: wp(4) }}>
            {leftColumnBooks.map((book: BookItemType, index: number) => (
              <View key={`left-${book.id}`} style={{ marginBottom: wp(12) }}>
                <BookItem
                  item={book}
                  index={index * 3}
                  viewType="grid"
                  isEditing={isEditing}
                  isSelected={selectedBooks.includes(book.id)}
                  onPress={handleBookPress}
                  onSelect={handleBookSelect}
                  styles={styles}
                />
              </View>
            ))}
          </View>

          {/* 中列 */}
          <View style={{ flex: 1, marginHorizontal: wp(2) }}>
            {middleColumnBooks.map((book: BookItemType, index: number) => (
              <View key={`middle-${book.id}`} style={{ marginBottom: wp(12) }}>
                <BookItem
                  item={book}
                  index={index * 3 + 1}
                  viewType="grid"
                  isEditing={isEditing}
                  isSelected={selectedBooks.includes(book.id)}
                  onPress={handleBookPress}
                  onSelect={handleBookSelect}
                  styles={styles}
                />
              </View>
            ))}
          </View>

          {/* 右列 */}
          <View style={{ flex: 1, marginLeft: wp(4) }}>
            {rightColumnBooks.map((book: BookItemType, index: number) => (
              <View key={`right-${book.id}`} style={{ marginBottom: wp(12) }}>
                <BookItem
                  item={book}
                  index={index * 3 + 2}
                  viewType="grid"
                  isEditing={isEditing}
                  isSelected={selectedBooks.includes(book.id)}
                  onPress={handleBookPress}
                  onSelect={handleBookSelect}
                  styles={styles}
                />
              </View>
            ))}
          </View>
        </View>

        {loading && (
          <View style={styles.loadingContainer}>
            <Text style={styles.loadingText}>加载中...</Text>
          </View>
        )}
      </View>
    </ScrollView>
  );
});
