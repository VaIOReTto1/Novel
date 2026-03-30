import React, {useCallback, useEffect, useRef, useState} from 'react';
import {View} from 'react-native';
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
import {
  bootstrapAIWriteAssistantPage,
  createAIWriteAssistantHandlers,
} from './domain/aiWriteAssistantPageModel';

const AIWriteAssistant: React.FC = () => {
  const colors = useNovelColors();

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      NavigationBridge.navigateBack?.('AIWriteAssistantComponent');
      return true;
    });
  }, []);

  const styles = createAIStyles(colors);
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

  const handlers = React.useMemo(
    () =>
      createAIWriteAssistantHandlers({
        navigateBack: () =>
          NavigationBridge.navigateBack?.('AIWriteAssistantComponent'),
        setIdeaVisible,
        setIdeaPromptActive,
        setIdeaCategory,
        scrollToEnd: () => {
          try {
            listRef.current?.scrollToEnd?.({animated: true});
          } catch {}
        },
      }),
    [setIdeaCategory, setIdeaPromptActive],
  );

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
    bootstrapAIWriteAssistantPage({
      rehydrate,
    });
  }, [rehydrate]);

  useEffect(() => {
    if (!hydrated) {
      return;
    }
    try {
      setTimeout(() => listRef.current?.scrollToEnd?.({animated: true}), 0);
    } catch {}
  }, [hydrated]);

  useEffect(() => {
    if (!hydrated) {
      return;
    }
    if (!nearBottomRef.current && !sending) {
      return;
    }
    const doScroll = () => {
      try {
        listRef.current?.scrollToEnd?.({animated: true});
      } catch {}
    };
    const t1 = setTimeout(doScroll, 0);
    const t2 = setTimeout(doScroll, 80);
    return () => {
      clearTimeout(t1);
      clearTimeout(t2);
    };
  }, [messages.length, sending, hydrated]);

  const renderItem = useCallback(
    ({item}: {item: any}) =>
      item.role === 'assistant' && item.id === 'intro' ? (
        <IntroExample onRefresh={() => {}} />
      ) : (
        <ChatRow item={item} />
      ),
    [],
  );

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
          onBack={handlers.handleBack}
          onMenu={() => {}}
          quotaText={`今日剩余：${dailyRemaining}次`}
        />
      </View>
      <View style={[styles.headerSpacer, {height: headerHeight || undefined}]} />

      <FlatList
        ref={listRef}
        contentContainerStyle={{
          paddingHorizontal: wp(16),
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
        onIdea={() => handlers.handleToggleIdea(ideaVisible)}
      />

      {ideaVisible ? (
        <IdeaSelector
          onClose={handlers.handleCloseIdea}
          selected={ideaCategory}
          onSelect={handlers.handleSelectIdea}
        />
      ) : null}

      <InputBar
        value={input}
        onChange={setInput}
        sending={sending}
        onSend={send}
        onFocusInput={handlers.handleInputFocus}
      />
    </View>
  );
};

export default AIWriteAssistant;
