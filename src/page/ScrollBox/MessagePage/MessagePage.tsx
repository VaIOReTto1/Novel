import React, { useEffect, useCallback, useState, useRef } from 'react';
import { View, ScrollView } from 'react-native';
import { useMessageStore } from './store/messageStore';
import { useNovelColors } from '../../../utils/theme/colors';
import { wp } from '../../../utils/theme/dimensions';
import { useRefreshLogic } from './hooks/useRefreshLogic';
import { useMessageAnimations } from './hooks/useMessageAnimations';
import { createMessagePageStyles } from './styles/MessagePageStyles';
import { MESSAGE_TABS } from './utils/constants';
import { MessageItem } from './types';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import {
  TopBar,
  MessageItem as MessageItemComponent,
  TabsArea,
  EmptyState,
  RefreshIndicator,
  LoadMoreIndicator,
  MainMessagesSection,
} from './components';

const MessagePage: React.FC = () => {
  // ScrollView ref和tab位置ref
  const scrollViewRef = useRef<any>(null);
  const tabsRef = useRef<any>(null);
  const [tabsYPosition, setTabsYPosition] = useState(0);
  const [shouldScrollToTab, setShouldScrollToTab] = useState(false); // 控制是否需要滚动到tab

  // 二级tab状态管理
  const [secondaryTab, setSecondaryTab] = useState<'comment' | 'reply' | 'like'>('comment');

  // 使用Zustand store
  const {
    messages,
    loading,
    isRefreshing,
    hasMore,
    loadMessages,
    refreshMessages,
    loadMoreMessages,
    markMessageAsRead,
    markAllAsRead,
    setMessages,
  } = useMessageStore();

  const colors = useNovelColors();
  const styles = createMessagePageStyles(colors);

  // 使用自定义hooks
  const refreshLogic = useRefreshLogic({
    isRefreshing,
    loading,
    hasMore,
    refreshMessages,
    loadMoreMessages,
    currentType: secondaryTab,
  });

  const animations = useMessageAnimations(
    isRefreshing,
    refreshLogic.isPullingDown,
    refreshLogic.pullDistance
  );

  // 初始化数据 - 默认显示comment空状态
  useEffect(() => {
    console.log('[MessagePage] 初始化完成，默认显示comment空状态');
    // 默认显示comment tab的空状态，不加载数据
    setMessages([]);
  }, [setMessages]);

  // 返回按钮点击
  const handleBackPress = useCallback(() => {
    console.log('Message page back pressed');
    if (NavigationBridge?.navigateBack) {
      NavigationBridge.navigateBack('MessagePageComponent');
    } else {
      console.log('NavigationBridge.navigateBack not available');
    }
  }, []);

  // Android硬件返回按钮处理
  useEffect(() => {
    return registerHardwareBackHandler(() => {
      console.log('[MessagePage] Android硬件返回按钮被按下');
      if (NavigationBridge?.navigateBack) {
        NavigationBridge.navigateBack('MessagePageComponent');
      }
      return true; // 阻止默认行为
    });
  }, []);

  // 标记全部已读
  const handleMarkAllReadPress = useCallback(() => {
    console.log('Mark all messages as read');
    markAllAsRead();
  }, [markAllAsRead]);

  // Tab切换 - 滚动到tab位置并切换
  const handleTabPress = useCallback((tabId: string) => {
    const tabType = MESSAGE_TABS.find(tab => tab.id === tabId)?.type || 'comment';
    if (tabType === 'comment' || tabType === 'reply' || tabType === 'like') {
      setSecondaryTab(tabType);
      setShouldScrollToTab(true); // 设置滚动标志
    }
  }, []);

  // 只有在用户点击tab时才滚动
  useEffect(() => {
    if (shouldScrollToTab && tabsYPosition > 0 && scrollViewRef.current) {
      // 使用setTimeout确保布局完成后再滚动
      setTimeout(() => {
        if (scrollViewRef.current) {
          console.log('[MessagePage] Scrolling to tabs position:', tabsYPosition);
          scrollViewRef.current.scrollTo({
            y: tabsYPosition,
            animated: true,
          });
          setShouldScrollToTab(false); // 重置滚动标志
        }
      }, 100); // 延迟100ms确保内容渲染完成
    } else if (shouldScrollToTab && tabsYPosition === 0 && scrollViewRef.current) {
      // 如果tabsYPosition还没有获取到，使用估算位置进行滚动
      setTimeout(() => {
        if (scrollViewRef.current) {
          const estimatedTabPosition = wp(56) + wp(120) + wp(8); // 大概位置
          console.log('[MessagePage] Using estimated tab position:', estimatedTabPosition);
          scrollViewRef.current.scrollTo({
            y: estimatedTabPosition,
            animated: true,
          });
          setShouldScrollToTab(false); // 重置滚动标志
        }
      }, 100);
    }
  }, [shouldScrollToTab, tabsYPosition]);

  // 消息项点击
  const handleMessagePress = useCallback((message: MessageItem) => {
    console.log('Message pressed:', message.title);
    // 标记为已读
    if (!message.isRead) {
      markMessageAsRead(message.id);
    }
    // 这里可以导航到详情页或执行其他操作
  }, [markMessageAsRead]);

  // 当二级tab改变时，只为有数据的tab加载数据
  useEffect(() => {
    console.log('Secondary tab changed to:', secondaryTab);
    // 只为reply和like类型加载数据，comment显示空状态
    if (secondaryTab === 'reply' || secondaryTab === 'like') {
      console.log('Loading data for:', secondaryTab);
      loadMessages(true, secondaryTab);
    } else {
      console.log('Comment tab - showing empty state');
      // 对于comment类型，设置空数据但不触发loading
      setMessages([]);
    }
  }, [secondaryTab, loadMessages, setMessages]);

  // 渲染主消息列表（系统通知和粉丝） - 已被优化为独立组件
  // 这个函数虽然现在是空的，但保留它以维持结构清晰，以便未来扩展

  // 渲染二级消息列表（评论、回复、点赞）
  const renderSecondaryMessages = () => {
    // 根据secondaryTab状态过滤消息
    const secondaryMessages = messages.filter(msg => msg.type === secondaryTab);

    if (secondaryMessages.length === 0 && !loading) {
      return (
        <EmptyState
          styles={styles}
          message={getSecondaryEmptyMessage()}
          icon="📦"
        />
      );
    }

    return (
      <View>
        {secondaryMessages.map((message, index) => (
          <MessageItemComponent
            key={`secondary-${message.id}`}
            item={message}
            index={index}
            onPress={() => handleMessagePress(message)}
            styles={styles}
          />
        ))}

        <LoadMoreIndicator
          loading={loading}
          hasMore={hasMore}
          styles={styles}
        />
      </View>
    );
  };

  const getSecondaryEmptyMessage = (): string => {
    switch (secondaryTab) {
      case 'comment':
        return '暂无评论和@消息';
      case 'reply':
        return '暂无话题回帖';
      case 'like':
        return '暂无赞和收藏';
      default:
        return '暂无消息';
    }
  };

  return (
    <View style={styles.container}>
        <TopBar
          styles={styles}
          onBackPress={handleBackPress}
          onMarkAllReadPress={handleMarkAllReadPress}
        />

      <View style={styles.contentContainer}>
        <ScrollView
          ref={scrollViewRef}
          style={styles.scrollView}
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
          onScroll={refreshLogic.handleScroll}
          scrollEventThrottle={16}
          bounces={true}
          alwaysBounceVertical={true}
          scrollEnabled={true}
          stickyHeaderIndices={[1]} // TabsArea作为sticky header (索引1)
        >
          {/* 刷新指示器和主消息列表包装为一个View */}
          <View>
            <RefreshIndicator
              styles={styles}
              isPullingDown={refreshLogic.isPullingDown}
              isRefreshing={isRefreshing}
              pullDistance={refreshLogic.pullDistance}
              threshold={refreshLogic.PULL_THRESHOLD}
              spinStyle={animations.spinStyle}
            />
            {/* 主消息列表 - 使用优化后的组件 */}
            <MainMessagesSection
              styles={styles}
              onMessagePress={handleMessagePress}
            />
            {/* 分割区域 */}
            <View style={styles.divider} />
          </View>

          {/* 二级Tab区域 - 作为sticky header (索引1) */}
          <View
            ref={tabsRef}
            onLayout={(ev: { nativeEvent: { layout: { y: any; }; }; }) => {
              const y = ev.nativeEvent.layout.y;
              console.log('[MessagePage] tabsRef onLayout y:', y, 'current tabsYPosition:', tabsYPosition);
              // 总是更新位置，确保在内容变化时能获取到正确位置
              if (y > 0) {
                setTabsYPosition(y);
              }
            }}
            style={styles.tabsSectionContainer}
          >
            <TabsArea
              styles={styles}
              tabs={MESSAGE_TABS}
              selectedTab={secondaryTab}
              onTabPress={handleTabPress}
            />
          </View>

          {/* 二级消息列表 (索引2) */}
          <View>
            {renderSecondaryMessages()}
          </View>
        </ScrollView>
      </View>
    </View>
  );
};

export default MessagePage;
