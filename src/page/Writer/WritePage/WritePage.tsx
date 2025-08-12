import React, { useCallback, useEffect } from 'react';
import { View, Text, ScrollView } from 'react-native';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const RN: any = require('react-native');
const { TextInput, BackHandler, KeyboardAvoidingView, Platform } = RN;
import { createWritePageStyles } from './styles/WritePageStyles';
import { useNovelColors } from '../../../utils/theme/colors';
import { useWriteStore } from './store/writeStore';
import { TopBar } from './components/TopBar';
import { WelcomePanel } from './components/WelcomePanel';
import { VolumeBar } from './components/VolumeBar';
import { useWriteActions } from './hooks/useWriteActions';

const WritePage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);

  const { title, content, setTitle, setContent, publish, undo, redo, goAI, goBack } = useWriteActions();

  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => { goBack(); return true; });
    return () => backHandler.remove();
  }, [goBack]);

  return (
    <View style={styles.container}>
      <TopBar onBack={goBack} onUndo={undo} onRedo={redo} onAI={goAI} onPublish={publish} />

      <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
        <ScrollView style={styles.editor} keyboardShouldPersistTaps="handled">
          <TextInput
            style={styles.titleInput}
            placeholder="请输入标题"
            placeholderTextColor={colors.novelTextGray}
            value={title}
            onChangeText={setTitle}
          />
          <TextInput
            style={styles.contentInput}
            placeholder="请输入正文"
            placeholderTextColor={colors.novelTextGray}
            value={content}
            onChangeText={setContent}
            multiline
            textAlignVertical="top"
          />
        </ScrollView>
      </KeyboardAvoidingView>

      <WelcomePanel />
      <VolumeBar />
    </View>
  );
};

export default WritePage;


