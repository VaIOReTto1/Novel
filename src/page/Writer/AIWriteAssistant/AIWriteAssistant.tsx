import React, {useCallback, useEffect, useRef, useState} from 'react';
import {View} from 'react-native';
// 使用 require 兼容 RN 版本类型差异
const RN: any = require('react-native');
const {FlatList} = RN;
import {useNovelColors} from '../../../utils/theme/colors';
import {wp} from '../../../utils/theme/dimensions';
import {NavigationBridge} from '../../../utils/bridge/NavigationBridge';
import {registerHardwareBackHandler} from '../../../utils/runtime/backNavigation';
import {createAIStyles} from './styles/aiStyles';
import {useAIStore} from './store/aiStore';
import {useAIShortcuts} from './hooks/useAIShortcuts';
import {Header} from './components/Header';
import {IntroExample} from './components/IntroExample';
import {Suggestions} from './components/Suggestions';
import {InputBar} from './components/InputBar';
import {ChatRow} from './components/ChatRow';
import {IdeaSelector} from './components/IdeaSelector';

const AIWriteAssistant: React.FC = () => {
  const colors = useNovelColors();
  useEffect(() => {
    return registerHardwareBackHandler(() => {
      NavigationBridge.navigateBack?.('AIWriteAssistantComponent');
      return true;
    });
  }, []);

  const styles = createAIStyles(colors);
  // 精细化订阅，避免全量状态变更触发整页重渲染
  const messages = useAIStore(s => s.messages);
  const input = useAIStore(s => s.input);
  const setInput = useAIStore(s => s.setInput);
  const send = useAIStore(s => s.send);
  const dailyRemaining = useAIStore(s => s.dailyRemaining);
  const sending = useAIStore(s => s.sending);
  const rehydrate = useAIStore(s => s.rehydrate);
  const setIdeaCategory = useAIStore(s => s.setIdeaCategory);
  const ideaCategory = useAIStore(s => s.ideaCategory);
  const setIdeaPromptActive = useAIStore(s => s.setIdeaPromptActive);
  const hydrated = useAIStore(s => s.hydrated);
  const listRef = useRef<any>(null);
  const nearBottomRef = useRef(true);
  const [ideaVisible, setIdeaVisible] = useState(false);
  const [headerHeight, setHeaderHeight] = useState(0);

  const handleScroll = useCallback((e: any) => {
    try {
      const {layoutMeasurement, contentOffset, contentSize} = e.nativeEvent;
      const distanceFromBottom =
        contentSize.height - (contentOffset.y + layoutMeasurement.height);
      nearBottomRef.current = distanceFromBottom < wp(80);
    } catch {}
  }, []);
  const {toggleDeepThinkMode} = useAIShortcuts();

  useEffect(() => {
    rehydrate().catch(() => {});
  }, [rehydrate]);

  // 初次进入或恢复完成后，自动滚动到底部
  useEffect(() => {
    if (!hydrated) { return; }
    try {
      setTimeout(() => listRef.current?.scrollToEnd?.({animated: true}), 0);
    } catch {}
  }, [hydrated]);

  // 消息变化时，若仍在底部附近或正在发送，重复尝试滚动到底部（兼容虚拟化逐步渲染）
  useEffect(() => {
    if (!hydrated) { return; }
    if (!nearBottomRef.current && !sending) { return; }
    const doScroll = () => {
      try { listRef.current?.scrollToEnd?.({animated: true}); } catch {}
    };
    const t1 = setTimeout(doScroll, 0);
    const t2 = setTimeout(doScroll, 80);
    return () => { clearTimeout(t1); clearTimeout(t2); };
  }, [messages.length, sending, hydrated]);

  const renderItem = useCallback(({item}: {item: any}) => (
    item.role === 'assistant' && item.id === 'intro' ? (
      <IntroExample onRefresh={() => {}} />
    ) : (
      <ChatRow item={item} />
    )
  ), []);

  // 首屏加载：在未 hydrated 之前只显示 Loading
  if (!hydrated) {
    return (
      <View style={[styles.container, styles.loadingScreen]}>
        {RN.ActivityIndicator ? (
          <RN.ActivityIndicator size="large" color={colors.novelTextGray} />
        ) : null}
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View
        style={styles.headerFixed}
        pointerEvents="box-none"
        onLayout={(e: any) =>
          setHeaderHeight(e.nativeEvent?.layout?.height || 0)
        }>
        <Header
          onBack={() =>
            NavigationBridge.navigateBack?.('AIWriteAssistantComponent')
          }
          onMenu={() => {}}
          quotaText={`今日剩余：${dailyRemaining}次`}
        />
      </View>
      <View
        style={[styles.headerSpacer, {height: headerHeight || undefined}]}
      />

      {/* chat list */}
      <FlatList
        ref={listRef}
        contentContainerStyle={{
          paddingHorizontal: wp(16),
          // 预留底部 Suggestions + InputBar 空间，避免遮挡
          paddingBottom: wp(10),
        }}
        keyboardShouldPersistTaps="handled"
        removeClippedSubviews
        initialNumToRender={6}
        maxToRenderPerBatch={6}
        windowSize={7}
        updateCellsBatchingPeriod={50}
        onLayout={() => {
          try {
            if (hydrated) {
              setTimeout(() => listRef.current?.scrollToEnd?.({animated: true}), 0);
            }
          } catch {}
        }}
        data={messages}
        keyExtractor={(m: {id: string}) => m.id}
        renderItem={renderItem}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        onContentSizeChange={() => {
          try {
            if (nearBottomRef.current || sending) {
              listRef.current?.scrollToEnd?.({animated: true});
            }
          } catch {}
        }}
      />

      <Suggestions
        onDeepThink={toggleDeepThinkMode}
        onIdea={() => {
          setIdeaVisible(v => {
            const next = !v;
            try {
              setIdeaPromptActive(next);
            } catch {}
            return next;
          });
        }}
      />

      {ideaVisible ? (
        <IdeaSelector
          onClose={() => {
            setIdeaVisible(false);
            try {
              setIdeaPromptActive(false);
            } catch {}
          }}
          selected={ideaCategory}
          onSelect={c => {
            try {
              setIdeaCategory(c);
              // 选择后保持浮窗开启，方便连续切换
            } catch {}
          }}
        />
      ) : null}

      <InputBar
        value={input}
        onChange={setInput}
        sending={sending}
        onSend={send}
        onFocusInput={() => {
          try {
            // 聚焦输入时，自动滚到最底部
            setTimeout(
              () => listRef.current?.scrollToEnd?.({animated: true}),
              0,
            );
          } catch {}
        }}
      />
    </View>
  );
};

export default AIWriteAssistant;
