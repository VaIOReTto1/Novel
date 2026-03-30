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
import {
  appendToSelectedText,
  createWritePageHandlers,
  replaceSelectedText,
} from './domain/writePageModel';

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

  const [anchorY] = React.useState(120);
  const isToolbarVisible = useWriteStore(s => s.isToolbarVisible);
  const storeSelectedText = useWriteStore(s => s.selectedText);
  const updateSelection = useWriteStore(s => s.updateSelection);
  const releaseSelectionHold = useWriteStore(s => s.releaseSelectionHold);
  const focusRequestNonce = useWriteStore(s => s.focusRequestNonce);
  const suppressKeyboard = useWriteStore(s => s.suppressKeyboard);

  const contentRef = React.useRef<any>(null);
  const [paramInput, setParamInput] = React.useState('');
  const handlers = React.useMemo(
    () =>
      createWritePageHandlers({
        updateSelection,
        dismissKeyboard: () => {
          try {
            Keyboard?.dismiss?.();
          } catch {}
        },
        onPolish: () => writeStore.polishSelected(),
        onShowParamModal: (type, hint) => writeStore.showParamModal?.(type, hint),
      }),
    [updateSelection, writeStore],
  );

  useEffect(() => {
    try {
      contentRef.current?.focus?.();
    } catch {}
    setTimeout(() => {
      try {
        Keyboard?.dismiss?.();
      } catch {}
    }, 0);
  }, [focusRequestNonce]);

  const onSelectionChange = React.useCallback((e: any) => {
    const selection = e?.nativeEvent?.selection;
    if (!selection) {
      return;
    }
    const start = Math.min(selection.start, selection.end);
    const end = Math.max(selection.start, selection.end);
    const text = content?.slice(start, end) ?? '';
    if (end > start) {
      updateSelection(text, start, end);
    }
  }, [content, updateSelection]);

  useEffect(() => {
    if (RN.Platform?.OS !== 'android') {
      return;
    }
    const node = RN.findNodeHandle?.(contentRef.current);
    if (node != null) {
      NavigationBridge.attachSelectionMenu?.(node as number);
    }
    const cleanupSelectionSubscription = subscribeWritePageSelectionMenuAction((event: any) => {
      requestAnimationFrame(() => {
        handlers.handleSelectionMenuAction(event);
      });
    });
    return cleanupSelectionSubscription;
  }, [handlers]);

  const replaceSelected = (newText: string) => {
    setContent(replaceSelectedText(content, storeSelectedText, newText));
  };

  const appendToSelected = (newText: string) => {
    setContent(appendToSelectedText(content, storeSelectedText, newText));
  };

  return (
    <View style={styles.container}>
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
          multiline
          textAlignVertical="top"
          ref={contentRef}
          showSoftInputOnFocus={!suppressKeyboard}
          key={`content-${focusRequestNonce}`}
        />
      </ScrollView>

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
                const param = Number(paramInput);
                if (!param || Number.isNaN(param)) {
                  return;
                }
                const type = writeStore.modal?.type;
                writeStore.hideParamModal();
                setParamInput('');
                if (!type) {
                  return;
                }
                if (type === 'expand') {
                  writeStore.expandSelected(param);
                }
                if (type === 'condense') {
                  writeStore.condenseSelected(param);
                }
                if (type === 'continue') {
                  writeStore.continueSelected(param);
                }
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
