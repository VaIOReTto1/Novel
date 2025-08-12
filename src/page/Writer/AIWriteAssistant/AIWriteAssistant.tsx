import React, { useCallback, useEffect, useRef } from 'react';
import { View } from 'react-native';
// 使用 require 兼容 RN 版本类型差异
const RN: any = require('react-native');
const { FlatList, BackHandler } = RN;
import { useNovelColors } from '../../../utils/theme/colors';
import { wp } from '../../../utils/theme/dimensions';
import { NavigationBridge } from '../../../utils/bridge/NavigationBridge';
import { createAIStyles } from './styles/aiStyles';
import { useAIStore } from './store/aiStore';
import { useAIShortcuts } from './hooks/useAIShortcuts';
import { Header } from './components/Header';
import { IntroExample } from './components/IntroExample';
import { Suggestions } from './components/Suggestions';
import { InputBar } from './components/InputBar';
import { ChatRow } from './components/ChatRow';

const AIWriteAssistant: React.FC = () => {
  const colors = useNovelColors();
  useEffect(() => {
    const sub = BackHandler.addEventListener('hardwareBackPress', () => {
      NavigationBridge.navigateBack?.('AIWriteAssistantComponent');
      return true;
    });
    return () => sub.remove();
  }, []);

  const styles = createAIStyles(colors);
  const { messages, input, setInput, send, dailyRemaining, sending } = useAIStore();
  const listRef = useRef<any>(null);
  const nearBottomRef = useRef(true);

  const handleScroll = useCallback((e: any) => {
    try {
      const { layoutMeasurement, contentOffset, contentSize } = e.nativeEvent;
      const distanceFromBottom = contentSize.height - (contentOffset.y + layoutMeasurement.height);
      nearBottomRef.current = distanceFromBottom < wp(80);
    } catch {}
  }, []);
  const { toggleDeepThinkMode, fillIdea } = useAIShortcuts();

  return (
    <View style={styles.container}>
      <Header
        onBack={() => NavigationBridge.navigateBack?.('AIWriteAssistantComponent')}
        onMenu={() => {}}
        quotaText={`今日剩余：${dailyRemaining}次`}
      />

      {/* chat list */}
      <FlatList
        ref={listRef}
        contentContainerStyle={{ paddingHorizontal: wp(16), paddingBottom: wp(10) }}
        data={messages}
        keyExtractor={(m: { id: string }) => m.id}
        renderItem={({ item }: { item: any }) => (
          item.role === 'assistant' && item.id === 'intro' ? (
            <IntroExample onRefresh={() => { /* 可替换为刷新示例逻辑 */ }} />
          ) : (
            <ChatRow item={item} />
          )
        )}
        onScroll={handleScroll}
        scrollEventThrottle={16}
        onContentSizeChange={() => {
          try {
            if (nearBottomRef.current || sending) {
              listRef.current?.scrollToEnd?.({ animated: true });
            }
          } catch {}
        }}
      />

      <Suggestions onDeepThink={toggleDeepThinkMode} onIdea={fillIdea} />

      <InputBar value={input} onChange={setInput} sending={sending} onSend={send} />
    </View>
  );
};

export default AIWriteAssistant;


