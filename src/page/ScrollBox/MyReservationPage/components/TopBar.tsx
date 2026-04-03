import React, { useCallback } from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { TopBarProps } from '../types';

interface TopBarComponentProps extends TopBarProps {
  styles: any;
  selectedTab: 'new' | 'mine';
  onTabChange: (tab: 'new' | 'mine') => void;
}

const MAIN_TAB_DATA = [
  { key: 'new', name: '新剧预约' },
  { key: 'mine', name: '我的预约' },
] as const;

export const TopBar: React.FC<TopBarComponentProps> = ({
  styles,
  selectedTab,
  onTabChange,
  onBackPress,
}) => {
  const handleTabPress = useCallback(
    (tab: 'new' | 'mine') => {
      onTabChange(tab);
    },
    [onTabChange],
  );

  return (
    <View style={styles.topBar}>
      <TouchableOpacity
        style={styles.backButton}
        onPress={onBackPress}
        activeOpacity={0.7}>
        <Text style={styles.backArrow}>{'<'}</Text>
      </TouchableOpacity>

      <View style={styles.topBarTabsContainer}>
        {MAIN_TAB_DATA.map((tab) => (
          <TouchableOpacity
            key={tab.key}
            style={styles.topBarTab}
            onPress={() => handleTabPress(tab.key)}
            activeOpacity={0.7}>
            <Text
              style={[
                styles.topBarTabText,
                selectedTab === tab.key && styles.activeTopBarTabText,
              ]}>
              {tab.name}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      <View style={styles.backButton} />
    </View>
  );
};
