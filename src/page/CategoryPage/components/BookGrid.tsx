import React from 'react';
import { View, Image, Text, TouchableOpacity } from 'react-native';
import { useNovelColors } from '../../../utils/theme';
import { createCategoryPageStyles } from '../styles/CategoryPageStyles';
import { BookItem } from '../store/categoryStore';
import NavigationBridge from '../../../utils/bridge/NavigationBridge';

interface BookGridProps {
    data: BookItem[];
    columns: number;
    onEndReached?: () => void;
    isLoading?: boolean;
    hasMore?: boolean;
    refreshing?: boolean;
    onRefresh?: () => void;
}

export const BookGrid: React.FC<BookGridProps> = ({ data, columns, onEndReached, isLoading, hasMore, refreshing, onRefresh }) => {
    const colors = useNovelColors();
    const styles = createCategoryPageStyles(colors);
    const FlatList: any = (require('react-native') as any).FlatList;
    const ActivityIndicator: any = (require('react-native') as any).ActivityIndicator;

    const handleBookPress = (item: BookItem) => {
        console.log('[CategoryPage] Book pressed:', item.bookName);
        NavigationBridge.navigateToReader(item.id.toString());
    };

    const renderItem = ({ item }: { item: BookItem }) => (
        <TouchableOpacity
            style={[{ width: `${100 / columns}%` }, styles.gridItemWrapper]}
            onPress={() => handleBookPress(item)}
            activeOpacity={0.7}
        >
            <View style={styles.card}>
                {item.picUrl ? (
                    <Image source={{ uri: item.picUrl }} style={styles.cover} resizeMode="contain" />
                ) : (
                    <View style={styles.cover} />
                )}
                <View style={styles.titleBox}>
                    <Text numberOfLines={2} style={styles.title}>{item.bookName}</Text>
                </View>
            </View>
        </TouchableOpacity>
    );
    if (isLoading && (!data || data.length === 0)) {
        return (
            <View style={[styles.gridContainer, styles.loadingBox, styles.loadingContainer]}>
                <ActivityIndicator color={colors.novelMain} />
                <Text style={[styles.title, styles.loadingText]}>加载中...</Text>
            </View>
        );
    }

    return (
        <FlatList
            data={data}
            keyExtractor={(i: BookItem) => String(i.id)}
            renderItem={renderItem}
            numColumns={columns}
            onEndReached={onEndReached}
            onEndReachedThreshold={0.2}
            contentContainerStyle={styles.gridContainer}
            columnWrapperStyle={styles.columnWrapperStart}
            ListFooterComponent={
                <View style={[styles.listFooterSpacer, styles.footerCenter]}>
                    {isLoading && data && data.length > 0 ? (
                        <Text style={[styles.title, styles.loadingText]}>加载中...</Text>
                    ) : hasMore === false ? (
                        <View style={styles.footerRow}>
                            <View style={styles.endLine} />
                            <Text style={styles.endText}>已加载全部</Text>
                            <View style={styles.endLine} />
                        </View>
                    ) : null}
                </View>
            }
            refreshing={!!refreshing}
            onRefresh={onRefresh}
            style={styles.list}
            showsVerticalScrollIndicator={false}
        />
    );
};


