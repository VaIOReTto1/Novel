import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import BecomeWriterPage from './BecomeWriterPage';
import { useBecomeWriterStore } from './store/becomeWriterStore';
import { initializeRNPage } from '../../../utils/appInit';

interface BecomeWriterPageComponentProps {
  source?: string;
  isAuthor?: boolean;
}

const BecomeWriterPageComponent: React.FC<BecomeWriterPageComponentProps> = ({ source, isAuthor }) => {
  const { setIsAuthor } = useBecomeWriterStore();

  useEffect(() => {
    console.log('[BecomeWriterPageComponent] 组件初始化，来源:', source);
    if (isAuthor !== undefined) {
      console.log('[BecomeWriterPageComponent] 作家状态:', isAuthor);
      setIsAuthor(!!isAuthor);
    }

    // 初始化RN页面
    initializeRNPage('BecomeWriterPage').catch((error) => {
      console.error('[BecomeWriterPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[BecomeWriterPageComponent] 组件卸载');
    };
  }, [source, isAuthor, setIsAuthor]);

  return <BecomeWriterPage />;
};

// 注册React Native组件
AppRegistry.registerComponent('BecomeWriterPageComponent', () => BecomeWriterPageComponent);

export default BecomeWriterPageComponent;
