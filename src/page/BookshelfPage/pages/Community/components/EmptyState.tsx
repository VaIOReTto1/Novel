import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';

interface EmptyStateProps {
  title: string;
  description: string;
  buttonText?: string;
  onButtonPress?: () => void;
  icon?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  title,
  description,
  buttonText,
  onButtonPress,
  icon = 'forum',
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <View style={styles.emptyContainer}>
      <MaterialIcons name={icon} size={44} color={colors.novelTextGray} />
      <Text style={styles.emptyTitle}>{title}</Text>
      <Text style={styles.emptyDescription}>{description}</Text>

      {buttonText && onButtonPress ? (
        <TouchableOpacity
          style={styles.emptyButton}
          onPress={onButtonPress}>
          <Text style={styles.emptyButtonText}>{buttonText}</Text>
        </TouchableOpacity>
      ) : null}
    </View>
  );
};
