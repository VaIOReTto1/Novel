import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { useNovelColors } from '../../../utils/theme';
import { SexTab } from '../store/categoryStore';
import { createCategoryPageStyles } from '../styles/CategoryPageStyles';

interface TopTabsProps {
  tab: SexTab;
  onChange: (tab: SexTab) => void;
}

export const TopTabs: React.FC<TopTabsProps> = ({ tab, onChange }) => {
  const colors = useNovelColors();
  const styles = createCategoryPageStyles(colors);

  const renderBtn = (key: SexTab, label: string) => (
    <TouchableOpacity key={key} onPress={() => onChange(key)}>
      <Text style={[styles.topTabText, tab === key && styles.topTabActive]}>
        {label}
      </Text>
    </TouchableOpacity>
  );

  return (
    <View style={styles.topTabs}>
      {renderBtn('male', '男生')}
      {renderBtn('female', '女生')}
    </View>
  );
};
