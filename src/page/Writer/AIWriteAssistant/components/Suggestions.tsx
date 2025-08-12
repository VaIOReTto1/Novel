import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';

interface SuggestionsProps {
  onDeepThink: () => void;
  onIdea: () => void;
}

export const Suggestions: React.FC<SuggestionsProps> = ({ onDeepThink, onIdea }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  return (
    <View style={styles.suggestions}>
      <TouchableOpacity style={styles.suggestBtn} onPress={onDeepThink}>
        <Text style={styles.suggestText}>深度思考</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.suggestBtn} onPress={onIdea}>
        <Text style={styles.suggestText}>开书灵感</Text>
      </TouchableOpacity>
    </View>
  );
};


