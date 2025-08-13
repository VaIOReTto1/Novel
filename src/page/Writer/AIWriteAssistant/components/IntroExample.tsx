import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';
import { useAIStore } from '../store/aiStore';
import Icon from 'react-native-vector-icons/MaterialIcons';

interface IntroExampleProps {
  onRefresh: () => void;
}

export const IntroExample: React.FC<IntroExampleProps> = ({ onRefresh }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);
  const setInput = useAIStore(s => s.setInput);
  const send = useAIStore(s => s.send);

  const handlePress = (text: string) => {
    try {
      setInput(text);
      setTimeout(() => send(), 0);
    } catch { }
  };

  return (
    <View style={[styles.introBubble, styles.bubbleAssistantCorner]}>
      <Text style={styles.introTitle}>
        Hi，我是写作助手，会一直陪伴你在番茄的创作之旅！可以回答一些关于小说创作的问题，例如：
      </Text>

      <View style={styles.introCards}>
        <TouchableOpacity style={styles.introCard} onPress={() => handlePress('如何在不直接描写角色外貌的情况下，让读者在脑海中形成清晰的人物形象？')}>
          <Text>
            如何在不直接描写角色外貌的情况下，让读者在脑海中形成清晰的人物形象？
          </Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.introCard} onPress={() => handlePress('如果你的小说主角突然获得了一种与自身性格完全相悖的超能力，这会如何影响故事的核心冲突发展？')}>
          <Text>
            如果你的小说主角突然获得了一种与自身性格完全相悖的超能力，这会如何影响故事的核心冲突发展？
          </Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.introCard} onPress={() => handlePress('在创作历史背景小说时，如何平衡真实历史事件的准确性与虚构情节的戏剧张力？')}>
          <Text>
            在创作历史背景小说时，如何平衡真实历史事件的准确性与虚构情节的戏剧张力？
          </Text>
        </TouchableOpacity>
      </View>

      <TouchableOpacity style={styles.introRefresh} onPress={onRefresh}>
        <Icon name="refresh" size={18} color={colors.novelTextGray + 'a0'} />
      </TouchableOpacity>
    </View>
  );
};


