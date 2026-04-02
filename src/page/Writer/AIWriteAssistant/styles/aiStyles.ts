import {StyleSheet} from 'react-native';

import {
  createNovelDesignComponentRecipes,
  createNovelDesignLayoutRecipes,
  createNovelDesignSurfaceSpec,
  createNovelDesignUI,
} from '../../../../design-system/novelDesign';
import {NovelColors} from '../../../../utils/theme/colors';
import {fp, sp, wp} from '../../../../utils/theme/dimensions';

export const createAIStyles = (colors: NovelColors) => {
  const ui = createNovelDesignUI(colors);
  const layout = createNovelDesignLayoutRecipes(ui);
  const components = createNovelDesignComponentRecipes(ui);
  const surface = createNovelDesignSurfaceSpec('rn-host-ai-write-assistant-component', ui);

  return StyleSheet.create({
    container: layout.screenShell,

    // 棣栧睆鍔犺浇
    loadingScreen: {
      flex: 1,
      alignItems: 'center',
      justifyContent: 'center',
    },

    header: {
      ...layout.topBar,
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'space-between',
    },
    headerFixed: {
      position: 'absolute',
      top: 0,
      left: 0,
      right: 0,
      zIndex: 20,
      elevation: 10,
      backgroundColor: ui.color.bg.surface,
      shadowColor: ui.alpha(ui.color.text.primary, 0.14),
      shadowOpacity: 0.12,
      shadowRadius: 10,
    },
    headerSpacer: {height: wp(56)},
    headerAction: {
      ...components.quietIconButton,
    },
    headTitleRow: {alignItems: 'center', gap: ui.spacePx('050')},
    headTitle: components.titleText,
    headBack: {fontSize: fp(28), color: ui.color.text.primary},
    headEllipsis: {width: wp(28)},
    quota: {color: ui.color.text.secondary, fontSize: fp(10)},

    list: {flex: 1},
    listContent: {
      paddingHorizontal: surface.spacingRhythm.pageGutter,
      paddingBottom: ui.spacePx('200'),
    },
    bubble: {
      ...components.contentCard,
      borderRadius: ui.metrics.cardRadius,
      padding: ui.metrics.compactCardPadding,
      marginVertical: wp(8),
      maxWidth: '86%',
    },
    bubbleAssistant: {
      backgroundColor: ui.color.bg.surface,
      borderColor: ui.color.border.subtle,
    },
    bubbleUser: {
      backgroundColor: ui.alpha(ui.color.brand.primary, 0.12),
      borderColor: ui.alpha(ui.color.brand.primary, 0.2),
      alignSelf: 'flex-end',
    },
    bubbleAssistantCorner: {borderTopLeftRadius: 0},
    bubbleUserCorner: {borderTopRightRadius: 0},
    textAssistant: {color: ui.color.text.primary},
    textUser: {color: ui.color.text.primary},

    // 鍒濆寮曞澶ф皵娉?+ 鍗＄墖
    introBubble: {
      ...layout.raisedCard,
      borderRadius: ui.metrics.cardRadius,
      padding: ui.metrics.compactCardPadding,
      marginVertical: wp(8),
    },
    introTitle: {
      color: ui.color.text.primary,
      fontSize: fp(14),
      lineHeight: fp(20),
      marginBottom: wp(10),
    },
    introCards: {gap: wp(10)},
    introCard: {
      ...layout.paperCard,
      borderRadius: ui.metrics.cardRadius,
      paddingHorizontal: wp(12),
      paddingVertical: wp(10),
    },
    introRefresh: {
      marginTop: wp(10),
      alignSelf: 'flex-start',
      width: wp(28),
      height: wp(28),
      borderRadius: wp(4),
      backgroundColor: ui.alpha(ui.color.text.secondary, 0.12),
      alignItems: 'center',
      justifyContent: 'center',
    },
    introRefreshText: {color: ui.color.text.secondary, fontSize: fp(12)},

    // 搴曢儴杈撳叆鏍?
    inputBar: {
      ...layout.anchoredComposer,
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(10),
      zIndex: 10,
      elevation: 6,
    },
    input: {
      ...components.composerInput,
      flex: 1,
    },
    sendBtn: {
      ...components.primaryActionPill,
      width: wp(44),
      height: wp(44),
      paddingHorizontal: 0,
      paddingVertical: 0,
      alignItems: 'center',
      justifyContent: 'center',
    },
    sendBtnDisabled: {opacity: 0.5},
    sendText: {color: ui.color.text.inverse, fontWeight: '600'},

    // 搴曢儴涓や釜寤鸿鎸夐挳
    suggestions: {
      backgroundColor: 'transparent',
      flexDirection: 'row',
      gap: wp(12),
      paddingHorizontal: surface.spacingRhythm.pageGutter,
      paddingBottom: ui.spacePx('150'),
      zIndex: 5,
    },
    suggestBtn: {
      ...components.secondaryActionPill,
      gap: wp(6),
    },
    suggestText: {color: ui.color.text.primary, fontSize: sp(12)},

    // 寮€涔︾伒鎰熸诞绐楋紙涓?Suggestions 鍚屼綅缃紝浣嗗湪鍏朵笂灞傦級
    ideaFloatWrap: {
      position: 'absolute',
      left: 0,
      right: 0,
      bottom: ui.metrics.floatingComposerOffset,
      paddingHorizontal: wp(10),
      zIndex: 1000,
      elevation: 8,
    },
    ideaFloatPanel: {
      ...layout.floatingPanel,
      borderRadius: ui.metrics.panelRadius,
    },
    ideaSelectorHeader: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      marginBottom: wp(12),
    },
    ideaSelectorTitle: {
      color: ui.color.text.primary,
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
      borderRadius: ui.radiusPx('md'),
      paddingVertical: wp(10),
      alignItems: 'center',
      marginBottom: wp(8),
      backgroundColor: ui.color.bg.elevated,
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    ideaGridItemActive: {
      backgroundColor: ui.alpha(ui.color.brand.primary, 0.14),
      borderColor: ui.alpha(ui.color.brand.primary, 0.24),
    },
    ideaGridText: {color: ui.color.text.primary, fontSize: fp(13)},
    ideaGridTextActive: {
      color: ui.color.brand.primary,
      fontWeight: '800',
    },

    // 鎺ㄧ悊灞曠ず
    thinkingContainer: {
      ...layout.raisedCard,
      borderRadius: ui.radiusPx('md'),
      padding: wp(10),
      marginBottom: wp(8),
    },
    thinkingHeaderRow: {
      flexDirection: 'row',
      alignItems: 'center',
    },
    thinkingTitle: {color: ui.color.text.secondary, fontSize: fp(12), marginRight: wp(3)},
    thinkingCaret: {color: ui.color.text.secondary, fontSize: fp(12)},
    thinkingQuoteBox: {
      marginTop: wp(6),
      borderLeftWidth: 2,
      borderLeftColor: ui.color.border.strong,
      paddingLeft: wp(10),
    },
    thinkingQuoteText: {
      color: ui.color.text.secondary,
      fontSize: fp(12),
      lineHeight: fp(18),
    },

    // 姘旀场涓嬫柟鍔ㄤ綔鏉?
    actionBarWrap: {
      marginTop: ui.spacePx('100'),
      paddingTop: ui.spacePx('100'),
      borderTopWidth: 1,
      borderTopColor: ui.color.border.subtle,
      gap: ui.spacePx('100'),
    },
    aiDisclaimer: {
      color: ui.color.text.secondary,
      fontSize: fp(10),
    },
    actionBar: {
      ...components.actionStrip,
      alignItems: 'center',
    },
    actionBtn: {
      ...components.secondaryActionPill,
      minHeight: ui.metrics.buttonHeightMd,
    },
    actionBtnContent: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: ui.spacePx('050'),
    },
    actionText: {color: ui.color.text.primary, fontSize: fp(12), fontWeight: '600'},

    // 鍔犺浇涓?
    loadingRow: {
      flexDirection: 'row',
      alignItems: 'center',
      gap: wp(8),
      marginBottom: wp(6),
    },
    loadingText: {color: ui.color.text.secondary, fontSize: fp(12)},
  });
};
