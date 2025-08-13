import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../utils/theme/dimensions';
import { NovelColors } from '../../../utils/theme/colors';

export const createCategoryPageStyles = (colors: NovelColors) =>
    StyleSheet.create({
        container: {
            flex: 1,
            backgroundColor: colors.novelSecondaryBackground,
        },

        topTabs: {
            flexDirection: 'row',
            alignItems: 'center',
            justifyContent: 'center',
            paddingVertical: wp(14),
            gap: wp(50),
        },

        topTabText: {
            fontSize: fp(16),
            color: colors.novelTextGray,
            fontWeight: '500',
        },

        topTabActive: {
            color: colors.novelText,
            fontWeight: '600',
            fontSize: fp(18),
        },

        body: {
            flex: 1,
            flexDirection: 'row',
        },

        sidebar: {
            width: wp(100),
            flexShrink: 0,
            flexGrow: 0,
            backgroundColor: colors.novelSecondaryBackground,
            borderRightColor: colors.novelDivider,
        },

        sidebarItem: {
            paddingVertical: wp(18),
            paddingHorizontal: wp(12),
        },

        sidebarItemActive: {
            fontWeight: '600',
            color: colors.novelMain,
        },

        sidebarText: {
            fontSize: fp(14),
            color: colors.novelText,
            textAlign: 'center',
        },

        list: {
            flex: 1,
            borderTopLeftRadius: sp(12),
            backgroundColor: colors.novelBackground,
        },

        gridContainer: {
            paddingHorizontal: wp(12),
            paddingVertical: wp(12),
            backgroundColor: colors.novelBackground,
        },

        gridItem: {
            flex: 1,
            margin: wp(6),
            backgroundColor: colors.novelBackground,
        },

        card: {
            borderRadius: sp(8),
            overflow: 'hidden',
        },

        cover: {
            width: '100%',
            aspectRatio: 0.75,
            backgroundColor: colors.novelDivider,
            borderRadius: sp(8),
        },

        titleBox: {
            paddingVertical: wp(6),
        },

        title: {
            fontSize: fp(12),
            color: colors.novelText,
            fontWeight: '600',
            textAlign: 'center',
        },

        // BookGrid inline styles moved here
        gridItemWrapper: {
            padding: wp(8),
        },

        loadingBox: {
            paddingVertical: wp(24),
        },

        loadingContainer: {
            flex: 1,
            flexDirection: 'row',
            justifyContent: 'center',
            alignItems: 'center',
        },

        loadingText: {
            textAlign: 'center',
            opacity: 0.6,
        },

        columnWrapperStart: {
            justifyContent: 'flex-start',
            flexWrap: 'wrap',
        },

        listFooterSpacer: {
            paddingBottom: wp(12),
        },

        footerCenter: {
            alignItems: 'center',
        },

        footerRow: {
            flexDirection: 'row',
            alignItems: 'center',
        },

        endLine: {
            width: wp(30),
            height: wp(1),
            backgroundColor: colors.novelDivider,
            marginHorizontal: wp(6),
        },

        endText: {
            fontSize: fp(12),
            color: colors.novelTextGray,
        },
    });
