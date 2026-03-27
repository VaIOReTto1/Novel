import { CommunityPost } from '../types';

type AlertButton = {
  text: string;
  onPress?: () => void;
  style?: 'default' | 'cancel' | 'destructive';
};

type CommunityActionDeps = {
  posts: CommunityPost[];
  commentPost: (postId: string) => void;
  sharePost: (postId: string) => void;
  navigateToReviewDetail: (commentData: string) => void;
  navigateToSearch: (query: string) => void;
  navigateToMessage: () => void;
  navigateToWritePage: () => void;
  share: (content: { message: string }) => Promise<unknown>;
  alert: (title: string, message?: string, buttons?: AlertButton[]) => void;
};

const findPostById = (posts: CommunityPost[], postId: string): CommunityPost | undefined => {
  return posts.find((post) => post.id === postId);
};

export const buildCommunityCommentPayload = (post: CommunityPost): string => {
  return JSON.stringify({
    id: post.id,
    commentUser: post.author.name,
    commentUserPhoto: post.author.avatar,
    commentContent: post.content,
    commentTime: post.publishTime,
    likeCount: post.likeCount,
    bookInfo: post.novelName
      ? {
          bookId: post.id,
          bookName: post.novelName,
          authorName: post.author.name,
          picUrl: post.images?.[0] ?? '',
        }
      : undefined,
  });
};

export const buildCommunityShareMessage = (post: CommunityPost): string => {
  return [post.title ?? '社区分享', post.novelName ? `小说：${post.novelName}` : '', post.content]
    .filter(Boolean)
    .join('\n');
};

export const createCommunityActionHandlers = (deps: CommunityActionDeps) => {
  const handleComment = (postId: string) => {
    deps.commentPost(postId);
    const post = findPostById(deps.posts, postId);
    if (!post) {
      return;
    }
    deps.navigateToReviewDetail(buildCommunityCommentPayload(post));
  };

  const handleShare = async (postId: string) => {
    deps.sharePost(postId);
    const post = findPostById(deps.posts, postId);
    if (!post) {
      return;
    }
    await deps.share({
      message: buildCommunityShareMessage(post),
    });
  };

  const handleMore = (postId: string) => {
    deps.alert('更多操作', '请选择操作', [
      {
        text: '查看评论',
        onPress: () => handleComment(postId),
      },
      {
        text: '分享',
        onPress: () => {
          handleShare(postId).catch(() => undefined);
        },
      },
      {
        text: '取消',
        style: 'cancel',
      },
    ]);
  };

  return {
    handleComment,
    handleShare,
    handleMore,
    handleSearch: () => deps.navigateToSearch(''),
    handleNotification: () => deps.navigateToMessage(),
    handlePublish: () => deps.navigateToWritePage(),
    handleUserPress: () => deps.alert('暂未开放', '用户主页暂未开放'),
    handleSubscribe: () => deps.alert('暂未开放', '订阅功能暂未开放'),
  };
};
