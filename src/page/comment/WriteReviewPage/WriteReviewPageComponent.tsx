import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import WriteReviewPage from './WriteReviewPage';
import { initializeRNPage } from '../../../utils/appInit';

interface WriteReviewPageComponentProps {
  bookId: string;
  source?: string;
  initialRating?: string;
}

/**
 * 用于在Android中嵌入的写评论页面组件
 * 与主应用分离，可以独立渲染
 */
const WriteReviewPageComponent: React.FC<WriteReviewPageComponentProps> = ({ bookId, source, initialRating }) => {
  useEffect(() => {
    console.log('[WriteReviewPageComponent] 组件初始化，来源:', source, '书籍ID:', bookId, '初始评分:', initialRating);

    // 初始化RN页面
    initializeRNPage('WriteReviewPage').catch((error) => {
      console.error('[WriteReviewPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[WriteReviewPageComponent] 组件卸载');
    };
  }, [source, bookId, initialRating]);

  return <WriteReviewPage bookId={bookId} initialRating={Number(initialRating)} />;
};

// 注册React Native组件
AppRegistry.registerComponent('WriteReviewPageComponent', () => WriteReviewPageComponent);

export default WriteReviewPageComponent;