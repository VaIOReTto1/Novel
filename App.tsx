import React, { useEffect } from 'react';
import { SafeAreaView, StyleSheet } from 'react-native';
import ProfilePage from './src/page/ProfilePage/ProfilePage';
import { initializeApp, cleanupApp } from './src/utils/appInit';
import { useUserStore } from './src/page/ProfilePage/store/userStore';
import { useHomeStore } from './src/page/ProfilePage/store/BookStore';
import { useThemeStore } from './src/utils/theme/themeStore';
import { useNovelColors } from './src/utils/theme/colors';

interface AppProps {
  initialThemeMode?: string;
  initialActualTheme?: string;
  initialIsDarkMode?: boolean;
}

export default function App(props: AppProps = {}): React.JSX.Element {
  const { initializeFromProps } = useThemeStore();

  // 🎯 在组件渲染之前立即初始化主题（同步，避免闪烁）
  React.useMemo(() => {
    if (props.initialThemeMode || props.initialActualTheme !== undefined || props.initialIsDarkMode !== undefined) {
      initializeFromProps(props);
    }
  }, [props.initialThemeMode, props.initialActualTheme, props.initialIsDarkMode, initializeFromProps]);

  useEffect(() => {
    // 初始化应用（异步）
    const initApp = async () => {
      try {
        await initializeApp();
        console.log('[App] 🎨 应用初始化完成');
      } catch (error) {
        console.error('[App] ❌ 应用初始化失败:', error);
      }
    };
    
    initApp();

    // 监听store变化并打印日志
    const userUnsubscribe = useUserStore.subscribe((state) => {
      console.log('[App] 📱 用户状态更新:', {
        uid: state.uid,
        nickname: state.nickname,
        isLoggedIn: state.isLoggedIn,
      });
    });

    const homeUnsubscribe = useHomeStore.subscribe((state) => {
      console.log('[App] 🏠 首页状态更新:', {
        booksCount: state.recommendBooks.length,
        loading: state.loading,
        firstBookTitle: state.recommendBooks[0]?.title,
      });
    });

    // 清理函数
    return () => {
      cleanupApp();
      userUnsubscribe();
      homeUnsubscribe();
      console.log('[App] 🎨 应用清理完成');
    };
  }, []);

  // 动态获取颜色，确保主题初始化后使用
  const colors = useNovelColors();
  
  const styles = StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: colors.novelBackground,
    },
  });

  return (
    <SafeAreaView style={styles.container}>
      <ProfilePage />
    </SafeAreaView>
  );
}
