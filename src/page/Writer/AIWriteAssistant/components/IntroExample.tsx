import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';

interface IntroExampleProps {
  onRefresh: () => void;
}

export const IntroExample: React.FC<IntroExampleProps> = ({ onRefresh }) => {
  const colors = useNovelColors();
  const styles = createAIStyles(colors);

  return (
    <View style={styles.introBubble}>
      <Text style={styles.introTitle}>
        Hi，我是写作助手，会一直陪伴你在番茄的创作之旅！可以回答一些关于小说创作的问题，例如：
      </Text>

      <View style={styles.introCards}>
        <View style={styles.introCard}>
          <Text>
            如何在不直接描写角色外貌的情况下，让读者在脑海中形成清晰的人物形象？
          </Text>
        </View>
        <View style={styles.introCard}>
          <Text>
            如果你的小说主角突然获得了一种与自身性格完全相悖的超能力，这会如何影响故事的核心冲突发展？
          </Text>
        </View>
        <View style={styles.introCard}>
          <Text>
            在创作历史背景小说时，如何平衡真实历史事件的准确性与虚构情节的戏剧张力？
          </Text>
        </View>
      </View>

      <TouchableOpacity style={styles.introRefresh} onPress={onRefresh}>
        <Text style={styles.introRefreshText}>↻</Text>
      </TouchableOpacity>
    </View>
  );
};


