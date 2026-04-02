import React from 'react';
import { TouchableOpacity } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';

interface FloatingButtonProps {
  onPress: () => void;
  icon?: string;
}

export const FloatingButton: React.FC<FloatingButtonProps> = ({
  onPress,
  icon = 'edit',
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <TouchableOpacity
      style={styles.floatingButton}
      onPress={onPress}
      activeOpacity={0.8}>
      <MaterialIcons name={icon} size={22} color={colors.novelBackground} />
    </TouchableOpacity>
  );
};
