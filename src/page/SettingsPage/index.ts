// 导出各个页面组件
export { default as SettingsPage } from './settingspage/SettingsPage';
export { default as SettingsPageComponent } from './settingspage/SettingsPageComponent';

export { default as HelpSupportPage } from './helpsupportPage/HelpSupportPage';
export { default as HelpSupportPageComponent } from './helpsupportPage/HelpSupportPageComponent';

export { default as PrivacyPolicyPage } from './privacypolicyPage/PrivacyPolicyPage';
export { default as PrivacyPolicyPageComponent } from './privacypolicyPage/PrivacyPolicyPageComponent';

export { default as TimedSwitchPage } from './TimeSwitchPage/TimedSwitchPage';
export { default as TimedSwitchPageComponent } from './TimeSwitchPage/TimedSwitchPageComponent';

// 导出共享的组件和类型
export { SettingRow } from './settingspage/components';
export { TimePickerModal } from './TimeSwitchPage/components';
export { useSettingsStore } from './settingspage/store/settingsStore';
export type { SettingItem, SettingsSection, ColorScheme, SettingsStore } from './settingspage/types';
