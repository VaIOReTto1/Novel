import React from 'react';
import { View, TouchableOpacity, Text, Image } from 'react-native';
import { BookItemProps } from '../types';
import { calculateBookItemHeight, getDescriptionLines, getBookItemWidth } from '../utils/helpers';
import { cleanHtml } from '../../../utils/htmlTextUtil';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';

interface BookItemComponentProps extends BookItemProps {
  styles: any;
}

export const BookItem: React.FC<BookItemComponentProps> = React.memo(({
  book,
  styles,
}) => {
  const imageHeight = React.useMemo(() => {
    return calculateBookItemHeight(book.id);
  }, [book.id]);

  const descriptionLines = React.useMemo(() => {
    return getDescriptionLines(book.description);
  }, [book.description]);

  const itemWidth = React.useMemo(() => {
    return getBookItemWidth();
  }, []);

  const handlePress = () => {
    console.log('[ProfilePage] Book pressed:', book.title);
    NavigationBridge.navigateToReader(book.id.toString());
  };

  return (
    <TouchableOpacity
      style={[styles.waterfallBookItem, { width: itemWidth }]}
      onPress={handlePress}
      activeOpacity={0.7}
    >
      {/* 书籍封面 */}
      <View style={[styles.waterfallBookCover, { height: imageHeight }]}>
        {book.coverUrl ? (
          <Image
            source={{ uri: book.coverUrl }}
            style={styles.waterfallCoverImage}
            resizeMode="cover"
          />
        ) : (
          <View style={styles.waterfallPlaceholderCover}>
            <Text style={styles.waterfallPlaceholderText}>暂无封面</Text>
          </View>
        )}
      </View>

      {/* 书籍信息 */}
      <View style={styles.waterfallBookInfo}>
        {/* 书名 */}
        <Text
          style={styles.waterfallBookTitle}
          numberOfLines={2}
          ellipsizeMode="tail"
        >
          {book.title}
        </Text>

        {/* 作者 */}
        <Text
          style={styles.waterfallBookAuthor}
          numberOfLines={1}
          ellipsizeMode="tail"
        >
          {book.author}
        </Text>

        {/* 描述 */}
        {book.description && (
          <Text
            style={styles.waterfallBookDescription}
            numberOfLines={descriptionLines}
            ellipsizeMode="tail"
          >
            {cleanHtml(book.description)}
          </Text>
        )}
      </View>
    </TouchableOpacity>
  );
});
