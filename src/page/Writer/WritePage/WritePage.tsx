import React, { useEffect } from 'react';
import { View, ScrollView, Text, TouchableOpacity } from 'react-native';

const RN: any = require('react-native');
const { TextInput, Modal, ActivityIndicator, Keyboard } = RN;
import { createWritePageStyles } from './styles/WritePageStyles';
import { useNovelColors } from '../../../utils/theme/colors';
import { useWriteStore } from './store/writeStore';
import { TopBar } from './components/TopBar';
import { WelcomePanel } from './components/WelcomePanel';
import { VolumeBar } from './components/VolumeBar';
import { useWriteActions } from './hooks/useWriteActions';
import { SelectionToolbar } from './components/SelectionToolbar';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import { subscribeWritePageSelectionMenuAction } from '../../../utils/runtime/eventHub';

const WritePage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);

  const { title, content, setTitle, setContent, publish, undo, redo, goAI, goBack } = useWriteActions();
  const writeStore = useWriteStore();

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      goBack();
      return true;
    });
  }, [goBack]);

  // 简易选择监听：以最后一次输入焦点为锚点，真实实现需自定义可编辑组件
  const [anchorY] = React.useState(120);

  // 从全局写作 store 读选区与可见性
  const isToolbarVisible = useWriteStore(s => s.isToolbarVisible);
  const storeSelectedText = useWriteStore(s => s.selectedText);
  const updateSelection = useWriteStore(s => s.updateSelection);
  const releaseSelectionHold = useWriteStore(s => s.releaseSelectionHold);
  const focusRequestNonce = useWriteStore(s => s.focusRequestNonce);
  const suppressKeyboard = useWriteStore(s => s.suppressKeyboard);

  const contentRef = React.useRef<any>(null);
  const [paramInput, setParamInput] = React.useState('');

  // 无键盘聚焦：让输入框获得焦点以显示选择手柄，同时尽量保持键盘隐藏
  useEffect(() => {
    // 先聚焦，再立刻尝试关闭键盘（Android + iOS 兜底）
    try { contentRef.current?.focus?.(); } catch (_e) { }
    setTimeout(() => { try { Keyboard?.dismiss?.(); } catch (_e) { } }, 0);
  }, [focusRequestNonce]);

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

  // Attach native selection menu on Android for this TextInput only
  useEffect(() => {
    if (RN.Platform?.OS !== 'android') { return; }
    const node = RN.findNodeHandle?.(contentRef.current);
    if (node != null) {
      NavigationBridge.attachSelectionMenu?.(node as number);
    }
    const cleanupSelectionSubscription = subscribeWritePageSelectionMenuAction((evt: any) => {
      const action = evt?.action as 'polish' | 'expand' | 'condense' | 'continue' | undefined;
      const selected = evt?.selectedText as string | undefined;
      if (!action || !selected) { return; }
      // 确保 store 中有选区信息
      updateSelection(selected, evt?.start ?? 0, evt?.end ?? 0);
      try { Keyboard?.dismiss?.(); } catch (_e) {}
      requestAnimationFrame(() => {
        if (action === 'polish') { writeStore.polishSelected(); return; }
        if (action === 'expand') { writeStore.showParamModal?.('expand', '请输入扩写比例（百分比），如 150 表示约 150%'); return; }
        if (action === 'condense') { writeStore.showParamModal?.('condense', '请输入缩写比例（分母），如 2 表示约 1/2'); return; }
        if (action === 'continue') { writeStore.showParamModal?.('continue', '请输入续写目标字数，如 200 表示约 200 字'); return; }
      });
    });
    return cleanupSelectionSubscription;
  }, [contentRef, updateSelection, writeStore]);

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
    <View
      style={styles.container}
    >
      <TopBar onBack={goBack} onUndo={undo} onRedo={redo} onAI={goAI} onPublish={publish} />

      <ScrollView
        style={styles.editor}
        keyboardShouldPersistTaps="handled"
        automaticallyAdjustKeyboardInsets={false}
        scrollEnabled={!isToolbarVisible}
        scrollEventThrottle={16}
      >
        <TextInput
          style={styles.titleInput}
          placeholder="请输入标题"
          placeholderTextColor={colors.novelTextGray}
          value={title}
          onChangeText={setTitle}
          selectionColor={colors.novelMain}
          cursorColor={colors.novelMain}
        />
        <TextInput
          style={styles.contentInput}
          placeholder="请输入正文"
          placeholderTextColor={colors.novelTextGray}
          value={content}
          onChangeText={setContent}
          onSelectionChange={onSelectionChange}
          selectionColor={colors.novelMain}
          cursorColor={colors.novelMain}
          // 为了保留系统长按手柄与粘贴菜单，暂不隐藏
          multiline
          textAlignVertical="top"
          ref={contentRef}
          showSoftInputOnFocus={!suppressKeyboard}
          // 当收到 focus 请求时，让输入框获得焦点，但不强制弹出键盘（iOS/Android 表现略异，避免调用 focus() 时显式唤起键盘）
          // 这里通过 key 变更触发内部更新后保持 selection，不主动调用 focus()
          key={`content-${focusRequestNonce}`}
        />
      </ScrollView>

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
        visible={RN.Platform?.OS !== 'android' && isToolbarVisible}
        anchorY={anchorY}
        selectedText={storeSelectedText}
        onReplaceSelected={replaceSelected}
        onAppendToSelected={appendToSelected}
        onRequestClose={() => releaseSelectionHold()}
      />

      {/* 参数输入（用于系统菜单触发的 AI 操作） */}
      <Modal
        visible={!!writeStore.modal?.visible}
        transparent
        animationType="fade"
        onRequestClose={() => { writeStore.hideParamModal(); setParamInput(''); }}
      >
        <View style={styles.overlayMask}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalHint}>{writeStore.modal?.hint}</Text>
            <TextInput
              value={paramInput}
              onChangeText={setParamInput}
              placeholder={writeStore.modal?.type === 'continue' ? '长度(字数)' : '比例'}
              placeholderTextColor={colors.novelTextGray}
              keyboardType="numeric"
              style={styles.modalInput}
            />
            <View style={styles.modalActions}>
              <TouchableOpacity onPress={() => { writeStore.hideParamModal(); setParamInput(''); }}>
                <Text style={styles.modalCancel}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => {
                const p = Number(paramInput);
                if (!p || Number.isNaN(p)) { return; }
                const type = writeStore.modal?.type;
                writeStore.hideParamModal();
                setParamInput('');
                if (!type) { return; }
                if (type === 'expand') { writeStore.expandSelected(p); }
                if (type === 'condense') { writeStore.condenseSelected(p); }
                if (type === 'continue') { writeStore.continueSelected(p); }
              }}>
                <Text style={styles.modalOk}>确定</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

      {writeStore.overlayLoading ? (
        <View style={styles.overlayMask}>
          <ActivityIndicator size="large" color={colors.novelBackground} />
        </View>
      ) : null}
    </View>
  );
};

export default WritePage;

