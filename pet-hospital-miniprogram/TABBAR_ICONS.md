# TabBar 图标说明

## 当前状态
为了快速启动项目，当前 tabBar 配置为纯文字模式（不显示图标）。

## 添加图标（可选）

如果需要添加 tabBar 图标，请按以下步骤操作：

### 方案1: 使用 iconfont 图标

1. 访问 [iconfont.cn](https://www.iconfont.cn/)
2. 搜索并下载以下图标（81x81px，PNG格式）:
   - 首页图标（home）
   - 宠物图标（paw）
   - 日历图标（calendar）
   - 用户图标（user）

3. 每个图标需要两个版本：
   - 未选中状态：灰色 (#8E8E93)
   - 选中状态：蓝色 (#3B82F6)

4. 将图标重命名并放置到 `src/static/images/` 目录：
   ```
   tab-home.png
   tab-home-active.png
   tab-pet.png
   tab-pet-active.png
   tab-appointment.png
   tab-appointment-active.png
   tab-profile.png
   tab-profile-active.png
   ```

5. 在 `src/pages.json` 中恢复 iconPath 配置：
   ```json
   {
     "pagePath": "pages/index/index",
     "text": "首页",
     "iconPath": "static/images/tab-home.png",
     "selectedIconPath": "static/images/tab-home-active.png"
   }
   ```

### 方案2: 使用在线图标生成工具

访问 [flaticon.com](https://www.flaticon.com/) 或 [icons8.com](https://icons8.com/) 下载合适的图标。

### 图标规范

- **尺寸**: 81x81 像素（推荐）或 40x40dp
- **格式**: PNG（支持透明背景）
- **颜色**: 
  - 未选中: #8E8E93
  - 选中: #3B82F6
- **风格**: 简洁、线条风格

## 注意事项

- tabBar 图标是可选的，纯文字模式也是合法的
- 如果不添加图标，当前配置可以正常运行
- 添加图标后需要重新编译项目
