import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { CommentPage } from './CommentPage';
import { initializeRNPage } from '../../../utils/appInit';

interface CommentPageComponentProps {
  bookData?: string;
  bookId?: string; // 保持向后兼容
  source?: string;
}

/**
 * 用于在Android中嵌入的评论页面组件
 * 与主应用分离，可以独立渲染
 */
const CommentPageComponent: React.FC<CommentPageComponentProps> = ({ bookData, bookId, source }) => {
  useEffect(() => {
    console.log('[CommentPageComponent] 组件初始化，来源:', source, '书籍ID:', bookId, 'bookData:', bookData);

    // 初始化RN页面
    initializeRNPage('CommentPage').catch((error) => {
      console.error('[CommentPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[CommentPageComponent] 组件卸载');
    };
  }, [source, bookId, bookData]);

  // 解析bookData或使用bookId
  let parsedBookInfo = null;
  let finalBookId = bookId;
  
  if (bookData) {
    try {
      parsedBookInfo = JSON.parse(bookData);
      finalBookId = parsedBookInfo.bookId;
    } catch (error) {
      console.error('Failed to parse bookData:', error);
    }
  }

  return <CommentPage bookId={finalBookId || ''} bookInfo={parsedBookInfo} />;
};

// 注册React Native组件
AppRegistry.registerComponent('CommentPageComponent', () => CommentPageComponent);

export default CommentPageComponent;