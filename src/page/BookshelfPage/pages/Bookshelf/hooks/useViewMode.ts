import { useState, useCallback } from 'react';
import { ViewType } from '../types';

export const useViewMode = (initialView: ViewType = 'grid') => {
  const [currentView, setCurrentView] = useState<ViewType>(initialView);
  const [isTransitioning, setIsTransitioning] = useState(false);

  const switchView = useCallback((newView: ViewType) => {
    if (newView === currentView || isTransitioning) return;

    setIsTransitioning(true);
    
    // 添加过渡动画延迟
    setTimeout(() => {
      setCurrentView(newView);
      setIsTransitioning(false);
    }, 150);
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