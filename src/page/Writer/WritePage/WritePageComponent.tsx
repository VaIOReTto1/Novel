import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { initializeRNPage } from '../../../utils/appInit';
import WritePage from './WritePage';

const WritePageComponent: React.FC<{ source?: string }> = ({ source }) => {
  useEffect(() => {
    initializeRNPage('WritePage').catch(() => {});
  }, [source]);
  return <WritePage />;
};

AppRegistry.registerComponent('WritePageComponent', () => WritePageComponent);

export default WritePageComponent;


