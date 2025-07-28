import React from 'react';
import { View } from 'react-native';
import { MessageItem as MessageItemComponent } from './MessageItem';
import { MessageItem } from '../types';

// 主消息列表的静态数据
const mainMessages: MessageItem[] = [
  {
    id: 1,
    type: 'system',
    title: '系统通知',
    content: '《暗星长曜》1&2，套装限时优惠20元',
    time: '7-20',
    isRead: false,
    hasNotification: true,
    icon: '🔔',
  },
  {
    id: 2,
    type: 'fan',
    title: '粉丝',
    content: '暂无粉丝消息',
    time: '',
    isRead: true,
    hasNotification: false,
    icon: '👤',
  },
];

interface MainMessagesSectionProps {
  styles: any;
  onMessagePress: (item: MessageItem) => void;
}

// 使用React.memo包裹，只有在props变化时才会重新渲染
export const MainMessagesSection: React.FC<MainMessagesSectionProps> = React.memo(({ styles, onMessagePress }) => {
  console.log('[MainMessagesSection] Rendering');
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
});
