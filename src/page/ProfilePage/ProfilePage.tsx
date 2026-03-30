import React, { useEffect, useCallback } from 'react';
import { View, ScrollView } from 'react-native';
import { useUserStore } from './store/userStore';
import { useHomeStore } from './store/BookStore';
import { useNovelColors } from '../../utils/theme/colors';
import { useProfilePageAnimations } from './hooks/useProfilePageAnimations';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { convertHomeBooksToBooks } from './utils/helpers';
import { createHomePageStyles } from './styles/ProfilePageStyles';
import { Book } from './types';
import { useThemeStore } from '../../utils/theme/themeStore';
import NavBridge from '../../utils/bridge/NavigationBridge';
import { getCurrentUserData, getUserBalance, getUserCoins } from '../../utils/bridge/UserBridge';
import {
  TopBar,
  LoginBar,
  RefreshIndicator,
  ScrollableArea,
  BottomBox,
  WaterfallGrid,
} from './components';

const ProfilePage: React.FC = () => {

  // 使用Zustand stores
  const { nickname, photo, isLoggedIn, balance, coins, isAuthor, handleNativeUserData, setBalance, setCoins, initializeFromCache, setAuthorStatus } = useUserStore();
  const {
    homeRecommendBooks,
    homeRecommendLoading,
    isRefreshing,
    hasMoreHomeRecommend,
    loadHomeRecommendBooks,
    refreshBooks,
    loadMoreBooks,
  } = useHomeStore();

  // 添加主题store
  const { initializeFromNative, isInitialized } = useThemeStore();

  const colors = useNovelColors();
  const styles = createHomePageStyles(colors);

  // 将HomeBook转换为Book格式
  const convertedBooks = React.useMemo(() => {
    return convertHomeBooksToBooks(homeRecommendBooks);
  }, [homeRecommendBooks]);

  // 使用自定义hooks
  const refreshLogic = useRefreshLogic({
    isRefreshing,
    homeRecommendLoading,
    hasMoreHomeRecommend,
    refreshBooks,
    loadMoreBooks,
  });

  const animations = useProfilePageAnimations(
    isRefreshing,
    refreshLogic.isPullingDown,
    refreshLogic.pullDistance,
    refreshLogic.PULL_THRESHOLD
  );

  // 初始化数据和主题
  useEffect(() => {
    const initializePageData = async () => {
      try {
        // 🎯 只有在主题未初始化时才从原生端获取（避免重复初始化）
        if (!isInitialized) {
          console.log('[ProfilePage] 🎨 主题未初始化，从原生端获取');
          await initializeFromNative();
          console.log('[ProfilePage] 🎨 主题同步完成');
        } else {
          console.log('[ProfilePage] 🎨 主题已初始化，跳过原生端获取');
        }

        // 然后加载数据
        await loadHomeRecommendBooks();
        console.log('[ProfilePage] 📊 数据加载完成');
      } catch (error) {
        console.error('[ProfilePage] 初始化失败:', error);
        // 即使主题初始化失败，也要尝试加载数据
        loadHomeRecommendBooks();
      }
    };

    initializePageData();
  }, [loadHomeRecommendBooks, initializeFromNative, isInitialized]);

  // 初始化用户缓存 + 从原生刷新登录/余额/作家状态
  useEffect(() => {
    const initUserAndAuthor = async () => {
      try {
        await initializeFromCache();

        const [ud, bal, c] = await Promise.all([
          getCurrentUserData().catch(() => null),
          getUserBalance().catch(() => 0),
          getUserCoins().catch(() => 0),
        ]);

        if (ud) { handleNativeUserData(ud); }
        if (typeof bal === 'number') { setBalance(bal); }
        if (typeof c === 'number') { setCoins(c); }

        try {
          const status = await NavBridge.getAuthorStatus();
          if (status && typeof status.isAuthor === 'boolean') { setAuthorStatus(status.isAuthor); }
        } catch (e) {
          // ignore
        }
      } catch (e) {
        // ignore
      }
    };
    initUserAndAuthor();
  }, [initializeFromCache, handleNativeUserData, setBalance, setCoins, setAuthorStatus]);

  // 登录函数
  const toLogin = useCallback(() => {
    NavBridge.goToLogin();
  }, []);

  // 书籍点击
  const handleBookPress = useCallback((book: Book) => {
    console.log('Book pressed:', book.title);
    // 这里可以导航到书籍详情页
  }, []);

  // 设置按钮点击
  const handleSettingsPress = useCallback(() => {
    NavBridge.navigateToSettings();
  }, []);

  // 成为作家/写小说按钮点击（来自第一页图标）
  const handleBeWriterPress = useCallback(() => {
    NavBridge.navigateToBecomeWriterWithFlag(!!isAuthor);
  }, [isAuthor]);

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
        onScroll={refreshLogic.handleScroll}
        scrollEventThrottle={16}
        bounces={true}
        alwaysBounceVertical={true}
        scrollEnabled={true}
      >
        <RefreshIndicator
          styles={styles}
          isPullingDown={refreshLogic.isPullingDown}
          isRefreshing={isRefreshing}
          pullDistance={refreshLogic.pullDistance}
          threshold={refreshLogic.PULL_THRESHOLD}
          spinStyle={animations.spinStyle}
        />

        <TopBar styles={styles} onSettingsPress={handleSettingsPress} />

        <LoginBar
          styles={styles}
          photo={photo || undefined}
          isLoggedIn={isLoggedIn}
          nickname={nickname || undefined}
          onLogin={toLogin}
        />

        <ScrollableArea
          styles={styles}
          scrollX={animations.scrollX}
          animatedContainerStyle={animations.animatedContainerStyle}
          firstPageIconsStyle={animations.firstPageIconsStyle}
          secondPageIconsStyle={animations.secondPageIconsStyle}
          thirdPageIconsStyle={animations.thirdPageIconsStyle}
          firstPageAdStyle={animations.firstPageAdStyle}
          colors={colors}
          onBeWriterPress={handleBeWriterPress}
          isAuthor={!!isAuthor}
        />

        <BottomBox
          styles={styles}
          coins={coins}
          balance={balance}
        />

        <WaterfallGrid
          styles={styles}
          books={convertedBooks}
          loading={homeRecommendLoading}
          hasMore={hasMoreHomeRecommend}
          onBookPress={handleBookPress}
        />
      </ScrollView>
    </View>
  );
};

export default ProfilePage;
