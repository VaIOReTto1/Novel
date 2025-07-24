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
    return (
        <Modal
            visible={visible}
            transparent={true}
            animationType="fade"
            statusBarTranslucent={true}
        >
            <View style={styles.modalOverlay}>
                <View style={styles.modalContainer}>
                    {/* 关闭按钮 */}
                    <TouchableOpacity
                        style={styles.modalCloseButton}
                        onPress={onClose}
                        activeOpacity={0.7}
                    >
                        <Text style={styles.modalCloseText}>✕</Text>
                    </TouchableOpacity>

                    {/* 标题 */}
                    <Text style={styles.modalTitle}>成为番茄作家</Text>

                    {/* 描述文字 */}
                    <Text style={styles.modalDescription}>
                        入驻番茄，享亿级现金扶持内容
                    </Text>
                    <Text style={styles.modalSubDescription}>
                        番茄助你成长，成神之路不孤单
                    </Text>

                    {/* 立即入驻按钮 */}
                    <LinearGradient
                        colors={[
                            // 20% 透明度；如果 novelMain 是 "#FF995D"，可这样写：
                            useNovelColors().novelMain + '99',
                            useNovelColors().novelMain,
                            useNovelColors().novelMain
                        ]}
                        start={{ x: 0, y: 0 }}
                        end={{ x: 1, y: 0 }}
                        style={styles.modalRegisterButton}
                    >
                        <TouchableOpacity
                            onPress={onRegister}
                            activeOpacity={0.8}
                        >
                            <Text style={styles.modalRegisterButtonText}>立即入驻</Text>
                        </TouchableOpacity>
                    </LinearGradient>

                    {/* 协议复选框 */}
                    <View style={styles.modalAgreementContainer}>
                        <TouchableOpacity
                            style={[
                                styles.modalCheckbox,
                                isAgreementChecked && styles.modalCheckboxChecked
                            ]}
                            onPress={() => onAgreementChange(!isAgreementChecked)}
                            activeOpacity={0.7}
                        >
                            {isAgreementChecked && (
                                <Text style={styles.modalCheckboxCheck}>✓</Text>
                            )}
                        </TouchableOpacity>
                        <Text style={styles.modalAgreementText}>
                            我已阅读并同意
                        </Text>
                        <TouchableOpacity onPress={onAgreementPress} activeOpacity={0.7}>
                            <Text style={styles.modalAgreementLink}>
                                《个人信息保护声明》
                            </Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </View>
        </Modal>
    );
});