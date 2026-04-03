import React from 'react';
import { View } from 'react-native';

import { MessageItem } from '../types';
import { MessageItem as MessageItemComponent } from './MessageItem';

const mainMessages: MessageItem[] = [
  {
    id: 1,
    type: 'system',
    title: '系统通知',
    content: '《暗星长歌》套装限时优惠 10 元。',
    time: '7-20',
    isRead: false,
    hasNotification: true,
    icon: '通知',
  },
  {
    id: 2,
    type: 'fan',
    title: '粉丝',
    content: '暂无粉丝消息',
    time: '',
    isRead: true,
    hasNotification: false,
    icon: '粉丝',
  },
];

interface MainMessagesSectionProps {
  styles: any;
  onMessagePress: (item: MessageItem) => void;
}

export const MainMessagesSection: React.FC<MainMessagesSectionProps> = React.memo(
  ({ styles, onMessagePress }) => {
    return (
      <View style={styles.mainMessagesSection}>
        {mainMessages.map((message, index) => (
          <MessageItemComponent
            key={`main-${message.id}`}
            item={message}
            index={index}
            onPress={() => onMessagePress(message)}
            styles={styles}
          />
        ))}
      </View>
    );
  },
);
