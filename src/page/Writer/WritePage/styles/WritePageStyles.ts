import {StyleSheet} from 'react-native';
import {NovelColors} from '../../../../utils/theme/colors';
import {fp, sp, wp, hp} from '../../../../utils/theme/dimensions';

export const createWritePageStyles = (colors: NovelColors) =>
  StyleSheet.create({
    container: {flex: 1, backgroundColor: colors.novelBackground},
    topBar: {
      flexDirection: 'row',
      justifyContent: 'space-between',
      alignItems: 'center',
      paddingHorizontal: wp(16),
      paddingVertical: wp(10),
    },
    navBtn: {
      minWidth: wp(32),
      minHeight: wp(32),
      alignItems: 'center',
      justifyContent: 'center',
    },
    navIcon: {fontSize: fp(28), color: colors.novelText},
    toolbar: {flexDirection: 'row', alignItems: 'center', gap: wp(12)},
    toolBtn: {},
    toolIcon: {fontSize: fp(18), color: colors.novelText},
    publishBtn: {
      backgroundColor: colors.novelMain,
      paddingHorizontal: wp(14),
      paddingVertical: wp(8),
      borderRadius: sp(16),
    },
    publishText: {color: colors.novelBackground, fontWeight: '600'},
    editor: {flex: 1, paddingHorizontal: wp(16)},
    titleInput: {
      fontSize: fp(18),
      color: colors.novelText,
      marginBottom: wp(12),
      fontWeight: '600',
    },
    contentInput: {
      minHeight: hp(500),
      fontSize: fp(14),
      color: colors.novelText,
    },

    // 底部提示面板（欢迎成为作家）
    welcomePanel: {
      position: 'absolute',
      left: wp(16),
      right: wp(16),
      bottom: wp(70),
      backgroundColor: '#F2F9FF',
      borderRadius: sp(12),
      padding: wp(16),
    },
    welcomeRow: {flexDirection: 'row', justifyContent: 'space-between'},
    welcomeItem: {alignItems: 'center'},
    welcomeTitle: {
      color: colors.novelText,
      fontWeight: '600',
      marginBottom: wp(10),
    },
    welcomeSub: {
      color: colors.novelTextGray,
      fontSize: fp(10),
      marginTop: wp(4),
    },
    closeBtn: {position: 'absolute', right: wp(8), top: wp(8)},
    closeText: {color: colors.novelTextGray},

    // 卷选择
    volumeBar: {
      position: 'absolute',
      left: wp(16),
      right: wp(16),
      bottom: wp(20),
    },
    volumeText: {color: colors.novelTextGray},

    // 选择工具条
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
      backgroundColor: colors.novelBackground,
      borderRadius: sp(8),
      padding: wp(8),
      flexDirection: 'row',
      justifyContent: 'space-around',
      elevation: 8,
      shadowColor: '#000',
      shadowOpacity: 0.1,
      shadowRadius: 6,
    },
    toolbarBtnText: {fontSize: fp(12), color: colors.novelText},
    toolbarBtnTextSecondary: {fontSize: fp(12), color: colors.novelTextGray},

    // 遮罩与弹窗
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
      backgroundColor: colors.novelBackground,
      padding: wp(16),
      borderRadius: sp(10),
      width: '80%',
    },
    modalHint: {color: colors.novelText, marginBottom: wp(8)},
    modalInput: {
      borderWidth: 1,
      borderColor: colors.outline,
      color: colors.novelText,
      padding: wp(8),
      borderRadius: sp(6),
      marginBottom: wp(12),
    },
    modalActions: {flexDirection: 'row', justifyContent: 'flex-end'},
    modalCancel: {color: colors.novelTextGray, marginRight: wp(16)},
    modalOk: {color: colors.novelMain},
  });
