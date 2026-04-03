import React, { useCallback } from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

interface SubTabsSectionProps {
  styles: any;
  selectedTab: 'online' | 'offline';
  onlineCount: number;
  offlineCount: number;
  onTabChange: (tab: 'online' | 'offline') => void;
}

export const SubTabsSection: React.FC<SubTabsSectionProps> = React.memo(({
  styles,
  selectedTab,
  onlineCount,
  offlineCount,
  onTabChange,
}) => {
  const handleTabPress = useCallback(
    (tab: 'online' | 'offline') => {
      onTabChange(tab);
    },
    [onTabChange],
  );

  return (
    <View style={styles.subTabsContainer}>
      <TouchableOpacity
        style={[
          styles.subTab,
          selectedTab === 'online' && styles.activeSubTab,
        ]}
        onPress={() => handleTabPress('online')}
        activeOpacity={0.7}>
        <Text
          style={[
            styles.subTabText,
            selectedTab === 'online' && styles.activeSubTabText,
          ]}>
          {`已上线 ${onlineCount}`}
        </Text>
      </TouchableOpacity>

      <TouchableOpacity
        style={[
          styles.subTab,
          selectedTab === 'offline' && styles.activeSubTab,
        ]}
        onPress={() => handleTabPress('offline')}
        activeOpacity={0.7}>
        <Text
          style={[
            styles.subTabText,
            selectedTab === 'offline' && styles.activeSubTabText,
          ]}>
          {`待上线 ${offlineCount}`}
        </Text>
      </TouchableOpacity>
    </View>
  );
});
