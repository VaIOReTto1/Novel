import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import { createAIStyles } from '../styles/aiStyles';

interface Props {
	onClose: () => void;
	onSelect: (category: string) => void;
  selected?: string;
}

const OPTIONS: Array<{ key: string; label: string }> = [
	{ key: '女频悬疑', label: '女频悬疑' },
	{ key: '东方仙侠', label: '东方仙侠' },
	{ key: '科幻末世', label: '科幻末世' },
	{ key: '女频衍生', label: '女频衍生' },
	{ key: '西方奇幻', label: '西方奇幻' },
	{ key: '古风世情', label: '古风世情' },
	{ key: '男频衍生', label: '男频衍生' },
	{ key: '民国言情', label: '民国言情' },
];

export const IdeaSelector: React.FC<Props> = ({ onClose, onSelect, selected }) => {
	const colors = useNovelColors();
	const styles = createAIStyles(colors);
	return (
        <View style={styles.ideaFloatWrap} pointerEvents="box-none">
            <View style={styles.ideaFloatPanel}>
                <View style={styles.ideaSelectorHeader}>
                    <Text style={styles.ideaSelectorTitle}>选择灵感分类</Text>
                    <TouchableOpacity onPress={onClose}><Text style={styles.thinkingCaret}>✕</Text></TouchableOpacity>
                </View>
                <View style={styles.ideaGridWrap}>
                    {OPTIONS.map(opt => {
                        const active = selected === opt.key;
                        return (
                            <TouchableOpacity
                                key={opt.key}
                                style={[styles.ideaGridItem, active && { backgroundColor: colors.novelMain + '20' }]}
                                onPress={() => onSelect(opt.key)}
                            >
                                <Text style={[styles.ideaGridText, active && { color: colors.novelMain, fontWeight: '800' }]}>{opt.label}</Text>
                            </TouchableOpacity>
                        );
                    })}
                </View>
            </View>
        </View>
	);
};


