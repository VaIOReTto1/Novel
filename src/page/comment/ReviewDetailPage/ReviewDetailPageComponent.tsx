import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { ReviewDetailPage } from './ReviewDetailPage';
import { initializeRNPage } from '../../../utils/appInit';

interface ReviewDetailPageComponentProps {
  commentData?: string;
  source?: string;
}

/**
 * 用于在Android中嵌入的评论详情页面组件
 * 与主应用分离，可以独立渲染
 */
const ReviewDetailPageComponent: React.FC<ReviewDetailPageComponentProps> = ({ commentData, source }) => {
  useEffect(() => {
    console.log('[ReviewDetailPageComponent] 组件初始化，来源:', source, '评论数据:', commentData);

    // 初始化RN页面
    initializeRNPage('ReviewDetailPage').catch((error) => {
      console.error('[ReviewDetailPageComponent] 页面初始化失败:', error);
    });

    return () => {
      console.log('[ReviewDetailPageComponent] 组件卸载');
    };
  }, [source, commentData]);

  // 解析commentData以提取bookInfo
  let parsedCommentData = null;
  let bookInfo = null;
  
  try {
    if (commentData) {
      parsedCommentData = JSON.parse(commentData);
      bookInfo = parsedCommentData.bookInfo;
    }
  } catch (error) {
    console.error('Failed to parse commentData:', error);
  }

  return <ReviewDetailPage commentData={commentData || ''} bookInfo={bookInfo} />;
};

// 注册React Native组件
AppRegistry.registerComponent('ReviewDetailPageComponent', () => ReviewDetailPageComponent);

export default ReviewDetailPageComponent;