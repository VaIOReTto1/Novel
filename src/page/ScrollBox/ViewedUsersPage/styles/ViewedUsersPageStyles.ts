import { StyleSheet } from 'react-native';
import { wp, fp, sp } from '../../../../utils/theme/dimensions';
import { typography } from '../../../../utils/theme/typography';
import { NovelColors } from '../../../../utils/theme/colors';

export const createViewedUsersPageStyles = (colors: NovelColors) => StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.novelBackground,
  },

  // 顶部Bar样式
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

  // Tab导航样式
  tabsContainer: {
    flexDirection: 'row',
    backgroundColor: colors.novelBackground,
    borderBottomWidth: 1,
    borderBottomColor: colors.novelDivider,
    paddingHorizontal: wp(16),
  },
  tab: {
    flex: 1,
    paddingVertical: wp(8),
    alignItems: 'center',
    position: 'relative',
  },
  tabText: {
    fontSize: fp(16),
    color: colors.novelTextGray,
  },
  activeTabText: {
    color: colors.novelText,
    fontWeight: '600',
  },

  // ScrollView 样式
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: wp(20),
  },

  // 空状态样式
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: wp(80),
    paddingHorizontal: wp(20),
  },
  emptyIcon: {
    width: wp(180),
    height: wp(180),
    backgroundColor: colors.novelSecondaryBackground,
    borderRadius: wp(90),
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: wp(20),
  },
  emptyIconText: {
    fontSize: fp(60),
  },
  emptyTitle: {
    fontSize: fp(14),
    color: colors.novelTextGray,
    marginBottom: wp(20),
  },
  emptyButton: {
    backgroundColor: colors.novelMain,
    paddingHorizontal: wp(20),
    paddingVertical: wp(10),
    borderRadius: sp(22),
  },
  emptyButtonText: {
    fontSize: fp(12),
    color: colors.novelBackground,
    fontWeight: '600',
  },

  // 用户列表样式
  usersList: {
    paddingHorizontal: wp(16),
  },
  userItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: wp(14),
  },
  lastUserItem: {
    borderBottomWidth: 0,
  },

  // 头像区域
  avatarContainer: {
    position: 'relative',
    marginRight: wp(12),
  },
  avatar: {
    width: wp(48),
    height: wp(48),
    borderRadius: wp(24),
    backgroundColor: colors.novelBookBackground,
  },
  vBadge: {
    position: 'absolute',
    right: -2,
    bottom: -2,
    width: wp(18),
    height: wp(18),
    backgroundColor: '#1e90ff',
    borderRadius: wp(9),
    borderWidth: wp(1.5),
    borderColor: colors.novelBackground,
    justifyContent: 'center',
    alignItems: 'center',
  },
  vBadgeText: {
    color: colors.novelBackground,
    fontSize: fp(8),
    fontWeight: '600',
  },

  // 用户信息区域
  userInfo: {
    flex: 1,
    minWidth: 0,
  },
  userNameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: wp(2),
  },
  userName: {
    fontSize: fp(15),
    color: colors.novelText,
    fontWeight: '500',
  },
  userTag: {
    marginLeft: wp(4),
    marginRight: wp(2),
    paddingHorizontal: wp(4),
    paddingVertical: wp(1),
    borderRadius: sp(3),
    justifyContent: 'center',
    alignItems: 'center',
  },
  // 标签渐变色现在由LinearGradient组件处理，不再需要backgroundColor
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    alignItems: 'center',
    marginLeft: wp(4),
  },
  userTagText: {
    fontSize: fp(8),
    color: colors.novelBackground,
    fontWeight: '600',
  },
  userDescription: {
    fontSize: fp(10),
    color: colors.novelTextGray,
    marginTop: wp(4),
  },

  // 关注按钮
  followButton: {
    paddingHorizontal: wp(8),
    paddingVertical: wp(4),
    borderRadius: sp(22),
    backgroundColor: colors.novelDivider + '99',
    flexDirection: 'row',
    alignItems: 'center',
    minWidth: wp(70),
    justifyContent: 'center',
  },
  followedButton: {
    backgroundColor: colors.novelDivider + '99',
  },
  followButtonText: {
    fontSize: fp(14),
    color: colors.novelMain,
    fontWeight: '600',
  },
  followedButtonText: {
    fontSize: fp(14),
    color: colors.novelMain,
    fontWeight: '600',
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