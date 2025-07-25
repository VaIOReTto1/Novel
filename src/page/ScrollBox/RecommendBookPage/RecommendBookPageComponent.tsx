import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import RecommendBookPage from './RecommendBookPage';
import { initializeRNPage } from '../../../utils/appInit';

interface RecommendBookPageComponentProps {
  source?: string;
}

const RecommendBookPageComponent: React.FC<RecommendBookPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[RecommendBookPageComponent] 组件初始化，来源:', source);
    
    // 初始化RN页面
    initializeRNPage('RecommendBookPage').catch((error) => {
      console.error('[RecommendBookPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[RecommendBookPageComponent] 组件卸载');
    };
  }, [source]);

  return <RecommendBookPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('RecommendBookPageComponent', () => RecommendBookPageComponent);

export default RecommendBookPageComponent; 