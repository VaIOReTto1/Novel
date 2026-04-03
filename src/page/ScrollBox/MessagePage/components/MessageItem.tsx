import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import { MessageItemProps } from '../types';

interface MessageItemComponentProps extends MessageItemProps {
  styles: any;
}

export const MessageItem: React.FC<MessageItemComponentProps> = React.memo(({
  item,
  onPress,
  styles,
}) => {
  const formatTime = (timeString: string): string => {
    if (timeString.includes('-')) {
      return timeString;
    }
    return timeString;
  };

  return (
    <TouchableOpacity
      style={styles.messageItem}
      onPress={onPress}
      activeOpacity={0.7}>
      <View style={styles.messageIcon}>
        <Text style={styles.messageIconText}>{item.icon || '消息'}</Text>
      </View>

      <View style={styles.messageContent}>
        <View style={styles.messageHeader}>
          <Text style={styles.messageTitle}>{item.title}</Text>
          <Text style={styles.messageTime}>{formatTime(item.time)}</Text>
        </View>

        <View style={styles.messageBodyRow}>
          <Text style={styles.messageBody} numberOfLines={2}>
            {item.content}
          </Text>
          {item.hasNotification ? <View style={styles.notificationBadge} /> : null}
        </View>
      </View>
    </TouchableOpacity>
  );
});
