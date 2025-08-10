import React from 'react';
import { View, ScrollView, Animated, Text, TouchableOpacity, Image, Dimensions } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';
import { wp, sp } from '../../../../utils/theme/dimensions';
import { parseNewsDate } from '../../../../utils/time/timeUtils';
import { useReviewDetailPageStyles } from '../hooks/useReviewDetailPageStyles';

const { height: screenHeight } = Dimensions.get('window');

interface RepliesSheetProps {
  selectedComment: any;
  showRepliesSheet: boolean;
  inputBarHeight: number;
  sheetHeight: number;
  sheetTranslateY: Animated.Value;
  backdropOpacity: Animated.Value;
  onClose: () => void;
}

export const RepliesSheet: React.FC<RepliesSheetProps> = ({
  selectedComment,
  showRepliesSheet,
  inputBarHeight,
  sheetHeight,
  sheetTranslateY,
  backdropOpacity,
  onClose,
}) => {
  const { colors, styles } = useReviewDetailPageStyles();

  if (!selectedComment || !showRepliesSheet) return null;

  const renderPrimaryComment = () => (
    <View style={styles.replyItem}>
      <View style={styles.commentUserInfo}>
        <View style={styles.commentAvatar}>
          {selectedComment.userAvatar ? (
            <Image
              source={{ uri: selectedComment.userAvatar }}
              style={styles.commentAvatarImage}
            />
          ) : (
            <Icon name="person" size={20} color={colors.novelTextGray} />
          )}
        </View>
        <View style={styles.commentUserDetails}>
          <View style={styles.commentUserNameRow}>
            <View style={styles.commentUserNameContainer}>
              <Text style={styles.commentUserName}>{selectedComment.userName}</Text>
              {selectedComment.tag && (
                <View style={[
                  styles.commentTag, 
                  selectedComment.tag === 'first_comment' ? styles.commentTag_first_comment :
                  selectedComment.tag === 'true_fan' ? styles.commentTag_true_fan :
                  selectedComment.tag === 'vip' ? styles.commentTag_vip : {}
                ]}>
                  <Text style={styles.commentTagText}>
                    {selectedComment.tag === 'first_comment' ? '首评' : 
                     selectedComment.tag === 'true_fan' ? '真爱粉' : 
                     selectedComment.tag === 'vip' ? 'VIP' : ''}
                  </Text>
                </View>
              )}
            </View>
            <TouchableOpacity style={styles.followButton} activeOpacity={0.7}>
              <Text style={styles.followButtonText}>+ 关注</Text>
            </TouchableOpacity>
          </View>
          <Text style={styles.commentContent}>{selectedComment.content}</Text>
          <View style={styles.commentActions}>
            <View style={styles.commentLeftActions}>
              <Text style={styles.commentTime}>{parseNewsDate(selectedComment.createTime)}</Text>
            </View>
            <View style={styles.commentRightActions}>
              <TouchableOpacity style={styles.commentActionButton}>
                <Icon
                  name={selectedComment.isLiked ? 'favorite' : 'favorite-border'}
                  size={20}
                  color={selectedComment.isLiked ? colors.novelMain : colors.novelTextGray}
                />
                <Text style={[styles.commentActionText, selectedComment.isLiked && { color: colors.novelMain }]}>
                  {selectedComment.likeCount}
                </Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.commentActionButton}>
                <Icon
                  name={selectedComment.isDisliked ? 'thumb-down' : 'thumb-down-off-alt'}
                  size={20}
                  color={selectedComment.isDisliked ? colors.novelMain : colors.novelTextGray}
                />
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </View>
    </View>
  );

  const renderReplyItem = (reply: any) => (
    <View key={reply.id} style={styles.replyItem}>
      <View style={styles.commentUserInfo}>
        <View style={styles.commentAvatar}>
          {reply.userAvatar ? (
            <Image
              source={{ uri: reply.userAvatar }}
              style={styles.commentAvatarImage}
            />
          ) : (
            <Icon name="person" size={20} color={colors.novelTextGray} />
          )}
        </View>
        <View style={styles.commentUserDetails}>
                      <View style={styles.commentUserNameRow}>
              <View style={styles.commentUserNameContainer}>
                <Text style={styles.commentUserName}>{reply.userName}</Text>
              </View>
              <TouchableOpacity style={styles.commentMenuButton} activeOpacity={0.7}>
                <Icon name="more-vert" size={20} color={colors.novelTextGray} />
              </TouchableOpacity>
            </View>
          <Text style={styles.commentContent}>{reply.content}</Text>
          <View style={styles.commentActions}>
            <View style={styles.commentLeftActions}>
              <Text style={styles.commentTime}>{parseNewsDate(reply.createTime)}</Text>
            </View>
            <View style={styles.commentRightActions}>
              <TouchableOpacity style={styles.commentActionButton}>
                <Icon
                  name={reply.isLiked ? 'favorite' : 'favorite-border'}
                  size={20}
                  color={reply.isLiked ? colors.novelMain : colors.novelTextGray}
                />
                <Text style={[styles.commentActionText, reply.isLiked && { color: colors.novelMain }]}>
                  {reply.likeCount}
                </Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.commentActionButton}>
                <Icon
                  name={reply.isDisliked ? 'thumb-down' : 'thumb-down-off-alt'}
                  size={20}
                  color={reply.isDisliked ? colors.novelMain : colors.novelTextGray}
                />
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </View>
    </View>
  );

  return (
    <>
      {/* 只覆盖输入框以上区域的遮罩 */}
      <Animated.View
        pointerEvents="auto"
        style={[
          styles.repliesSheetBackdrop,
          {
            bottom: inputBarHeight,
            opacity: backdropOpacity,
          }
        ]}
      >
        <TouchableOpacity
          activeOpacity={1}
          onPress={onClose}
          style={{ flex: 1 }}
        />
      </Animated.View>

      {/* 半弹窗本体：贴着输入框顶边往上升，高度为 90% 屏幕 */}
      <Animated.View
        style={[
          styles.repliesSheetContainer,
          {
            bottom: inputBarHeight,
            height: sheetHeight,
            transform: [{ translateY: sheetTranslateY }],
          }
        ]}
      >
        {/* 顶部栏 */}
        <View style={styles.repliesSheetTopBar}>
          <TouchableOpacity onPress={onClose} style={styles.repliesSheetCloseButton}>
            <Icon name="keyboard-arrow-down" size={30} color={colors.novelText} />
          </TouchableOpacity>
          <Text style={styles.repliesSheetTitle}>评论详情</Text>
          <Icon name="more-vert" size={24} color={colors.novelText} />
        </View>

        {/* 内容区 */}
        <ScrollView
          style={styles.repliesSheetScrollView}
          contentContainerStyle={styles.repliesSheetScrollContent}
          showsVerticalScrollIndicator={false}
        >
          {/* 显示一级评论内容 */}
          {renderPrimaryComment()}

          {/* 分隔线 */}
          <View style={styles.repliesSheetDivider} />

          <View style={styles.commentListHeader}>
            <Text style={styles.commentListTitle}>全部评论</Text>
            <Text style={styles.commentCount}>{selectedComment.replies?.length}</Text>
          </View>

          {/* 显示所有回复 */}
          {selectedComment.replies?.map((reply: any) => renderReplyItem(reply))}
        </ScrollView>
      </Animated.View>
    </>
  );
};