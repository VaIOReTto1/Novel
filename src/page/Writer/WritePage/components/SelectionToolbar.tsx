import React, { useMemo, useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const RN: any = require('react-native');
const { Modal, TextInput } = RN;
import { useNovelColors } from '../../../../utils/theme/colors';
import { wp, sp } from '../../../../utils/theme/dimensions';
import { createWritePageStyles } from '../styles/WritePageStyles';
import { useWriteStore } from '../store/writeStore';

interface SelectionToolbarProps {
  visible: boolean;
  anchorY: number; // 位置（简单版：仅 Y）
  selectedText: string;
  onReplaceSelected: (text: string) => void;
  onAppendToSelected: (text: string) => void;
  onRequestClose: () => void;
}

export const SelectionToolbar: React.FC<SelectionToolbarProps> = ({ visible, anchorY, selectedText: _selectedText, onReplaceSelected: _onReplaceSelected, onAppendToSelected: _onAppendToSelected, onRequestClose }) => {
  const colors = useNovelColors();
  const styles = createWritePageStyles(colors);
  const { polishSelected, expandSelected, condenseSelected, continueSelected, setOverlay, selectAll, copySelected, cutSelected, pasteAtSelection } = useWriteStore();
  const [promptVisible, setPromptVisible] = useState<null | { type: 'expand' | 'condense' | 'continue' }>(null);
  const [param, setParam] = useState('');

  const containerStyle = useMemo(() => ({
    position: 'absolute' as const,
    top: Math.max(0, anchorY - wp(48)),
    left: wp(16),
    right: wp(16),
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    padding: wp(8),
    flexDirection: 'row' as const,
    justifyContent: 'space-around' as const,
    elevation: 8,
  }), [anchorY, colors]);

  const run = async (fn: () => Promise<void>) => {
    try { setOverlay(true); await fn(); setOverlay(false); onRequestClose(); } catch (e) { setOverlay(false); /* 错误弹窗在页面层统一处理，这里静默 */ }
  };

  if (!visible) { return null; }

  return (
    <View pointerEvents="box-none" style={styles.selectionToolbarBackdrop}>
      {/* 遮罩与loading */}
      {/* 遮罩加载在页面层，由 store 控制，这里不重复渲染 */}

      {/* 工具条 */}
      <View style={containerStyle}>
        <TouchableOpacity onPress={() => run(() => polishSelected())}>
          <Text style={styles.toolbarBtnText}>润色</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setPromptVisible({ type: 'expand' })}>
          <Text style={styles.toolbarBtnText}>扩写</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setPromptVisible({ type: 'condense' })}>
          <Text style={styles.toolbarBtnText}>缩写</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => setPromptVisible({ type: 'continue' })}>
          <Text style={styles.toolbarBtnText}>续写</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => { selectAll(); }}>
          <Text style={styles.toolbarBtnText}>全选</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => { copySelected(); }}>
          <Text style={styles.toolbarBtnText}>复制</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => { cutSelected(); }}>
          <Text style={styles.toolbarBtnText}>剪切</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={() => { pasteAtSelection(''); }}>
          <Text style={styles.toolbarBtnText}>粘贴</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={onRequestClose}>
          <Text style={styles.toolbarBtnTextSecondary}>关闭</Text>
        </TouchableOpacity>
      </View>

      {/* 参数输入弹窗 */}
      <Modal visible={!!promptVisible} transparent animationType="fade" onRequestClose={() => setPromptVisible(null)}>
        <View style={styles.overlayMask}>
          <View style={styles.modalContainer}>
            <Text style={styles.modalHint}>
              {promptVisible?.type === 'expand' && '请输入扩写比例（百分比），如 150 表示约 150%'}
              {promptVisible?.type === 'condense' && '请输入缩写比例（分母），如 2 表示约 1/2'}
              {promptVisible?.type === 'continue' && '请输入续写目标字数，如 200 表示约 200 字'}
            </Text>
            <TextInput
              value={param}
              onChangeText={setParam}
              placeholder={promptVisible?.type === 'continue' ? '长度(字数)' : '比例'}
              placeholderTextColor={colors.novelTextGray}
              keyboardType="numeric"
              style={styles.modalInput}
            />
            <View style={styles.modalActions}>
              <TouchableOpacity onPress={() => { setPromptVisible(null); setParam(''); }}>
                <Text style={styles.modalCancel}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity onPress={() => {
                const p = Number(param);
                if (!p || Number.isNaN(p)) { return; }
                const type = promptVisible?.type; // 先缓存，避免异步状态导致取不到
                setPromptVisible(null);
                setParam('');
                if (!type) { return; }
                if (type === 'expand') { run(() => expandSelected(p)); }
                if (type === 'condense') { run(() => condenseSelected(p)); }
                if (type === 'continue') { run(() => continueSelected(p)); }
              }}>
                <Text style={styles.modalOk}>确定</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
};


