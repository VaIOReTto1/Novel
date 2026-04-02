import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';

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
          <TouchableOpacity style={styles.topBarButton} onPress={onSearch}>
            <MaterialIcons name="search" size={20} color={colors.novelMain} />
          </TouchableOpacity>

          <TouchableOpacity style={styles.topBarButton} onPress={onNotification}>
            <MaterialIcons
              name="notifications-none"
              size={20}
              color={colors.novelMain}
            />
          </TouchableOpacity>

          <TouchableOpacity style={styles.topBarButton} onPress={onPublish}>
            <MaterialIcons name="edit" size={20} color={colors.novelMain} />
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};
