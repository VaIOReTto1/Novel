const mockRemove = jest.fn();
const mockAddListener = jest.fn((_eventName, _handler) => ({
  remove: mockRemove,
}));
const mockEmit = jest.fn();

jest.mock('react-native', () => ({
  DeviceEventEmitter: {
    addListener: mockAddListener,
    emit: mockEmit,
  },
}));

describe('RN event hub', () => {
  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('subscribes to ThemeChanged through the shared event hub', () => {
    const {
      subscribeThemeChanged,
    } = require('../../src/utils/runtime/eventHub') as typeof import('../../src/utils/runtime/eventHub');

    const cleanup = subscribeThemeChanged(jest.fn());

    expect(mockAddListener).toHaveBeenCalledWith(
      'ThemeChanged',
      expect.any(Function),
    );

    cleanup();
    expect(mockRemove).toHaveBeenCalledTimes(1);
  });

  it('emits ThemeChanged through the shared event hub', () => {
    const {
      emitThemeChanged,
    } = require('../../src/utils/runtime/eventHub') as typeof import('../../src/utils/runtime/eventHub');

    emitThemeChanged({
      colorScheme: 'dark',
      currentThemeMode: 'auto',
      followSystem: true,
    });

    expect(mockEmit).toHaveBeenCalledWith('ThemeChanged', {
      colorScheme: 'dark',
      currentThemeMode: 'auto',
      followSystem: true,
    });
  });
});
