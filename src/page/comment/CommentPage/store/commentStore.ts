import { create } from 'zustand';
import { CommentStore, Comment, CommentResponse } from '../types';

// API基础URL - 仿照BookStore的实现
const BASE_URL_FRONT = 'http://47.110.147.60:8080/api/front/';

// API响应接口定义
interface ApiCommentResponse {
  code: string;
  message: string;
  data: {
    commentTotal: number;
    comments: {
      id: number;
      commentContent: string;
      commentUser: string;
      commentUserId: number;
      commentUserPhoto: string;
      commentTime: string;
    }[];
  } | null;
  ok: boolean;
}

// 生成mock评论数据 - 覆盖timeUtils的所有时间格式化情况
const generateMockComments = (): Comment[] => {
  return [
    {
      id: "1",
      commentContent: "这个职业(老板)无敌了，全天下的天才为之打工。",
      rating: 5,
      commentTime: "2025-08-10T10:30:00Z", // 今天 - 显示"10:30"
      commentUser: "书虫小王",
      commentUserId: 1,
      commentUserPhoto: "https://example.com/avatar1.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: 'first_comment',
    },
    {
      id: "2",
      commentContent: "很不错的脑洞，题材也很新颖，就是主角有点感太低了，全是手下在发力，主角变考全程躺平。不过这种设定也挺有意思的，让人看得很爽，期待后续剧情发展。作者的文笔也不错，人物刻画比较立体，配角们都有自己的特色和故事线。整体来说是一本值得追读的好书，推荐给喜欢这类题材的朋友们。",
      rating: 4,
      commentTime: "2025-08-09T08:45:00Z", // 昨天 - 显示"昨天 08:45"
      commentUser: "阅读达人",
      commentUserId: 2,
      commentUserPhoto: "https://example.com/avatar2.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: 'true_fan',
    },
    {
      id: "3",
      commentContent: "我看书是不是有点太快了",
      rating: 5,
      commentTime: "2025-08-10T14:20:00Z", // 今天下午 - 显示"14:20"
      commentUser: "快速阅读者",
      commentUserId: 3,
      commentUserPhoto: "https://example.com/avatar3.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "4",
      commentContent: "这本书的世界观设定非常宏大，作者在细节方面处理得很到位。从开篇的铺垫到后面的高潮迭起，每一个转折都让人意想不到。特别是主角的成长线，从一个普通人逐渐变成掌控全局的存在，这个过程写得很有说服力。配角们也都有血有肉，不是简单的工具人。唯一的小缺点就是有些地方节奏稍微慢了一点，但整体瑕不掩瑜。强烈推荐！",
      rating: 5,
      commentTime: "2025-07-20T16:30:00Z", // 本年其他日期 - 显示"7月20日"
      commentUser: "文学爱好者",
      commentUserId: 4,
      commentUserPhoto: "https://example.com/avatar4.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: 'vip',
    },
    {
      id: "5",
      commentContent: "作者的想象力真的很丰富，这种职业设定我还是第一次见到。",
      rating: 4,
      commentTime: "2025-08-09T21:15:00Z", // 昨天晚上 - 显示"昨天 21:15"
      commentUser: "新手读者",
      commentUserId: 5,
      commentUserPhoto: "https://example.com/avatar5.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "6",
      commentContent: "看了几十章了，感觉剧情发展得很有层次感。作者对于权谋和商战的描写很到位，让人看得津津有味。主角虽然看起来很佛系，但实际上每一步都在布局，这种反差萌很有意思。而且书中的各种职业设定也很新颖，不是传统的修仙或者玄幻套路。配角们的智商都在线，不会为了突出主角而故意降智，这点很难得。期待作者能保持这个水准继续更新下去。",
      rating: 5,
      commentTime: "2025-05-15T11:45:00Z", // 本年春天 - 显示"5月15日"
      commentUser: "资深书友",
      commentUserId: 6,
      commentUserPhoto: "https://example.com/avatar6.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "7",
      commentContent: "更新速度有点慢，希望作者能加快节奏。",
      rating: 3,
      commentTime: "2024-11-14T20:30:00Z", // 往年 - 显示"2024-11-14 20:30"
      commentUser: "催更大队",
      commentUserId: 7,
      commentUserPhoto: "https://example.com/avatar7.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "8",
      commentContent: "这本书给我最大的感受就是爽！主角的每一次出手都让人拍案叫绝，特别是那些商业布局和人才收割的桥段，看得人热血沸腾。作者对于现代商业模式的理解很深刻，融入到小说中毫不违和。而且人物对话也很有趣，不会显得生硬。唯一的问题就是感情线有点薄弱，希望后面能加强一下。总的来说，这是一本让人欲罢不能的好书，已经加入书架了！",
      rating: 5,
      commentTime: "2025-08-10T06:20:00Z", // 今天早上 - 显示"06:20"
      commentUser: "商战迷",
      commentUserId: 8,
      commentUserPhoto: "https://example.com/avatar8.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "9",
      commentContent: "第一次看这种类型的小说，感觉很新鲜。",
      rating: 4,
      commentTime: "2025-02-12T07:50:00Z", // 本年冬天 - 显示"2月12日"
      commentUser: "探索者",
      commentUserId: 9,
      commentUserPhoto: "https://example.com/avatar9.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "10",
      commentContent: "作者的文笔很不错，故事节奏把握得也很好。从开头的设定介绍到后面的剧情展开，每一章都有看点。主角的人设很讨喜，不是那种装逼打脸的套路，而是真正有智慧有格局的角色。配角们也都很有特色，每个人都有自己的目标和动机，不是简单的背景板。世界观构建得很完整，各种职业和技能设定都很合理。唯一的小建议就是希望能多一些感情戏，让故事更加丰满。",
      rating: 4,
      commentTime: "2023-12-11T15:40:00Z", // 往年 - 显示"2023-12-11 15:40"
      commentUser: "细节控",
      commentUserId: 10,
      commentUserPhoto: "https://example.com/avatar10.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
      tag: undefined,
    },
    {
      id: "11",
      commentContent: "看了朋友推荐才来的，确实没让我失望！",
      rating: 5,
      commentTime: "2025-01-10T12:10:00Z", // 本年元月 - 显示"1月10日"
      commentUser: "朋友推荐",
      commentUserId: 11,
      commentUserPhoto: "https://example.com/avatar11.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
    },
    {
      id: "12",
      commentContent: "这本书最吸引我的地方就是它的创新性。在网文同质化严重的今天，能看到这样有新意的作品真的很难得。作者没有走传统的升级打怪路线，而是选择了一个全新的角度来展现主角的成长。商业帝国的构建过程写得很详细，让人看得很过瘾。而且作者对于人性的刻画也很到位，每个角色都有自己的立场和考虑，没有绝对的好人和坏人。这种灰度的处理方式让故事更加真实可信。期待后续的发展！",
      rating: 5,
      commentTime: "2024-05-09T18:25:00Z", // 往年 - 显示"2024-05-09 18:25"
      commentUser: "创新追求者",
      commentUserId: 12,
      commentUserPhoto: "https://example.com/avatar12.jpg",
      likeCount: Math.floor(Math.random() * 200) + 10,
      isLiked: Math.random() > 0.7,
    }
  ];
};

// 从API获取评论数据 - 仿照BookStore的loadHomeRecommendBooks实现
const fetchCommentsFromApi = async (bookId: string): Promise<CommentResponse> => {
  try {
    console.log('正在获取评论数据，bookId:', bookId);

    const response = await fetch(`${BASE_URL_FRONT}book/comment/newest_list?bookId=${bookId}`, {
      method: 'GET',
      headers: {
        'Accept': '*/*',
        'Content-Type': 'application/json',
      },
    });

    console.log('API响应状态:', response.status, response.statusText);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const apiData: ApiCommentResponse = await response.json();
    console.log('API响应数据:', apiData);

    if (apiData.ok && apiData.data && apiData.data.comments.length > 0) {
      // 转换API数据格式并添加缺失字段（使用mock数据）
      const comments: Comment[] = apiData.data.comments.map((comment, index) => ({
        id: comment.id.toString(),
        commentContent: comment.commentContent,
        commentUser: comment.commentUser,
        commentUserId: comment.commentUserId,
        commentUserPhoto: comment.commentUserPhoto || `https://example.com/avatar${(index % 5) + 1}.jpg`,
        commentTime: comment.commentTime,
        // 缺失字段用mock数据生成
        rating: Math.floor(Math.random() * 2) + 4, // 4-5星评分
        likeCount: Math.floor(Math.random() * 200) + 10, // 10-210点赞数
        isLiked: Math.random() > 0.7, // 30%概率已点赞
      }));

      console.log(`获取到${comments.length}条评论数据`);
      return {
        commentTotal: apiData.data.commentTotal,
        comments: comments,
      };
    } else {
      // 数据为空时，30%概率返回mock数据
      if (Math.random() < 2) {
        console.log('API数据为空，返回mock数据');
        const mockComments = generateMockComments();
        return {
          commentTotal: mockComments.length,
          comments: mockComments,
        };
      } else {
        console.log('API数据为空，返回空数据');
        return {
          commentTotal: 0,
          comments: [],
        };
      }
    }
  } catch (error) {
    console.error('获取评论数据失败:', error);
    // 发生错误时，30%概率返回mock数据
    if (Math.random() < 0.3) {
      console.log('API请求失败，返回mock数据');
      const mockComments = generateMockComments();
      return {
        commentTotal: mockComments.length,
        comments: mockComments,
      };
    } else {
      console.log('API请求失败，返回空数据');
      return {
        commentTotal: 0,
        comments: [],
      };
    }
  }
};

export const useCommentStore = create<CommentStore>((set, get) => ({
  comments: [],
  loading: false,
  isRefreshing: false,
  hasMore: true,
  error: null,
  currentBookId: null, // 添加当前书籍ID字段
  currentPage: 0, // 添加当前页码字段
  pageSize: 6, // 每页显示6条评论

  loadComments: async (bookId: string) => {
    const { loading, isRefreshing, pageSize } = get();
    if (loading || isRefreshing) return;

    set({ loading: true, error: null, currentBookId: bookId, currentPage: 0 });
    
    try {
      console.log('[CommentStore] 开始加载评论数据，bookId:', bookId);
      const response = await fetchCommentsFromApi(bookId);
      
      // 实现分页逻辑，初始只显示前pageSize条评论
      const initialComments = response.comments.slice(0, pageSize);
      const hasMoreData = response.comments.length > pageSize;
      
      set({
        comments: initialComments,
        loading: false,
        hasMore: hasMoreData,
        currentPage: 1,
      });
      
      console.log('[CommentStore] 评论数据加载成功，显示', initialComments.length, '条，总共', response.comments.length, '条');
    } catch (error) {
      console.error('[CommentStore] 加载评论数据失败:', error);
      set({ loading: false, error: '加载评论失败' });
    }
  },

  refreshComments: async () => {
    const { isRefreshing, loading, pageSize } = get();
    if (isRefreshing || loading) return;

    set({ isRefreshing: true, error: null });
    
    try {
      console.log('[CommentStore] 开始刷新评论数据');
      // 获取当前bookId，从comments中获取或使用默认值
      const currentBookId = get().currentBookId || 'default-book-id';
      const response = await fetchCommentsFromApi(currentBookId);
      
      // 刷新时重置为第一页
      const initialComments = response.comments.slice(0, pageSize);
      const hasMoreData = response.comments.length > pageSize;
      
      set({
        comments: initialComments,
        isRefreshing: false,
        hasMore: hasMoreData,
        currentPage: 1,
      });
      
      console.log('[CommentStore] 评论数据刷新成功');
    } catch (error) {
      console.error('[CommentStore] 刷新评论数据失败:', error);
      set({ isRefreshing: false, error: '刷新评论失败' });
    }
  },

  loadMoreComments: async () => {
    const { loading, isRefreshing, hasMore, currentPage, pageSize, comments } = get();
    if (loading || isRefreshing || !hasMore) return;

    set({ loading: true });
    
    try {
      console.log('[CommentStore] 开始加载更多评论数据，当前页:', currentPage);
      
      // 获取当前bookId
      const currentBookId = get().currentBookId || 'default-book-id';
      const response = await fetchCommentsFromApi(currentBookId);
      
      // 计算下一页的数据
      const startIndex = currentPage * pageSize;
      const endIndex = startIndex + pageSize;
      const nextPageComments = response.comments.slice(startIndex, endIndex);
      
      if (nextPageComments.length > 0) {
        // 合并新数据到现有评论列表
        const updatedComments = [...comments, ...nextPageComments];
        const hasMoreData = endIndex < response.comments.length;
        
        set({
          comments: updatedComments,
          loading: false,
          hasMore: hasMoreData,
          currentPage: currentPage + 1,
        });
        
        console.log('[CommentStore] 加载更多评论成功，新增', nextPageComments.length, '条，总计', updatedComments.length, '条');
      } else {
        // 没有更多数据
        set({ loading: false, hasMore: false });
        console.log('[CommentStore] 没有更多评论数据');
      }
    } catch (error) {
      console.error('[CommentStore] 加载更多评论数据失败:', error);
      set({ loading: false, error: '加载更多评论失败' });
    }
  },

  likeComment: (commentId: string) => {
    const { comments } = get();
    const updatedComments = comments.map(comment => {
      if (comment.id === commentId) {
        const isLiked = !comment.isLiked;
        const likeCount = (comment.likeCount || 0) + (isLiked ? 1 : -1);
        return {
          ...comment,
          isLiked,
          likeCount: Math.max(0, likeCount),
        };
      }
      return comment;
    });
    
    set({ comments: updatedComments });
    console.log('[CommentStore] 评论点赞状态已更新，commentId:', commentId);
  },

  reset: () => {
    set({
      comments: [],
      loading: false,
      isRefreshing: false,
      hasMore: true,
      error: null,
      currentBookId: null,
      currentPage: 0,
    });
    console.log('[CommentStore] 状态已重置');
  },
}));