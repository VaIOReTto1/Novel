import React from 'react';
import { View, TouchableOpacity, TextInput } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';
import Icon from 'react-native-vector-icons/MaterialIcons';

interface InputBarProps {
  value: string;
  onChange: (v: string) => void;
  placeholder?: string;
  sending?: boolean;
  onSend: () => void;
  onFocusInput?: () => void;
}

export const InputBar: React.FC<InputBarProps> = ({ value, onChange, placeholder = '有什么问题尽管问我', sending, onSend, onFocusInput }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  const disabled = !value?.trim() || sending;
  return (
    <View style={styles.inputBar}>
      <TextInput
        style={styles.input}
        value={value}
        onChangeText={onChange}
        placeholder={placeholder}
        placeholderTextColor={colors.novelTextGray}
        onFocus={onFocusInput}
        onTouchStart={() => onFocusInput?.()}
        multiline
      />
      <TouchableOpacity style={[styles.sendBtn, disabled ? styles.sendBtnDisabled : null]} disabled={disabled} onPress={disabled ? undefined : onSend} activeOpacity={disabled ? 1 : 0.7}>
        <Icon name="keyboard-arrow-up" size={20} color={colors.novelBackground} />
      </TouchableOpacity>
    </View>
  );
};


