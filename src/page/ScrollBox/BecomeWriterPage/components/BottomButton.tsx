import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';

interface BottomButtonProps {
  styles: any;
  onPress: () => void;
}

export const BottomButton: React.FC<BottomButtonProps> = React.memo(({
  styles,
  onPress,
}) => {
  return (
    <View style={styles.bottomButtonContainer}>
      <TouchableOpacity
        style={[styles.bottomButton, styles.bottomButtonGradient]}
        onPress={onPress}
        activeOpacity={0.8}
      >
        <Text style={styles.bottomButtonText}>成为番茄作家</Text>
      </TouchableOpacity>
    </View>
  );
}); 