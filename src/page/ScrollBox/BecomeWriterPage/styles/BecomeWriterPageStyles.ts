import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createBecomeWriterPageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f0f6fb', // 使用 ui.html 中的背景色
  },

  // 顶部Bar样式 - 透明背景
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(20),
    paddingVertical: wp(16),
    backgroundColor: 'transparent', // 透明背景
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

  // ScrollView 样式
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: wp(100), // 为底部按钮留出空间
  },

  // 通用卡片样式
  section: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    padding: wp(16),
    marginHorizontal: wp(16),
    marginBottom: wp(16),
  },

  // 用户信息样式
  profileSection: {
    flexDirection: 'row',
    alignItems: 'center',
    borderRadius: sp(8),
    // padding: wp(12),
    marginHorizontal: wp(16),
    marginBottom: wp(16),
  },
  profileAvatar: {
    width: wp(35),
    height: wp(35),
    borderRadius: wp(20),
    marginRight: wp(15),
    backgroundColor: colors.novelBookBackground,
  },
  profileName: {
    flex: 1,
    fontSize: fp(12),
    color: colors.novelText,
  },
  profileArrow: {
    fontSize: fp(20),
    color: colors.novelTextGray,
  },

  // 公告样式
  announcementSection: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    paddingHorizontal: wp(12),
    paddingVertical: wp(12),
    marginHorizontal: wp(16),
    marginBottom: wp(16),
  },
  announcementTag: {
    backgroundColor: '#eaf5ff',
    color: '#1890ff',
    fontSize: fp(8),
    paddingHorizontal: wp(4),
    paddingVertical: wp(2),
    borderRadius: sp(2),
    marginRight: wp(6),
    overflow: 'hidden',
  },
  announcementCarouselContainer: {
    flex: 1,
    height: wp(20),
    overflow: 'hidden',
  },
  announcementCarousel: {
    flexDirection: 'column',
  },
  announcementItem: {
    height: wp(20),
    justifyContent: 'center',
  },
  announcementText: {
    fontSize: fp(10),
    color: colors.novelText,
  },
  announcementMore: {
    fontSize: fp(10),
    color: colors.novelTextGray,
  },

  // Section 标题样式
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: wp(12),
  },
  sectionTitle: {
    ...typography.bodyMedium,
    fontSize: fp(16),
    fontWeight: '500',
    color: colors.novelText,
  },
  sectionSubtitle: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    marginVertical: wp(4),
  },
  moreLink: {
    fontSize: fp(10),
    color: colors.novelTextGray,
  },

  // Tabs 样式
  tabsContainer: {
    flexDirection: 'row',
    borderWidth: 1,
    borderColor: colors.novelDivider,
    borderRadius: sp(8),
    padding: wp(1),
    backgroundColor: '#f5f5f5',
    marginBottom: wp(12),
  },
  tab: {
    flex: 1,
    paddingHorizontal: wp(16),
    paddingVertical: wp(6),
    borderRadius: sp(5),
    alignItems: 'center',
  },
  activeTab: {
    backgroundColor: colors.novelBackground,
  },
  tabText: {
    ...typography.labelLarge,
    color: colors.novelTextGray,
  },
  activeTabText: {
    color: colors.novelText,
    fontWeight: 'bold',
  },

  // Subtabs 样式
  subtabsContainer: {
    flexDirection: 'row',
    gap: wp(8),
    marginBottom: wp(12),
  },
  subtab: {
    paddingHorizontal: wp(14),
    paddingVertical: wp(6),
    borderRadius: sp(4),
    backgroundColor: colors.novelSecondaryBackground + '99',
    alignItems: 'center',
  },
  activeSubtab: {
    backgroundColor: colors.novelMain + '33',
  },
  subtabText: {
    fontSize: wp(12),
    color: colors.novelTextGray,
  },
  activeSubtabText: {
    color: colors.novelMain,
    fontWeight: 'bold',
  },

  // 数据统计样式
  statsContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginTop: wp(12),
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
  },
  statNumber: {
    fontSize: fp(16),
    color: colors.novelText,
  },
  statLabel: {
    fontSize: fp(10),
    fontWeight: '300',
    color: colors.novelTextGray,
    marginVertical: wp(4),
  },
  // 数据统计破折号
  statDash: {
    fontSize: fp(12),
    color: colors.novelTextGray,
  },

  // 福利列表样式
  benefitsList: {
    gap: wp(8),
  },
  benefitItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: wp(8),
  },
  benefitIcon: {
    width: wp(24),
    height: wp(24),
    paddingLeft: wp(2),
    paddingTop: wp(1.5),
  },
  benefitIconWrapper: {
    width: wp(48),
    height: wp(48),
    borderRadius: wp(24),               // 宽高一半，正好是圆
    backgroundColor: 'rgb(241,248,254)',// 目标背景色
    justifyContent: 'center',           // 让 icon 居中
    alignItems: 'center',
    marginRight: wp(8),
  },
  benefitTextGroup: {
    flex: 1,
    paddingLeft: wp(8),
  },
  benefitTitle: {
    ...typography.labelLarge,
    fontWeight: '600',
    color: colors.novelText,
  },
  benefitSubtitle: {
    fontSize: fp(10),
    color: colors.novelTextGray,
    marginTop: wp(4),
  },
  viewAllLink: {
    alignItems: 'center',
  },
  viewAllText: {
    fontSize: fp(12),
    color: colors.novelTextGray,
  },

  // 时间线样式
  timelineContainer: {
    flex: 1,
    paddingLeft: wp(12),
  },
  timelineRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingVertical: wp(10),
  },
  timelineIndicator: {
    width: wp(24),
    alignItems: 'center',
  },
  timelineLine: {
    width: 1,
    backgroundColor: colors.novelDivider,
    flex: 1,
  },
  timelineIcon: {
    width: wp(24),
    height: wp(24),
    borderRadius: wp(12),
    paddingLeft: wp(2.5),
    paddingTop: wp(1.5),
  },
  timelineIconWrapper: {
    width: wp(32),
    height: wp(32),
    borderRadius: wp(16),               // 宽高一半，正好是圆
    backgroundColor: 'rgb(241,248,254)',// 目标背景色
    justifyContent: 'center',           // 让 icon 居中
    alignItems: 'center',
    marginRight: wp(8),                 // （可选）和文字保持间距
    marginVertical: wp(4),
  },
  timelineTextGroup: {
    flex: 1,
    paddingLeft: wp(12),
  },
  timelineTitle: {
    ...typography.labelLarge,
    fontWeight: '500',
    color: colors.novelText,
  },
  timelineSubtitle: {
    fontSize: fp(10),
    color: colors.novelTextGray,
    marginTop: wp(4),
  },


  // 平台图标样式
  platformsContainer: {
    flexDirection: 'row',
    gap: wp(16),
    paddingVertical: wp(8),
  },
  platformItem: {
    alignItems: 'center',
  },
  platformLogo: {
    backgroundColor: colors.novelSecondaryBackground,
    width: wp(50),
    height: wp(50),
    borderRadius: sp(10),
  },
  platformName: {
    fontSize: fp(12),
    fontWeight: '300',
    color: colors.novelTextGray,
    marginTop: wp(4),
    textAlign: 'center',
  },

  // 版权作品网格样式
  copyrightGrid: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    // 可选：如果想让网格左右与父容器对齐，可以加一点负边距
    marginHorizontal: -wp(6),
  },
  copyrightItem: {
    width: (wp(350) - wp(32) - wp(24)) / 3, // 保持三列布局
    paddingHorizontal: wp(6),               // 相当于左右各留一半 gap
    marginBottom: wp(12),                   // 行间距
  },
  copyrightCover: {
    width: '100%',
    aspectRatio: 0.75,                      // 高度 = 宽度 * 4/3
    backgroundColor: colors.novelSecondaryBackground,
    borderRadius: sp(4),
  },
  copyrightTitle: {
    fontSize: fp(12),
    color: colors.novelText,
    textAlign: 'center',
    marginTop: wp(4),
  },

  // 活动列表样式
  activityList: {
    // 模拟 gap: 12
    marginVertical: -wp(6),
  },
  activityItem: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.novelBackground,
    borderRadius: sp(8),
    marginVertical: wp(6),
  },
  activityCover: {
    width: wp(75),
    height: wp(75),
    backgroundColor: colors.novelDivider,
    borderRadius: sp(8),
    marginRight: wp(8),
  },
  activityInfo: {
    flex: 1,
  },
  activityTitle: {
    fontSize: fp(12),
    color: colors.novelText,
  },
  activityTime: {
    fontSize: fp(10),
    fontWeight: '300',
    color: colors.novelTextGray,
    marginTop: wp(10),
  },
  activityButton: {
    marginLeft: wp(10),
    paddingHorizontal: wp(12),
    paddingVertical: wp(6),
    borderRadius: sp(12),
    backgroundColor: colors.novelMain + '33',
  },
  activityButtonText: {
    fontSize: fp(12),
    color: colors.novelMain,
  },
  activityDivider:{
    height: wp(1),
    width: wp(350) - wp(32) - wp(75 + 12),
    backgroundColor: colors.novelDivider,
    marginLeft: wp(75 + 8),
    alignSelf: 'stretch',
  },

  // 课程卡片样式
  coursesContainer: {
    flexDirection: 'row',
    gap: wp(12),
    paddingBottom: wp(16),
  },
  courseInfo: {
    padding: wp(8),
  },
  // 两列网格容器
  coursesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -wp(8),        // 抵消左右 padding
  },
  // 卡片样式：两列等分，含左右间距
  courseCard: {
    marginTop: wp(8),
    width: '50%',                     // 50% 宽度
    paddingHorizontal: wp(8),         // 左右间距 wp(8)*2 = 网格 gap wp(16)
  },
  courseCover: {
    width: '100%',
    height: wp(80),
    borderRadius: sp(8),
    backgroundColor: colors.novelDivider,
  },
  courseTitle: {
    numberOfLines: 2,
    marginTop: wp(8),
    fontSize: fp(13),
    color: colors.novelText,
    fontWeight: '600',
  },

  // 底部固定按钮样式
  bottomButtonContainer: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: 'transparent',
    padding: wp(16),
  },
  bottomButton: {
    marginHorizontal: wp(25),
    paddingVertical: wp(12),
    borderRadius: sp(24),
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 4,
    elevation: 5,
  },
  bottomButtonGradient: {
    backgroundColor: colors.novelMain, // 使用主题色作为fallback
  },
  bottomButtonText: {
    color: colors.novelBackground,
    fontSize: fp(16),
    fontWeight: 'bold',
  },

  // 加载状态样式
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: wp(20),
  },
  loadingText: {
    ...typography.bodyMedium,
    color: colors.novelTextGray,
    marginTop: wp(8),
  },

  // 空状态样式
  emptyStateContainer: {
    flexDirection: 'row',         // 横向排列
    alignItems: 'center',         // 图标与文字垂直居中对齐
  },
  emptyStateIcon: {
    width: wp(60),
    height: wp(80),
    borderRadius: wp(8),
    backgroundColor: '#f5f5f5',
    marginRight: wp(12),          // 图标和文字之间留空
  },
  emptyStateTextGroup: {
    flex: 1,                      // 文本区占剩余空间
  },
  emptyStateTitle: {
    fontSize: fp(16),
    fontWeight: '400',
    color: colors.novelText,
    marginBottom: wp(8),
  },
  emptyStateSubtitle: {
    fontSize: fp(10),
    color: colors.novelTextGray,
  },

  // 展开/收起按钮样式
  toggleButton: {
    alignItems: 'center',
    paddingTop: wp(8),
  },
  toggleButtonText: {
    fontSize: fp(11),
    fontweight: '200',
    color: colors.novelTextGray,
  },
   // ===== 弹窗样式 =====
   modalOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000,
  },
  modalContainer: {
    backgroundColor: colors.novelBackground,
    borderRadius: sp(16),
    marginHorizontal: wp(32),
    paddingTop: wp(24),
    paddingBottom: wp(20),
    paddingHorizontal: wp(24),
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 8,
    elevation: 10,
    position: 'relative',
  },
  modalCloseButton: {
    position: 'absolute',
    top: wp(10),
    right: wp(10),
    zIndex: 1,
  },
  modalCloseText: {
    fontSize: fp(16),
    color: colors.novelTextGray,
    fontWeight: '300',
  },
  modalTitle: {
    marginTop: wp(8),
    fontSize: fp(18),
    fontWeight: 'bold',
    color: colors.novelText,
    textAlign: 'center',
    marginBottom: wp(16),
  },
  modalDescription: {
    fontSize: fp(14),
    color: colors.novelText,
    textAlign: 'center',
    lineHeight: fp(20),
    marginBottom: wp(8),
  },
  modalSubDescription: {
    fontSize: fp(14),
    color: colors.novelText,
    textAlign: 'center',
    marginBottom: wp(25),
  },
  modalRegisterButton: {
    marginHorizontal: wp(32),
    overflow: 'hidden',
    paddingVertical: wp(10),
    borderRadius: sp(24),
    alignItems: 'center',
    marginBottom: wp(16),
  },
  modalRegisterButtonText: {
    color: colors.novelBackground,
    fontSize: fp(16),
    fontWeight: 'bold',
  },
  modalAgreementContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  modalCheckbox: {
    marginTop: wp(2),
    width: wp(12),
    height: wp(12),
    borderRadius: wp(8),
    borderWidth: 0.1,
    borderColor: colors.novelTextGray,
    marginRight: wp(4),
    justifyContent: 'center',
    alignItems: 'center',
  },
  modalCheckboxChecked: {
    backgroundColor: colors.novelMain,
    borderColor: colors.novelMain,
  },
  modalCheckboxCheck: {
    color: colors.novelBackground,
    fontSize: fp(10),
    fontWeight: 'bold',
  },
  modalAgreementText: {
    fontSize: fp(12),
    color: colors.novelTextGray,
  },
  modalAgreementLink: {
    fontSize: fp(12),
    color: colors.novelMain,
    textDecorationLine: 'underline',
  },
});
