import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

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
  title = '点评此书',
}) => {
  const colors = useNovelColors();
  const styles = createWriteReviewPageStyles(colors);

  return (
    <View style={styles.topBar}>
      <TouchableOpacity
        style={styles.closeButton}
        onPress={onBackPress}
        activeOpacity={0.7}>
        <Text style={styles.closeIcon}>关闭</Text>
      </TouchableOpacity>

      <View style={styles.titleContainer}>
        <Text style={styles.topBarTitle}>{title}</Text>
      </View>

      <TouchableOpacity
        style={styles.submitButton}
        onPress={onSubmit}
        activeOpacity={0.7}
        disabled={!canSubmit}>
        <Text style={styles.submitButtonText}>发布</Text>
      </TouchableOpacity>
    </View>
  );
};
