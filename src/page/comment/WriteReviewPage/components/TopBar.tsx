import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createWriteReviewPageStyles } from '../styles/WriteReviewPageStyles';

interface TopBarProps {
  onBackPress: () => void;
  onSubmit?: () => void;
  canSubmit?: boolean;
  title?: string;
}

export const TopBar: React.FC<TopBarProps> = ({ 
  onBackPress, 
  onSubmit,
  canSubmit = false,
  title = '点评此书' 
}) => {
  const colors = useNovelColors();
  const styles = createWriteReviewPageStyles(colors);

  return (
    <View style={styles.topBar}>
      {/* 关闭按钮 */}
      <TouchableOpacity
        style={styles.closeButton}
        onPress={onBackPress}
        activeOpacity={0.7}
      >
        <Text style={styles.closeIcon}>×</Text>
      </TouchableOpacity>

      {/* 标题 */}
      <View style={styles.titleContainer}>
        <Text style={styles.topBarTitle}>{title}</Text>
      </View>

      {/* 发表按钮 */}
      <TouchableOpacity
        style={[styles.submitButton]}
        onPress={onSubmit}
        activeOpacity={0.7}
        disabled={!canSubmit}
      >
        <Text style={styles.submitButtonText}>发表</Text>
      </TouchableOpacity>
    </View>
  );
};