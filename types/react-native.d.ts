declare module 'react-native' {
  export * from 'react-native-web';

  // Web端DeviceEventEmitter实现
  export const DeviceEventEmitter: {
    addListener: (eventType: string, listener: (...args: any[]) => void) => {
      remove: () => void;
    };
    emit: (eventType: string, ...args: any[]) => void;
    removeAllListeners: (eventType?: string) => void;
  };

  // Web端NativeModules空实现
  export const NativeModules: Record<string, any>;

  export const TextInput: any;
}
