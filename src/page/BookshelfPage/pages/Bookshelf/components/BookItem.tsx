import React from 'react';
import { View, Text, Image, TouchableOpacity } from 'react-native';
import { BookshelfItem } from '../types';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface BookItemProps {
  item: BookshelfItem;
  onPress?: (item: BookshelfItem) => void;
  onLongPress?: (item: BookshelfItem) => void;
  onMenuPress?: (item: BookshelfItem) => void;
  isSelected?: boolean;
  isEditMode?: boolean;
  viewType?: 'grid' | 'list' | 'waterfall';
}

export const BookItem: React.FC<BookItemProps> = ({
  item,
  onPress,
  onLongPress,
  onMenuPress,
  isSelected = false,
  isEditMode = false,
  viewType = 'grid',
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  const handlePress = () => {
    onPress?.(item);
  };

  const handleLongPress = () => {
    onLongPress?.(item);
  };

  const handleMenuPress = () => {
    onMenuPress?.(item);
  };

  const getContainerStyle = () => {
    switch (viewType) {
      case 'list':
        return styles.listBookItem;
      case 'waterfall':
        return styles.waterfallBookItem;
      default:
        return styles.gridBookItem;
    }
  };

  const getCoverStyle = () => {
    switch (viewType) {
      case 'list':
        return styles.listBookCover;
      case 'waterfall':
        return styles.waterfallBookCover;
      default:
        return styles.gridBookCover;
    }
  };

  const getInfoStyle = () => {
    switch (viewType) {
      case 'list':
        return styles.listBookInfo;
      case 'waterfall':
        return styles.waterfallBookInfo;
      default:
        return styles.gridBookInfo;
    }
  };

  // Waterfall模式需要特殊布局
  if (viewType === 'waterfall') {
    return (
      <View style={styles.waterfallItemContainer}>
        {/* 书籍封面 - 在外层，不占满宽度 */}
        <View style={styles.waterfallCoverContainer}>
          <TouchableOpacity
            onPress={handlePress}
            onLongPress={handleLongPress}
            activeOpacity={0.8}
          >
            {(item.cover || item.coverUrl) ? (
              <Image
                source={{ uri: item.cover || item.coverUrl }}
                style={styles.waterfallBookCover}
                resizeMode="cover"
                onError={() => console.log('Image load error')}
              />
            ) : (
              <View style={[styles.waterfallBookCover, styles.imagePlaceholder]} />
            )}

            {/* 标签 */}
            {item.tags && item.tags.length > 0 && (
              <View style={styles.bookTag}>
                <Text style={styles.bookTagText}>{item.tags[0]}</Text>
              </View>
            )}

            {/* 编辑模式选择框 */}
            {isEditMode && (
              <View style={styles.selectionOverlay}>
                <View style={[
                  styles.selectionCheckbox,
                  isSelected && styles.selectedCheckbox,
                ]}>
                  {isSelected && (
                    <Text style={styles.checkboxText}>✓</Text>
                  )}
                </View>
              </View>
            )}
          </TouchableOpacity>
        </View>

        {/* 书籍信息容器 */}
        <TouchableOpacity
          style={[
            styles.waterfallBookInfo,
            isSelected && styles.selectedBookItem,
          ]}
          onPress={handlePress}
          onLongPress={handleLongPress}
          activeOpacity={0.8}
        >
          <View style={styles.waterfallTitleRow}>
            <Text
              style={styles.waterfallBookTitle}
              numberOfLines={2}
            >
              {item.title}
            </Text>
          </View>

          <View style={styles.gridChapterRow}>
            <Text style={styles.waterfallCurrentChapter} numberOfLines={1}>
              {item.currentChapter ? `${item.currentChapter}章/${item.chapterCount || '?'}章` : '未读过'}
            </Text>
            {!isEditMode && (
              <TouchableOpacity
                style={styles.waterfallMenuButton}
                onPress={handleMenuPress}
                hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
              >
                <Text style={styles.menuButtonText}>⋮</Text>
              </TouchableOpacity>
            )}
          </View>

          <Text
            style={styles.waterfallDescription}
            numberOfLines={4}
          >
            {item.description || '暂无简介'}
          </Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <TouchableOpacity
      style={[
        getContainerStyle(),
        isSelected && styles.selectedBookItem,
      ]}
      onPress={handlePress}
      onLongPress={handleLongPress}
      activeOpacity={0.7}
    >
      {/* 书籍封面 */}
      <View style={styles.bookCoverContainer}>
        {(item.cover || item.coverUrl) ? (
          <Image
            source={{ uri: item.cover || item.coverUrl }}
            style={getCoverStyle()}
            resizeMode="contain"
            onError={() => console.log('Image load error')}
          />
        ) : (
          <View style={[getCoverStyle(), styles.imagePlaceholder]} />
        )}

        {/* 标签 */}
        {item.tags && item.tags.length > 0 && (
          <View style={styles.bookTag}>
            <Text style={styles.bookTagText}>{item.tags[0]}</Text>
          </View>
        )}

        {/* 编辑模式选择框 */}
        {isEditMode && (
          <View style={styles.selectionOverlay}>
            <View style={[
              styles.selectionCheckbox,
              isSelected && styles.selectedCheckbox,
            ]}>
              {isSelected && (
                <Text style={styles.checkboxText}>✓</Text>
              )}
            </View>
          </View>
        )}
      </View>

      {/* 书籍信息 */}
      {viewType === 'grid' ? (
        <View>
          <View style={getInfoStyle()}>
            <View style={styles.gridBookContent}>
              <Text
                style={styles.bookTitle}
                numberOfLines={2}
              >
                {item.title}
              </Text>
            </View>
          </View>
          <View style={styles.gridChapterRow}>
            <Text style={styles.gridCurrentChapter}>
              {item.currentChapter ? `${item.currentChapter}章/${item.chapterCount || '?'}章` : '未读过'}
            </Text>
            {!isEditMode && (
              <TouchableOpacity
                style={styles.gridMenuButton}
                onPress={handleMenuPress}
              >
                <Text style={styles.menuButtonText}>⋮</Text>
              </TouchableOpacity>
            )}
          </View>
        </View>
      ) : viewType === 'list' ? (
        <View style={getInfoStyle()}>
          <View style={styles.listBookHeader}>
            <View style={styles.listBookTitleContainer}>
              <Text
                style={[styles.bookTitle, styles.listBookTitle]}
                numberOfLines={1}
              >
                {item.title}
              </Text>
            </View>
            {!isEditMode && (
              <TouchableOpacity
                style={styles.listMenuButton}
                onPress={handleMenuPress}
              >
                <Text style={styles.menuButtonText}>⋮</Text>
              </TouchableOpacity>
            )}
          </View>
          <Text style={styles.listCurrentChapter}>
            {item.currentChapter ? `${item.currentChapter}章/${item.chapterCount || '?'}章` : '未读过'}
          </Text>
          <Text style={styles.listLastUpdate}>
            {item.isCompleted ?
              `已完结 · 共${item.chapterCount || '?'}章` :
              `${item.lastUpdate || '未知时间'}更新 · 第${item.currentChapter || 1}章${item.chapterTitle ? item.chapterTitle.substring(0, 10) + '...' : ''}`
            }
          </Text>
        </View>
      ) : null}
    </TouchableOpacity>
  );
};
