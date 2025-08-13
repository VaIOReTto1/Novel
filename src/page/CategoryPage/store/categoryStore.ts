import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { NativeModules } from 'react-native';

const { NavigationBridge } = NativeModules as any;

export type SexTab = 'male' | 'female';

export interface CategoryItem {
    id: number;
    name: string;
}
export interface BookItem {
    id: number;
    bookName: string;
    authorName?: string;
    picUrl?: string;
}

interface CategoryState {
    tab: SexTab;
    categories: CategoryItem[];
    activeCategory: number | null;
    books: BookItem[];
    pageNum: number;
    hasMore: boolean;
    loading: boolean;
    // actions
    setTab: (tab: SexTab) => void;
    setActiveCategory: (id: number | null) => void;
    loadCategories: () => Promise<void>;
    loadBooks: (reset?: boolean) => Promise<void>;
    resetAndLoad: () => Promise<void>;
}

export const useCategoryStore = create<CategoryState>()(
    immer((set, get) => ({
        tab: 'male',
        categories: [],
        activeCategory: null,
        books: [],
        pageNum: 1,
        hasMore: true,
        loading: false,

        setTab: tab =>
            set(s => {
                s.tab = tab;
            }),
        setActiveCategory: id =>
            set(s => {
                s.activeCategory = id;
            }),

        loadCategories: async () => {
            const { tab } = get();
            if (tab !== 'male') return;
            try {
                const res = await NavigationBridge.getBookCategories(0);
                const list = (res?.list || []) as any[];
                set(s => {
                    s.categories = list.map(x => ({ id: x.id, name: x.name }));
                    if (!s.activeCategory && s.categories.length > 0)
                        s.activeCategory = s.categories[0].id;
                });
            } catch (e) {
                console.warn('[categoryStore] loadCategories failed', e);
            }
        },

        loadBooks: async (reset = false) => {
            const { tab, activeCategory, pageNum, loading } = get();
            if (loading) return;
            const workDirection = tab === 'male' ? 0 : 1;
            try {
                set(s => {
                    s.loading = true;
                });
                const nextPage = reset ? 1 : pageNum;
                const res = await NavigationBridge.searchBooks(
                    workDirection,
                    tab === 'male' ? activeCategory || 0 : 0,
                    nextPage,
                    20,
                );
                const list = (res?.list || []) as any[];
                const pages = res?.pages ? Number(res.pages) : 0;
                set(s => {
                    s.pageNum = nextPage + 1;
                    s.hasMore = list.length > 0 && (pages === 0 || nextPage < pages);
                    s.books = reset ? list : [...s.books, ...list];
                });
            } catch (e) {
                console.warn('[categoryStore] loadBooks failed', e);
            } finally {
                set(s => {
                    s.loading = false;
                });
            }
        },

        resetAndLoad: async () => {
            set(s => {
                s.books = [];
                s.pageNum = 1;
                s.hasMore = true;
            });
            await get().loadBooks(true);
        },
    })),
);
