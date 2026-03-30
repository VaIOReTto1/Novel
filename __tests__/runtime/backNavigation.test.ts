const mockRemove = jest.fn();
const mockAddEventListener = jest.fn((_eventName, handler) => ({
  remove: mockRemove,
  handler,
}));

jest.mock('react-native', () => ({
  BackHandler: {
    addEventListener: mockAddEventListener,
  },
}));

describe('RN back navigation policy', () => {
  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
  });

  it('registers a hardware back handler and returns cleanup', () => {
    const {
      registerHardwareBackHandler,
    } = require('../../src/utils/runtime/backNavigation') as typeof import('../../src/utils/runtime/backNavigation');

    const onBack = jest.fn(() => true);
    const cleanup = registerHardwareBackHandler(onBack);

    expect(mockAddEventListener).toHaveBeenCalledWith(
      'hardwareBackPress',
      expect.any(Function),
    );

    const handler = mockAddEventListener.mock.calls[0][1];
    expect(handler()).toBe(true);
    expect(onBack).toHaveBeenCalledTimes(1);

    cleanup();
    expect(mockRemove).toHaveBeenCalledTimes(1);
  });
});
