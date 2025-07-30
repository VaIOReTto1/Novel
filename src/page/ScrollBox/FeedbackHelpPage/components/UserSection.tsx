import React from 'react';
import { View, Text } from 'react-native';

interface UserSectionProps {
  styles: any;
  userName?: string;
}

export const UserSection: React.FC<UserSectionProps> = React.memo(({
  styles,
  userName = "安国的尹锋"
}) => {
  return (
    <View style={styles.userSection}>
      <Text style={styles.userGreeting}>Hi，{userName}</Text>
      <Text style={styles.userSubtitle}>很高兴为您服务</Text>
    </View>
  );
});