import { useCallback } from 'react';
import { useWriteStore } from '../store/writeStore';
import { NavigationBridge } from '../../../../utils/bridge/NavigationBridge';

export function useWriteActions() {
  const { title, content, setTitle, setContent, publish, undo, redo } = useWriteStore();

  const goBack = useCallback(() => {
    NavigationBridge.navigateBack?.('WritePageComponent');
  }, []);

  const goAI = useCallback(() => {
    NavigationBridge.navigateToAIPage?.();
  }, []);

  return {
    title,
    content,
    setTitle,
    setContent,
    publish,
    undo,
    redo,
    goBack,
    goAI,
  };
}


