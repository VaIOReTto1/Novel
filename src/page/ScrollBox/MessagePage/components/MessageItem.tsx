import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
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
    // 如果是具体日期格式如"7-20"，直接返回
    if (timeString.includes('-')) {
      return timeString;
    }
    return timeString;
  };

  return (
    <TouchableOpacity
      style={styles.messageItem}
      onPress={onPress}
      activeOpacity={0.7}
    >
      {/* 消息图标 */}
      <View style={styles.messageIcon}>
        <Text style={styles.messageIconText}>
          {item.icon || '📧'}
        </Text>
      </View>

      {/* 消息内容 */}
      <View style={styles.messageContent}>
        <View style={styles.messageHeader}>
          <Text style={styles.messageTitle}>
            {item.title}
          </Text>
          <Text style={styles.messageTime}>
            {formatTime(item.time)}
          </Text>
        </View>

        <View style={styles.messageBodyRow}>
          <Text style={styles.messageBody} numberOfLines={2}>
            {item.content}
          </Text>
           {/* 未读标识 */}
          {item.hasNotification && (
            <View style={styles.notificationBadge} />
          )}
        </View>
      </View>
    </TouchableOpacity>
  );
});
