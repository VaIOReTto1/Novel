import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

interface LoginBarProps {
  styles: any;
  photo?: string;
  isLoggedIn: boolean;
  nickname?: string;
  onLogin: () => void;
}

export const LoginBar: React.FC<LoginBarProps> = ({
  styles,
  photo,
  isLoggedIn,
  nickname,
  onLogin,
}) => (
  <View style={styles.loginBar}>
    <View style={styles.avatar}>
      {photo ? <View style={styles.avatarImage} /> : <View style={styles.defaultAvatar} />}
    </View>
    <TouchableOpacity
      onPress={isLoggedIn ? undefined : onLogin}
      disabled={isLoggedIn}
      style={styles.loginButton}>
      <Text style={styles.loginText}>
        {isLoggedIn && nickname ? nickname : '点击登录 / 注册'}
      </Text>
    </TouchableOpacity>
  </View>
);
