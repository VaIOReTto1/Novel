// 咨询场景分类
export interface ConsultCategory {
  id: string;
  title: string;
  icon: string;
  bgGradient: string[];
  items: string[];
}

// 常见问题
export interface FrequentQuestion {
  id: string;
  title: string;
  category: string;
  priority: number;
  viewCount: number;
}

// 问题详情
export interface QuestionDetail {
  id: string;
  title: string;
  content: string;
  category: string;
  tags: string[];
  isResolved?: boolean;
  relatedQuestions?: string[];
  lastUpdated: string;
}

// 页面状态
export interface FeedbackHelpState {
  // 数据
  consultCategories: ConsultCategory[];
  frequentQuestions: FrequentQuestion[];
  questionDetails: { [id: string]: QuestionDetail };
  
  // UI状态
  currentView: 'main' | 'questions' | 'detail';
  selectedCategory: string | null;
  selectedQuestion: string | null;
  isLoading: boolean;
  error: string | null;
  
  // 方法
  setCurrentView: (view: 'main' | 'questions' | 'detail') => void;
  selectCategory: (categoryId: string) => void;
  selectQuestion: (questionId: string) => void;
  markQuestionResolved: (questionId: string, isResolved: boolean) => void;
  goBack: () => void;
  resetToMain: () => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  getCategoryQuestions: (categoryId: string) => FrequentQuestion[];
  getQuestionDetail: (questionId: string) => QuestionDetail | null;
  searchQuestions: (keyword: string) => FrequentQuestion[];
}

// TopBar属性
export interface TopBarProps {
  styles: any;
  title: string;
  onBack: () => void;
  onSearch?: () => void;
  showSearch?: boolean;
  pageType?: 'main' | 'list' | 'detail'; // 页面类型，用于区分不同的TopBar样式
  searchPlaceholder?: string; // 搜索框占位符
}

// 组件属性
export interface ConsultSectionProps {
  styles: any;
  categories: ConsultCategory[];
  onCategoryPress: (categoryId: string) => void;
}

export interface FrequentQuestionsProps {
  styles: any;
  questions: FrequentQuestion[];
  onQuestionPress: (questionId: string) => void;
}

export interface QuestionDetailProps {
  styles: any;
  detail: QuestionDetail;
  onResolve: (isResolved: boolean) => void;
  onRelatedQuestionPress: (questionId: string) => void;
}

export interface QuestionListProps {
  styles: any;
  questions: FrequentQuestion[];
  category: string;
  onQuestionPress: (questionId: string) => void;
  onBack: () => void;
}