import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

interface ContactSectionProps {
  styles: any;
  onContactPress: () => void;
}

export const ContactSection: React.FC<ContactSectionProps> = React.memo(({
  styles,
  onContactPress,
}) => {
  return (
    <View style={styles.contactSection}>
      <TouchableOpacity style={styles.contactButton} onPress={onContactPress}>
        <Text style={styles.contactButtonIcon}>✏️</Text>
        <Text style={styles.contactButtonText}>意见反馈</Text>
      </TouchableOpacity>

      <View style={styles.contactInfo}>
        <Text style={styles.contactInfoText}>
          番茄官方服务热线：957124，工作时间8:30-22:00
        </Text>
      </View>
    </View>
  );
});
