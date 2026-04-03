import React from 'react';
import { Text, TextInput, TouchableOpacity, View } from 'react-native';

import { TopBarProps } from '../types';

export const TopBar: React.FC<TopBarProps> = React.memo(({
  styles,
  title,
  onBack,
  onSearch,
  showSearch = false,
  pageType = 'main',
  searchPlaceholder = '搜索帮助内容',
}) => {
  if (pageType === 'main') {
    return (
      <View style={styles.topBar}>
        <TouchableOpacity style={styles.backButton} onPress={onBack}>
          <Text style={styles.backArrow}>{'<'}</Text>
        </TouchableOpacity>

        <Text style={styles.title}>{title}</Text>

        <View style={styles.searchContainer}>
          <View style={styles.searchInputContainer}>
            <Text style={styles.searchIcon}>搜索</Text>
            <TextInput
              style={styles.searchInput}
              placeholder={searchPlaceholder}
              placeholderTextColor={styles.searchPlaceholder?.color}
              editable={false}
            />
          </View>
        </View>

        <TouchableOpacity style={styles.historyButton}>
          <Text style={styles.historyIcon}>历史</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.topBar}>
      <TouchableOpacity style={styles.backButton} onPress={onBack}>
        <Text style={styles.backArrow}>{'<'}</Text>
      </TouchableOpacity>

      <View style={styles.titleContainer}>
        <Text style={styles.title}>{title}</Text>
      </View>

      {showSearch ? (
        <TouchableOpacity style={styles.searchButton} onPress={onSearch}>
          <Text style={styles.searchIcon}>搜索</Text>
        </TouchableOpacity>
      ) : (
        <View style={styles.searchButton} />
      )}
    </View>
  );
});
