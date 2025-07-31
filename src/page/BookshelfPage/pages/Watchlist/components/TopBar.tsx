import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { createWatchlistPageStyles } from '../styles/WatchlistPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

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
      {/* 左侧广告区域 */}
      <View style={styles.topBarLeft}>
        <TouchableOpacity style={styles.adBanner} activeOpacity={0.8}>
          <Text style={styles.adBannerText}>广告位</Text>
        </TouchableOpacity>
      </View>

      {/* 中间空白区域 */}
      <View style={styles.topBarCenter} />

      {/* 右侧编辑按钮 */}
      <View style={styles.topBarRight}>
        <TouchableOpacity
          style={styles.topBarActionButton}
          onPress={onEditPress}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.topBarActionLabel,
            isEditMode && styles.activeTopBarActionLabel
          ]}>
            {isEditMode ? '完成' : '编辑'}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};