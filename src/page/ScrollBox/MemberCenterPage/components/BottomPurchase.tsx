import React from 'react';
import { Text, TouchableOpacity, View } from 'react-native';

import { BottomPurchaseProps } from '../types';

export const BottomPurchase: React.FC<BottomPurchaseProps & { styles: any }> =
  React.memo(({
    styles,
    selectedPackage,
    onPurchase,
    onPrivacyPress,
    onTermsPress,
  }) => {
    const price = selectedPackage?.price || '¥10';

    return (
      <View style={styles.bottomPurchaseContainer}>
        <TouchableOpacity
          style={styles.purchaseButton}
          onPress={onPurchase}
          activeOpacity={0.8}>
          <Text style={styles.purchaseButtonText}>{`${price} 立即开通`}</Text>
          <Text style={styles.purchaseButtonSubtext}>{`到期前自动续费 ${price}/月`}</Text>
        </TouchableOpacity>

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
