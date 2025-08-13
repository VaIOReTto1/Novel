import React from 'react';
import { View, Text } from 'react-native';
// 兼容 require 方式
const RN: any = require('react-native');
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';
import { MarkdownText } from './MarkdownText';
import { ThinkingBlock } from './ThinkingBlock';
import { ActionBar } from './ActionBar';
import { useAIStore } from '../store/aiStore';
import type { ChatMessage } from '../types';

const ChatRowComponent: React.FC<{ item: ChatMessage }> = ({ item }) => {
    const colors = useNovelColors();
    const styles = createAIStyles(colors);
  const setInput = useAIStore((s) => s.setInput);
  const send = useAIStore((s) => s.send);
  const thinkingNow = item.thinking ?? '';
  const textNow = item.text ?? '';
  const isLoading = !item.done && !(thinkingNow.length > 0) && !(textNow.length > 0);
  const reasoningDone = textNow.length > 0; // content started means reasoning finished

    if (item.role !== 'assistant') {
        return (
            <View style={[styles.bubble, styles.bubbleUser, styles.bubbleUserCorner]}>
                <Text style={styles.textUser}>{item.text}</Text>
            </View>
        );
    }

    return (
        <View style={[styles.bubble, styles.bubbleAssistant, styles.bubbleAssistantCorner]}>
            {isLoading ? (
                <View style={styles.loadingRow}>
                    {RN.ActivityIndicator ? (
                        <RN.ActivityIndicator size="small" color={colors.novelTextGray} />
                    ) : null}
                    <Text style={styles.loadingText}>生成中…</Text>
                </View>
            ) : null}
      {thinkingNow ? (
        <ThinkingBlock thinking={thinkingNow} done={reasoningDone} />
            ) : null}
      <MarkdownText text={textNow} />
            {item.done ? (
                <ActionBar
                    onLike={() => console.log('[AI] like', item.id)}
                    onDislike={() => console.log('[AI] dislike', item.id)}
                    onCopy={async () => {
                        try {
                            const { Clipboard } = RN;
                            if (Clipboard?.setString) { Clipboard.setString(item.text); }
                        } catch { }
                    }}
                    onRetry={() => {
                        try {
                            const store = useAIStore.getState();
                            const lastUser = [...store.messages].reverse().find((m: any) => m.role === 'user');
                            console.log('[AI] retry for', item.id, 'using lastUser', lastUser?.id);
                            if (lastUser?.text) {
                                setInput(lastUser.text);
                                setTimeout(() => send(), 0);
                            }
                        } catch { }
                    }}
                />
            ) : null}
        </View>
    );
};

export const ChatRow = React.memo(
  ChatRowComponent,
  (prev, next) =>
    prev.item.id === next.item.id &&
    prev.item.text === next.item.text &&
    (prev.item.thinking ?? '') === (next.item.thinking ?? '') &&
    !!prev.item.done === !!next.item.done
);


