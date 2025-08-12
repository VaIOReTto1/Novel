import React, { useEffect } from 'react';
import { View, ScrollView, Text, TouchableOpacity } from 'react-native';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const RN: any = require('react-native');
const { TextInput, BackHandler, KeyboardAvoidingView, Platform, Modal, ActivityIndicator } = RN;
import { createWritePageStyles } from './styles/WritePageStyles';
import { useNovelColors } from '../../../utils/theme/colors';
import { useWriteStore } from './store/writeStore';
import { TopBar } from './components/TopBar';
import { WelcomePanel } from './components/WelcomePanel';
import { VolumeBar } from './components/VolumeBar';
import { useWriteActions } from './hooks/useWriteActions';
import { SelectionToolbar } from './components/SelectionToolbar';

const WritePage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);

  const { title, content, setTitle, setContent, publish, undo, redo, goAI, goBack } = useWriteActions();
  const writeStore = useWriteStore();

  useEffect(() => {
    const backHandler = BackHandler.addEventListener('hardwareBackPress', () => { goBack(); return true; });
    return () => backHandler.remove();
  }, [goBack]);

  // 简易选择监听：以最后一次输入焦点为锚点，真实实现需自定义可编辑组件
  const [anchorY] = React.useState(120);

  // 从全局写作 store 读选区与可见性
  const isToolbarVisible = useWriteStore(s => s.isToolbarVisible);
  const storeSelectedText = useWriteStore(s => s.selectedText);
  const selectionRange = useWriteStore(s => s.selectionRange);
  const updateSelection = useWriteStore(s => s.updateSelection);
  const releaseSelectionHold = useWriteStore(s => s.releaseSelectionHold);

  const onSelectionChange = React.useCallback((e: any) => {
    const sel = e?.nativeEvent?.selection;
    if (!sel) { return; }
    const start = Math.min(sel.start, sel.end);
    const end = Math.max(sel.start, sel.end);
    const text = content?.slice(start, end) ?? '';
    if (end > start) {
      updateSelection(text, start, end);
    }
    // 零选区不立即关闭，由工具条的“关闭/操作完成”主动关闭
  }, [content, updateSelection]);

  const replaceSelected = (newText: string) => {
    // 交给 store：直接 setContent；此函数保留以兼容组件 props
    const idx = content.indexOf(storeSelectedText);
    if (idx < 0) { return; }
    setContent(content.slice(0, idx) + newText + content.slice(idx + storeSelectedText.length));
  };

  const appendToSelected = (newText: string) => {
    const idx = content.indexOf(storeSelectedText);
    if (idx < 0) { return; }
    setContent(content.slice(0, idx + storeSelectedText.length) + newText + content.slice(idx + storeSelectedText.length));
  };

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
            onSelectionChange={onSelectionChange}
            selection={selectionRange || undefined}
            // 关闭原生选择菜单（仅提示效果，真实环境需自定义可编辑组件/原生扩展）
            contextMenuHidden
            multiline
            textAlignVertical="top"
          />
        </ScrollView>
      </KeyboardAvoidingView>

      {/* 错误弹窗 */}
      <Modal visible={!!writeStore.errorModal?.visible} transparent animationType="fade" onRequestClose={() => writeStore.hideError()}>
        <View style={styles.overlayMask}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalHint}>{writeStore.errorModal?.message}</Text>
            <View style={styles.modalActions}>
              <TouchableOpacity onPress={() => writeStore.hideError()}>
                <Text style={styles.modalCancel}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => { writeStore.hideError(); writeStore.retryLastOperation(); }}>
                <Text style={styles.modalOk}>重试</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      <WelcomePanel />
      <VolumeBar />

      <SelectionToolbar
        visible={isToolbarVisible}
        anchorY={anchorY}
        selectedText={storeSelectedText}
        onReplaceSelected={replaceSelected}
        onAppendToSelected={appendToSelected}
        onRequestClose={() => releaseSelectionHold()}
      />

      {writeStore.overlayLoading ? (
        <View style={styles.overlayMask}>
          <ActivityIndicator size="large" color={colors.novelBackground} />
        </View>
      ) : null}
    </View>
  );
};

export default WritePage;


