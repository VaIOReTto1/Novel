/**
 * @format
 */

import { AppRegistry } from 'react-native';
import App from './App';
import appConfig from './app.json';
import './src/page/BookshelfPage/BookshelfPageComponent';


const appName = appConfig.name;

// 注册根组件
AppRegistry.registerComponent(appName, () => App);
