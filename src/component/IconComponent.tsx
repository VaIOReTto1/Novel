import React from 'react';

import { Stage7Icon } from '../design-system/icons/Stage7Icon';

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
    <Stage7Icon
      name={`legacy.${name}`}
      width={width}
      height={height}
      color={color}
    />
  );
};

export default IconComponent;
