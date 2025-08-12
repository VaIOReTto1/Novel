import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { initializeRNPage } from '../../../utils/appInit';
import AIWriteAssistant from './AIWriteAssistant';

const AIWriteAssistantComponent: React.FC<{ source?: string }> = ({ source }) => {
  useEffect(() => {
    initializeRNPage('AIWriteAssistant').catch(() => {});
  }, [source]);
  return <AIWriteAssistant />;
};

AppRegistry.registerComponent('AIWriteAssistantComponent', () => AIWriteAssistantComponent);

export default AIWriteAssistantComponent;


