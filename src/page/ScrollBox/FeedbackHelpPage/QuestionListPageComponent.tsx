import React from 'react';
import { AppRegistry } from 'react-native';
import { QuestionListPage } from './QuestionListPage';

const QuestionListPageComponent: React.FC = () => {
  return <QuestionListPage />;
};

AppRegistry.registerComponent('QuestionListPageComponent', () => QuestionListPageComponent);

console.log('[QuestionListPageComponent] Registered React Native component');

export default QuestionListPageComponent;
