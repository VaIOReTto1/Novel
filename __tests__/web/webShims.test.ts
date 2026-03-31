import AsyncStorage from '../../src/web/shims/asyncStorage';
import Reanimated, {
  runOnUI,
  useSharedValue,
} from '../../src/web/shims/reactNativeReanimated';

describe('web shims', () => {
  it('stores values in async storage shim', async () => {
    await AsyncStorage.setItem('stage7-test', 'value');
    expect(await AsyncStorage.getItem('stage7-test')).toBe('value');

    await AsyncStorage.removeItem('stage7-test');
    expect(await AsyncStorage.getItem('stage7-test')).toBeNull();
  });

  it('exposes minimal reanimated-compatible primitives', () => {
    const shared = useSharedValue(1);
    expect(shared.value).toBe(1);
    expect(typeof Reanimated.createAnimatedComponent).toBe('function');

    const onUI = runOnUI((value: number) => value + 1);
    expect(onUI(2)).toBe(3);
  });
});
