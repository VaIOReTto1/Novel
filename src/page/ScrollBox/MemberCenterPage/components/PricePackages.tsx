import React, { useCallback } from 'react';
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';
import LinearGradient from 'react-native-linear-gradient';   // ← 新增
import { PricePackage } from '../types';

interface PricePackagesProps {
  styles: any;
  packages: PricePackage[];
  gradientColors: string[];               // ← 新增
  onSelectPackage: (packageId: string) => void;
}

export const PricePackages: React.FC<PricePackagesProps> = React.memo(
  ({ styles, packages, gradientColors, onSelectPackage }) => {

    const handlePackagePress = useCallback(
      (packageId: string) => onSelectPackage(packageId),
      [onSelectPackage],
    );

    return (
      <View style={styles.priceContainer}>
        <View style={styles.pricePackagesRow}>
          {packages.map((pkg) => {
            const Content = (
              <>
                <Text style={styles.priceDuration}>{pkg.duration}</Text>
                <Text style={styles.priceValue}>{pkg.price}</Text>
                {pkg.originalPrice && (
                  <Text style={styles.priceOriginal}>{pkg.originalPrice}</Text>
                )}
                {pkg.discount && (
                  <Text style={styles.priceDiscount}>{pkg.discount}</Text>
                )}
              </>
            );

            // 选中状态 ⇒ 渐变背景
            if (pkg.isSelected) {
              return (
                <LinearGradient
                  key={pkg.id}
                  colors={gradientColors}
                  locations={[0, 0.4, 1]}               // 顶部色占 60%，云雾渐散
                  start={{ x: 0.5, y: 0 }}              // 垂直自上而下
                  end={{ x: 0.5, y: 1 }}
                  style={[styles.pricePackage, styles.pricePackageSelected]}
                >
                  <TouchableOpacity
                    onPress={() => handlePackagePress(pkg.id)}
                    activeOpacity={0.8}
                  />
                  {Content}
                </LinearGradient>
              );
            }

            // 未选中 ⇒ 保持纯色背景
            return (
              <TouchableOpacity
                key={pkg.id}
                style={[
                  styles.pricePackage,
                  pkg.isRecommended && styles.pricePackageRecommended,
                ]}
                onPress={() => handlePackagePress(pkg.id)}
                activeOpacity={0.8}
              >
                {Content}
              </TouchableOpacity>
            );
          })}
        </View>
      </View>
    );
  },
);