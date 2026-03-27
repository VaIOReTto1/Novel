import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { createBookshelfPageStyles } from '../styles/BookshelfPageStyles';
import { useNovelColors } from '../../../../../utils/theme';

interface EditToolbarProps {
  isEditMode: boolean;
  selectedCount: number;
  totalCount: number;
  onEnterEdit: () => void;
  onExitEdit: () => void;
  onSelectAll: () => void;
  onDelete: () => void;
  onMove?: () => void;
}

export const EditToolbar: React.FC<EditToolbarProps> = ({
  isEditMode,
  selectedCount,
  totalCount,
  onEnterEdit,
  onExitEdit,
  onSelectAll,
  onDelete,
  onMove,
}) => {
  const colors = useNovelColors();
  const styles = createBookshelfPageStyles(colors);
  if (!isEditMode) {
    return (
      <View style={styles.toolbar}>
        <TouchableOpacity
          style={styles.toolbarButton}
          onPress={onEnterEdit}
          activeOpacity={0.7}
        >
          <Text style={styles.toolbarButtonText}>
            编辑
          </Text>
        </TouchableOpacity>
      </View>
    );
  }

  const isAllSelected = selectedCount === totalCount && totalCount > 0;
  const hasSelection = selectedCount > 0;

  return (
    <View style={styles.editToolbar}>
      {/* 左侧操作 */}
      <View style={styles.editToolbarLeft}>
        <TouchableOpacity
          style={styles.editToolbarButton}
          onPress={onExitEdit}
          activeOpacity={0.7}
        >
          <Text style={styles.editToolbarButtonText}>
            取消
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.editToolbarButton}
          onPress={onSelectAll}
          activeOpacity={0.7}
        >
          <Text style={styles.editToolbarButtonText}>
            {isAllSelected ? '取消全选' : '全选'}
          </Text>
        </TouchableOpacity>
      </View>

      {/* 中间选择计数 */}
      <View style={styles.editToolbarCenter}>
        <Text style={styles.selectionCount}>
          已选择 {selectedCount} 项
        </Text>
      </View>

      {/* 右侧操作 */}
      <View style={styles.editToolbarRight}>
        {onMove && (
          <TouchableOpacity
            style={[
              styles.editActionButton,
              !hasSelection && styles.disabledButton,
            ]}
            onPress={onMove}
            disabled={!hasSelection}
            activeOpacity={0.7}
          >
            <Text style={[
              styles.editActionButtonText,
              !hasSelection && styles.disabledButtonText,
            ]}>
              移动
            </Text>
          </TouchableOpacity>
        )}

        <TouchableOpacity
          style={[
            styles.editActionButton,
            styles.deleteButton,
            !hasSelection && styles.disabledButton,
          ]}
          onPress={onDelete}
          disabled={!hasSelection}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.editActionButtonText,
            styles.deleteButtonText,
            !hasSelection && styles.disabledButtonText,
          ]}>
            删除
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};
