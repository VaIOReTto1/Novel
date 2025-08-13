/**
 * @format
 */

import { AppRegistry } from 'react-native';
import App from './App';
import appConfig from './app.json';
import './src/page/BookshelfPage/BookshelfPageComponent';
import './src/page/CategoryPage/CategoryPageComponent';
import './src/page/comment/CommentPage/CommentPageComponent';
import './src/page/comment/ReviewDetailPage/ReviewDetailPageComponent';
import './src/page/comment/WriteReviewPage/WriteReviewPageComponent';
import './src/page/CategoryPage/CategoryPageComponent';


const appName = appConfig.name;

// 注册根组件
AppRegistry.registerComponent(appName, () => App);
