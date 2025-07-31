import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface EmptyStateProps {
  type: 'bookshelf' | 'recommendations';
  onAction?: () => void;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  type,
  onAction,
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  const getEmptyContent = () => {
    switch (type) {
      case 'bookshelf':
        return {
          icon: '📚',
          title: '书架空空如也',
          description: '快去添加一些喜欢的书籍吧',
          actionText: '去发现好书',
        };
      case 'recommendations':
        return {
          icon: '🔍',
          title: '暂无推荐',
          description: '稍后再来看看吧',
          actionText: '刷新推荐',
        };
      default:
        return {
          icon: '📖',
          title: '暂无内容',
          description: '',
          actionText: '',
        };
    }
  };

  const content = getEmptyContent();

  return (
    <View style={styles.emptyContainer}>
      <Text style={styles.emptyIcon}>
        {content.icon}
      </Text>
      
      <Text style={styles.emptyTitle}>
        {content.title}
      </Text>
      
      {content.description && (
        <Text style={styles.emptyDescription}>
          {content.description}
        </Text>
      )}
      
      {content.actionText && onAction && (
        <TouchableOpacity
          style={styles.emptyAction}
          onPress={onAction}
          activeOpacity={0.7}
        >
          <Text style={styles.emptyActionText}>
            {content.actionText}
          </Text>
        </TouchableOpacity>
      )}
    </View>
  );
};