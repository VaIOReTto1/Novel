import React from 'react';
import { useNovelColors } from '../../../../utils/theme/colors';
import Markdown from 'react-native-markdown-display';

const MarkdownTextComponent: React.FC<{ text: string }> = ({ text }) => {
  const colors = useNovelColors();
  const fontSize = 15; // 兜底字号稍大
  return (
    <Markdown
      style={{
        body: { color: colors.novelText, fontSize, lineHeight: fontSize * 1.6 },
        text: { color: colors.novelText },
        heading1: { color: colors.novelText, fontSize: fontSize + 6, marginTop: 8, marginBottom: 4, fontWeight: '700' },
        heading2: { color: colors.novelText, fontSize: fontSize + 4, marginTop: 8, marginBottom: 4, fontWeight: '700' },
        heading3: { color: colors.novelText, fontSize: fontSize + 2, marginTop: 8, marginBottom: 4, fontWeight: '600' },
        paragraph: { marginTop: 6, marginBottom: 6 },
        list_item: { marginVertical: 2 },
        bullet_list: { color: colors.novelText },
        ordered_list: { color: colors.novelText },
        blockquote: {
          backgroundColor: colors.novelSecondaryBackground,
          borderLeftColor: colors.outline,
          borderLeftWidth: 3,
          paddingHorizontal: 10,
          paddingVertical: 6,
          borderRadius: 6,
          marginVertical: 6,
        },
        code_inline: {
          backgroundColor: colors.novelSecondaryBackground,
          color: colors.novelText,
          paddingHorizontal: 4,
          paddingVertical: 2,
          borderRadius: 4,
        },
        code_block: {
          backgroundColor: colors.novelSecondaryBackground,
          color: colors.novelText,
          padding: 10,
          borderRadius: 8,
          marginVertical: 6,
        },
        hr: { backgroundColor: colors.outline, height: 1, marginVertical: 8 },
        link: { color: colors.novelMain },
        table: { borderColor: colors.outline },
        th: { backgroundColor: colors.novelSecondaryBackground, color: colors.novelText, padding: 6 },
        tr: { borderBottomColor: colors.outline, borderBottomWidth: 1 },
        td: { padding: 6, color: colors.novelText },
      }}
    >
      {text || ''}
    </Markdown>
  );
};

export const MarkdownText = React.memo(MarkdownTextComponent, (prev, next) => prev.text === next.text);


