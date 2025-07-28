import React from 'react';
import { View, Text, Image, TouchableOpacity } from 'react-native';
import { UserInfo } from '../types';

interface UserSectionProps {
  styles: any;
  userInfo: UserInfo | null;
}

export const UserSection: React.FC<UserSectionProps> = React.memo(({
  styles,
  userInfo,
}) => {
  return (
    <>
      {/* 用户信息 */}
      {userInfo && (
        <TouchableOpacity style={styles.profileSection} activeOpacity={0.7}>
          <Image
            source={{ uri: userInfo.avatar }}
            style={styles.profileAvatar}
          />
          <Text style={styles.profileName}>{userInfo.name}</Text>
        </TouchableOpacity>
      )}
    </>
  );
});
