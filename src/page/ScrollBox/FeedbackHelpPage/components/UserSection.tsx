import React from 'react';
import { Text, View } from 'react-native';

interface UserSectionProps {
  styles: any;
  userName?: string;
}

export const UserSection: React.FC<UserSectionProps> = React.memo(({
  styles,
  userName = '测试用户',
}) => {
  return (
    <View style={styles.userSection}>
      <Text style={styles.userGreeting}>{`Hi，${userName}`}</Text>
      <Text style={styles.userSubtitle}>很高兴为您服务</Text>
    </View>
  );
});
