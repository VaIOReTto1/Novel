import { BackHandler } from 'react-native';

export const registerHardwareBackHandler = (
  onBack: () => boolean | void,
): (() => void) => {
  const subscription = BackHandler.addEventListener('hardwareBackPress', () => {
    const result = onBack();
    return typeof result === 'boolean' ? result : true;
  });

  return () => subscription.remove();
};
