import React, { memo, useState } from 'react';
import { View, Text, TouchableOpacity, TextInput } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createCommentPageStyles } from '../styles/CommentPageStyles';

interface TopBarProps {
  title?: string;
  onBackPress: () => void;
  onSearch?: (query: string) => void;
}

export const TopBar = memo(({ title = '书评详情', onBackPress, onSearch }: TopBarProps) => {
  const colors = useNovelColors();
  const styles = createCommentPageStyles(colors);
  const [searchQuery, setSearchQuery] = useState('');
  Icon.loadFont(); 
  
  const handleSearch = () => {
    if (onSearch) {
      onSearch(searchQuery);
    }
  };
  
  return (
    <View style={styles.topBar}>
      <TouchableOpacity
        style={styles.backButton}
        onPress={onBackPress}
        activeOpacity={0.7}
      >
        <Icon name="arrow-back-ios" size={24} color={colors.novelText} />
      </TouchableOpacity>
      
      <View style={styles.searchContainer}>
        <Icon name="search" size={20} color={colors.novelTextGray} />
        <TextInput
          style={styles.searchInput}
          placeholder="搜索 分身分身闯上嗨，...的书评"
          placeholderTextColor={colors.novelTextGray}
          value={searchQuery}
          onChangeText={setSearchQuery}
          onSubmitEditing={handleSearch}
        />
        <TouchableOpacity
          style={styles.searchButton}
          onPress={handleSearch}
          activeOpacity={0.7}
        >
        </TouchableOpacity>
      </View>
    </View>
  );
});