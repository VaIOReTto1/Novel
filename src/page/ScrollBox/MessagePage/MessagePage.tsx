import React, { useEffect, useCallback, useState, useRef, useMemo } from 'react';
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
import {
  bootstrapMessagePage,
  createMessagePageHandlers,
  getSecondaryEmptyMessage,
} from './domain/messagePageModel';

const MessagePage: React.FC = () => {
  const scrollViewRef = useRef<any>(null);
  const tabsRef = useRef<any>(null);
  const [tabsYPosition, setTabsYPosition] = useState(0);
  const [shouldScrollToTab, setShouldScrollToTab] = useState(false);
  const [secondaryTab, setSecondaryTab] = useState<'comment' | 'reply' | 'like'>('comment');

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
    refreshLogic.pullDistance,
  );

  useEffect(() => {
    bootstrapMessagePage({
      setMessages,
    });
  }, [setMessages]);

  const handlers = useMemo(
    () =>
      createMessagePageHandlers({
        setSecondaryTab,
        setShouldScrollToTab,
        markAllAsRead,
        markMessageAsRead,
        handleViewMoreRecommend: () => {},
      }),
    [markAllAsRead, markMessageAsRead],
  );

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      NavigationBridge.navigateBack('MessagePageComponent');
      return true;
    });
  }, []);

  useEffect(() => {
    if (shouldScrollToTab && tabsYPosition > 0 && scrollViewRef.current) {
      setTimeout(() => {
        if (scrollViewRef.current) {
          scrollViewRef.current.scrollTo({
            y: tabsYPosition,
            animated: true,
          });
          setShouldScrollToTab(false);
        }
      }, 100);
    } else if (shouldScrollToTab && tabsYPosition === 0 && scrollViewRef.current) {
      setTimeout(() => {
        if (scrollViewRef.current) {
          const estimatedTabPosition = wp(56) + wp(120) + wp(8);
          scrollViewRef.current.scrollTo({
            y: estimatedTabPosition,
            animated: true,
          });
          setShouldScrollToTab(false);
        }
      }, 100);
    }
  }, [shouldScrollToTab, tabsYPosition]);

  useEffect(() => {
    if (secondaryTab === 'reply' || secondaryTab === 'like') {
      loadMessages(true, secondaryTab);
    } else {
      setMessages([]);
    }
  }, [secondaryTab, loadMessages, setMessages]);

  const renderSecondaryMessages = () => {
    const secondaryMessages = messages.filter(msg => msg.type === secondaryTab);

    if (secondaryMessages.length === 0 && !loading) {
      return (
        <EmptyState
          styles={styles}
          message={getSecondaryEmptyMessage(secondaryTab)}
          icon="消息"
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
            onPress={() => handlers.handleMessagePress(message)}
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

  return (
    <View style={styles.container}>
      <TopBar
        styles={styles}
        onBackPress={() => NavigationBridge.navigateBack('MessagePageComponent')}
        onMarkAllReadPress={handlers.handleMarkAllReadPress}
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
          stickyHeaderIndices={[1]}
        >
          <View>
            <RefreshIndicator
              styles={styles}
              isPullingDown={refreshLogic.isPullingDown}
              isRefreshing={isRefreshing}
              pullDistance={refreshLogic.pullDistance}
              threshold={refreshLogic.PULL_THRESHOLD}
              spinStyle={animations.spinStyle}
            />
            <MainMessagesSection
              styles={styles}
              onMessagePress={handlers.handleMessagePress}
            />
            <View style={styles.divider} />
          </View>

          <View
            ref={tabsRef}
            onLayout={(ev: { nativeEvent: { layout: { y: any } } }) => {
              const y = ev.nativeEvent.layout.y;
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
              onTabPress={handlers.handleTabPress}
            />
          </View>

          <View>
            {renderSecondaryMessages()}
          </View>
        </ScrollView>
      </View>
    </View>
  );
};

export default MessagePage;
