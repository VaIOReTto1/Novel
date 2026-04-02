import React from 'react';
import { Animated, Image, ScrollView, Text, TouchableOpacity, View } from 'react-native';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { parseNewsDate } from '../../../../utils/time/timeUtils';
import { useReviewDetailPageStyles } from '../hooks/useReviewDetailPageStyles';

const backdropTouchAreaStyle = { flex: 1 } as const;

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

  if (!selectedComment || !showRepliesSheet) {
    return null;
  }

  const renderTagText = (tag?: string) => {
    if (tag === 'first_comment') {
      return '首评';
    }
    if (tag === 'true_fan') {
      return '真爱粉';
    }
    if (tag === 'vip') {
      return 'VIP';
    }
    return '';
  };

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
              {selectedComment.tag ? (
                <View
                  style={[
                    styles.commentTag,
                    selectedComment.tag === 'first_comment'
                      ? styles.commentTag_first_comment
                      : selectedComment.tag === 'true_fan'
                        ? styles.commentTag_true_fan
                        : selectedComment.tag === 'vip'
                          ? styles.commentTag_vip
                          : {},
                  ]}>
                  <Text style={styles.commentTagText}>
                    {renderTagText(selectedComment.tag)}
                  </Text>
                </View>
              ) : null}
            </View>
            <TouchableOpacity style={styles.followButton} activeOpacity={0.7}>
              <Text style={styles.followButtonText}>+ 关注</Text>
            </TouchableOpacity>
          </View>
          <Text style={styles.commentContent}>{selectedComment.content}</Text>
          <View style={styles.commentActions}>
            <View style={styles.commentLeftActions}>
              <Text style={styles.commentTime}>
                {parseNewsDate(selectedComment.createTime)}
              </Text>
            </View>
            <View style={styles.commentRightActions}>
              <TouchableOpacity style={styles.commentActionButton}>
                <Icon
                  name={selectedComment.isLiked ? 'favorite' : 'favorite-border'}
                  size={20}
                  color={selectedComment.isLiked ? colors.novelMain : colors.novelTextGray}
                />
                <Text
                  style={[
                    styles.commentActionText,
                    selectedComment.isLiked && { color: colors.novelMain },
                  ]}>
                  {selectedComment.likeCount}
                </Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.commentActionButton}>
                <Icon
                  name={selectedComment.isDisliked ? 'thumb-down' : 'thumb-down-off-alt'}
                  size={20}
                  color={
                    selectedComment.isDisliked ? colors.novelMain : colors.novelTextGray
                  }
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
            <Image source={{ uri: reply.userAvatar }} style={styles.commentAvatarImage} />
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
                <Text
                  style={[
                    styles.commentActionText,
                    reply.isLiked && { color: colors.novelMain },
                  ]}>
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
      <Animated.View
        pointerEvents="auto"
        style={[
          styles.repliesSheetBackdrop,
          {
            bottom: inputBarHeight,
            opacity: backdropOpacity,
          },
        ]}>
        <TouchableOpacity
          activeOpacity={1}
          onPress={onClose}
          style={backdropTouchAreaStyle}
        />
      </Animated.View>

      <Animated.View
        style={[
          styles.repliesSheetContainer,
          {
            bottom: inputBarHeight,
            height: sheetHeight,
            transform: [{ translateY: sheetTranslateY }],
          },
        ]}>
        <View style={styles.repliesSheetTopBar}>
          <TouchableOpacity onPress={onClose} style={styles.repliesSheetCloseButton}>
            <Icon name="keyboard-arrow-down" size={30} color={colors.novelText} />
          </TouchableOpacity>
          <Text style={styles.repliesSheetTitle}>评论详情</Text>
          <Icon name="more-vert" size={24} color={colors.novelText} />
        </View>

        <ScrollView
          style={styles.repliesSheetScrollView}
          contentContainerStyle={styles.repliesSheetScrollContent}
          showsVerticalScrollIndicator={false}>
          {renderPrimaryComment()}

          <View style={styles.repliesSheetDivider} />

          <View style={styles.commentListHeader}>
            <Text style={styles.commentListTitle}>全部评论</Text>
            <Text style={styles.commentCount}>{selectedComment.replies?.length}</Text>
          </View>

          {selectedComment.replies?.map((reply: any) => renderReplyItem(reply))}
        </ScrollView>
      </Animated.View>
    </>
  );
};
