import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

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
        <Text style={styles.contactButtonIcon}>反馈</Text>
        <Text style={styles.contactButtonText}>意见反馈</Text>
      </TouchableOpacity>

      <View style={styles.contactInfo}>
        <Text style={styles.contactInfoText}>
          番茄官方服务热线：57124，工作时间 7:30-22:00
        </Text>
      </View>
    </View>
  );
});
