import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../../utils/theme';
import { createCommunityPageStyles } from '../styles/CommunityPageStyles';

interface TopBarProps {
  onSearch?: () => void;
  onNotification?: () => void;
  onPublish?: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  onSearch,
  onNotification,
  onPublish,
}) => {
  const colors = useNovelColors();
  const styles = createCommunityPageStyles(colors);

  return (
    <View style={styles.topBar}>
      <View style={styles.topBarContent}>
        <Text style={styles.topBarTitle}>圈子</Text>
        
        <View style={styles.topBarActions}>
          <TouchableOpacity 
            style={styles.topBarButton}
            onPress={onSearch}
          >
            <Text style={styles.topBarButtonText}>🔍</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.topBarButton}
            onPress={onNotification}
          >
            <Text style={styles.topBarButtonText}>🔔</Text>
          </TouchableOpacity>
          
          <TouchableOpacity 
            style={styles.topBarButton}
            onPress={onPublish}
          >
            <Text style={styles.topBarButtonText}>✏️</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};