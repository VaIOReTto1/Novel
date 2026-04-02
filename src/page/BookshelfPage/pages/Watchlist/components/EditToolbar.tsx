import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { useNovelColors } from '../../../../../utils/theme';
import { createWatchlistPageStyles } from '../styles/WatchlistPageStyles';

interface EditToolbarProps {
  selectedCount: number;
  totalCount: number;
  onSelectAll: () => void;
  onDelete: () => void;
  isAllSelected: boolean;
}

export const EditToolbar: React.FC<EditToolbarProps> = ({
  selectedCount,
  totalCount,
  onSelectAll,
  onDelete,
  isAllSelected,
}) => {
  const colors = useNovelColors();
  const styles = createWatchlistPageStyles(colors);

  return (
    <View style={styles.editToolbar}>
      <TouchableOpacity
        style={styles.editToolbarButton}
        onPress={onSelectAll}
        activeOpacity={0.7}>
        <Text style={styles.editToolbarButtonText}>
          {isAllSelected ? '取消全选' : '全选'}
        </Text>
      </TouchableOpacity>

      <View style={styles.editToolbarCenter}>
        <Text style={styles.editToolbarSelectedText}>
          {`已选择 ${selectedCount}/${totalCount} 项`}
        </Text>
      </View>

      <TouchableOpacity
        style={[
          styles.editToolbarButton,
          styles.deleteButton,
          selectedCount === 0 && styles.disabledButton,
        ]}
        onPress={onDelete}
        disabled={selectedCount === 0}
        activeOpacity={0.7}>
        <Text
          style={[
            styles.editToolbarButtonText,
            styles.deleteButtonText,
            selectedCount === 0 && styles.disabledButtonText,
          ]}>
          删除
        </Text>
      </TouchableOpacity>
    </View>
  );
};
