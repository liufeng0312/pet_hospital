# 宠物医院微信小程序

基于 uni-app 开发的宠物医院客户端微信小程序，提供在线预约、宠物档案管理、病历查询等功能。

## 项目结构

```
pet-hospital-miniprogram/
├── pages/                    # 页面
│   ├── index/               # 首页
│   ├── login/               # 登录页
│   ├── pets/                # 我的宠物
│   ├── appointment/         # 预约
│   └── profile/             # 个人中心
├── components/               # 组件
├── api/                      # API封装
│   ├── request.ts           # 请求封装
│   ├── auth.ts              # 认证API
│   ├── pet.ts               # 宠物API
│   └── appointment.ts       # 预约API
├── store/                    # 状态管理
│   └── user.ts              # 用户状态
├── utils/                    # 工具函数
├── static/                   # 静态资源
├── App.vue                   # 应用入口
├── main.ts                   # 主入口
├── manifest.json             # 应用配置
├── pages.json                # 页面路由配置
└── package.json
```

## 技术栈

- **框架**: uni-app (Vue 3 + TypeScript)
- **状态管理**: Pinia
- **UI组件**: 原生组件
- **网络请求**: uni.request 封装

## 功能模块

### Phase 1 (已完成)
- ✅ 微信登录 + 手机号绑定
- ✅ 首页（快捷入口 + 医院信息）
- ✅ 我的宠物（列表 + 详情 + 添加/编辑）
- ✅ 在线预约（完整流程）
- ✅ 个人中心（基本信息 + 我的预约）

### Phase 2 (计划中)
- ⏳ 病历查询（列表 + 详情）
- ⏳ 健康提醒（列表 + 微信通知）
- ⏳ 我的账单（列表 + 详情）

### Phase 3 (计划中)
- ⏳ 在线咨询（提交 + 查看回复）
- ⏳ 健康资讯（列表 + 详情）
- ⏳ 微信支付

## 开发指南

### 环境要求

- Node.js >= 14
- pnpm / npm / yarn
- 微信开发者工具

### 安装依赖

```bash
cd pet-hospital-miniprogram
npm install
```

### 开发运行

```bash
# 微信小程序
npm run dev:mp-weixin

# H5
npm run dev:h5
```

### 编译打包

```bash
# 微信小程序
npm run build:mp-weixin

# H5
npm run build:h5
```

### 微信开发者工具

1. 打开微信开发者工具
2. 导入项目，选择 `dist/dev/mp-weixin` 目录
3. 填写 AppID（测试可使用测试号）
4. 开始开发调试

## 配置说明

### 1. 修改后端API地址

编辑 `api/request.ts`:

```typescript
const baseURL = 'http://localhost:8080' // 修改为实际后端地址
```

### 2. 配置小程序 AppID

编辑 `manifest.json`:

```json
{
  "mp-weixin": {
    "appid": "你的小程序AppID"
  }
}
```

### 3. 配置服务器域名

在微信公众平台配置以下域名（需备案）:
- request合法域名: `https://your-api-domain.com`
- uploadFile合法域名: `https://your-api-domain.com`
- downloadFile合法域名: `https://your-api-domain.com`

## 后端API要求

小程序需要后端提供以下接口：

### 认证相关
- `POST /api/auth/wechat-login` - 微信登录
- `POST /api/customers/bind-phone` - 绑定手机号

### 宠物管理
- `GET /api/pets/my-pets` - 我的宠物列表
- `GET /api/pets/{id}` - 宠物详情
- `POST /api/pets/create` - 创建宠物
- `PUT /api/pets/{id}/update` - 更新宠物
- `POST /api/upload/image` - 上传图片

### 预约管理
- `GET /api/employees/doctors` - 医生列表
- `GET /api/registrations/available-slots` - 可预约时间段
- `POST /api/registrations/create` - 创建预约
- `GET /api/registrations/my-appointments` - 我的预约
- `PUT /api/registrations/{id}/cancel` - 取消预约

详细API文档请参考 `../wechat_miniprogram_requirements.md`

## 设计规范

### 色彩方案
- Primary: #3B82F6 (医疗蓝)
- Secondary: #60A5FA (浅蓝)
- CTA: #F97316 (橙色)
- Background: #F8FAFC (浅灰)
- Text: #1E293B (深灰)
- Border: #E2E8F0 (边框灰)

### 字体
- 标题: 加粗，32-36rpx
- 正文: 常规，26-28rpx
- 辅助文字: 24rpx

### 圆角
- 卡片: 16rpx
- 按钮: 12rpx
- 头像: 50%

## 注意事项

1. **开发环境**: 本地开发时需要在微信开发者工具中勾选"不校验合法域名"
2. **真机调试**: 真机调试需要配置合法的HTTPS域名
3. **图片资源**: tabBar图标需要放在 `static/images/` 目录下
4. **权限申请**: 使用定位、相机等功能需要在 `manifest.json` 中声明权限

## 常见问题

### 1. 登录失败
- 检查后端API地址是否正确
- 检查微信AppID是否配置
- 检查后端是否正确处理微信登录

### 2. 图片上传失败
- 检查上传接口地址
- 检查文件大小限制
- 检查服务器域名配置

### 3. 页面跳转失败
- 检查页面路径是否正确
- 检查 `pages.json` 配置
- tabBar页面使用 `switchTab`，普通页面使用 `navigateTo`

## 开发团队

- 项目负责人: 刘峰
- 技术支持: feng

## License

Copyright © 2026 Pet Hospital
