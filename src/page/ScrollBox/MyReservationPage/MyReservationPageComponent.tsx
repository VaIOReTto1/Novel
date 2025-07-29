import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import MyReservationPage from './MyReservationPage';
import { initializeRNPage } from '../../../utils/appInit';

interface MyReservationPageComponentProps {
  source?: string;
}

const MyReservationPageComponent: React.FC<MyReservationPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[MyReservationPageComponent] 组件初始化，来源:', source);
    
    // 初始化RN页面
    initializeRNPage('MyReservationPage').catch((error) => {
      console.error('[MyReservationPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[MyReservationPageComponent] 组件卸载');
    };
  }, [source]);

  return <MyReservationPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('MyReservationPageComponent', () => MyReservationPageComponent);

export default MyReservationPageComponent;