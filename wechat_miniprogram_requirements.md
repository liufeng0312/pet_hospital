# 宠物医院微信小程序需求文档

> **项目名称**: 宠物医院客户端微信小程序  
> **版本**: v1.0  
> **创建日期**: 2026-01-16  
> **目标用户**: 宠物主人（客户端）

---

## 一、项目概述

### 1.1 背景
基于现有的宠物医院管理系统（Spring Boot + Vue 3），开发配套的微信小程序，为宠物主人提供便捷的移动端服务入口，实现在线预约、病历查询、提醒接收等功能，提升客户体验和医院运营效率。

### 1.2 目标
- 为宠物主人提供7×24小时的自助服务平台
- 减少前台人工预约压力，提高预约效率
- 增强客户粘性，提升医院品牌形象
- 实现线上线下业务闭环

### 1.3 核心价值
- **便捷性**: 随时随地预约、查询、咨询
- **透明化**: 实时查看宠物健康档案和病历
- **智能化**: 自动提醒疫苗、复诊等重要事项
- **互动性**: 在线咨询、健康知识学习

---

## 二、用户角色定义

### 2.1 宠物主人（客户）
- **权限**: 查看自己的宠物档案、预约挂号、查看病历、接收提醒、在线咨询
- **认证方式**: 微信授权登录 + 手机号绑定
- **数据关联**: 通过手机号关联后台 `customers` 表

---

## 三、功能模块设计

### 3.1 首页（Home）

#### 3.1.1 功能要点
- **顶部Banner**: 医院宣传图/活动推广（轮播）
- **快捷入口**: 
  - 在线预约
  - 我的宠物
  - 病历查询
  - 健康提醒
  - 在线咨询
- **医院信息**: 地址、电话、营业时间、导航
- **健康资讯**: 宠物养护知识、疾病预防科普（列表）
- **服务项目**: 疫苗接种、体检套餐、手术服务等（卡片展示）

#### 3.1.2 UI设计建议
- 采用温馨、活泼的配色（蓝色/绿色系）
- 使用宠物相关图标和插画
- 卡片式布局，信息层次清晰

---

### 3.2 在线预约（Appointment）

#### 3.2.1 预约流程
1. **选择宠物**: 从"我的宠物"列表中选择
2. **选择服务类型**: 
   - 常规门诊
   - 疫苗接种
   - 体检套餐
   - 手术预约
3. **选择医生**: 显示医生头像、姓名、职称、擅长领域
4. **选择日期和时间**: 
   - 日历选择器（显示可预约日期）
   - 时间段选择（显示剩余号源）
5. **填写主诉**: 简要描述宠物症状（可选）
6. **确认提交**: 显示预约详情，提交后生成预约记录

#### 3.2.2 功能要求
- 实时显示医生排班和可预约时间段
- 支持预约取消（提前24小时）
- 预约成功后发送微信服务通知
- 预约前1小时发送提醒通知

#### 3.2.3 API接口需求
```
POST /api/registrations/create
- 参数: customerId, petId, doctorId, appointmentTime, type, symptoms
- 返回: 预约记录详情

GET /api/employees/doctors
- 返回: 医生列表（含排班信息）

GET /api/registrations/available-slots
- 参数: doctorId, date
- 返回: 可预约时间段列表

PUT /api/registrations/{id}/cancel
- 取消预约
```

---

### 3.3 我的宠物（My Pets）

#### 3.3.1 宠物列表
- 显示所有宠物的卡片（头像、昵称、品种、年龄）
- 支持添加新宠物
- 点击进入宠物详情页

#### 3.3.2 宠物档案详情
- **基本信息**: 
  - 昵称、品种、性别、出生日期、体重
  - 宠物照片（支持上传）
- **健康档案**:
  - 过敏史
  - 特殊注意事项
  - 疫苗接种记录（时间、类型、下次接种日期）
  - 驱虫记录
- **就诊历史**: 
  - 历史病历列表（时间、诊断、医生）
  - 点击查看病历详情

#### 3.3.3 添加/编辑宠物
- 表单字段: 昵称、物种、品种、性别、出生日期、体重、照片、备注
- 照片上传（调用微信 `wx.chooseImage` + 后台上传接口）

#### 3.3.4 API接口需求
```
GET /api/pets/my-pets
- 参数: customerId
- 返回: 宠物列表

POST /api/pets/create
- 参数: customerId, name, species, breed, gender, birthDate, weight, photoUrl, notes
- 返回: 新建宠物信息

PUT /api/pets/{id}/update
- 更新宠物信息

POST /api/upload/image
- 上传宠物照片
- 返回: 图片URL
```

---

### 3.4 病历查询（Medical Records）

#### 3.4.1 病历列表
- 按时间倒序显示所有病历
- 每条记录显示: 日期、宠物名称、诊断摘要、医生姓名
- 支持筛选（按宠物、按时间范围）

#### 3.4.2 病历详情
- **就诊信息**: 就诊日期、医生、宠物
- **主诉症状**: 客户描述的症状
- **诊断结果**: 医生的诊断
- **治疗方案**: 处理措施
- **处方信息**: 
  - 药品列表（名称、规格、数量、用法用量）
  - 总金额
- **检验报告**: 
  - 检验项目、结果、报告文件（可下载）
- **医嘱**: 医生的建议和注意事项

#### 3.4.3 API接口需求
```
GET /api/medical-records/my-records
- 参数: customerId, petId (可选), startDate, endDate
- 返回: 病历列表

GET /api/medical-records/{id}/detail
- 返回: 病历详情（含处方、检验报告）

GET /api/prescriptions/{id}
- 返回: 处方详情

GET /api/lab-tests/{id}
- 返回: 检验报告详情
```

---

### 3.5 健康提醒（Reminders）

#### 3.5.1 提醒列表
- 显示所有待处理和历史提醒
- 提醒类型:
  - 疫苗接种提醒
  - 驱虫提醒
  - 复诊提醒
  - 宠物生日祝福
- 每条提醒显示: 类型图标、宠物名称、提醒内容、到期日期
- 状态标识: 待处理（红色）、已完成（灰色）

#### 3.5.2 提醒详情
- 提醒内容详细说明
- 快捷操作: "立即预约"按钮（跳转到预约页面）

#### 3.5.3 微信服务通知
- 提前7天、3天、1天发送微信服务通知
- 通知内容: 宠物名称、提醒类型、到期日期

#### 3.5.4 API接口需求
```
GET /api/reminders/my-reminders
- 参数: customerId
- 返回: 提醒列表

PUT /api/reminders/{id}/mark-done
- 标记提醒为已完成
```

---

### 3.6 在线咨询（Consultation）

#### 3.6.1 功能描述
- 客户可以向医院发起在线咨询
- 咨询类型: 健康咨询、用药咨询、行为问题
- 支持文字描述 + 图片上传

#### 3.6.2 咨询流程
1. 选择咨询的宠物
2. 选择咨询类型
3. 填写问题描述
4. 上传相关图片（可选）
5. 提交咨询

#### 3.6.3 咨询记录
- 显示历史咨询列表
- 查看咨询详情和医生回复（如有）

#### 3.6.4 数据库设计建议
新增 `consultations` 表:
```sql
CREATE TABLE `consultations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `customer_id` BIGINT NOT NULL,
  `pet_id` BIGINT NOT NULL,
  `type` VARCHAR(50) NOT NULL COMMENT '咨询类型',
  `question` TEXT NOT NULL COMMENT '问题描述',
  `images` TEXT COMMENT '图片URL列表(JSON)',
  `reply` TEXT COMMENT '医生回复',
  `status` TINYINT DEFAULT 0 COMMENT '0:待回复, 1:已回复',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `replied_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 3.6.5 API接口需求
```
POST /api/consultations/create
- 参数: customerId, petId, type, question, images
- 返回: 咨询记录

GET /api/consultations/my-consultations
- 参数: customerId
- 返回: 咨询列表

GET /api/consultations/{id}
- 返回: 咨询详情
```

---

### 3.7 个人中心（Profile）

#### 3.7.1 功能要点
- **用户信息**: 
  - 微信头像、昵称
  - 绑定手机号（用于关联客户账户）
  - 客户等级（VIP/普通）
- **我的预约**: 显示待就诊预约（快捷入口）
- **我的宠物**: 宠物数量统计（快捷入口）
- **我的账单**: 历史账单查询
- **设置**:
  - 消息通知设置
  - 关于我们
  - 联系客服
  - 退出登录

#### 3.7.2 账单查询
- 显示历史账单列表（日期、金额、支付状态）
- 账单详情: 服务项目、药品明细、优惠金额、实付金额

#### 3.7.3 API接口需求
```
GET /api/customers/profile
- 参数: customerId
- 返回: 客户信息

GET /api/bills/my-bills
- 参数: customerId
- 返回: 账单列表

GET /api/bills/{id}
- 返回: 账单详情
```

---

### 3.8 健康资讯（Health Articles）

#### 3.8.1 功能描述
- 展示宠物健康养护知识、疾病预防科普文章
- 文章分类: 养护知识、疾病预防、营养饮食、行为训练

#### 3.8.2 文章列表
- 卡片式展示（封面图、标题、摘要、发布日期）
- 支持分类筛选

#### 3.8.3 文章详情
- 富文本内容展示
- 支持收藏、分享

#### 3.8.4 数据库设计建议
新增 `articles` 表:
```sql
CREATE TABLE `articles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `category` VARCHAR(50) NOT NULL,
  `cover_image` VARCHAR(255),
  `summary` TEXT,
  `content` LONGTEXT NOT NULL,
  `author` VARCHAR(50),
  `publish_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `is_published` TINYINT DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 3.8.5 API接口需求
```
GET /api/articles/list
- 参数: category (可选), page, pageSize
- 返回: 文章列表

GET /api/articles/{id}
- 返回: 文章详情
```

---

## 四、技术架构

### 4.1 前端技术栈
- **框架**: 微信小程序原生开发 / uni-app（推荐，可跨平台）
- **UI组件库**: 
  - 原生: WeUI / Vant Weapp
  - uni-app: uni-ui / uView UI
- **状态管理**: Vuex (uni-app) / 小程序全局数据
- **网络请求**: 
  - 原生: `wx.request` 封装
  - uni-app: uni.request 封装 + 拦截器
- **图片上传**: `wx.chooseImage` + `wx.uploadFile`

### 4.2 后端API扩展
基于现有 Spring Boot 后端，需新增以下接口:

#### 4.2.1 微信登录认证
```java
@PostMapping("/api/auth/wechat-login")
public Result wechatLogin(@RequestBody WechatLoginDTO dto) {
    // 1. 调用微信API验证 code，获取 openid 和 session_key
    // 2. 根据 openid 查询或创建用户
    // 3. 生成 JWT token
    // 4. 返回 token 和用户信息
}
```

#### 4.2.2 手机号绑定
```java
@PostMapping("/api/customers/bind-phone")
public Result bindPhone(@RequestBody BindPhoneDTO dto) {
    // 1. 验证手机号和验证码
    // 2. 关联 openid 和 customers 表
    // 3. 返回客户信息
}
```

#### 4.2.3 其他新增接口
- 医生排班查询: `GET /api/employees/schedule`
- 可预约时间段: `GET /api/registrations/available-slots`
- 我的宠物列表: `GET /api/pets/my-pets`
- 我的病历: `GET /api/medical-records/my-records`
- 我的提醒: `GET /api/reminders/my-reminders`
- 我的账单: `GET /api/bills/my-bills`
- 在线咨询: `POST /api/consultations/create`
- 健康资讯: `GET /api/articles/list`

### 4.3 数据库扩展
需新增以下表:

1. **微信用户表** (`wechat_users`):
```sql
CREATE TABLE `wechat_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `openid` VARCHAR(100) NOT NULL COMMENT '微信openid',
  `unionid` VARCHAR(100) COMMENT '微信unionid',
  `customer_id` BIGINT COMMENT '关联客户ID',
  `nickname` VARCHAR(100),
  `avatar_url` VARCHAR(255),
  `phone` VARCHAR(20),
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

2. **在线咨询表** (`consultations`) - 见 3.6.4

3. **健康资讯表** (`articles`) - 见 3.8.4

### 4.4 安全性设计
- **认证**: JWT Token 认证
- **授权**: 客户只能访问自己的数据（通过 customerId 过滤）
- **数据加密**: HTTPS 传输
- **敏感信息**: 手机号脱敏显示

---

## 五、UI/UX设计规范

### 5.1 设计风格
- **色彩**: 
  - 主色调: #3b82f6（蓝色）
  - 辅助色: #10b981（绿色）、#f59e0b（橙色）
  - 背景色: #f8fafc（浅灰）
- **字体**: 
  - 标题: 18px / 16px，加粗
  - 正文: 14px，常规
  - 辅助文字: 12px，灰色
- **圆角**: 统一使用 12px / 16px 圆角
- **阴影**: 卡片使用轻微阴影 `box-shadow: 0 2px 8px rgba(0,0,0,0.05)`

### 5.2 组件规范
- **按钮**: 主按钮（蓝色）、次要按钮（白色边框）
- **卡片**: 白色背景 + 圆角 + 阴影
- **列表**: 带分割线，支持左滑操作
- **表单**: 清晰的标签和占位符
- **图标**: 使用 Element Plus Icons / Vant Icons

### 5.3 交互规范
- **加载状态**: 显示 loading 动画
- **空状态**: 友好的空状态提示（图标 + 文字）
- **错误提示**: Toast 提示，3秒自动消失
- **确认操作**: 重要操作（取消预约）需二次确认

---

## 六、开发阶段规划

### 第一阶段：MVP核心功能（2-3周）
- [x] 微信登录 + 手机号绑定
- [x] 首页（快捷入口 + 医院信息）
- [x] 我的宠物（列表 + 详情 + 添加/编辑）
- [x] 在线预约（完整流程）
- [x] 个人中心（基本信息 + 我的预约）

### 第二阶段：数据查询功能（1-2周）
- [x] 病历查询（列表 + 详情）
- [x] 健康提醒（列表 + 微信通知）
- [x] 我的账单（列表 + 详情）

### 第三阶段：增值功能（1-2周）
- [x] 在线咨询（提交 + 查看回复）
- [x] 健康资讯（列表 + 详情）
- [x] 消息通知优化
- [x] 数据统计（我的宠物健康报告）

### 第四阶段：优化与上线（1周）
- [x] 性能优化（图片懒加载、分页加载）
- [x] 兼容性测试（不同机型、微信版本）
- [x] 用户体验优化
- [x] 提交微信审核并上线

---

## 七、API接口汇总

### 7.1 认证相关
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/wechat-login` | POST | 微信登录 |
| `/api/customers/bind-phone` | POST | 绑定手机号 |

### 7.2 宠物管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/pets/my-pets` | GET | 我的宠物列表 |
| `/api/pets/create` | POST | 添加宠物 |
| `/api/pets/{id}/update` | PUT | 更新宠物信息 |
| `/api/pets/{id}` | GET | 宠物详情 |

### 7.3 预约管理
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/registrations/create` | POST | 创建预约 |
| `/api/registrations/my-appointments` | GET | 我的预约列表 |
| `/api/registrations/{id}/cancel` | PUT | 取消预约 |
| `/api/registrations/available-slots` | GET | 可预约时间段 |
| `/api/employees/doctors` | GET | 医生列表 |

### 7.4 病历查询
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/medical-records/my-records` | GET | 我的病历列表 |
| `/api/medical-records/{id}/detail` | GET | 病历详情 |
| `/api/prescriptions/{id}` | GET | 处方详情 |
| `/api/lab-tests/{id}` | GET | 检验报告 |

### 7.5 提醒服务
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/reminders/my-reminders` | GET | 我的提醒列表 |
| `/api/reminders/{id}/mark-done` | PUT | 标记完成 |

### 7.6 账单查询
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/bills/my-bills` | GET | 我的账单列表 |
| `/api/bills/{id}` | GET | 账单详情 |

### 7.7 在线咨询
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/consultations/create` | POST | 提交咨询 |
| `/api/consultations/my-consultations` | GET | 我的咨询列表 |
| `/api/consultations/{id}` | GET | 咨询详情 |

### 7.8 健康资讯
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/articles/list` | GET | 文章列表 |
| `/api/articles/{id}` | GET | 文章详情 |

### 7.9 文件上传
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/upload/image` | POST | 上传图片 |

---

## 八、测试策略

### 8.1 功能测试
- 登录认证流程测试
- 预约流程完整性测试
- 数据查询准确性测试
- 提醒推送测试

### 8.2 兼容性测试
- iOS 微信客户端（不同版本）
- Android 微信客户端（不同版本）
- 不同屏幕尺寸适配

### 8.3 性能测试
- 页面加载速度（< 2秒）
- 图片加载优化
- 接口响应时间（< 500ms）

### 8.4 安全测试
- Token 过期处理
- 数据权限验证
- SQL 注入防护

---

## 九、上线准备

### 9.1 微信小程序注册
- 注册小程序账号（企业主体）
- 完成微信认证
- 配置服务器域名（需备案）
- 配置业务域名

### 9.2 服务器配置
- 配置 HTTPS 证书
- 配置微信支付（如需）
- 配置微信服务通知模板

### 9.3 提交审核
- 准备小程序截图和说明
- 填写隐私政策
- 提交审核（通常1-3个工作日）

---

## 十、后续迭代方向

### 10.1 功能增强
- 在线支付（微信支付）
- 视频问诊
- 宠物社区（分享、交流）
- 积分系统和会员权益

### 10.2 数据分析
- 用户行为分析
- 预约转化率统计
- 热门服务分析

### 10.3 智能化
- AI 智能问诊
- 宠物健康评分
- 个性化推荐

---

## 附录

### A. 参考资料
- 微信小程序官方文档: https://developers.weixin.qq.com/miniprogram/dev/framework/
- uni-app 官方文档: https://uniapp.dcloud.net.cn/
- 现有后端 API 文档: 参考 `pet-hospital-backend` 项目

### B. 联系方式
- 项目负责人: [刘峰]
- 技术支持: [feng]

---

**文档版本历史**:
- v1.0 (2026-01-16): 初始版本，完成需求分析和功能设计
