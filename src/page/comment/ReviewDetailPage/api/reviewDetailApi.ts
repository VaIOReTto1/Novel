import { ApiResponse, ReviewDetail, CommentListResponse } from '../types/reviewDetailTypes';

// 模拟API延迟
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// 模拟评论详情数据
const mockReviewDetail: ReviewDetail = {
  id: 'review_001',
  bookId: 'book_001',
  userId: 'user_001',
  userName: '书友小明',
  title: '非常精彩的小说',
  content: '这本小说的情节跌宕起伏，人物刻画生动，语言优美，是一部不可多得的佳作。作者的文笔功底深厚，能够将复杂的情感和人物关系处理得恰到好处。强烈推荐给所有喜欢这类题材的读者。',
  rating: 5,
  likeCount: 128,
  commentCount: 45,
  isLiked: false,
  createTime: '2025-08-10T14:30:00Z', // 今天的时间
  updateTime: '2025-08-10T14:30:00Z',
};

// 模拟评论数据 - 覆盖timeUtils的所有时间格式化情况
const mockComments = [
  {
    id: 'comment_001',
    reviewId: 'review_001',
    userId: 'user_002',
    userName: '毛线球提督',
    userAvatar: 'https://example.com/avatar1.jpg',
    content: '全部评论',
    likeCount: 4,
    dislikeCount: 0,
    isLiked: false,
    isDisliked: false,
    createTime: '2025-08-10T16:45:00Z', // 今天 - 显示"16:45"
    tag: 'first_comment',
    replies: [
      {
        id: 'reply_001',
        reviewId: 'review_001',
        userId: 'user_005',
        userName: 'FL理想',
        userAvatar: 'https://example.com/avatar5.jpg',
        content: '可以，兄弟是真的这么想的吗？不过这样子',
        likeCount: 2,
        dislikeCount: 0,
        isLiked: false,
        isDisliked: false,
        createTime: '2025-08-09T20:30:00Z', // 昨天 - 显示"昨天 20:30"
        parentId: 'comment_001',
        replyToUser: '毛线球提督',
        tag: undefined,
      },
      {
        id: 'reply_002',
        reviewId: 'review_001',
        userId: 'user_006',
        userName: '收天涯',
        userAvatar: 'https://example.com/avatar6.jpg',
        content: '这书太多章，看着累死了上去请问了',
        likeCount: 2,
        dislikeCount: 0,
        isLiked: false,
        isDisliked: false,
        createTime: '2025-07-15T14:20:00Z', // 本年其他日期 - 显示"7月15日"
        parentId: 'comment_001',
        replyToUser: '毛线球提督',
        tag: undefined,
      },
      {
        id: 'reply_003',
        reviewId: 'review_001',
        userId: 'user_007',
        userName: '爱吃鱼的小司',
        userAvatar: 'https://example.com/avatar7.jpg',
        content: '超级的，首先是真的很好，可想想也，改十分多写的体验😊',
        likeCount: 2,
        dislikeCount: 0,
        isLiked: false,
        isDisliked: false,
        createTime: '2024-12-25T09:15:00Z', // 往年 - 显示"2024-12-25 09:15"
        parentId: 'comment_001',
        replyToUser: '毛线球提督',
        tag: undefined,
      },
    ],
  },
  {
    id: 'comment_002',
    reviewId: 'review_001',
    userId: 'user_003',
    userName: '毛线球提督',
    userAvatar: 'https://example.com/avatar2.jpg',
    content: '后面加油😊',
    likeCount: 1,
    dislikeCount: 0,
    isLiked: false,
    isDisliked: false,
    createTime: '2025-08-10T08:30:00Z', // 今天早上 - 显示"08:30"
    tag: undefined,
    replies: [
      {
        id: 'reply_004',
        reviewId: 'review_001',
        userId: 'user_008',
        userName: '毛线球提督',
        userAvatar: 'https://example.com/avatar8.jpg',
        content: '后面加油😊',
        likeCount: 1,
        dislikeCount: 0,
        isLiked: false,
        isDisliked: false,
        createTime: '2025-08-09T15:45:00Z', // 昨天下午 - 显示"昨天 15:45"
        parentId: 'comment_002',
        replyToUser: '毛线球提督',
        tag: undefined,
      },
    ],
  },
  {
    id: 'comment_003',
    reviewId: 'review_001',
    userId: 'user_004',
    userName: 'FL理想',
    userAvatar: 'https://example.com/avatar3.jpg',
    content: '可以，兄弟是真的这么想的吗？不过这样子',
    likeCount: 2,
    dislikeCount: 0,
    isLiked: false,
    isDisliked: false,
    createTime: '2025-03-20T11:00:00Z', // 本年春天 - 显示"3月20日"
    tag: 'true_fan',
    replies: [],
  },
  {
    id: 'comment_004',
    reviewId: 'review_001',
    userId: 'user_009',
    userName: '收天涯',
    userAvatar: 'https://example.com/avatar4.jpg',
    content: '这书太多章，看着累死了上去请问了',
    likeCount: 2,
    dislikeCount: 0,
    isLiked: false,
    isDisliked: false,
    createTime: '2025-01-01T00:00:00Z', // 本年元旦 - 显示"1月1日"
    tag: undefined,
    replies: [],
  },
  {
    id: 'comment_005',
    reviewId: 'review_001',
    userId: 'user_010',
    userName: '爱吃鱼的小司',
    userAvatar: 'https://example.com/avatar5.jpg',
    content: '超级的，首先是真的很好，可想想也，改十分多写的体验😊',
    likeCount: 2,
    dislikeCount: 0,
    isLiked: false,
    isDisliked: false,
    createTime: '2023-06-15T18:30:00Z', // 往年 - 显示"2023-06-15 18:30"
    tag: 'vip',
    replies: [],
  },
];

export const reviewDetailApi = {
  // 获取评论详情
  getReviewDetail: async (reviewId: string): Promise<ApiResponse<ReviewDetail>> => {
    console.log('[ReviewDetailApi] 获取评论详情:', reviewId);
    await delay(50);

    return {
      code: 200,
      message: '获取成功',
      data: {
        ...mockReviewDetail,
        id: reviewId,
      },
    };
  },

  // 获取评论列表
  getComments: async (
    reviewId: string,
    page: number = 1,
    pageSize: number = 10
  ): Promise<ApiResponse<CommentListResponse>> => {
    console.log('[ReviewDetailApi] 获取评论列表:', { reviewId, page, pageSize });
    await delay(100);

    const startIndex = (page - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const comments = mockComments.slice(startIndex, endIndex);

    return {
      code: 200,
      message: '获取成功',
      data: {
        list: comments,
        total: mockComments.length,
        page,
        pageSize,
        hasMore: endIndex < mockComments.length,
      },
    };
  },

  // 点赞/取消点赞评论
  toggleLike: async (reviewId: string): Promise<ApiResponse<{ isLiked: boolean; likeCount: number }>> => {
    console.log('[ReviewDetailApi] 切换点赞状态:', reviewId);
    await delay(300);

    // 模拟切换点赞状态
    const isLiked = !mockReviewDetail.isLiked;
    const likeCount = isLiked ? mockReviewDetail.likeCount + 1 : mockReviewDetail.likeCount - 1;

    mockReviewDetail.isLiked = isLiked;
    mockReviewDetail.likeCount = likeCount;

    return {
      code: 200,
      message: isLiked ? '点赞成功' : '取消点赞成功',
      data: {
        isLiked,
        likeCount,
      },
    };
  },

  // 点赞/取消点赞评论回复
  toggleCommentLike: async (commentId: string): Promise<ApiResponse<{ isLiked: boolean; likeCount: number; isDisliked: boolean; dislikeCount: number }>> => {
    console.log('[ReviewDetailApi] 切换评论点赞状态:', commentId);
    await delay(300);

    const findComment = (comments: any[], id: string): any => {
      for (const comment of comments) {
        if (comment.id === id) {return comment;}
        if (comment.replies) {
          const found = findComment(comment.replies, id);
          if (found) {return found;}
        }
      }
      return null;
    };

    const comment = findComment(mockComments, commentId);
    if (!comment) {
      throw new Error('评论不存在');
    }

    // 模拟切换点赞状态
    const wasLiked = comment.isLiked;
    const wasDisliked = comment.isDisliked;

    if (wasLiked) {
      // 取消点赞
      comment.isLiked = false;
      comment.likeCount = Math.max(0, comment.likeCount - 1);
    } else {
      // 点赞
      comment.isLiked = true;
      comment.likeCount += 1;
      // 如果之前是踩，取消踩
      if (wasDisliked) {
        comment.isDisliked = false;
        comment.dislikeCount = Math.max(0, comment.dislikeCount - 1);
      }
    }

    return {
      code: 200,
      message: comment.isLiked ? '点赞成功' : '取消点赞成功',
      data: {
        isLiked: comment.isLiked,
        likeCount: comment.likeCount,
        isDisliked: comment.isDisliked,
        dislikeCount: comment.dislikeCount,
      },
    };
  },

  // 踩/取消踩评论回复
  toggleCommentDislike: async (commentId: string): Promise<ApiResponse<{ isLiked: boolean; likeCount: number; isDisliked: boolean; dislikeCount: number }>> => {
    console.log('[ReviewDetailApi] 切换评论踩状态:', commentId);
    await delay(300);

    const findComment = (comments: any[], id: string): any => {
      for (const comment of comments) {
        if (comment.id === id) {return comment;}
        if (comment.replies) {
          const found = findComment(comment.replies, id);
          if (found) {return found;}
        }
      }
      return null;
    };

    const comment = findComment(mockComments, commentId);
    if (!comment) {
      throw new Error('评论不存在');
    }

    // 模拟切换踩状态
    const wasLiked = comment.isLiked;
    const wasDisliked = comment.isDisliked;

    if (wasDisliked) {
      // 取消踩
      comment.isDisliked = false;
      comment.dislikeCount = Math.max(0, comment.dislikeCount - 1);
    } else {
      // 踩
      comment.isDisliked = true;
      comment.dislikeCount += 1;
      // 如果之前是点赞，取消点赞
      if (wasLiked) {
        comment.isLiked = false;
        comment.likeCount = Math.max(0, comment.likeCount - 1);
      }
    }

    return {
      code: 200,
      message: comment.isDisliked ? '踩成功' : '取消踩成功',
      data: {
        isLiked: comment.isLiked,
        likeCount: comment.likeCount,
        isDisliked: comment.isDisliked,
        dislikeCount: comment.dislikeCount,
      },
    };
  },
};
