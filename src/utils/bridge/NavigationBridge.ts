import { NativeModules } from 'react-native';

interface NavigationBridgeInterface {
  goToLogin(): void;
  navigateToSettings(): void;
  navigateBack(componentName?: string): void;
  navigateToWritePage(): void;
  navigateToAIPage(): void;
  navigateToTimedSwitch(): void;
  navigateToHelpSupport(): void;
  navigateToPrivacyPolicy(): void;
  navigateToHistory(): void;
  navigateToMessage(): void;
  navigateToBecomeWriter(): void;
  navigateToBecomeWriterWithStatus(): void;
  navigateToRecommendBook(): void;
  navigateToMyReservation(): void;
  navigateToMemberCenter(): void;
  navigateToViewedUsers(): void;
  navigateToFeedbackHelp(): void;
  navigateToQuestionList(): void;
  navigateToQuestionDetail(): void;
  navigateToBookManage(): void;
  navigateToWriteReview(bookId?: string, rating?: number): void;
  navigateToReviewDetail(commentData: string): void;
  clearComponentCache(componentName: string, callback: (result: any) => void): void;
  clearAllComponentCache(callback: (result: any) => void): void;
  registerComponent(componentName: string): void;
  notifyRouteChanged(route: string): void;
  getBridgeStatus(callback: (result: any) => void): void;
  getCurrentActualTheme(callback: (result: any) => void): void;
  getCurrentNightMode(callback: (result: any) => void): void;
  changeTheme(theme: string): Promise<any>;
  registerAuthor(penName: string, sex: number): Promise<any>;
  aiPolish(text: string): Promise<string>;
  aiExpand(text: string, ratio: number): Promise<string>;
  aiCondense(text: string, ratio: number): Promise<string>;
  aiContinue(text: string, length: number): Promise<string>;
  getHomeBooksHighPriority(): Promise<any>;
  getAuthorStatus(): Promise<{ isAuthor: boolean; code?: string; message?: string; ok?: boolean }>;
  getAuthorBooks(pageNum?: number, pageSize?: number): Promise<{ list: Array<{ id: number; bookName: string; wordCount: number }> }>;
  getReadingHistory(): Promise<{ historyItems: Array<{ id: string; title: string; author: string; description: string; coverUrl: string; lastReadTime: number; readProgress: number; type: string; categoryId: string; readCount: number; rating: number }>; success: boolean }>;
  navigateToBecomeWriterWithFlag(isAuthor: boolean): void;
  // Android only: attach/detach custom selection menu to a specific TextInput view
  attachSelectionMenu?(viewTag: number): void;
  detachSelectionMenu?(viewTag: number): void;
}

const { NavigationBridge: NativeNavigationBridge } = NativeModules;

export const NavigationBridge: NavigationBridgeInterface = {
  goToLogin: () => {
    console.log('[NavigationBridge] Navigating to login');
    NativeNavigationBridge?.goToLogin();
  },

  navigateToSettings: () => {
    console.log('[NavigationBridge] Navigating to settings');
    NativeNavigationBridge?.navigateToSettings();
  },

  navigateBack: (componentName?: string) => {
    console.log('[NavigationBridge] Navigating back from:', componentName);
    NativeNavigationBridge?.navigateBack(componentName);
  },

  navigateToWritePage: () => {
    console.log('[NavigationBridge] Navigating to write page');
    NativeNavigationBridge?.navigateToWritePage();
  },

  navigateToAIPage: () => {
    console.log('[NavigationBridge] Navigating to AI page');
    NativeNavigationBridge?.navigateToAIPage();
  },

  navigateToTimedSwitch: () => {
    console.log('[NavigationBridge] Navigating to timed switch');
    NativeNavigationBridge?.navigateToTimedSwitch();
  },

  navigateToHelpSupport: () => {
    console.log('[NavigationBridge] Navigating to help support');
    NativeNavigationBridge?.navigateToHelpSupport();
  },

  navigateToPrivacyPolicy: () => {
    console.log('[NavigationBridge] Navigating to privacy policy');
    NativeNavigationBridge?.navigateToPrivacyPolicy();
  },

  navigateToHistory: () => {
    console.log('[NavigationBridge] Navigating to history');
    NativeNavigationBridge?.navigateToHistory();
  },

  navigateToMessage: () => {
    console.log('[NavigationBridge] Navigating to message');
    NativeNavigationBridge?.navigateToMessage();
  },

  navigateToBecomeWriter: () => {
    console.log('[NavigationBridge] Navigating to become writer');
    NativeNavigationBridge?.navigateToBecomeWriter();
  },

  navigateToBecomeWriterWithStatus: () => {
    console.log('[NavigationBridge] Navigating to become writer with status');
    NativeNavigationBridge?.navigateToBecomeWriterWithStatus?.();
  },

  navigateToRecommendBook: () => {
    console.log('[NavigationBridge] Navigating to recommend book');
    NativeNavigationBridge?.navigateToRecommendBook();
  },

  navigateToMyReservation: () => {
    console.log('[NavigationBridge] Navigating to my reservation');
    NativeNavigationBridge?.navigateToMyReservation();
  },

  navigateToMemberCenter: () => {
    console.log('[NavigationBridge] Navigating to member center');
    NativeNavigationBridge?.navigateToMemberCenter();
  },

  navigateToViewedUsers: () => {
    console.log('[NavigationBridge] Navigating to viewed users');
    NativeNavigationBridge?.navigateToViewedUsers();
  },

  navigateToFeedbackHelp: () => {
    console.log('[NavigationBridge] Navigating to feedback help');
    NativeNavigationBridge?.navigateToFeedbackHelp();
  },

  navigateToQuestionList: () => {
    console.log('[NavigationBridge] Navigating to question list');
    NativeNavigationBridge?.navigateToQuestionList();
  },

  navigateToQuestionDetail: () => {
    console.log('[NavigationBridge] Navigating to question detail');
    NativeNavigationBridge?.navigateToQuestionDetail();
  },

  navigateToWriteReview: (bookId?: string, rating?: number) => {
    console.log('[NavigationBridge] Navigating to write review with bookId:', bookId, 'rating:', rating);
    NativeNavigationBridge?.navigateToWriteReview(bookId, rating);
  },

  navigateToReviewDetail: (commentData: string) => {
    console.log('[NavigationBridge] Navigating to review detail with data:', commentData);
    NativeNavigationBridge?.navigateToReviewDetail(commentData);
  },

  clearComponentCache: (componentName: string, callback: (result: any) => void) => {
    console.log('[NavigationBridge] Clearing component cache:', componentName);
    NativeNavigationBridge?.clearComponentCache(componentName, callback);
  },

  clearAllComponentCache: (callback: (result: any) => void) => {
    console.log('[NavigationBridge] Clearing all component cache');
    NativeNavigationBridge?.clearAllComponentCache(callback);
  },

  registerComponent: (componentName: string) => {
    console.log('[NavigationBridge] Registering component:', componentName);
    NativeNavigationBridge?.registerComponent(componentName);
  },

  notifyRouteChanged: (route: string) => {
    console.log('[NavigationBridge] Route changed:', route);
    NativeNavigationBridge?.notifyRouteChanged(route);
  },

  getBridgeStatus: (callback: (result: any) => void) => {
    console.log('[NavigationBridge] Getting bridge status');
    NativeNavigationBridge?.getBridgeStatus(callback);
  },

  getCurrentActualTheme: (callback: (result: any) => void) => {
    console.log('[NavigationBridge] Getting current actual theme');
    NativeNavigationBridge?.getCurrentActualTheme(callback);
  },

  getCurrentNightMode: (callback: (result: any) => void) => {
    console.log('[NavigationBridge] Getting current night mode');
    NativeNavigationBridge?.getCurrentNightMode(callback);
  },

  changeTheme: (theme: string): Promise<any> => {
    console.log('[NavigationBridge] Changing theme to:', theme);
    return NativeNavigationBridge?.changeTheme(theme) || Promise.resolve();
  },

  registerAuthor: (penName: string, sex: number): Promise<any> => {
    console.log('[NavigationBridge] Register author:', penName, sex);
    if (!NativeNavigationBridge?.registerAuthor) {
      return Promise.reject('registerAuthor not available');
    }
    return NativeNavigationBridge.registerAuthor(penName, sex);
  },

  navigateToBookManage: () => {
    console.log('[NavigationBridge] Navigating to book manage');
    NativeNavigationBridge?.navigateToBookManage?.();
  },

  aiPolish: (text: string): Promise<string> => {
    return (NativeNavigationBridge?.aiPolish?.(text)) || Promise.reject('aiPolish not available');
  },
  aiExpand: (text: string, ratio: number): Promise<string> => {
    return (NativeNavigationBridge?.aiExpand?.(text, Math.round(ratio))) || Promise.reject('aiExpand not available');
  },
  aiCondense: (text: string, ratio: number): Promise<string> => {
    return (NativeNavigationBridge?.aiCondense?.(text, Math.round(ratio))) || Promise.reject('aiCondense not available');
  },
  aiContinue: (text: string, length: number): Promise<string> => {
    return (NativeNavigationBridge?.aiContinue?.(text, Math.round(length))) || Promise.reject('aiContinue not available');
  },

  getHomeBooksHighPriority: (): Promise<any> => {
    return (NativeNavigationBridge?.getHomeBooksHighPriority?.()) || Promise.reject('getHomeBooksHighPriority not available');
  },

  getAuthorStatus: (): Promise<{ isAuthor: boolean; code?: string; message?: string; ok?: boolean }> => {
    if (!NativeNavigationBridge?.getAuthorStatus) {
      return Promise.reject('getAuthorStatus not available');
    }
    return NativeNavigationBridge.getAuthorStatus();
  },

  getAuthorBooks: (pageNum: number = 1, pageSize: number = 50): Promise<{ list: Array<{ id: number; bookName: string; wordCount: number }> }> => {
    if (!NativeNavigationBridge?.getAuthorBooks) {
      return Promise.reject('getAuthorBooks not available');
    }
    return NativeNavigationBridge.getAuthorBooks(pageNum, pageSize);
  },

  getReadingHistory: (): Promise<{ historyItems: Array<{ id: string; title: string; author: string; description: string; coverUrl: string; lastReadTime: number; readProgress: number; type: string; categoryId: string; readCount: number; rating: number }>; success: boolean }> => {
    console.log('[NavigationBridge] Getting reading history');
    return NativeNavigationBridge?.getReadingHistory() || Promise.resolve({ historyItems: [], success: false });
  },

  navigateToBecomeWriterWithFlag: (isAuthor: boolean) => {
    if (NativeNavigationBridge?.navigateToBecomeWriterWithFlag) {
      NativeNavigationBridge.navigateToBecomeWriterWithFlag(isAuthor);
    } else {
      // 兼容旧接口：回退到原有的状态查询再跳转
      NativeNavigationBridge?.navigateToBecomeWriterWithStatus?.();
    }
  },

  attachSelectionMenu: (viewTag: number) => {
    try {
      NativeNavigationBridge?.attachSelectionMenu?.(viewTag);
    } catch (e) {
      console.warn('[NavigationBridge] attachSelectionMenu failed', e);
    }
  },
  detachSelectionMenu: (viewTag: number) => {
    try {
      NativeNavigationBridge?.detachSelectionMenu?.(viewTag);
    } catch (e) {
      console.warn('[NavigationBridge] detachSelectionMenu failed', e);
    }
  },
};

export default NavigationBridge;
