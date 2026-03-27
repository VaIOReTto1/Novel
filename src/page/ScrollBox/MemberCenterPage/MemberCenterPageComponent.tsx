import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import MemberCenterPage from './MemberCenterPage';
import { initializeRNPage } from '../../../utils/appInit';

interface MemberCenterPageComponentProps {
  source?: string;
}

const MemberCenterPageComponent: React.FC<MemberCenterPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[MemberCenterPageComponent] 组件初始化，来源:', source);

    // 初始化RN页面
    initializeRNPage('MemberCenterPage').catch((error) => {
      console.error('[MemberCenterPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[MemberCenterPageComponent] 组件卸载');
    };
  }, [source]);

  return <MemberCenterPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('MemberCenterPageComponent', () => MemberCenterPageComponent);

export default MemberCenterPageComponent;
