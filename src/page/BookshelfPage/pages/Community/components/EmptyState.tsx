import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
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
  icon = '📝',
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <View style={styles.emptyContainer}>
      <Text style={styles.emptyIcon}>{icon}</Text>
      <Text style={styles.emptyTitle}>{title}</Text>
      <Text style={styles.emptyDescription}>{description}</Text>
      
      {buttonText && onButtonPress && (
        <TouchableOpacity 
          style={styles.emptyButton}
          onPress={onButtonPress}
        >
          <Text style={styles.emptyButtonText}>{buttonText}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};