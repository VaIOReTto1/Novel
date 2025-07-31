// 历史记录数据
export interface HistoryItem {
  id: string;
  title: string;
  author: string;
  description: string;
  cover: string;
  coverUrl: string;
  lastReadTime: number;
  lastChapter: string;
  readProgress: number;
  progress: number;
  type: 'reading' | 'listening' | 'drama';
  categoryId: number;
  readCount: number;
  rating: number;
  tags?: string[];
  isFinished: boolean;
  updateStatus: string;
  isInShelf?: boolean;
}

// 书籍数据
export interface BookItem {
    id: string;
    title: string;
    author: string;
    coverUrl?: string;
    description?: string;
    readProgress: number; // 阅读进度百分比
    chapterInfo?: string; // 章节信息，如"第1章 开始"
    lastReadTime?: string; // 最后阅读时间
    category: 'novel' | 'comic' | 'audiobook'; // 书籍类型
    isNew?: boolean; // 是否为新书
    tags?: string[]; // 标签
  }
  
  // Tab数据
export interface TabData {
  id: string;
  name: string;
  type: 'all' | 'reading' | 'listening' | 'drama';
  count: number;
}

export interface HistoryItemProps {
  styles: any;
  item: HistoryItem;
  viewType: 'grid' | 'list';
  isEditing: boolean;
  isSelected: boolean;
  onPress: () => void;
  onSelect: () => void;
  onAddToShelf?: (item: HistoryItem) => void;
}

// TabBar组件Props
export interface HistoryTabBarProps {
  styles: any;
  tabs: TabData[];
  selectedTab: string;
  onTabPress: (tabId: string) => void;
  viewType: 'grid' | 'list';
  onViewTypeChange: (type: 'grid' | 'list') => void;
  sortType: 'lastRead' | 'addTime' | 'title' | 'progress';
  onSortTypeChange: (type: 'lastRead' | 'addTime' | 'title' | 'progress') => void;
  isEditing: boolean;
  onEditToggle: () => void;
}

// TabsArea组件Props
export interface TabsAreaProps {
  styles: any;
  tabs: TabData[];
  selectedTab: string;
  onTabPress: (tabId: string) => void;
  viewType: 'grid' | 'list';
  onViewTypeChange: (type: 'grid' | 'list') => void;
}

// RefreshIndicator组件Props
export interface RefreshIndicatorProps {
  styles: any;
  isPullingDown: boolean;
  isRefreshing: boolean;
  pullDistance: number;
  threshold: number;
  spinStyle: any;
}

// EditToolbar组件Props
export interface HistoryEditToolbarProps {
  styles: any;
  selectedCount: number;
  onSelectAll: () => void;
  onRemove: () => void;
  onCancel: () => void;
  visible: boolean;
}

// ContentArea组件Props
export interface HistoryContentAreaProps {
  styles: any;
  historyItems: HistoryItem[];
  viewType: 'grid' | 'list';
  isEditing: boolean;
  selectedItems: string[];
  onItemPress: (item: HistoryItem) => void;
  onItemSelect: (itemId: string) => void;
  onAddToShelf?: (item: HistoryItem) => void;
  loading: boolean;
  error: string | null;
  onRefresh: () => void;
  refreshing: boolean;
  onLoadMore?: () => void;
}

export interface ViewControlProps {
  viewType: 'grid' | 'list';
  onViewTypeChange: (type: 'grid' | 'list') => void;
}

export interface RefreshIndicatorState {
  isPullingDown: boolean;
  isRefreshing: boolean;
  pullDistance: number;
  threshold: number;
}
  
  // 社区帖子数据
  export interface CommunityPost {
    id: string;
    title: string;
    author: string;
    authorAvatar?: string;
    content: string;
    images?: string[];
    likeCount: number;
    commentCount: number;
    shareCount: number;
    publishTime: string;
    isFollowed?: boolean;
    tags?: string[];
  }
  
  // 页面状态
  export interface BookshelfState {
    // 数据
    bookshelfBooks: BookItem[];
    historyBooks: BookItem[];
    seriesBooks: BookItem[];
    communityPosts: CommunityPost[];
    
    // UI状态
    currentTab: string;
    viewType: 'grid' | 'list'; // 网格/列表视图
    isLoading: boolean;
    isRefreshing: boolean;
    hasMore: boolean;
    error: string | null;
    
    // 方法
    setCurrentTab: (tabId: string) => void;
    setViewType: (type: 'grid' | 'list') => void;
    setLoading: (loading: boolean) => void;
    setRefreshing: (refreshing: boolean) => void;
    setError: (error: string | null) => void;
    refreshData: () => void;
    loadMoreData: () => void;
    addBook: (book: BookItem) => void;
    removeBook: (bookId: string) => void;
  }
  
  // TopBar属性
  export interface BookshelfTopBarProps {
    styles: any;
    title: string;
    onBack: () => void;
    onSearch?: () => void;
    showSearch?: boolean;
  }
  
  // Tab区域属性
  export interface BookshelfTabsProps {
    styles: any;
    tabs: TabData[];
    selectedTab: string;
    onTabPress: (tabId: string) => void;
    viewType: 'grid' | 'list';
    onViewTypeChange: (type: 'grid' | 'list') => void;
  }
  
  // 书籍项属性
  export interface BookshelfItemProps {
    item: BookItem;
    index: number;
    viewType: 'grid' | 'list';
    onPress: () => void;
  }
  
  // 内容区域属性
  export interface BookshelfContentProps {
    styles: any;
    books: BookItem[];
    viewType: 'grid' | 'list';
    loading: boolean;
    hasMore: boolean;
    onItemPress: (item: BookItem) => void;
    onRefresh: () => void;
    onLoadMore: () => void;
  }
  
  // 社区内容属性
  export interface CommunityContentProps {
    styles: any;
    posts: CommunityPost[];
    loading: boolean;
    hasMore: boolean;
    onPostPress: (post: CommunityPost) => void;
    onRefresh: () => void;
    onLoadMore: () => void;
  }
  
  // 空状态属性
  export interface EmptyStateProps {
    styles: any;
    icon: string;
    title: string;
    description: string;
    buttonText?: string;
    onButtonPress?: () => void;
  }
  
  // 加载指示器属性
  export interface LoadMoreIndicatorProps {
    loading: boolean;
    hasMore: boolean;
    styles: any;
  }