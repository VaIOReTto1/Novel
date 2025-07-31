import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { MainPage } from './BookshelfPage';
import { initializeRNPage } from '../../utils/appInit';

interface BookshelfPageComponentProps {
  source?: string;
}

/**
 * 用于在Android中嵌入的书架页面组件
 * 与主应用分离，可以独立渲染
 */
const BookshelfPageComponent: React.FC<BookshelfPageComponentProps> = ({ source }) => {
  useEffect(() => {
    console.log('[BookshelfPageComponent] 组件初始化，来源:', source);

    // 初始化RN页面
    initializeRNPage('BookshelfPage').catch((error) => {
      console.error('[BookshelfPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[BookshelfPageComponent] 组件卸载');
    };
  }, [source]);

  return <MainPage />;
};

//注册React Native组件
AppRegistry.registerComponent('BookshelfPageComponent', () => BookshelfPageComponent);

export default BookshelfPageComponent;
