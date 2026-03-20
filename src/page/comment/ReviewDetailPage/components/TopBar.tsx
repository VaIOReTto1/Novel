import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useReviewDetailPageStyles } from '../hooks/useReviewDetailPageStyles';

interface TopBarProps {
  onBack?: () => void;
  bookInfo?: {
    bookId: string;
    bookName: string;
    authorName: string;
    picUrl: string;
  };
}

const TopBar: React.FC<TopBarProps> = ({ onBack, bookInfo: _bookInfo }) => {
  const { colors, styles } = useReviewDetailPageStyles();

  return (
    <View style={styles.topBar}>
      <TouchableOpacity style={styles.backButton} onPress={onBack}>
        <Icon name="arrow-back-ios" size={24} color={colors.novelText} />
      </TouchableOpacity>

      <Text style={styles.topBarTitle}>评论详情</Text>

      <TouchableOpacity style={styles.menuButton} activeOpacity={0.7}>
        <Icon name="more-vert" size={24} color={colors.novelText} />
      </TouchableOpacity>
    </View>
  );
};

export { TopBar };
