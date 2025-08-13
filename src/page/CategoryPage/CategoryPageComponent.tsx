import React, { useEffect } from 'react';
import { AppRegistry } from 'react-native';
import { initializeRNPage } from '../../utils/appInit';
import { CategoryPage } from './CategoryPage';

interface CategoryPageComponentProps {
    source?: string;
}

const CategoryPageComponent: React.FC<CategoryPageComponentProps> = ({ source }) => {
    useEffect(() => {
        console.log('[CategoryPageComponent] init, source:', source);
        initializeRNPage('CategoryPage').catch((e) => console.error('[CategoryPageComponent] init failed', e));
    }, [source]);

    return <CategoryPage />;
};

AppRegistry.registerComponent('CategoryPageComponent', () => CategoryPageComponent);

export default CategoryPageComponent;


