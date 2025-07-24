import React from 'react';
import { View, Text, Image, TouchableOpacity } from 'react-native';
import { UserInfo, AnnouncementData } from '../types';

interface UserSectionProps {
  styles: any;
  userInfo: UserInfo | null;
  announcement: AnnouncementData;
}

export const UserSection: React.FC<UserSectionProps> = React.memo(({
  styles,
  userInfo,
  announcement,
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
          <Text style={styles.profileArrow}>›</Text>
        </TouchableOpacity>
      )}

      {/* 公告 */}
      <View style={styles.announcementSection}>
        <Text style={styles.announcementTag}>{announcement.tag}</Text>
        <Text style={styles.announcementText} numberOfLines={1}>
          {announcement.text}
        </Text>
        <Text style={styles.announcementMore}>更多  ›</Text>
      </View>
    </>
  );
}); 