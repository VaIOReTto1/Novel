import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createWritePageStyles } from '../styles/WritePageStyles';
import Icon from 'react-native-vector-icons/MaterialIcons';

interface TopBarProps {
  onBack: () => void;
  onUndo: () => void;
  onRedo: () => void;
  onAI: () => void;
  onPublish: () => void;
}

export const TopBar: React.FC<TopBarProps> = ({ onBack, onUndo, onRedo, onAI, onPublish }) => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);
  return (
    <View style={styles.topBar}>
      <TouchableOpacity style={styles.navBtn} onPress={onBack}>
        <Icon name="arrow-back-ios" size={24} color={colors.novelText} />
      </TouchableOpacity>
      <View style={styles.toolbar}>
        <TouchableOpacity style={styles.toolBtn} onPress={onUndo}><Text style={styles.toolIcon}>↶</Text></TouchableOpacity>
        <TouchableOpacity style={styles.toolBtn} onPress={onRedo}><Text style={styles.toolIcon}>↷</Text></TouchableOpacity>
        <TouchableOpacity style={styles.toolBtn} onPress={onAI}><Text style={styles.toolIcon}>🤖</Text></TouchableOpacity>
        <TouchableOpacity style={styles.publishBtn} onPress={onPublish}><Text style={styles.publishText}>发布</Text></TouchableOpacity>
      </View>
    </View>
  );
};


