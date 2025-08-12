import React from 'react';
import { View, Text, TouchableOpacity, TextInput } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';

interface InputBarProps {
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
  sending?: boolean;
  onSend: () => void;
}

export const InputBar: React.FC<InputBarProps> = ({ value, onChange, placeholder = '有什么问题尽管问我', sending, onSend }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  return (
    <View style={styles.inputBar}>
      <TextInput
        style={styles.input}
        value={value}
        onChangeText={onChange}
        placeholder={placeholder}
        placeholderTextColor={colors.novelTextGray}
        multiline
      />
      <TouchableOpacity style={styles.sendBtn} onPress={sending ? undefined : onSend} activeOpacity={sending ? 1 : 0.7}>
        <Text style={styles.sendText}>↑</Text>
      </TouchableOpacity>
    </View>
  );
};


