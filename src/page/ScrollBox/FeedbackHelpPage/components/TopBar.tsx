import React from 'react';
import { View, Text, TouchableOpacity, TextInput } from 'react-native';
import { TopBarProps } from '../types';

export const TopBar: React.FC<TopBarProps> = React.memo(({
  styles,
  title,
  onBack,
  onSearch,
  showSearch = false,
  pageType = 'main',
  searchPlaceholder = '听书',
}) => {
  // 主页面显示搜索框
  if (pageType === 'main') {
    return (
      <View style={styles.topBar}>
        <TouchableOpacity style={styles.backButton} onPress={onBack}>
          <Text style={styles.backArrow}>‹</Text>
        </TouchableOpacity>

         <Text style={styles.title}>{title}</Text>

        <View style={styles.searchContainer}>
          <View style={styles.searchInputContainer}>
            <Text style={styles.searchIcon}>🔍</Text>
            <TextInput
              style={styles.searchInput}
              placeholder={searchPlaceholder}
              placeholderTextColor={styles.searchPlaceholder?.color}
              editable={false} // 写死搜索框，不可编辑
            />
          </View>
        </View>

        <TouchableOpacity style={styles.historyButton}>
          <Text style={styles.historyIcon}>📖</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // 其他页面显示标题
  return (
    <View style={styles.topBar}>
      <TouchableOpacity style={styles.backButton} onPress={onBack}>
        <Text style={styles.backArrow}>‹</Text>
      </TouchableOpacity>

      <View style={styles.titleContainer}>
        <Text style={styles.title}>{title}</Text>
      </View>

      {showSearch ? (
        <TouchableOpacity style={styles.searchButton} onPress={onSearch}>
          <Text style={styles.searchIcon}>🔍</Text>
        </TouchableOpacity>
      ) : (
        <View style={styles.searchButton} />
      )}
    </View>
  );
});