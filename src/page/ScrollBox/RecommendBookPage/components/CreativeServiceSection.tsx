import React, { useCallback } from 'react';
import { Text, TouchableOpacity, View } from 'react-native';
import { ServiceItem } from '../types';

interface CreativeServiceSectionProps {
    styles: any;
    services: ServiceItem[];
    onServicePress: (serviceId: string) => void;
}

export const CreativeServiceSection: React.FC<CreativeServiceSectionProps> = React.memo(({
    styles,
    services,
    onServicePress,
}) => {
    const handleServicePress = useCallback((serviceId: string) => {
        onServicePress(serviceId);
    }, [onServicePress]);

    return (
        <View style={styles.section}>
            <View style={styles.sectionHeader}>
                <Text style={styles.sectionTitle}>创作服务</Text>
            </View>


            <View style={styles.servicesList}>
                {services.map((service) => (
                    <TouchableOpacity
                        key={service.id}
                        style={styles.serviceItem}
                        onPress={() => handleServicePress(service.id)}
                        activeOpacity={0.7}
                    >
                        <View style={styles.serviceIconWrapper}>
                            <Text style={styles.serviceIcon}>{service.icon}</Text>
                        </View>
                        <View style={styles.serviceTextGroup}>
                            <Text style={styles.serviceTitle}>{service.title}</Text>
                        </View>
                        <Text style={styles.serviceArrow}>{'>'}</Text>
                    </TouchableOpacity>
                ))}
            </View>
        </View>
    );
});
