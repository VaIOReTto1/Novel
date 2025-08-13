import React from 'react';
import { ScrollView, TouchableOpacity, Text } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createCategoryPageStyles } from '../styles/CategoryPageStyles';
import { CategoryItem } from '../store/categoryStore';

interface SidebarProps {
    items: CategoryItem[];
    activeId: number | null;
    onSelect: (id: number) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ items, activeId, onSelect }) => {
    const colors = useNovelColors();
    const styles = createCategoryPageStyles(colors);
    return (
        <ScrollView style={styles.sidebar}>
            {items.map((c) => (
                <TouchableOpacity
                    key={c.id}
                    onPress={() => onSelect(c.id)}
                    style={[styles.sidebarItem]}
                >
                    <Text style={[styles.sidebarText, activeId === c.id && styles.sidebarItemActive]}>{c.name}</Text>
                </TouchableOpacity>
            ))}
        </ScrollView>
    );
};
