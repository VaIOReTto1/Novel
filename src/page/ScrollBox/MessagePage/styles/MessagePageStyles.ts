import { StyleSheet } from 'react-native';
import { wp, fp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createMessagePageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
  },

  // 顶部Bar样式 - 与HistoryPage保持一致
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(20),
    paddingVertical: wp(16),
    backgroundColor: colors.novelBackground,
    minHeight: wp(56),
  },
  backButton: {
    justifyContent: 'center',
    alignItems: 'center',
    minWidth: wp(32),
    minHeight: wp(32),
  },
  backArrow: {
    fontSize: fp(40),
    color: colors.novelText,
    fontWeight: '300',
    lineHeight: fp(28),
  },
  titleContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  title: {
    ...typography.bodyMedium,
    fontSize: fp(18),
    color: colors.novelText,
    fontWeight: '600',
    textAlign: 'center',
  },
  markAllButton: {
    justifyContent: 'center',
    alignItems: 'center',
    minWidth: wp(32),
    minHeight: wp(32),
  },

  // 主消息列表样式
  mainMessagesSection: {
    backgroundColor: colors.novelBackground,
  },
  messageItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingHorizontal: wp(16),
    paddingVertical: wp(12),
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
    backgroundColor: colors.novelBackground,
  },
  messageIcon: {
    width: wp(40),
    height: wp(40),
    borderRadius: wp(20),
    backgroundColor: colors.novelMain,
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: wp(4),
    marginRight: wp(12),
  },
  messageIconText: {
    fontSize: fp(18),
  },
  messageContent: {
    flex: 1,
    marginRight: wp(12),
  },
  messageHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: wp(4),
  },
  messageTitle: {
    ...typography.bodyMedium,
    color: colors.novelText,
    fontWeight: '500',
    fontSize: fp(15),
  },
  messageTime: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    fontSize: fp(12),
  },
  messageBody: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
    lineHeight: fp(18),
    marginBottom: wp(4),
  },
  notificationBadge: {
    width: wp(8),
    height: wp(8),
    borderRadius: wp(4),
    backgroundColor: colors.novelError,
    alignSelf: 'center',
  },
  messageBodyRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },


  // 分割区域
  divider: {
    height: wp(8),
    backgroundColor: colors.novelDivider,
  },

  // Tabs样式 - 与HistoryPage保持一致，支持sticky
  tabsContainer: {
    backgroundColor: colors.novelBackground,
    paddingHorizontal: wp(12),
    paddingVertical: wp(12),
    borderTopWidth: 0.5,
    borderTopColor: colors.novelDivider,
    borderBottomWidth: 0.5,
    borderBottomColor: colors.novelDivider,
  },
  tabsScroll: {
    flexDirection: 'row',
  },
  tab: {
    marginRight: wp(20),
    minWidth: wp(60),
    alignItems: 'center',
  },
  activeTab: {
    // 移除背景色，只保留文字样式
  },
  tabText: {
    fontSize: wp(16),
    color: colors.novelTextGray,
    fontWeight: '400',
  },
  activeTabText: {
    color: colors.novelText,
    fontWeight: '600',
  },
  // 添加水平分割线样式
  tabsDivider: {
    height: 0.5,
    backgroundColor: colors.novelDivider,
  },

  // 内容区域样式
  contentContainer: {
    flex: 1,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: wp(32),
  },

  // 空状态
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(80),
    paddingHorizontal: wp(40),
  },
  emptyImage: {
    width: wp(120),
    height: wp(120),
    marginBottom: wp(16),
    borderRadius: wp(60),
    backgroundColor: colors.novelDivider,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyImageText: {
    fontSize: fp(60),
    opacity: 0.3,
  },
  emptyText: {
    ...typography.bodyMedium,
    color: colors.novelTextGray,
    textAlign: 'center',
    lineHeight: fp(22),
  },

  // 加载指示器样式
  loadingContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(20),
    gap: wp(8),
  },
  loadingText: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
  },
  endLine: {
    width: wp(30),
    height: wp(1),
    backgroundColor: colors.novelDivider,
  },
  endText: {
    ...typography.labelSmall,
    color: colors.novelTextGray,
    marginHorizontal: wp(10),
  },

  // 刷新指示器样式
  refreshIndicator: {
    backgroundColor: colors.novelBackground,
    paddingVertical: wp(15),
    paddingHorizontal: wp(20),
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
    marginBottom: wp(10),
  },
  refreshContent: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  refreshText: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },
  loadingSpinner: {
    marginRight: wp(8),
  },
  spinnerText: {
    fontSize: fp(14),
    color: colors.novelMain,
    fontWeight: '500',
  },
}); 