import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';

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
      {photo ? (
        <View style={styles.avatarImage} />
      ) : (
        <View style={styles.defaultAvatar} />
      )}
    </View>
    <TouchableOpacity onPress={isLoggedIn ? undefined : onLogin} // 已登录不响应点击
      disabled={isLoggedIn} style={styles.loginButton}>
      <Text style={styles.loginText}>
        {isLoggedIn && nickname ? nickname : '点击登录/注册'}
      </Text>
    </TouchableOpacity>
  </View>
);
