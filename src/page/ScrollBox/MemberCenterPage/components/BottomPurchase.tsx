import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { BottomPurchaseProps } from '../types';

export const BottomPurchase: React.FC<BottomPurchaseProps & { styles: any }> = React.memo(({
  styles,
  selectedPackage,
  onPurchase,
  onPrivacyPress,
  onTermsPress,
}) => {
  return (
    <View style={styles.bottomPurchaseContainer}>
      {/* 购买按钮 */}
      <TouchableOpacity
        style={styles.purchaseButton}
        onPress={onPurchase}
        activeOpacity={0.8}
      >
        <Text style={styles.purchaseButtonText}>
          {selectedPackage?.price || '¥10'} 立即开通
        </Text>
        <Text style={styles.purchaseButtonSubtext}>
          到期前自动续费{selectedPackage?.price || '¥10'}/月
        </Text>
      </TouchableOpacity>

      {/* 协议链接 */}
      <View style={styles.agreementContainer}>
        <Text style={styles.agreementText}>已阅读并同意 </Text>
        <TouchableOpacity onPress={onTermsPress}>
          <Text style={styles.agreementLink}>会员服务条款</Text>
        </TouchableOpacity>
        <Text style={styles.agreementText}> | </Text>
        <TouchableOpacity onPress={onPrivacyPress}>
          <Text style={styles.agreementLink}>自动续费协议</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
});