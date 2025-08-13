import React from 'react';
import { View, TouchableOpacity, Text } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createCategoryPageStyles } from '../styles/CategoryPageStyles';
import { SexTab } from '../store/categoryStore';

interface TopTabsProps {
    tab: SexTab;
    onChange: (tab: SexTab) => void;
}

export const TopTabs: React.FC<TopTabsProps> = ({ tab, onChange }) => {
    const colors = useNovelColors();
    const styles = createCategoryPageStyles(colors);
    const renderBtn = (key: SexTab, label: string) => (
        <TouchableOpacity onPress={() => onChange(key)}>
            <Text style={[styles.topTabText, tab === key && styles.topTabActive]}>{label}</Text>
        </TouchableOpacity>
    );
    return (
        <View style={styles.topTabs}>
            {renderBtn('male', '男生')}
            {renderBtn('female', '女生')}
        </View>
    );
};


