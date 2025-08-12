declare module 'react-native-web' {
    export const AppRegistry: {
      registerComponent: (appName: string, componentProvider: () => React.ComponentType<any>) => void;
      runApplication: (appName: string, appParameters: any) => void;
    };

    export const View: React.ComponentType<any>;
    export const Text: React.ComponentType<any>;
    export const TextInput: React.ComponentType<any>;
    export const TouchableOpacity: React.ComponentType<any>;
    export const Image: React.ComponentType<any>;
    export const ScrollView: React.ComponentType<any>;
    export const SafeAreaView: React.ComponentType<any>;
    export const StyleSheet: {
      create: <T>(styles: T) => T;
    };
    export const Dimensions: {
      get: (dimension: 'window' | 'screen') => { width: number; height: number };
    };
  }
