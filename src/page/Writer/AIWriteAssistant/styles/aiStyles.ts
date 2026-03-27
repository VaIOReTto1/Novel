import {StyleSheet} from 'react-native';
import {NovelColors} from '../../../../utils/theme/colors';
import {fp, sp, wp} from '../../../../utils/theme/dimensions';

export const createAIStyles = (colors: NovelColors) =>
  StyleSheet.create({
    container: {flex: 1, backgroundColor: colors.novelSecondaryBackground},

    // 首屏加载
    loadingScreen: {
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
    },

    header: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingHorizontal: wp(16),
      backgroundColor: colors.novelBackground,
      paddingVertical: wp(12),
    },
    headerFixed: {
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      zIndex: 20,
      elevation: 10,
    },
    headerSpacer: { height: wp(56) },
    headTitleRow: {alignItems: 'flex-start'},
    headTitle: {fontSize: fp(16), color: colors.novelText, fontWeight: '600'},
    headBack: {fontSize: fp(28), color: colors.novelText},
    headEllipsis: {width: wp(28)},
    quota: {color: colors.novelTextGray, fontSize: fp(10), marginTop: wp(4)},

    list: {flex: 1, paddingHorizontal: wp(16)},
    bubble: {
      borderRadius: sp(10),
      padding: wp(12),
      marginVertical: wp(8),
      maxWidth: '86%',
    },
    bubbleAssistant: {backgroundColor: colors.novelBackground},
    bubbleUser: {
      backgroundColor: colors.novelMain + '22',
      alignSelf: 'flex-end',
    },
    bubbleAssistantCorner: {borderTopLeftRadius: 0},
    bubbleUserCorner: {borderTopRightRadius: 0},
    textAssistant: {color: colors.novelText},
    textUser: {color: colors.novelText},

    // 初始引导大气泡 + 卡片
    introBubble: {
      backgroundColor: '#FFEFF6',
      borderRadius: sp(12),
      padding: wp(14),
      marginVertical: wp(8),
    },
    introTitle: {
      color: colors.novelText,
      fontSize: fp(14),
      lineHeight: fp(20),
      marginBottom: wp(10),
    },
    introCards: {gap: wp(10)},
    introCard: {
      backgroundColor: colors.novelBackground,
      borderRadius: sp(12),
      paddingHorizontal: wp(12),
      paddingVertical: wp(10),
    },
    introRefresh: {
      marginTop: wp(10),
      alignSelf: 'flex-start',
      width: wp(28),
      height: wp(28),
      borderRadius: wp(4),
      backgroundColor: colors.novelTextGray + '15',
      alignItems: 'center',
      justifyContent: 'center',
    },
    introRefreshText: {color: colors.novelTextGray, fontSize: fp(12)},

    // 底部输入栏
    inputBar: {
      paddingHorizontal: wp(12),
      paddingVertical: wp(10),
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(10),
      backgroundColor: colors.novelBackground,
      zIndex: 10,
      elevation: 6,
    },
    input: {
      flex: 1,
      minHeight: wp(30),
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
    sendBtnDisabled: {opacity: 0.5},
    sendText: {color: colors.novelBackground, fontWeight: '600'},

    // 底部两个建议按钮
    suggestions: {
      backgroundColor: 'transparent',
      flexDirection: 'row',
      gap: wp(12),
      paddingHorizontal: wp(16),
      paddingBottom: wp(10),
      zIndex: 5,
    },
    suggestBtn: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(6),
      borderRadius: sp(8),
      borderWidth: 0.5,
      borderColor: colors.novelTextGray,
      paddingHorizontal: wp(12),
      paddingVertical: wp(5),
      backgroundColor: colors.novelSecondaryBackground,
    },
    suggestText: {color: colors.novelText, fontSize: sp(12)},

    // 开书灵感浮窗（与 Suggestions 同位置，但在其上层）
    ideaFloatWrap: {
      position: 'absolute',
      left: 0,
      right: 0,
      // 预估输入栏高度 ≈ 64dp，确保浮窗在输入栏之上
      bottom: wp(75),
      paddingHorizontal: wp(10),
      zIndex: 1000,
      elevation: 8,
    },
    ideaFloatPanel: {
      backgroundColor: colors.novelBackground,
      borderRadius: sp(12),
      paddingHorizontal: wp(12),
      paddingVertical: wp(12),
    },
    ideaSelectorHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: wp(12),
    },
    ideaSelectorTitle: {
      color: colors.novelText,
      fontSize: fp(14),
      fontWeight: '600',
    },
    ideaGridWrap: {
      flexDirection: 'row',
      flexWrap: 'wrap',
      justifyContent: 'space-between',
    },
    ideaGridItem: {
      width: '23%',
      borderRadius: sp(10),
      paddingVertical: wp(10),
      alignItems: 'center',
      marginBottom: wp(8),
      backgroundColor: colors.novelSecondaryBackground,
    },
    ideaGridItemActive: {
      backgroundColor: colors.novelMain + '20',
    },
    ideaGridText: {color: colors.novelText, fontSize: fp(13)},
    ideaGridTextActive: {
      color: colors.novelMain,
      fontWeight: '800',
    },

    // 推理展示
    thinkingContainer: {
      backgroundColor: colors.novelBackground,
      borderRadius: sp(8),
      padding: wp(10),
      marginBottom: wp(8),
    },
    thinkingHeaderRow: {
      flexDirection: 'row',
      alignItems: 'center',
    },
    thinkingTitle: {color: colors.novelTextGray, fontSize: fp(12), marginRight: wp(3)},
    thinkingCaret: {color: colors.novelTextGray, fontSize: fp(12)},
    thinkingQuoteBox: {
      marginTop: wp(6),
      borderLeftWidth: 1,
      borderLeftColor: colors.outline,
      paddingLeft: wp(10),
    },
    thinkingQuoteText: {
      color: colors.novelTextGray,
      fontSize: fp(12),
      lineHeight: fp(18),
    },

    // 气泡下方动作条
    actionBarWrap: {marginTop: wp(8)},
    aiDisclaimer: {
      color: colors.novelTextGray,
      fontSize: fp(10),
      marginBottom: wp(6),
    },
    actionBar: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(8),
    },
    actionBtn: {
      paddingHorizontal: wp(10),
      paddingVertical: wp(6),
      borderRadius: sp(12),
      backgroundColor: colors.novelSecondaryBackground,
    },
    actionText: {color: colors.novelText},

    // 加载中
    loadingRow: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(8),
      marginBottom: wp(6),
    },
    loadingText: {color: colors.novelTextGray, fontSize: fp(12)},
  });
