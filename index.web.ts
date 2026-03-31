/**
 * @format
 */

import { AppRegistry } from 'react-native';

import App from './App';
import appConfig from './app.json';
import { Stage7Showcase } from './src/design-system/showcase/Stage7Showcase';
import { shouldRenderStage7Showcase } from './src/web/webEntryConfig';
import './src/utils/webEventEmitter';
import './src/utils/webDebugTools';

if (typeof window !== 'undefined') {
  (globalThis as any).NativeModules = {
    NavigationUtil: {
      goToLogin: () => {
        console.log('Web login navigation mock');
      },
    },
  };

  const style = document.createElement('style');
  style.textContent = `
    * {
      box-sizing: border-box;
    }
    body {
      margin: 0;
      padding: 0;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    }
    #root {
      width: 100%;
      height: 100vh;
    }
  `;
  document.head.appendChild(style);
}

const appName: string = appConfig.name;
const shouldShowStage7Showcase =
  typeof window !== 'undefined'
    ? shouldRenderStage7Showcase(window.location.search)
    : false;
const rootComponent = shouldShowStage7Showcase ? Stage7Showcase : App;

AppRegistry.registerComponent(appName, () => rootComponent);
AppRegistry.runApplication(appName, {
  rootTag: document.getElementById('root') as HTMLElement,
});
