import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import ViewedUsersPage from './ViewedUsersPage';
import { initializeRNPage } from '../../../utils/appInit';

interface ViewedUsersPageComponentProps {
  source?: string;
}

const ViewedUsersPageComponent: React.FC<ViewedUsersPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[ViewedUsersPageComponent] 组件初始化，来源:', source);
    
    // 初始化RN页面
    initializeRNPage('ViewedUsersPage').catch((error) => {
      console.error('[ViewedUsersPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[ViewedUsersPageComponent] 组件卸载');
    };
  }, [source]);

  return <ViewedUsersPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('ViewedUsersPageComponent', () => ViewedUsersPageComponent);

export default ViewedUsersPageComponent; 