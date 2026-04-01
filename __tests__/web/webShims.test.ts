import AsyncStorage from '../../src/web/shims/asyncStorage';
import Reanimated, {
  runOnUI,
  useSharedValue,
} from '../../src/web/shims/reactNativeReanimated';

describe('web shims', () => {
  it('stores values in async storage shim', async () => {
    await AsyncStorage.setItem('novel-design-test', 'value');
    expect(await AsyncStorage.getItem('novel-design-test')).toBe('value');

    await AsyncStorage.removeItem('novel-design-test');
    expect(await AsyncStorage.getItem('novel-design-test')).toBeNull();
  });

  it('exposes minimal reanimated-compatible primitives', () => {
    const shared = useSharedValue(1);
    expect(shared.value).toBe(1);
    expect(typeof Reanimated.createAnimatedComponent).toBe('function');

    const onUI = runOnUI((value: number) => value + 1);
    expect(onUI(2)).toBe(3);
  });
});
