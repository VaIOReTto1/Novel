import { useState, useCallback } from 'react';
import { ViewType } from '../types';
import { ANIMATION_DURATION } from '../utils/constants';

export const useViewMode = (initialView: ViewType = 'grid') => {
  const [currentView, setCurrentView] = useState<ViewType>(initialView);
  const [isTransitioning, setIsTransitioning] = useState(false);

  const switchView = useCallback((newView: ViewType) => {
    if (newView === currentView || isTransitioning) return;

    setIsTransitioning(true);
    
    // 添加过渡动画
    setTimeout(() => {
      setCurrentView(newView);
      setTimeout(() => {
        setIsTransitioning(false);
      }, ANIMATION_DURATION / 2);
    }, ANIMATION_DURATION / 2);
  }, [currentView, isTransitioning]);

  const isGridView = currentView === 'grid';
  const isListView = currentView === 'list';
  const isWaterfallView = currentView === 'waterfall';

  return {
    currentView,
    isGridView,
    isListView,
    isWaterfallView,
    isTransitioning,
    switchView,
  };
};