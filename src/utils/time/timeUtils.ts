/**
 * 小说日期格式化工具类
 *
 * 功能特点：
 * - 新闻时间智能展示格式
 * - 多种时间范围适配
 * - 异常安全处理机制
 *
 * 展示规则：
 * - 今天：只显示"HH:mm"
 * - 昨天：前缀"昨天 " + "HH:mm"
 * - 本年：显示"M月d日"
 * - 往年：显示"yyyy-MM-dd HH:mm"
 *
 * 技术实现：
 * - 支持多种输入格式
 * - 线程安全的时间处理
 * - 异常安全处理机制
 */

/**
 * 将时间字符串格式化为新闻展示格式
 *
 * 支持格式：
 * - "2025-05-06T14:30:00"
 * - "2025-05-06 14:30:00"
 * - 时间戳
 * - Date对象
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @return 格式化后的时间字符串，出错返回空串
 */
export const parseNewsDate = (dateString: string | number | Date): string => {
  try {
    console.log('格式化日期:', dateString);

    // 1. 解析输入时间字符串
    let dateTime: Date;

    if (typeof dateString === 'string') {
      // 处理字符串格式
      if (dateString.includes('T')) {
        // ISO格式: "2025-05-06T14:30:00"
        dateTime = new Date(dateString);
      } else if (dateString.includes('-') && dateString.includes(':')) {
        // 格式: "2025-05-06 14:30:00"
        dateTime = new Date(dateString.replace(' ', 'T'));
      } else {
        // 其他格式尝试直接解析
        dateTime = new Date(dateString);
      }
    } else if (typeof dateString === 'number') {
      // 时间戳
      dateTime = new Date(dateString);
    } else {
      // Date对象
      dateTime = dateString;
    }

    // 检查日期是否有效
    if (isNaN(dateTime.getTime())) {
      console.warn('日期解析失败:', dateString);
      return '';
    }

    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);
    const targetDate = new Date(dateTime.getFullYear(), dateTime.getMonth(), dateTime.getDate());

    // 格式化时间部分 "HH:mm"
    const formatTime = (date: Date): string => {
      const hours = date.getHours().toString().padStart(2, '0');
      const minutes = date.getMinutes().toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    };

    // 2. 根据时间范围选择展示格式
    let result: string;

    if (targetDate.getTime() === today.getTime()) {
      // 今天：只显示时间
      result = formatTime(dateTime);                              // "HH:mm"
    } else if (targetDate.getTime() === yesterday.getTime()) {
      // 昨天：前缀"昨天"
      result = `昨天 ${formatTime(dateTime)}`;                     // "昨天 HH:mm"
    } else if (dateTime.getFullYear() === now.getFullYear()) {
      // 本年：显示月日
      const month = dateTime.getMonth() + 1;
      const day = dateTime.getDate();
      result = `${month}月${day}日`;                              // "M月d日"
    } else {
      // 往年：显示完整日期时间
      const year = dateTime.getFullYear();
      const month = (dateTime.getMonth() + 1).toString().padStart(2, '0');
      const day = dateTime.getDate().toString().padStart(2, '0');
      const time = formatTime(dateTime);
      result = `${year}-${month}-${day} ${time}`;                // "yyyy-MM-dd HH:mm"
    }

    console.log('格式化完成:', dateString, '->', result);
    return result;
  } catch (exception) {
    console.error('日期格式化失败:', dateString, exception);
    return '';                                                   // 异常时返回空串
  }
};

/**
 * 格式化相对时间（如"3分钟前"、"2小时前"等）
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @returns 相对时间字符串
 */
export const formatRelativeTime = (dateString: string | number | Date): string => {
  try {
    let date: Date;

    if (typeof dateString === 'string') {
      date = new Date(dateString.includes('T') ? dateString : dateString.replace(' ', 'T'));
    } else if (typeof dateString === 'number') {
      date = new Date(dateString);
    } else {
      date = dateString;
    }

    if (isNaN(date.getTime())) {
      return '';
    }

    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMinutes = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

    if (diffMinutes < 1) {
      return '刚刚';
    } else if (diffMinutes < 60) {
      return `${diffMinutes}分钟前`;
    } else if (diffHours < 24) {
      return `${diffHours}小时前`;
    } else if (diffDays < 7) {
      return `${diffDays}天前`;
    } else {
      // 超过7天使用标准格式
      return parseNewsDate(date);
    }
  } catch (error) {
    console.error('相对时间格式化失败:', error);
    return '';
  }
};

/**
 * 检查日期是否为今天
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @returns 是否为今天
 */
export const isToday = (dateString: string | number | Date): boolean => {
  try {
    let date: Date;

    if (typeof dateString === 'string') {
      date = new Date(dateString.includes('T') ? dateString : dateString.replace(' ', 'T'));
    } else if (typeof dateString === 'number') {
      date = new Date(dateString);
    } else {
      date = dateString;
    }

    if (isNaN(date.getTime())) {
      return false;
    }

    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const targetDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());

    return targetDate.getTime() === today.getTime();
  } catch (error) {
    console.error('日期比较失败:', error);
    return false;
  }
};

/**
 * 检查日期是否为昨天
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @returns 是否为昨天
 */
export const isYesterday = (dateString: string | number | Date): boolean => {
  try {
    let date: Date;

    if (typeof dateString === 'string') {
      date = new Date(dateString.includes('T') ? dateString : dateString.replace(' ', 'T'));
    } else if (typeof dateString === 'number') {
      date = new Date(dateString);
    } else {
      date = dateString;
    }

    if (isNaN(date.getTime())) {
      return false;
    }

    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);
    const targetDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());

    return targetDate.getTime() === yesterday.getTime();
  } catch (error) {
    console.error('日期比较失败:', error);
    return false;
  }
};

/**
 * 格式化时间为标准格式 "HH:mm"
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @returns 格式化的时间字符串
 */
export const formatTime = (dateString: string | number | Date): string => {
  try {
    let date: Date;

    if (typeof dateString === 'string') {
      date = new Date(dateString.includes('T') ? dateString : dateString.replace(' ', 'T'));
    } else if (typeof dateString === 'number') {
      date = new Date(dateString);
    } else {
      date = dateString;
    }

    if (isNaN(date.getTime())) {
      return '';
    }

    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  } catch (error) {
    console.error('时间格式化失败:', error);
    return '';
  }
};

/**
 * 格式化日期为 "M月d日" 格式
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @returns 格式化的日期字符串
 */
export const formatMonthDay = (dateString: string | number | Date): string => {
  try {
    let date: Date;

    if (typeof dateString === 'string') {
      date = new Date(dateString.includes('T') ? dateString : dateString.replace(' ', 'T'));
    } else if (typeof dateString === 'number') {
      date = new Date(dateString);
    } else {
      date = dateString;
    }

    if (isNaN(date.getTime())) {
      return '';
    }

    const month = date.getMonth() + 1;
    const day = date.getDate();
    return `${month}月${day}日`;
  } catch (error) {
    console.error('日期格式化失败:', error);
    return '';
  }
};

/**
 * 格式化完整日期时间为 "yyyy-MM-dd HH:mm" 格式
 *
 * @param dateString 时间字符串、时间戳或Date对象
 * @returns 格式化的完整日期时间字符串
 */
export const formatFullDateTime = (dateString: string | number | Date): string => {
  try {
    let date: Date;

    if (typeof dateString === 'string') {
      date = new Date(dateString.includes('T') ? dateString : dateString.replace(' ', 'T'));
    } else if (typeof dateString === 'number') {
      date = new Date(dateString);
    } else {
      date = dateString;
    }

    if (isNaN(date.getTime())) {
      return '';
    }

    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error('完整日期时间格式化失败:', error);
    return '';
  }
};

// 默认导出主要的格式化函数
export default parseNewsDate;
