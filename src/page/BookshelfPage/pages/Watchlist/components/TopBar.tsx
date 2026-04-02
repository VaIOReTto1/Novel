import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { useNovelColors } from '../../../../../utils/theme';
import { createWatchlistPageStyles } from '../styles/WatchlistPageStyles';

interface TopBarProps {
  onEditPress: () => void;
  isEditMode?: boolean;
}

export const TopBar: React.FC<TopBarProps> = ({
  onEditPress,
  isEditMode = false,
}) => {
  const colors = useNovelColors();
  const styles = createWatchlistPageStyles(colors);

  return (
    <View style={styles.topBarContainer}>
      <View style={styles.topBarLeft}>
        <TouchableOpacity style={styles.adBanner} activeOpacity={0.8}>
          <Text style={styles.adBannerText}>编辑精选</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.topBarCenter} />

      <View style={styles.topBarRight}>
        <TouchableOpacity
          style={styles.topBarActionButton}
          onPress={onEditPress}
          activeOpacity={0.7}>
          <Text
            style={[
              styles.topBarActionLabel,
              isEditMode && styles.activeTopBarActionLabel,
            ]}>
            {isEditMode ? '完成' : '编辑'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};
