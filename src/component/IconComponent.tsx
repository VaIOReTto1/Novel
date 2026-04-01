import React from 'react';

import { NovelDesignIcon } from '../design-system/icons/NovelDesignIcon';

interface IconProps {
  name: string;
  width?: number;
  height?: number;
  color?: string;
}

const IconComponent: React.FC<IconProps> = ({
  name,
  width = 24,
  height = 24,
  color = '#333333',
}) => {
  return (
    <NovelDesignIcon
      name={`legacy.${name}`}
      width={width}
      height={height}
      color={color}
    />
  );
};

export default IconComponent;
