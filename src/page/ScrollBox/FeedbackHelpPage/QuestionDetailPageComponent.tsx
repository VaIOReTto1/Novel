import React from 'react';
import { AppRegistry } from 'react-native';
import { QuestionDetailPage } from './QuestionDetailPage';

const QuestionDetailPageComponent: React.FC = () => {
  return <QuestionDetailPage />;
};

AppRegistry.registerComponent('QuestionDetailPageComponent', () => QuestionDetailPageComponent);

console.log('[QuestionDetailPageComponent] Registered React Native component');

export default QuestionDetailPageComponent;