import { create } from 'zustand';
import { immer } from 'zustand/middleware/immer';
import { 
  FeedbackHelpState, 
  ConsultCategory, 
  FrequentQuestion, 
  QuestionDetail 
} from '../types';

// Mock 咨询场景数据
const mockConsultCategories: ConsultCategory[] = [
  {
    id: 'member',
    title: '会员问题',
    icon: '👑',
    bgGradient: ['#ffb3ba', '#ffdfba'],
    items: ['开通续费', '类型权益', '会员特权', '价格政策']
  },
  {
    id: 'account',
    title: '账号问题',
    icon: '👤',
    bgGradient: ['#baffc9', '#bae1ff'],
    items: ['登录问题', '资料修改', '账号换绑', '密码重置']
  },
  {
    id: 'subscription',
    title: '开通续费',
    icon: '💳',
    bgGradient: ['#ffb3ba', '#ffdfba'],
    items: ['支付问题', '续费失败', '退款申请', '发票开具']
  },
  {
    id: 'benefits',
    title: '类型权益',
    icon: '🎁',
    bgGradient: ['#ffffba', '#ffb3ba'],
    items: ['VIP权益', '免广告', '离线下载', '专属内容']
  },
  {
    id: 'reading',
    title: '看书问题',
    icon: '📖',
    bgGradient: ['#baffc9', '#ffffba'],
    items: ['找书催更', '低质侵权', '下载导入', '阅读设置']
  },
  {
    id: 'listening',
    title: '听书问题',
    icon: '🎧',
    bgGradient: ['#bae1ff', '#ffdfba'],
    items: ['听书方式', '听书下载', '听书异常', '音质问题']
  }
];

// Mock 常见问题数据
const mockFrequentQuestions: FrequentQuestion[] = [
  {
    id: 'q1',
    title: '书籍下架/分类消失',
    category: 'reading',
    priority: 1,
    viewCount: 15847
  },
  {
    id: 'q2',
    title: '如何打开/关闭评论、段评、章评',
    category: 'reading',
    priority: 2,
    viewCount: 12456
  },
  {
    id: 'q3',
    title: '成为作者/作者收益/作者数据问题',
    category: 'member',
    priority: 3,
    viewCount: 9876
  },
  {
    id: 'q4',
    title: '没有找到我的问题怎么办?',
    category: 'other',
    priority: 4,
    viewCount: 7654
  },
  {
    id: 'q5',
    title: 'VIP会员有什么权益?',
    category: 'member',
    priority: 5,
    viewCount: 8765
  },
  {
    id: 'q6',
    title: '听书功能如何使用?',
    category: 'listening',
    priority: 6,
    viewCount: 6543
  },
  {
    id: 'q7',
    title: '如何修改个人资料?',
    category: 'account',
    priority: 7,
    viewCount: 5432
  },
  {
    id: 'q8',
    title: '支付失败怎么办?',
    category: 'subscription',
    priority: 8,
    viewCount: 4321
  },
  {
    id: 'q9',
    title: '如何开通VIP会员?',
    category: 'subscription',
    priority: 9,
    viewCount: 3987
  },
  {
    id: 'q10',
    title: '忘记密码怎么办?',
    category: 'account',
    priority: 10,
    viewCount: 3654
  },
  {
    id: 'q11',
    title: '如何下载离线阅读?',
    category: 'reading',
    priority: 11,
    viewCount: 3321
  },
  {
    id: 'q12',
    title: '听书音质问题',
    category: 'listening',
    priority: 12,
    viewCount: 2987
  },
  {
    id: 'q13',
    title: '会员自动续费如何取消?',
    category: 'subscription',
    priority: 13,
    viewCount: 2654
  },
  {
    id: 'q14',
    title: '如何绑定手机号?',
    category: 'account',
    priority: 14,
    viewCount: 2321
  },
  {
    id: 'q15',
    title: '阅读记录丢失怎么办?',
    category: 'reading',
    priority: 15,
    viewCount: 1987
  },
  {
    id: 'q16',
    title: '听书下载失败',
    category: 'listening',
    priority: 16,
    viewCount: 1654
  },
  {
    id: 'q17',
    title: 'VIP特权无法使用',
    category: 'benefits',
    priority: 17,
    viewCount: 1432
  },
  {
    id: 'q18',
    title: '如何申请退款?',
    category: 'subscription',
    priority: 18,
    viewCount: 1298
  }
];

// Mock 问题详情数据
const mockQuestionDetails: { [id: string]: QuestionDetail } = {
  'q1': {
    id: 'q1',
    title: '书籍下架/分类消失',
    content: `您好，若您遇到了书籍下架/分类消失的问题，可能因为内容调整中，感谢您的理解和支持。我们在书城为您推荐了更多好书，赶紧去看看吧~

常见原因：
1. 内容审核调整
2. 版权方要求下架
3. 内容违规处理
4. 系统维护更新

解决方案：
1. 查看书城推荐内容
2. 关注官方公告
3. 联系客服咨询
4. 加入书友群获取最新信息`,
    category: 'reading',
    tags: ['书籍下架', '内容调整', '版权', '推荐'],
    isResolved: false,
    relatedQuestions: ['q2', 'q5'],
    lastUpdated: '2024-01-15 14:30:00'
  },
  'q2': {
    id: 'q2', 
    title: '如何打开/关闭评论、段评、章评',
    content: `您可以在阅读设置中管理评论显示功能：

操作步骤：
1. 打开任意书籍阅读页面
2. 点击屏幕中央调出菜单
3. 选择"设置"图标
4. 找到"评论设置"选项
5. 根据需要开启或关闭各类评论

评论类型说明：
• 段评：显示在段落旁的评论
• 章评：章节结尾的评论区
• 书评：整本书的评价和讨论

温馨提示：
关闭评论后可以获得更专注的阅读体验，随时可以重新开启。`,
    category: 'reading',
    tags: ['评论设置', '阅读体验', '段评', '章评'],
    isResolved: true,
    relatedQuestions: ['q1', 'q7'],
    lastUpdated: '2024-01-14 16:45:00'
  },
  'q3': {
    id: 'q3',
    title: '成为作者/作者收益/作者数据问题', 
    content: `关于成为作者的相关问题，为您详细说明：

成为作者条件：
1. 年满18周岁
2. 具备基本写作能力
3. 同意平台创作协议
4. 通过身份认证

作者收益说明：
• 订阅分成：读者付费阅读的分成收益
• 奖励收益：平台活动和创作奖励
• 广告分成：作品页面广告收益分成
• 其他收益：推荐、打赏等额外收入

数据查看：
1. 登录作者后台
2. 查看"收益统计"
3. 查看"作品数据"
4. 下载收益明细

如需了解更多详情，请联系编辑或客服。`,
    category: 'member',
    tags: ['作者申请', '收益分成', '数据统计', '创作'],
    isResolved: false,
    relatedQuestions: ['q5', 'q8'],
    lastUpdated: '2024-01-13 10:20:00'
  },
  'q4': {
    id: 'q4',
    title: '没有找到我的问题怎么办?',
    content: `如果您没有找到相关问题的解答，可以通过以下方式获得帮助：

联系方式：
📞 客服热线：957124
🕐 工作时间：8:30-22:00

📧 在线客服：
1. 点击页面右下角客服图标
2. 选择"在线咨询"
3. 描述您遇到的问题
4. 等待客服回复

📝 意见反馈：
1. 点击下方"意见反馈"
2. 详细描述问题
3. 留下联系方式
4. 我们会及时跟进处理

💬 社区求助：
加入官方QQ群或微信群，与其他用户交流经验。

我们承诺：
• 24小时内回复您的问题
• 提供专业的解决方案
• 持续改进服务质量`,
    category: 'other',
    tags: ['客服热线', '在线咨询', '意见反馈', '社区'],
    isResolved: true,
    relatedQuestions: ['q1', 'q8'],
    lastUpdated: '2024-01-12 18:00:00'
  },
  'q5': {
    id: 'q5',
    title: 'VIP会员有什么权益?',
    content: `VIP会员享有多项专属权益，让您的阅读体验更加精彩：

📚 阅读权益：
• 海量VIP专属图书免费阅读
• 新书抢先看，提前获取更新
• 无广告纯净阅读环境
• 高清图书资源优先访问

🎵 听书权益：
• VIP听书内容免费收听
• 高品质音频体验
• 离线下载，随时随地听书
• 专业播音员朗读

⭐ 专属服务：
• VIP专属客服通道
• 优先技术支持
• 个性化推荐算法
• 会员专属活动参与资格

💰 经济优惠：
• 付费内容会员折扣
• 充值返赠优惠
• 周边商品会员价
• 线下活动门票优惠

立即开通VIP，享受全方位阅读特权！`,
    category: 'member',
    tags: ['VIP权益', '会员特权', '免费阅读', '听书'],
    isResolved: true,
    relatedQuestions: ['q3', 'q6'],
    lastUpdated: '2024-01-11 14:15:00'
  },
  'q6': {
    id: 'q6',
    title: '听书功能如何使用?',
    content: `听书功能让您在忙碌时也能享受阅读乐趣：

🎧 开启听书：
1. 打开任意支持听书的图书
2. 点击页面底部"听书"按钮
3. 选择合适的播音员和语速
4. 点击播放开始收听

⚙️ 听书设置：
• 播放速度：0.5x - 2.0x可调
• 定时关闭：支持15分钟到2小时
• 后台播放：支持锁屏继续播放
• 断点续听：自动记录播放进度

📱 操作技巧：
• 双击快进/快退15秒
• 长按调节播放速度
• 摇一摇切换下一章
• 耳机线控支持暂停/播放

💾 离线下载：
VIP用户可下载听书内容，无网络也能收听。`,
    category: 'listening',
    tags: ['听书使用', '播放设置', '离线下载', '操作技巧'],
    isResolved: true,
    relatedQuestions: ['q5', 'q12'],
    lastUpdated: '2024-01-10 11:30:00'
  },
  'q7': {
    id: 'q7',
    title: '如何修改个人资料?',
    content: `修改个人资料让您的账号信息更完善：

📝 修改步骤：
1. 进入"我的"页面
2. 点击头像或"编辑资料"
3. 选择要修改的信息项
4. 输入新信息并保存

✏️ 可修改内容：
• 昵称：支持中英文、数字
• 头像：上传本地图片或选择默认
• 性别：男/女/保密
• 生日：用于个性化推荐
• 简介：展示个人特色

🔒 安全验证：
修改重要信息（如绑定手机）需要：
• 输入当前密码
• 短信验证码确认
• 邮箱验证（如已绑定）

💡 温馨提示：
昵称30天内只能修改一次，请谨慎操作。`,
    category: 'account',
    tags: ['个人资料', '昵称修改', '头像上传', '安全验证'],
    isResolved: true,
    relatedQuestions: ['q10', 'q14'],
    lastUpdated: '2024-01-09 15:20:00'
  },
  'q8': {
    id: 'q8',
    title: '支付失败怎么办?',
    content: `遇到支付问题时，请按以下步骤排查：

🔍 常见原因：
• 网络连接不稳定
• 银行卡余额不足
• 支付密码错误
• 银行系统维护
• 超出单日限额

💳 解决方案：
1. 检查网络连接
2. 确认账户余额充足
3. 重新输入支付密码
4. 尝试其他支付方式
5. 联系银行客服

🛠️ 其他方法：
• 清除应用缓存后重试
• 更新到最新版本
• 重启手机后再次尝试
• 使用其他设备支付

📞 仍无法解决？
请联系客服：957124
提供订单号和错误截图，我们将快速为您处理。`,
    category: 'subscription',
    tags: ['支付失败', '网络问题', '银行卡', '客服'],
    isResolved: true,
    relatedQuestions: ['q9', 'q13'],
    lastUpdated: '2024-01-08 09:45:00'
  }
};

// 生成更多问题详情
const generateMoreQuestionDetails = (): { [id: string]: QuestionDetail } => {
  const details: { [id: string]: QuestionDetail } = {
    'q9': {
      id: 'q9',
      title: '如何开通VIP会员?',
      content: `开通VIP会员，享受更多阅读特权：

💎 开通方式：
1. 进入"我的"页面
2. 点击"开通VIP"
3. 选择合适的套餐
4. 选择支付方式完成付费

📋 套餐选择：
• 月卡：30天VIP权益
• 季卡：90天VIP权益，更优惠
• 年卡：365天VIP权益，最划算
• 连续包月：自动续费，享受折扣

💳 支付方式：
• 微信支付
• 支付宝
• 银行卡支付
• 话费充值

🎁 新用户福利：
首次开通享受7天免费试用，不满意可随时取消。`,
      category: 'subscription',
      tags: ['VIP开通', '套餐选择', '支付方式', '新用户'],
      isResolved: true,
      relatedQuestions: ['q5', 'q8'],
      lastUpdated: '2024-01-07 16:30:00'
    },
    'q10': {
      id: 'q10',
      title: '忘记密码怎么办?',
      content: `忘记密码时可通过以下方式重置：

📱 手机号重置：
1. 登录页面点击"忘记密码"
2. 输入绑定的手机号
3. 获取短信验证码
4. 设置新密码

📧 邮箱重置：
1. 选择"邮箱找回"
2. 输入绑定邮箱地址
3. 查收重置邮件
4. 点击链接设置新密码

🔐 密码要求：
• 长度8-20位
• 包含字母和数字
• 建议包含特殊字符
• 不要使用生日等简单密码

⚠️ 安全提醒：
重置后请及时登录，确保账号安全。`,
      category: 'account',
      tags: ['密码重置', '手机验证', '邮箱找回', '账号安全'],
      isResolved: true,
      relatedQuestions: ['q7', 'q14'],
      lastUpdated: '2024-01-06 13:45:00'
    },
    'q11': {
      id: 'q11',
      title: '如何下载离线阅读?',
      content: `离线下载让您随时随地享受阅读：

📥 下载步骤：
1. 打开要下载的图书
2. 点击"下载"按钮
3. 选择下载章节范围
4. 等待下载完成

⚙️ 下载设置：
• 仅WiFi下载：节省流量
• 下载质量：高清/标清可选
• 存储位置：内部存储/SD卡
• 自动下载：订阅书籍自动下载新章节

💾 存储管理：
• 查看已下载内容
• 删除不需要的章节
• 清理缓存释放空间
• 设置存储上限

🔋 省电模式：
下载时建议连接充电器，避免电量不足中断下载。`,
      category: 'reading',
      tags: ['离线下载', '存储管理', '下载设置', '省电'],
      isResolved: true,
      relatedQuestions: ['q1', 'q15'],
      lastUpdated: '2024-01-05 10:20:00'
    },
    'q12': {
      id: 'q12',
      title: '听书音质问题',
      content: `听书音质问题解决方案：

🎵 音质设置：
1. 进入听书界面
2. 点击设置图标
3. 选择"音质设置"
4. 选择高品质音频

🔧 常见问题：
• 声音断断续续：检查网络连接
• 音量太小：调节系统音量和应用音量
• 有杂音：重启应用或重新下载
• 播放卡顿：清理缓存后重试

📶 网络优化：
• 使用稳定的WiFi网络
• 避免网络高峰期使用
• 预先下载避免在线播放问题

🎧 设备建议：
使用优质耳机或音响设备，获得更好的听书体验。`,
      category: 'listening',
      tags: ['音质设置', '播放问题', '网络优化', '设备建议'],
      isResolved: true,
      relatedQuestions: ['q6', 'q16'],
      lastUpdated: '2024-01-04 14:10:00'
    },
    'q13': {
      id: 'q13',
      title: '会员自动续费如何取消?',
      content: `取消自动续费的详细步骤：

📱 应用内取消：
1. 进入"我的"页面
2. 点击"会员中心"
3. 找到"管理续费"
4. 关闭自动续费开关

💳 支付平台取消：

微信支付：
1. 打开微信"我-支付"
2. 点击右上角"..."
3. 选择"扣费服务"
4. 找到本应用并关闭

支付宝：
1. 打开支付宝"我的"
2. 点击"设置-支付设置"
3. 选择"免密支付/自动扣款"
4. 找到本应用并关闭

⏰ 生效时间：
取消后在当前计费周期结束前仍可正常使用VIP服务。`,
      category: 'subscription',
      tags: ['自动续费', '取消续费', '微信支付', '支付宝'],
      isResolved: true,
      relatedQuestions: ['q8', 'q9'],
      lastUpdated: '2024-01-03 11:55:00'
    },
    'q14': {
      id: 'q14',
      title: '如何绑定手机号?',
      content: `绑定手机号提升账号安全性：

📱 绑定步骤：
1. 进入"我的-设置"
2. 选择"账号与安全"
3. 点击"绑定手机号"
4. 输入手机号获取验证码
5. 输入验证码完成绑定

🔄 更换手机号：
1. 先验证当前绑定手机
2. 输入新手机号
3. 获取新手机验证码
4. 完成更换

🛡️ 安全提醒：
• 绑定后可用于密码找回
• 重要操作需要手机验证
• 定期检查绑定信息
• 手机号变更及时更新

❓ 无法接收验证码？
检查短信拦截、网络状况，或联系客服协助处理。`,
      category: 'account',
      tags: ['手机绑定', '验证码', '账号安全', '更换手机'],
      isResolved: true,
      relatedQuestions: ['q7', 'q10'],
      lastUpdated: '2024-01-02 09:30:00'
    },
    'q15': {
      id: 'q15',
      title: '阅读记录丢失怎么办?',
      content: `阅读记录丢失的恢复方法：

☁️ 云端同步：
1. 确保已登录账号
2. 进入设置-数据同步
3. 点击"同步阅读记录"
4. 等待同步完成

🔄 手动恢复：
1. 重新打开之前阅读的书籍
2. 系统会自动定位到上次位置
3. 如未自动定位，查看"最近阅读"

💾 本地备份：
• 定期备份阅读数据
• 使用云端同步功能
• 避免频繁清理应用数据

🔧 预防措施：
• 保持网络连接良好
• 及时更新应用版本
• 不要随意清理应用缓存
• 定期检查同步状态`,
      category: 'reading',
      tags: ['阅读记录', '云端同步', '数据恢复', '备份'],
      isResolved: true,
      relatedQuestions: ['q1', 'q11'],
      lastUpdated: '2024-01-01 16:20:00'
    },
    'q16': {
      id: 'q16',
      title: '听书下载失败',
      content: `听书下载失败的解决方案：

🔍 问题排查：
• 检查网络连接是否稳定
• 确认存储空间是否充足
• 验证是否为VIP用户
• 检查应用版本是否最新

🛠️ 解决步骤：
1. 重启应用重新下载
2. 清理缓存释放空间
3. 切换到WiFi网络
4. 更新到最新版本
5. 重启设备后再试

📱 存储检查：
• 确保至少有1GB可用空间
• 检查SD卡是否正常
• 清理不需要的文件

🎧 替代方案：
如下载仍失败，可选择在线听书，或联系客服获取技术支持。`,
      category: 'listening',
      tags: ['下载失败', '存储空间', '网络问题', '技术支持'],
      isResolved: true,
      relatedQuestions: ['q6', 'q12'],
      lastUpdated: '2023-12-31 12:40:00'
    },
    'q17': {
      id: 'q17',
      title: 'VIP特权无法使用',
      content: `VIP特权无法使用的处理方法：

✅ 状态确认：
1. 检查VIP是否在有效期内
2. 确认登录的是正确账号
3. 查看会员中心的权益状态

🔄 刷新权益：
1. 退出应用重新登录
2. 进入会员中心刷新状态
3. 重启应用后再试

🎯 常见问题：
• 免广告失效：清理缓存重启
• 专属内容无法访问：检查网络
• 下载权限受限：确认VIP状态
• 听书权益异常：重新登录

📞 联系客服：
如问题持续存在，请联系客服并提供：
• 用户ID
• VIP购买凭证
• 问题截图`,
      category: 'benefits',
      tags: ['VIP特权', '权益失效', '状态刷新', '客服'],
      isResolved: false,
      relatedQuestions: ['q5', 'q9'],
      lastUpdated: '2023-12-30 15:15:00'
    },
    'q18': {
      id: 'q18',
      title: '如何申请退款?',
      content: `退款申请的详细流程：

📋 退款条件：
• 购买后7天内
• 未使用相关服务
• 符合退款政策要求

📝 申请步骤：
1. 进入"我的-客服中心"
2. 选择"退款申请"
3. 填写退款原因
4. 上传相关凭证
5. 提交申请等待审核

⏱️ 处理时间：
• 审核时间：1-3个工作日
• 退款到账：3-7个工作日
• 特殊情况可能延长

💰 退款方式：
原路返回到支付账户，如微信、支付宝等。

📞 咨询客服：
如有疑问请联系客服：957124`,
      category: 'subscription',
      tags: ['退款申请', '退款条件', '处理时间', '客服咨询'],
      isResolved: true,
      relatedQuestions: ['q8', 'q13'],
      lastUpdated: '2023-12-29 10:05:00'
    }
  };
  
  return details;
};

const allQuestionDetails = { ...mockQuestionDetails, ...generateMoreQuestionDetails() };

export const useFeedbackHelpStore = create<FeedbackHelpState>()(
  immer((set, get) => ({
    // 初始数据
    consultCategories: mockConsultCategories,
    frequentQuestions: mockFrequentQuestions,
    questionDetails: allQuestionDetails,
    
    // 初始状态
    currentView: 'main',
    selectedCategory: null,
    selectedQuestion: null,
    isLoading: false,
    error: null,
    
    // Actions
    setCurrentView: (view: 'main' | 'questions' | 'detail') => set(state => {
      state.currentView = view;
    }),
    
    selectCategory: (categoryId: string) => set(state => {
      state.selectedCategory = categoryId;
      state.currentView = 'questions';
    }),
    
    selectQuestion: (questionId: string) => set(state => {
      state.selectedQuestion = questionId;
      state.currentView = 'detail';
    }),
    
    markQuestionResolved: (questionId: string, isResolved: boolean) => set(state => {
      if (state.questionDetails[questionId]) {
        state.questionDetails[questionId].isResolved = isResolved;
      }
    }),
    
    goBack: () => set(state => {
      if (state.currentView === 'detail') {
        state.currentView = 'questions';
        state.selectedQuestion = null;
      } else if (state.currentView === 'questions') {
        state.currentView = 'main';
        state.selectedCategory = null;
      }
    }),
    
    resetToMain: () => set(state => {
      state.currentView = 'main';
      state.selectedCategory = null;
      state.selectedQuestion = null;
    }),
    
    setLoading: (loading: boolean) => set(state => {
      state.isLoading = loading;
    }),
    
    setError: (error: string | null) => set(state => {
      state.error = error;
    }),
    
    // 获取分类相关问题
    getCategoryQuestions: (categoryId: string): FrequentQuestion[] => {
      const state = get();
      return state.frequentQuestions.filter(q => q.category === categoryId);
    },
    
    // 获取问题详情
    getQuestionDetail: (questionId: string): QuestionDetail | null => {
      const state = get();
      return state.questionDetails[questionId] || null;
    },
    
    // 搜索问题
    searchQuestions: (keyword: string): FrequentQuestion[] => {
      const state = get();
      if (!keyword.trim()) return state.frequentQuestions;
      
      return state.frequentQuestions.filter(q => 
        q.title.toLowerCase().includes(keyword.toLowerCase())
      );
    }
  }))
);

console.log('[FeedbackHelpStore] Store initialized with categories:', mockConsultCategories.length);
console.log('[FeedbackHelpStore] Store initialized with questions:', mockFrequentQuestions.length);