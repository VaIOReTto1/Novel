/* eslint-env jest */

const React = require('react');
const ReactNative = require('react-native');

const createNativeComponentMock = () => {
  return ({ children, ...props }) =>
    React.createElement(ReactNative.View, props, children);
};

jest.mock(
  '@react-native-async-storage/async-storage',
  () =>
    require('@react-native-async-storage/async-storage/jest/async-storage-mock'),
);

jest.mock('react-native-reanimated', () => {
  const ReactNativeModule = require('react-native');
  const createAnimatedComponent = (component) => component;
  const Animated = {
    View: ReactNativeModule.View,
    Text: ReactNativeModule.Text,
    ScrollView: ReactNativeModule.ScrollView,
    FlatList: ReactNativeModule.FlatList,
    createAnimatedComponent,
  };

  const Reanimated = {
    __esModule: true,
    default: Animated,
    View: ReactNativeModule.View,
    Text: ReactNativeModule.Text,
    ScrollView: ReactNativeModule.ScrollView,
    FlatList: ReactNativeModule.FlatList,
    createAnimatedComponent,
    useSharedValue: (value) => ({ value }),
    useAnimatedStyle: (updater) => updater(),
    useAnimatedProps: (updater) => updater(),
    useAnimatedScrollHandler: () => jest.fn(),
    useDerivedValue: (updater) => ({ value: updater() }),
    withTiming: (value) => value,
    interpolate: (_value, _input, output) => output[0],
    Extrapolate: {
      CLAMP: 'clamp',
    },
    runOnJS: (fn) => fn,
    runOnUI: (fn) => fn,
  };

  return Reanimated;
});

jest.mock('react-native-linear-gradient', () => createNativeComponentMock());

jest.mock('react-native-vector-icons/MaterialIcons', () => 'MaterialIcon');
jest.mock('react-native-vector-icons/Feather', () => 'FeatherIcon');

jest.mock('react-native-svg', () => {
  const MockComponent = createNativeComponentMock();

  return {
    __esModule: true,
    default: MockComponent,
    Svg: MockComponent,
    Circle: MockComponent,
    ClipPath: MockComponent,
    Defs: MockComponent,
    Ellipse: MockComponent,
    G: MockComponent,
    Line: MockComponent,
    LinearGradient: MockComponent,
    Mask: MockComponent,
    Path: MockComponent,
    Polygon: MockComponent,
    Polyline: MockComponent,
    RadialGradient: MockComponent,
    Rect: MockComponent,
    Stop: MockComponent,
    Text: MockComponent,
    TSpan: MockComponent,
  };
});
