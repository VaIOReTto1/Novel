import { StyleSheet } from 'react-native';

import { createNovelDesignUI } from '../../../../design-system/novelDesign';
import { NovelColors } from '../../../../utils/theme/colors';
import { fp, sp, wp, hp } from '../../../../utils/theme/dimensions';

export const createWritePageStyles = (colors: NovelColors) => {
  const ui = createNovelDesignUI(colors);

  return StyleSheet.create({
    container: {
      flex: 1,
      backgroundColor: ui.color.bg.canvas,
    },
    topBar: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      paddingHorizontal: ui.metrics.pageGutter,
      paddingVertical: ui.spacePx('150'),
      backgroundColor: ui.color.bg.surface,
      borderBottomWidth: 1,
      borderBottomColor: ui.color.border.subtle,
    },
    navBtn: {
      minWidth: wp(32),
      minHeight: wp(32),
      alignItems: 'center',
      justifyContent: 'center',
    },
    navIcon: { fontSize: fp(28), color: colors.novelText },
    toolbar: { flexDirection: 'row', alignItems: 'center', gap: wp(12) },
    toolBtn: {},
    toolIcon: { fontSize: fp(18), color: colors.novelText },
    publishBtn: {
      backgroundColor: ui.color.brand.primary,
      paddingHorizontal: wp(14),
      paddingVertical: wp(8),
      borderRadius: sp(16),
    },
    publishText: { color: ui.color.text.inverse, fontWeight: '600' },
    editor: {
      flex: 1,
      paddingHorizontal: wp(16),
      backgroundColor: ui.color.bg.surface,
    },
    titleInput: {
      fontSize: fp(18),
      color: ui.color.text.primary,
      marginBottom: wp(12),
      fontWeight: '600',
    },
    contentInput: {
      minHeight: hp(500),
      fontSize: fp(14),
      color: ui.color.text.primary,
    },

    welcomePanel: {
      position: 'absolute',
      left: wp(16),
      right: wp(16),
      bottom: wp(70),
      backgroundColor: ui.color.bg.elevated,
      borderRadius: sp(12),
      padding: wp(16),
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    welcomeRow: { flexDirection: 'row', justifyContent: 'space-between' },
    welcomeItem: { alignItems: 'center' },
    welcomeTitle: {
      color: ui.color.text.primary,
      fontWeight: '600',
      marginBottom: wp(10),
    },
    welcomeSub: {
      color: ui.color.text.secondary,
      fontSize: fp(10),
      marginTop: wp(4),
    },
    closeBtn: { position: 'absolute', right: wp(8), top: wp(8) },
    closeText: { color: ui.color.text.secondary },

    volumeBar: {
      position: 'absolute',
      left: wp(16),
      right: wp(16),
      bottom: wp(20),
    },
    volumeText: { color: ui.color.text.secondary },

    selectionToolbarBackdrop: {
      position: 'absolute',
      left: 0,
      right: 0,
      top: 0,
      bottom: 0,
    },
    selectionToolbarContainer: {
      position: 'absolute',
      left: wp(16),
      right: wp(16),
      top: wp(72),
      backgroundColor: ui.color.bg.surface,
      borderRadius: sp(8),
      padding: wp(8),
      flexDirection: 'row',
      justifyContent: 'space-around',
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
      elevation: 8,
      shadowColor: '#000',
      shadowOpacity: 0.1,
      shadowRadius: 6,
    },
    toolbarBtnText: { fontSize: fp(12), color: ui.color.text.primary },
    toolbarBtnTextSecondary: { fontSize: fp(12), color: ui.color.text.secondary },

    overlayMask: {
      position: 'absolute',
      left: 0,
      right: 0,
      top: 0,
      bottom: 0,
      backgroundColor: '#0006',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 1000,
    },
    modalContainer: {
      backgroundColor: ui.color.bg.surface,
      padding: wp(16),
      borderRadius: sp(10),
      width: '80%',
      borderWidth: 1,
      borderColor: ui.color.border.subtle,
    },
    modalHint: { color: ui.color.text.primary, marginBottom: wp(8) },
    modalInput: {
      borderWidth: 1,
      borderColor: colors.outline,
      color: ui.color.text.primary,
      padding: wp(8),
      borderRadius: sp(6),
      marginBottom: wp(12),
    },
    modalActions: { flexDirection: 'row', justifyContent: 'flex-end' },
    modalCancel: { color: ui.color.text.secondary, marginRight: wp(16) },
    modalOk: { color: ui.color.brand.primary },
  });
};
