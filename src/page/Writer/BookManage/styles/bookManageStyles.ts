import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';

export const createBookManageStyles = (colors: NovelColors) => {
  const ui = createNovelDesignUI(colors);

  return StyleSheet.create({
    container: { flex: 1, backgroundColor: ui.color.bg.canvas },

    header: {
      flexDirection: 'row',
      alignItems: 'center',
      paddingHorizontal: ui.metrics.pageGutter,
      paddingVertical: ui.spacePx('150'),
      backgroundColor: ui.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: ui.color.border.subtle,
    },
    headerAction: {
      width: wp(36),
      height: wp(36),
      borderRadius: sp(18),
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: ui.color.bg.elevated,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    back: { fontSize: fp(28), color: ui.color.text.primary },
    title: { marginLeft: wp(12), fontSize: fp(16), color: ui.color.text.primary, fontWeight: '600' },

    banner: { paddingHorizontal: wp(16), paddingBottom: wp(12) },
    bannerCard: {
      flexDirection: 'row',
      alignItems: 'center',
      backgroundColor: ui.color.bg.surface,
      borderRadius: ui.metrics.cardRadius,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
      padding: ui.metrics.cardPadding,
      marginTop: ui.metrics.blockGap,
    },
    cover: {
      width: ui.metrics.writerCover.width,
      height: ui.metrics.writerCover.height,
      borderRadius: sp(8),
      backgroundColor: ui.color.bg.elevated,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    bannerInfo: { marginLeft: wp(12), flex: 1 },
    bookTitle: { fontSize: fp(18), color: ui.color.text.primary, fontWeight: '600' },
    author: { marginTop: wp(6), color: ui.color.text.secondary },
    status: { marginTop: wp(6), color: ui.color.brand.primary },

    draft: {
      marginHorizontal: wp(16),
      backgroundColor: ui.color.bg.elevated,
      padding: wp(12),
      borderRadius: ui.metrics.cardRadius,
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    draftText: { color: ui.color.text.primary },
    draftAction: { color: ui.color.brand.primary, fontWeight: '600' },

    sectionHeaderRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: wp(16), paddingTop: wp(16) },
    sectionTitle: { color: ui.color.text.primary, fontWeight: '600', fontSize: fp(16) },
    chapterList: { paddingHorizontal: wp(16), gap: wp(10) },
    chapterCard: {
      backgroundColor: ui.color.bg.surface,
      borderRadius: ui.metrics.cardRadius,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
      padding: ui.metrics.compactCardPadding,
    },
    chapterTitle: { color: ui.color.text.primary, fontSize: fp(15), fontWeight: '600' },
    chapterMeta: { color: ui.color.text.secondary, marginTop: wp(4) },
    volume: { paddingHorizontal: wp(16), paddingVertical: wp(12), alignItems: 'flex-end' },
    volumeText: { color: ui.color.text.secondary },

    emptyWrap: { flexGrow: 1, justifyContent: 'center', alignItems: 'center' },
    emptyCard: {
      backgroundColor: ui.color.bg.surface,
      borderRadius: ui.metrics.cardRadius,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
      paddingHorizontal: ui.metrics.cardPadding,
      paddingVertical: ui.metrics.compactCardPadding,
    },
    emptyText: { color: ui.color.text.secondary, marginTop: wp(8) },

    footer: {
      padding: wp(16),
      backgroundColor: ui.color.bg.surface,
      borderTopWidth: 1,
      borderTopColor: ui.color.border.subtle,
    },
    createBtn: { backgroundColor: ui.color.brand.primary, borderRadius: sp(24), alignItems: 'center', paddingVertical: wp(12) },
    createText: { color: ui.color.text.inverse, fontWeight: '600' },
    tip: { textAlign: 'center', marginTop: wp(8), color: ui.color.text.secondary },
  });
};
