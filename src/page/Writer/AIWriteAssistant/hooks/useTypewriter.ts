import {useEffect, useRef, useState} from 'react';

/**
 * 将传入的字符串以打字机动画输出。
 * - 当 fullText 变长时，逐字增长到最新长度
 * - 当 fullText 变短时，立即收敛到较短长度（避免残留）
 */
export function useTypewriter(fullText: string, msPerChar = 20) {
  const [typedLength, setTypedLength] = useState(0);
  const textRef = useRef(fullText);
  textRef.current = fullText;

  useEffect(() => {
    if (!fullText) {
      setTypedLength(0);
      return;
    }

    // 立刻收敛到更短的文本长度
    if (typedLength > fullText.length) {
      setTypedLength(fullText.length);
    }

    const timer = setInterval(() => {
      setTypedLength(len => {
        const target = textRef.current.length;
        if (len >= target) {return len;}
        return Math.min(target, len + 1);
      });
    }, msPerChar);

    return () => clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [fullText, msPerChar]);

  return fullText.slice(0, typedLength);
}
