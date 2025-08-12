import React, { useEffect } from 'react';
import { View, Text } from 'react-native';
// 使用 require 兼容 RN 版本类型差异
// eslint-disable-next-line @typescript-eslint/no-var-requires
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
  const { fillDeepThink, fillIdea } = useAIShortcuts();

  return (
    <View style={styles.container}>
      <Header
        onBack={() => NavigationBridge.navigateBack?.('AIWriteAssistantComponent')}
        onMenu={() => {}}
        quotaText={`今日剩余：${dailyRemaining}次`}
      />

      {/* chat list */}
      <FlatList
        contentContainerStyle={{ paddingHorizontal: wp(16), paddingBottom: wp(10) }}
        data={messages}
        keyExtractor={(m: { id: string }) => m.id}
        renderItem={({ item }: { item: { id?: string; role: string; text: string } }) => (
          item.role === 'assistant' && item.id === 'intro' ? (
            <IntroExample onRefresh={() => { /* 可替换为刷新示例逻辑 */ }} />
          ) : (
            <View
              style={[
                styles.bubble,
                item.role === 'assistant' ? styles.bubbleAssistant : styles.bubbleUser,
              ]}
            >
              <Text style={item.role === 'assistant' ? styles.textAssistant : styles.textUser}>
                {item.text}
              </Text>
            </View>
          )
        )}
      />

      <Suggestions onDeepThink={fillDeepThink} onIdea={fillIdea} />

      <InputBar value={input} onChange={setInput} sending={sending} onSend={send} />
    </View>
  );
};

export default AIWriteAssistant;


