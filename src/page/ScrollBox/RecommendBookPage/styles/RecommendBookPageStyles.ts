import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createRecommendBookPageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f0f6fb', // 使用与 BecomeWriterPage 相同的背景色
  },

  // 顶部Bar样式 - 非透明背景
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: wp(20),
    paddingVertical: wp(16),
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
    paddingBottom: wp(20),
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
    marginHorizontal: wp(16),
    marginBottom: wp(16),
  },
  profileAvatar: {
    width: wp(40),
    height: wp(40),
    borderRadius: wp(20),
    marginRight: wp(12),
    backgroundColor: colors.novelBookBackground,
  },
  profileName: {
    flex: 1,
    ...typography.bodyMedium,
    color: colors.novelText,
  },
  profileArrow: {
    fontSize: fp(16),
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
  moreLink: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    alignItems: 'center',
  },

  // Tabs 样式
  subtabsContainer: {
    flexDirection: 'row',
    gap: wp(8),                       // RN ≥0.72 支持 gap；低版本用 marginLeft
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
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
  },
  statNumber: {
    fontSize: fp(18),
    fontWeight: 'bold',
    color: colors.novelText,
    marginVertical: wp(6),
    marginBottom: wp(6),
  },
  statLabel: {
    fontSize: fp(10),
    color: colors.novelText,
    textAlign: 'center',
  },
  statBottomLabel: {
    fontSize: fp(10),
    fontWeight: '300',
    color: colors.novelTextGray,
    textAlign: 'center',
  },
  withdrawButton: {
    alignItems: 'center',
  },
  withdrawButtonText: {
    fontSize: fp(10),
    fontWeight: '600',
    color: colors.novelMain,
  },

  // 创作服务样式
  servicesList: {
    gap: wp(12),
  },
  serviceItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: wp(12),
    paddingHorizontal: wp(16),
    backgroundColor: '#f8f9fa',
    borderRadius: sp(8),
  },
  serviceIconWrapper: {
    width: wp(40),
    height: wp(40),
    borderRadius: wp(20),
    backgroundColor: colors.novelBackground,
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: wp(12),
  },
  serviceIcon: {
    fontSize: fp(18),
  },
  serviceTextGroup: {
    flex: 1,
  },
  serviceTitle: {
    fontSize: fp(14),
    color: colors.novelText,
    fontWeight: '500',
  },
  serviceArrow: {
    fontSize: fp(16),
    color: colors.novelTextGray,
  },

  // 任务列表样式
  tasksList: {
    gap: wp(16),
  },
  taskItem: {
  },
  taskHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    marginBottom: wp(12),
  },
  taskCover: {
    backgroundColor: colors.novelBookBackground,
    width: wp(80),
    height: wp(80),
    borderRadius: sp(4),
    marginRight: wp(12),
  },
  taskInfo: {
    flex: 1,
  },
  taskMainRow: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  taskContent: {
    flex: 1,
  },
  taskTitle: {
    fontSize: fp(16),
    color: colors.novelText,
    fontWeight: '600',
    marginBottom: wp(4),
  },
  taskDescription: {
    fontSize: fp(12),
    color: colors.novelTextGray,
    marginVertical: wp(8),
    numberOfLines: 2,
  },
  taskEarnings: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  taskEarningsLabel: {
    fontSize: fp(12),
    color: colors.novelTextGray,
  },
  taskEarningsAmount: {
    marginLeft: wp(4),
    fontSize: fp(14),
    color: colors.novelMain,
  },
  taskFooter: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: wp(12),
  },
  taskTags: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
  },
  taskTag: {
    backgroundColor: colors.novelDivider + '99',
    paddingHorizontal: wp(8),
    paddingVertical: wp(4),
    borderRadius: sp(4),
    marginRight: wp(8),
    marginBottom: wp(8),
  },
  taskTagText: {
    fontSize: fp(6),
    color: colors.novelTextGray,
  },
  taskButton: {
    backgroundColor:colors.novelDivider + '99',
    paddingVertical: wp(4),
    paddingHorizontal: wp(8),
    borderRadius: sp(16),
  },
  taskButtonText: {
    color: colors.novelText,
    fontSize: fp(12),
    fontWeight: '600',
  },

  // 查看更多链接样式
  viewAllTasksContainer: {
    alignItems: 'center',
  },
  viewAllTasksButton: {
    flexDirection: 'row',
    paddingTop: wp(12),
    backgroundColor: colors.novelBackground,
  },
  viewAllTasksText: {
    fontSize: fp(14),
    color: colors.novelText,
    marginRight: wp(4),
  },
  viewAllTasksArrow: {
    fontSize: fp(14),
    color: colors.novelText,
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
});
