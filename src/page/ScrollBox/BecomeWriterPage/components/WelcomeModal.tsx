import React from 'react';
import { View, Text, TouchableOpacity, Modal } from 'react-native';
import { useNovelColors } from '../../../../utils/theme/colors';
import LinearGradient from 'react-native-linear-gradient';

interface WelcomeModalProps {
    styles: any;
    visible: boolean;
    isAgreementChecked: boolean;
    onClose: () => void;
    onRegister: () => void;
    onAgreementChange: (checked: boolean) => void;
    onAgreementPress: () => void;
}

export const WelcomeModal: React.FC<WelcomeModalProps> = React.memo(({
  styles,
  visible,
  isAgreementChecked,
  onClose,
  onRegister,
  onAgreementChange,
  onAgreementPress,
}) => {
  const colors = useNovelColors();

  return (
    <Modal
      visible={visible}
      transparent
      animationType="fade"
      statusBarTranslucent>
      <View style={styles.modalOverlay}>
        <View style={styles.modalContainer}>
          <TouchableOpacity
            style={styles.modalCloseButton}
            onPress={onClose}
            activeOpacity={0.7}>
            <Text style={styles.modalCloseText}>✕</Text>
          </TouchableOpacity>

          <Text style={styles.modalTitle}>成为番茄作家</Text>
          <Text style={styles.modalDescription}>
            入驻番茄，开启你的创作成长计划
          </Text>
          <Text style={styles.modalSubDescription}>
            从连载起步到作品运营，创作路上持续给你支持
          </Text>

          <LinearGradient
            colors={[`${colors.novelMain}99`, colors.novelMain, colors.novelMain]}
            start={{ x: 0, y: 0 }}
            end={{ x: 1, y: 0 }}
            style={styles.modalRegisterButton}>
            <TouchableOpacity
              onPress={onRegister}
              activeOpacity={0.8}>
              <Text style={styles.modalRegisterButtonText}>申请入驻</Text>
            </TouchableOpacity>
          </LinearGradient>

          <View style={styles.modalAgreementContainer}>
            <TouchableOpacity
              style={[
                styles.modalCheckbox,
                isAgreementChecked && styles.modalCheckboxChecked,
              ]}
              onPress={() => onAgreementChange(!isAgreementChecked)}
              activeOpacity={0.7}>
              {isAgreementChecked ? (
                <Text style={styles.modalCheckboxCheck}>✓</Text>
              ) : null}
            </TouchableOpacity>
            <Text style={styles.modalAgreementText}>我已阅读并同意</Text>
            <TouchableOpacity onPress={onAgreementPress} activeOpacity={0.7}>
              <Text style={styles.modalAgreementLink}>《个人信息保护声明》</Text>
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </Modal>
  );
});
