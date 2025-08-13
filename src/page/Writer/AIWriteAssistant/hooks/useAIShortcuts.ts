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
    // 改为由 IdeaSelector 进行输入填充
    setInput('');
  }, [setInput]);

  const quickSend = useCallback(() => {
    send();
  }, [send]);

  const toggleDeepThinkMode = useCallback(() => {
    toggleDeepThink();
  }, [toggleDeepThink]);

  return {fillDeepThink, fillIdea, quickSend, toggleDeepThinkMode, isDeepThink};
}
