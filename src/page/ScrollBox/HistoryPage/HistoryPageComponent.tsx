import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import HistoryPage from './HistoryPage';
import { initializeRNPage } from '../../../utils/appInit';

interface HistoryPageComponentProps {
  source?: string;
}

const HistoryPageComponent: React.FC<HistoryPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[HistoryPageComponent] 组件初始化，来源:', source);
    
    // 初始化RN页面
    initializeRNPage('HistoryPage').catch((error) => {
      console.error('[HistoryPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[HistoryPageComponent] 组件卸载');
    };
  }, [source]);

  return <HistoryPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('HistoryPageComponent', () => HistoryPageComponent);

export default HistoryPageComponent; 