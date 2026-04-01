import React from 'react';
import { View, Text, ActivityIndicator } from 'react-native';

import { createNovelDesignUI } from '../../../../../design-system/novelDesign';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';

interface LoadingIndicatorProps {
  text?: string;
}

export const LoadingIndicator: React.FC<LoadingIndicatorProps> = ({
  text = '加载中...',
}) => {
  const colors = useNovelColors();
  const ui = createNovelDesignUI(colors as any);
  const styles = createCommunityPageStyles(colors);

  return (
    <View style={styles.loadingContainer}>
      <ActivityIndicator
        size="small"
        color={ui.color.brand.primary}
      />
      <Text style={styles.loadingText}>{text}</Text>
    </View>
  );
};
