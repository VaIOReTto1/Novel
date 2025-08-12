import { StyleSheet } from 'react-native';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp } from '../../../../utils/theme/dimensions';

export const createAIStyles = (colors: NovelColors) => StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.novelBackground },

  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(16),
    paddingVertical: wp(12),
  },
  headTitleRow: { alignItems: 'center' },
  headTitle: { fontSize: fp(16), color: colors.novelText, fontWeight: '600' },
  headBack: { fontSize: fp(28), color: colors.novelText },
  headEllipsis: { width: wp(28) },
  quota: { color: colors.novelTextGray, fontSize: fp(10) },

  list: { flex: 1, paddingHorizontal: wp(16) },
  bubble: {
    borderRadius: sp(10),
    padding: wp(12),
    marginVertical: wp(8),
    maxWidth: '86%',
  },
  bubbleAssistant: { backgroundColor: colors.novelSecondaryBackground },
  bubbleUser: { backgroundColor: colors.novelMain + '22', alignSelf: 'flex-end' },
  textAssistant: { color: colors.novelText },
  textUser: { color: colors.novelText },

  // 初始引导大气泡 + 卡片
  introBubble: {
    backgroundColor: '#FFEFF6',
    borderRadius: sp(12),
    padding: wp(14),
    marginVertical: wp(8),
  },
  introTitle: { color: colors.novelText, fontSize: fp(14), lineHeight: fp(20), marginBottom: wp(10) },
  introCards: { gap: wp(10) },
  introCard: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(12),
    paddingHorizontal: wp(12),
    paddingVertical: wp(10),
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 1,
  },
  introRefresh: {
    marginTop: wp(10),
    alignSelf: 'flex-start',
    width: wp(28),
    height: wp(28),
    borderRadius: wp(14),
    backgroundColor: colors.novelBackground,
    alignItems: 'center',
    justifyContent: 'center',
  },
  introRefreshText: { color: colors.novelTextGray, fontSize: fp(12) },

  // 底部输入栏
  inputBar: {
    paddingHorizontal: wp(12),
    paddingVertical: wp(10),
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(10),
    borderTopWidth: 1,
    borderTopColor: colors.outline,
    backgroundColor: colors.novelBackground,
  },
  input: {
    flex: 1,
    minHeight: wp(40),
    maxHeight: wp(100),
    paddingHorizontal: wp(12),
    paddingVertical: wp(8),
    borderRadius: sp(12),
    backgroundColor: colors.novelSecondaryBackground,
    color: colors.novelText,
  },
  sendBtn: {
    width: wp(36),
    height: wp(36),
    borderRadius: wp(18),
    backgroundColor: colors.novelMain,
    alignItems: 'center',
    justifyContent: 'center',
  },
  sendText: { color: colors.novelBackground, fontWeight: '600' },

  // 底部两个建议按钮
  suggestions: { flexDirection: 'row', gap: wp(12), paddingHorizontal: wp(16), paddingBottom: wp(10) },
  suggestBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: wp(6),
    borderRadius: sp(20),
    paddingHorizontal: wp(12),
    paddingVertical: wp(8),
    backgroundColor: colors.novelSecondaryBackground,
  },
  suggestText: { color: colors.novelText },
});


