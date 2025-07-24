import React from 'react';
import { View, StyleSheet } from 'react-native';

// 导入SVG图标
import QrCodeIcon from '../../assets/image/qrscan.svg';
import MoonModeIcon from '../../assets/image/moon_mode.svg';
import SettingsIcon from '../../assets/image/settings.svg';
import WalletIcon from '../../assets/image/wallet.svg';
import DownloadIcon from '../../assets/image/download.svg';
import HistoryIcon from '../../assets/image/history.svg';
import SubscribeIcon from '../../assets/image/subscribe.svg';
import GameIcon from '../../assets/image/game.svg';
import MemberIcon from '../../assets/image/member.svg';
import BeWriterIcon from '../../assets/image/be_writer.svg';
import VedioCreationIcon from '../../assets/image/vedio_creation.svg';
import ReadingPreferenceIcon from '../../assets/image/reading_preference.svg';
import NoteIcon from '../../assets/image/note.svg';
import WhoHaveSeenIcon from '../../assets/image/who_have_seen.svg';
import VedioHaveFavoritedIcon from '../../assets/image/vedio_have_favorited.svg';
import GuideIcon from '../../assets/image/guide.svg';
import PublicWelfareIcon from '../../assets/image/public_welfare.svg';
import EmailIcon from '../../assets/image/email.svg';
import OrderIcon from '../../assets/image/order.svg';
import FeedbackIcon from '../../assets/image/feedback.svg';
import RecommendBookIcon from '../../assets/image/recommend_book.svg';
import SunModeIcon from '../../assets/image/sun_mode.svg';

interface IconProps {
  name: string;
  width?: number;
  height?: number;
  color?: string;
}

const IconComponent: React.FC<IconProps> = ({
  name,
  width = 24,
  height = 24,
  color = '#333333',
}) => {
  const renderIcon = () => {
    const iconProps = { width, height, fill: color };

    switch (name) {
      case 'qrscan':
        return <QrCodeIcon {...iconProps} />;
      case 'moon_mode':
        return <MoonModeIcon {...iconProps} />;
      case 'sun_mode':
        return <SunModeIcon {...iconProps} />;
      case 'settings':
        return <SettingsIcon {...iconProps} />;
      case 'wallet':
        return <WalletIcon {...iconProps} />;
      case 'download':
        return <DownloadIcon {...iconProps} />;
      case 'history':
        return <HistoryIcon {...iconProps} />;
      case 'subscribe':
        return <SubscribeIcon {...iconProps} />;
      case 'game':
        return <GameIcon {...iconProps} />;
      case 'member':
        return <MemberIcon {...iconProps} />;
      case 'be_writer':
        return <BeWriterIcon {...iconProps} />;
      case 'vedio_creation':
        return <VedioCreationIcon {...iconProps} />;
      case 'reading_preference':
        return <ReadingPreferenceIcon {...iconProps} />;
      case 'note':
        return <NoteIcon {...iconProps} />;
      case 'who_have_seen':
        return <WhoHaveSeenIcon {...iconProps} />;
      case 'vedio_have_favorited':
        return <VedioHaveFavoritedIcon {...iconProps} />;
      case 'guide':
        return <GuideIcon {...iconProps} />;
      case 'public_welfare':
        return <PublicWelfareIcon {...iconProps} />;
      case 'email':
        return <EmailIcon {...iconProps} />;
      case 'order':
        return <OrderIcon {...iconProps} />;
      case 'feedback':
        return <FeedbackIcon {...iconProps} />;
      case 'recommend_book':
        return <RecommendBookIcon {...iconProps} />;
      default:
        // 默认返回一个占位符
        return <View style={[styles.placeholder, { width, height }]} />;
    }
  };

  return renderIcon();
};

const styles = StyleSheet.create({
  placeholder: {
    backgroundColor: '#cccccc',
    borderRadius: 4,
  },
});

export default IconComponent;
