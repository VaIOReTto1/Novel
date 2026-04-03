import React from 'react';
import { Image, Text, TouchableOpacity, View } from 'react-native';

import { AnnouncementData, UserInfo } from '../types';

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
      {userInfo ? (
        <TouchableOpacity style={styles.profileSection} activeOpacity={0.7}>
          <Image
            source={{ uri: userInfo.avatar }}
            style={styles.profileAvatar}
          />
          <Text style={styles.profileName}>{`Hi，${userInfo.name}`}</Text>
          <Text style={styles.profileArrow}>{'>'}</Text>
        </TouchableOpacity>
      ) : null}

      <View style={styles.announcementSection}>
        <Text style={styles.announcementTag}>{announcement.tag}</Text>
        <View style={styles.announcementCarouselContainer}>
          <View style={styles.announcementItem}>
            <Text style={styles.announcementText} numberOfLines={1}>
              {announcement.text}
            </Text>
          </View>
        </View>
        <Text style={styles.announcementMore}>更多 &gt;</Text>
      </View>
    </>
  );
});
