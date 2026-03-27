import React from 'react';
import { TouchableOpacity, Text } from 'react-native';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';

interface FloatingButtonProps {
  onPress: () => void;
  icon?: string;
}

export const FloatingButton: React.FC<FloatingButtonProps> = ({
  onPress,
  icon = '✏️',
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <TouchableOpacity
      style={styles.floatingButton}
      onPress={onPress}
      activeOpacity={0.8}
    >
      <Text style={styles.floatingButtonIcon}>{icon}</Text>
    </TouchableOpacity>
  );
};
