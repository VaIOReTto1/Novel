import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import { Text, TouchableOpacity } from 'react-native';

jest.mock('../../src/utils/theme', () => ({
  useNovelColors: () => ({
    novelMain: '#C96A34',
    novelText: '#201A17',
    novelTextGray: '#6F6258',
    novelBackground: '#FFFDFC',
  }),
}));

jest.mock('../../src/page/BookshelfPage/pages/Bookshelf/styles/BookshelfPageStyles', () => ({
  createBookshelfPageStyles: () =>
    new Proxy(
      {},
      {
        get: (_target, prop) => String(prop),
      },
    ),
}));

import { EditToolbar } from '../../src/page/BookshelfPage/pages/Bookshelf/components/EditToolbar';

describe('Bookshelf EditToolbar novelDesign behavior', () => {
  it('renders entry button when not in edit mode', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <EditToolbar
          isEditMode={false}
          selectedCount={0}
          totalCount={0}
          onEnterEdit={jest.fn()}
          onExitEdit={jest.fn()}
          onSelectAll={jest.fn()}
          onDelete={jest.fn()}
        />,
      );
    });

    const texts = renderer.root
      .findAllByType(Text)
      .map((node) => node.props.children)
      .flat();
    expect(texts).toContain('编辑');
  });

  it('renders cancel/select-all and disables destructive actions without selection', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <EditToolbar
          isEditMode
          selectedCount={0}
          totalCount={3}
          onEnterEdit={jest.fn()}
          onExitEdit={jest.fn()}
          onSelectAll={jest.fn()}
          onDelete={jest.fn()}
          onMove={jest.fn()}
        />,
      );
    });

    const texts = renderer.root
      .findAllByType(Text)
      .map((node) => node.props.children)
      .flat();
    expect(texts).toEqual(
      expect.arrayContaining(['取消', '全选', '移动', '删除']),
    );
    expect(texts.join('')).toContain('已选择 0 项');

    const buttons = renderer.root.findAllByType(TouchableOpacity);
    const moveButton = buttons[2];
    const deleteButton = buttons[3];

    expect(moveButton.props.disabled).toBe(true);
    expect(deleteButton.props.disabled).toBe(true);
  });

  it('switches select-all label when everything is selected', () => {
    let renderer!: ReactTestRenderer.ReactTestRenderer;

    ReactTestRenderer.act(() => {
      renderer = ReactTestRenderer.create(
        <EditToolbar
          isEditMode
          selectedCount={3}
          totalCount={3}
          onEnterEdit={jest.fn()}
          onExitEdit={jest.fn()}
          onSelectAll={jest.fn()}
          onDelete={jest.fn()}
        />,
      );
    });

    const texts = renderer.root
      .findAllByType(Text)
      .map((node) => node.props.children)
      .flat();
    expect(texts).toContain('取消全选');
  });
});
