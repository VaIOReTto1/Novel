import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { HistoryEditToolbarProps } from '../types';

export const EditToolbar: React.FC<HistoryEditToolbarProps> = React.memo(({
  styles,
  selectedCount,
  onSelectAll,
  onRemove,
  onCancel,
  visible,
}) => {
  if (!visible) {return null;}

  return (
    <View style={styles.editToolbar}>
      <View style={styles.editToolbarLeft}>
        <TouchableOpacity
          style={styles.selectAllButton}
          onPress={onSelectAll}
          activeOpacity={0.7}
        >
          <View style={[
            styles.editCheckbox,
            selectedCount > 0 && styles.editCheckboxSelected,
          ]}>
            {selectedCount > 0 && (
              <Text style={styles.editCheckIcon}>✓</Text>
            )}
          </View>
          <Text style={styles.selectAllText}>
            全选 {selectedCount > 0 && `(${selectedCount})`}
          </Text>
        </TouchableOpacity>
      </View>

      <View style={styles.editToolbarRight}>
        <TouchableOpacity
          style={[styles.editActionButton, styles.removeButton]}
          onPress={onRemove}
          disabled={selectedCount === 0}
          activeOpacity={0.7}
        >
          <Text style={[
            styles.editActionText,
            selectedCount === 0 && { opacity: 0.5 },
          ]}>
            删除 {selectedCount > 0 && `(${selectedCount})`}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={[styles.editActionButton, styles.cancelButton]}
          onPress={onCancel}
          activeOpacity={0.7}
        >
          <Text style={styles.editActionText}>取消</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
});
