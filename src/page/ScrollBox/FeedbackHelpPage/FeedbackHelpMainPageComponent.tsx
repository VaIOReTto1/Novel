import React from 'react';
import { AppRegistry } from 'react-native';
import { FeedbackHelpMainPage } from './FeedbackHelpMainPage';

const FeedbackHelpMainPageComponent: React.FC = () => {
  return <FeedbackHelpMainPage />;
};

AppRegistry.registerComponent('FeedbackHelpMainPageComponent', () => FeedbackHelpMainPageComponent);

console.log('[FeedbackHelpMainPageComponent] Registered React Native component');

export default FeedbackHelpMainPageComponent;