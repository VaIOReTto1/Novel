import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { initializeRNPage } from '../../../utils/appInit';
import BookManagePage from './BookManagePage';

const BookManagePageComponent: React.FC<{ source?: string }> = ({ source }) => {
  useEffect(() => {
    initializeRNPage('BookManagePage').catch(() => {});
  }, [source]);
  return <BookManagePage />;
};

AppRegistry.registerComponent('BookManagePageComponent', () => BookManagePageComponent);

export default BookManagePageComponent;


