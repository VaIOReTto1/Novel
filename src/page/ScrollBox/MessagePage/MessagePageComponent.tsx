import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import MessagePage from './MessagePage';
import { initializeRNPage } from '../../../utils/appInit';

interface MessagePageComponentProps {
  source?: string;
}

const MessagePageComponent: React.FC<MessagePageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[MessagePageComponent] 组件初始化，来源:', source);

    // 初始化RN页面
    initializeRNPage('MessagePage').catch((error) => {
      console.error('[MessagePageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[MessagePageComponent] 组件卸载');
    };
  }, [source]);

  return <MessagePage />;
};

// 注册React Native组件
AppRegistry.registerComponent('MessagePageComponent', () => MessagePageComponent);

export default MessagePageComponent;
