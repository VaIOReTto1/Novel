import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { useNovelColors } from '../../../../utils/theme/colors';
import { createWritePageStyles } from '../styles/WritePageStyles';

interface TopBarProps {
  onBack: () => void;
  onUndo: () => void;
  onRedo: () => void;
  onAI: () => void;
  onPublish: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({
  onBack,
  onUndo,
  onRedo,
  onAI,
  onPublish,
}) => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);

  return (
    <View style={styles.topBar}>
      <TouchableOpacity style={styles.navBtn} onPress={onBack}>
        <Icon name="arrow-back-ios-new" size={18} color={colors.novelText} />
      </TouchableOpacity>

      <View style={styles.toolbar}>
        <TouchableOpacity style={styles.toolBtn} onPress={onUndo}>
          <Icon name="undo" size={18} color={colors.novelText} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.toolBtn} onPress={onRedo}>
          <Icon name="redo" size={18} color={colors.novelText} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.toolBtn} onPress={onAI}>
          <Icon name="auto-awesome" size={18} color={colors.novelText} />
        </TouchableOpacity>
        <TouchableOpacity style={styles.publishBtn} onPress={onPublish}>
          <Text style={styles.publishText}>发布</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};
