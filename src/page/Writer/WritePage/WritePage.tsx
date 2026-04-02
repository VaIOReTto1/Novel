import React, { useEffect } from 'react';
import { ScrollView, Text, TouchableOpacity, View } from 'react-native';

const RN: any = require('react-native');
const { ActivityIndicator, Keyboard, Modal, TextInput } = RN;

import NavigationBridge from '../../../utils/bridge/NavigationBridge';
import { registerHardwareBackHandler } from '../../../utils/runtime/backNavigation';
import { subscribeWritePageSelectionMenuAction } from '../../../utils/runtime/eventHub';
import { useNovelColors } from '../../../utils/theme/colors';
import {
  appendToSelectedText,
  createWritePageHandlers,
  getWritePageModalPlaceholder,
  getWritePageSelection,
  replaceSelectedText,
  runWritePageFocusSync,
} from './domain/writePageModel';
import { useWriteActions } from './hooks/useWriteActions';
import { useWriteStore } from './store/writeStore';
import { createWritePageStyles } from './styles/WritePageStyles';
import { SelectionToolbar } from './components/SelectionToolbar';
import { TopBar } from './components/TopBar';
import { VolumeBar } from './components/VolumeBar';
import { WelcomePanel } from './components/WelcomePanel';

const WritePage: React.FC = () => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);
  const { title, content, setTitle, setContent, publish, undo, redo, goAI, goBack } =
    useWriteActions();
  const writeStore = useWriteStore();

  useEffect(() => {
    return registerHardwareBackHandler(() => {
      goBack();
      return true;
    });
  }, [goBack]);

  const [anchorY] = React.useState(120);
  const isToolbarVisible = useWriteStore((s) => s.isToolbarVisible);
  const storeSelectedText = useWriteStore((s) => s.selectedText);
  const updateSelection = useWriteStore((s) => s.updateSelection);
  const releaseSelectionHold = useWriteStore((s) => s.releaseSelectionHold);
  const focusRequestNonce = useWriteStore((s) => s.focusRequestNonce);
  const suppressKeyboard = useWriteStore((s) => s.suppressKeyboard);

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
        hideParamModal: () => writeStore.hideParamModal(),
        setParamInput,
        expandSelected: (value) => writeStore.expandSelected(value),
        condenseSelected: (value) => writeStore.condenseSelected(value),
        continueSelected: (value) => writeStore.continueSelected(value),
      }),
    [updateSelection, writeStore],
  );

  useEffect(() => {
    runWritePageFocusSync({
      focus: () => {
        try {
          contentRef.current?.focus?.();
        } catch {}
      },
      dismissKeyboard: () => {
        setTimeout(() => {
          try {
            Keyboard?.dismiss?.();
          } catch {}
        }, 0);
      },
    });
  }, [focusRequestNonce]);

  const onSelectionChange = React.useCallback(
    (event: any) => {
      const selection = getWritePageSelection(content, event);
      if (!selection) {
        return;
      }
      updateSelection(selection.selectedText, selection.start, selection.end);
    },
    [content, updateSelection],
  );

  useEffect(() => {
    if (RN.Platform?.OS !== 'android') {
      return;
    }
    const node = RN.findNodeHandle?.(contentRef.current);
    if (node != null) {
      NavigationBridge.attachSelectionMenu?.(node as number);
    }
    const cleanupSelectionSubscription = subscribeWritePageSelectionMenuAction(
      (event: any) => {
        requestAnimationFrame(() => {
          handlers.handleSelectionMenuAction(event);
        });
      },
    );
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
      <TopBar
        onBack={goBack}
        onUndo={undo}
        onRedo={redo}
        onAI={goAI}
        onPublish={publish}
      />

      <ScrollView
        style={styles.editor}
        contentContainerStyle={styles.editorScrollContent}
        keyboardShouldPersistTaps="handled"
        automaticallyAdjustKeyboardInsets={false}
        scrollEnabled={!isToolbarVisible}
        scrollEventThrottle={16}>
        <WelcomePanel />

        <View style={styles.editorSheet}>
          <VolumeBar />
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
            placeholder="开始整理这一章的内容..."
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
        </View>
      </ScrollView>

      <Modal
        visible={!!writeStore.errorModal?.visible}
        transparent
        animationType="fade"
        onRequestClose={() => writeStore.hideError()}>
        <View style={styles.overlayMask}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalHint}>{writeStore.errorModal?.message}</Text>
            <View style={styles.modalActions}>
              <TouchableOpacity onPress={() => writeStore.hideError()}>
                <Text style={styles.modalCancel}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => {
                  writeStore.hideError();
                  writeStore.retryLastOperation();
                }}>
                <Text style={styles.modalOk}>重试</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>

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
        onRequestClose={() => {
          writeStore.hideParamModal();
          setParamInput('');
        }}>
        <View style={styles.overlayMask}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalHint}>{writeStore.modal?.hint}</Text>
            <TextInput
              value={paramInput}
              onChangeText={setParamInput}
              placeholder={getWritePageModalPlaceholder(writeStore.modal?.type)}
              placeholderTextColor={colors.novelTextGray}
              keyboardType="numeric"
              style={styles.modalInput}
            />
            <View style={styles.modalActions}>
              <TouchableOpacity
                onPress={() => {
                  writeStore.hideParamModal();
                  setParamInput('');
                }}>
                <Text style={styles.modalCancel}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() =>
                  handlers.handleConfirmParamModal(
                    paramInput,
                    writeStore.modal?.type,
                  )
                }>
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
