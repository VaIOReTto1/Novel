import { NativeModules } from 'react-native';

interface NavigationBridgeInterface {
  goToLogin(): void;
  navigateToSettings(): void;
  navigateBack(componentName?: string): void;
  navigateToTimedSwitch(): void;
  navigateToHelpSupport(): void;
  navigateToPrivacyPolicy(): void;
  navigateToHistory(): void;
  navigateToMessage(): void;
  navigateToBecomeWriter(): void;
  navigateToRecommendBook(): void;
  navigateToMyReservation(): void;
  navigateToMemberCenter(): void;
  navigateToViewedUsers(): void;
  navigateToFeedbackHelp(): void;
  navigateToQuestionList(): void;
  navigateToQuestionDetail(): void;
  clearComponentCache(componentName: string, callback: (result: any) => void): void;
  clearAllComponentCache(callback: (result: any) => void): void;
  registerComponent(componentName: string): void;
  notifyRouteChanged(route: string): void;
  getBridgeStatus(callback: (result: any) => void): void;
  getCurrentActualTheme(callback: (result: any) => void): void;
  getCurrentNightMode(callback: (result: any) => void): void;
  changeTheme(theme: string): Promise<any>;
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
};

export default NavigationBridge;