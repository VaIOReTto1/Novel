import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import BecomeWriterPage from './BecomeWriterPage';
import { initializeRNPage } from '../../../utils/appInit';

interface BecomeWriterPageComponentProps {
  source?: string;
}

const BecomeWriterPageComponent: React.FC<BecomeWriterPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[BecomeWriterPageComponent] 组件初始化，来源:', source);
    
    // 初始化RN页面
    initializeRNPage('BecomeWriterPage').catch((error) => {
      console.error('[BecomeWriterPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[BecomeWriterPageComponent] 组件卸载');
    };
  }, [source]);

  return <BecomeWriterPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('BecomeWriterPageComponent', () => BecomeWriterPageComponent);

export default BecomeWriterPageComponent; 