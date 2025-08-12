import {useCallback} from 'react';
import {useAIStore} from '../store/aiStore';

export function useAIShortcuts() {
  const {setInput, send, toggleDeepThink} = useAIStore();
  const isDeepThink = useAIStore(s => s.deepThinkEnabled);

  const fillDeepThink = useCallback(() => {
    setInput(
      '请围绕当前大纲的核心冲突，给出3种更具戏剧性的推进方案，并指出各自的情绪峰值。',
    );
  }, [setInput]);

  const fillIdea = useCallback(() => {
    setInput(
      '给我5个开书灵感：题材偏都市+悬疑，主角具“反常识优势”，每个点子含一句钩子。',
    );
  }, [setInput]);

  const quickSend = useCallback(() => {
    send();
  }, [send]);

  const toggleDeepThinkMode = useCallback(() => {
    toggleDeepThink();
  }, [toggleDeepThink]);

  return {fillDeepThink, fillIdea, quickSend, toggleDeepThinkMode, isDeepThink};
}
