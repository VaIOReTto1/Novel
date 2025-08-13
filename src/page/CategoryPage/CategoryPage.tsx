import React, { useEffect } from 'react';
import { View, SafeAreaView } from 'react-native';
import { useNovelColors } from '../../utils/theme';
import { createCategoryPageStyles } from './styles/CategoryPageStyles';
import { TopTabs } from './components/TopTabs';
import { Sidebar } from './components/Sidebar';
import { BookGrid } from './components/BookGrid';
import { useCategoryStore } from './store/categoryStore';

export const CategoryPage: React.FC = () => {
    const colors = useNovelColors();
    const styles = createCategoryPageStyles(colors);

    const {
        tab,
        categories,
        activeCategory,
        books,
        pageNum,
        hasMore,
        loading,
        setTab,
        setActiveCategory,
        loadCategories,
        loadBooks,
        resetAndLoad,
    } = useCategoryStore();

    // 初始化/切换性别
    useEffect(() => {
        if (tab === 'male') {
            // 仅加载分类，等待 activeCategory 设置后再触发列表加载
            loadCategories();
        } else {
            // 女生频道直接加载列表
            resetAndLoad();
        }
    }, [loadCategories, resetAndLoad, tab]);

    // 切换分类
    useEffect(() => {
        if (tab === 'male') {
            resetAndLoad();
        }
    }, [activeCategory, resetAndLoad, tab]);

    return (
        <SafeAreaView style={styles.container}>
            <TopTabs tab={tab} onChange={setTab} />
            {tab === 'male' ? (
                <View style={styles.body}>
                    <Sidebar items={categories} activeId={activeCategory} onSelect={(id) => setActiveCategory(id)} />
                    <BookGrid
                        data={books}
                        columns={2}
                        onEndReached={() => hasMore && !loading && loadBooks()}
                        isLoading={loading}
                        hasMore={hasMore}
                        refreshing={loading && pageNum === 1}
                        onRefresh={resetAndLoad}
                    />
                </View>
            ) : (
                <BookGrid
                    data={books}
                    columns={3}
                    onEndReached={() => hasMore && !loading && loadBooks()}
                    isLoading={loading}
                    hasMore={hasMore}
                    refreshing={loading && pageNum === 1}
                    onRefresh={resetAndLoad}
                />
            )}
        </SafeAreaView>
    );
};


