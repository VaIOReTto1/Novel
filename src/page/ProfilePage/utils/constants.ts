import { wp } from '../../../utils/theme/dimensions';
import { Dimensions } from 'react-native';
import { IconData } from '../types';

export const { width: screenWidth } = Dimensions.get('window');

// 预计算的项目高度缓存
export const itemHeightCache = new Map<number, number>();

// 下拉刷新相关常量
export const PULL_THRESHOLD = 80;

// 页面宽度
export const PAGE_WIDTH = wp(350);

// 预计算动画高度值
export const MIN_HEIGHT = wp(200);
export const MAX_HEIGHT = wp(250);

// 图标数据
export const ICONS_DATA: IconData[] = [
  // 第一页的4个图标
  { id: 'email', name: '我的消息', icon: 'email', onPress: () => {
    console.log('导航到我的消息');
    // 调用原生导航方法
    const { NativeModules } = require('react-native');
    const { NavigationBridge } = NativeModules;
    if (NavigationBridge?.navigateToMessage) {
      NavigationBridge.navigateToMessage();
    } else {
      console.log('NavigationBridge.navigateToMessage not available');
    }
  }},
  { id: 'be_writer', name: '成为作家', icon: 'be_writer', onPress: () => {
    console.log('导航到成为作家');
    // 调用原生导航方法
    const { NativeModules } = require('react-native');
    const { NavigationBridge } = NativeModules;
    if (NavigationBridge?.navigateToBecomeWriter) {
      NavigationBridge.navigateToBecomeWriter();
    } else {
      console.log('NavigationBridge.navigateToBecomeWriter not available');
    }
  }},
  { id: 'history', name: '浏览历史', icon: 'history', onPress: () => {
    console.log('导航到浏览历史');
    // 调用原生导航方法
    const { NativeModules } = require('react-native');
    const { NavigationBridge } = NativeModules;
    if (NavigationBridge?.navigateToHistory) {
      NavigationBridge.navigateToHistory();
    } else {
      console.log('NavigationBridge.navigateToHistory not available');
    }
  }},
  { id: 'subscribe', name: '推书中心', icon: 'subscribe', onPress: () => {
    console.log('导航到推书中心');
    // 调用原生导航方法
    const { NativeModules } = require('react-native');
    const { NavigationBridge } = NativeModules;
    if (NavigationBridge?.navigateToRecommendBook) {
      NavigationBridge.navigateToRecommendBook();
    } else {
      console.log('NavigationBridge.navigateToRecommendBook not available');
    }
  }},

  // 第二页的15个图标
  { id: 'game', name: '我的', icon: 'game', onPress: () => console.log('游戏') },
  { id: 'my_preorder', name: '我的预约', icon: 'member', onPress: () => console.log('我的预约') },
  { id: 'my_download', name: '我的下载', icon: 'download', onPress: () => console.log('我的下载') },
  { id: 'game_center', name: '游戏中心', icon: 'game', onPress: () => console.log('游戏中心') },
  { id: 'push_center', name: '推书中心', icon: 'recommend_book', onPress: () => console.log('推书中心') },
  { id: 'video_creation', name: '视频创作', icon: 'vedio_creation', onPress: () => console.log('视频创作') },
  { id: 'reading_preference', name: '阅读偏好', icon: 'reading_preference', onPress: () => console.log('阅读偏好') },
  { id: 'my_note', name: '我的笔记', icon: 'note', onPress: () => console.log('我的笔记') },
  { id: 'who_seen', name: '看过的人', icon: 'who_have_seen', onPress: () => {
    console.log('导航到看过的人');
    // 调用原生导航方法
    const { NativeModules } = require('react-native');
    const { NavigationBridge } = NativeModules;
    if (NavigationBridge?.navigateToViewedUsers) {
      NavigationBridge.navigateToViewedUsers();
    } else {
      console.log('NavigationBridge.navigateToViewedUsers not available');
    }
  }},
  { id: 'liked_video', name: '赞过的视频', icon: 'vedio_have_favorited', onPress: () => console.log('赞过的视频') },
  { id: 'help_guide', name: '帮助指南', icon: 'guide', onPress: () => console.log('帮助指南') },
  { id: 'my_public_welfare', name: '我的公益', icon: 'public_welfare', onPress: () => console.log('我的公益') },
  { id: 'member_center', name: '会员中心', icon: 'member', onPress: () => console.log('会员中心') },
  { id: 'my_wallet2', name: '我的钱包', icon: 'wallet', onPress: () => console.log('我的钱包') },
  { id: 'feedback_help', name: '反馈与帮助', icon: 'feedback', onPress: () => console.log('反馈与帮助') },

  // 第三页的剩余图标
  { id: 'my_wallet21', name: '我的钱包', icon: 'wallet', onPress: () => console.log('我的钱包') },
  { id: 'feedback_help1', name: '反馈与帮助', icon: 'feedback', onPress: () => console.log('反馈与帮助') },
  { id: 'my_wallet22', name: '我的钱包', icon: 'wallet', onPress: () => console.log('我的钱包') },
  { id: 'feedback_help2', name: '反馈与帮助', icon: 'feedback', onPress: () => console.log('反馈与帮助') },
];

// 分页图标数据获取函数
export const getPageIcons = (pageIndex: number): IconData[] => {
  if (pageIndex === 0) {
    return ICONS_DATA.slice(0, 4);
  } else if (pageIndex === 1) {
    return ICONS_DATA.slice(4, 16);
  } else {
    return ICONS_DATA.slice(19, 23);
  }
};
