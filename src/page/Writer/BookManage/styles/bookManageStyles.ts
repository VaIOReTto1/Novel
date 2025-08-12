import { StyleSheet } from 'react-native';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';

export const createBookManageStyles = (colors: NovelColors) => StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.novelBackground },

  header: { flexDirection: 'row', alignItems: 'center', padding: wp(16) },
  back: { fontSize: fp(28), color: colors.novelText },
  title: { marginLeft: wp(12), fontSize: fp(16), color: colors.novelText, fontWeight: '600' },

  banner: { paddingHorizontal: wp(16), paddingBottom: wp(12) },
  bannerRow: { flexDirection: 'row', alignItems: 'center' },
  cover: { width: wp(100), height: wp(130), borderRadius: sp(8), backgroundColor: colors.novelSecondaryBackground },
  bannerInfo: { marginLeft: wp(12), flex: 1 },
  bookTitle: { fontSize: fp(18), color: colors.novelText, fontWeight: '600' },
  author: { marginTop: wp(6), color: colors.novelTextGray },
  status: { marginTop: wp(6), color: colors.novelTextGray },

  draft: {
    marginHorizontal: wp(16),
    backgroundColor: colors.novelSecondaryBackground,
    padding: wp(12),
    borderRadius: sp(10),
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  draftText: { color: colors.novelText },
  draftAction: { color: colors.novelMain },

  sectionHeaderRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingHorizontal: wp(16), paddingTop: wp(16) },
  sectionTitle: { color: colors.novelText, fontWeight: '600', fontSize: fp(16) },
  volume: { paddingHorizontal: wp(16), paddingVertical: wp(12), alignItems: 'flex-end' },
  volumeText: { color: colors.novelTextGray },

  emptyWrap: { flexGrow: 1, justifyContent: 'center', alignItems: 'center' },
  emptyText: { color: colors.novelTextGray, marginTop: wp(8) },

  footer: { padding: wp(16) },
  createBtn: { backgroundColor: colors.novelMain, borderRadius: sp(24), alignItems: 'center', paddingVertical: wp(12) },
  createText: { color: colors.novelBackground, fontWeight: '600' },
  tip: { textAlign: 'center', marginTop: wp(8), color: colors.novelTextGray },
});


