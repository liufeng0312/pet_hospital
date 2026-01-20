<template>
  <view class="profile-container">
    <!-- 用户信息卡片 -->
    <view class="user-card card">
      <image 
        class="avatar" 
        :src="userInfo?.avatar || '/static/images/default-avatar.png'" 
        mode="aspectFill"
      ></image>
      <view class="user-info">
        <text class="user-name">{{ userInfo?.name || '宠物主人' }}</text>
        <text class="user-phone">{{ formatPhone(userInfo?.phone) }}</text>
      </view>
      <view class="user-level">
        <text class="level-badge">{{ getLevelText(userInfo?.level) }}</text>
      </view>
    </view>
    
    <!-- 快捷入口 -->
    <view class="quick-links">
      <view class="link-item" @tap="goToReminders">
        <view class="link-left">
          <text class="link-icon">⏰</text>
          <text class="link-text">健康提醒</text>
        </view>
        <view class="link-right">
          <text class="link-arrow">›</text>
        </view>
      </view>
      
      <view class="link-item" @tap="goToBills">
        <view class="link-left">
          <text class="link-icon">💰</text>
          <text class="link-text">我的账单</text>
        </view>
        <view class="link-right">
          <text class="link-arrow">›</text>
        </view>
      </view>
    </view>
    
    <!-- 设置选项 -->
    <view class="settings">
      <view class="setting-item" @tap="goToAbout">
        <text class="setting-text">关于我们</text>
        <text class="setting-arrow">›</text>
      </view>
      <view class="setting-item" @tap="contactService">
        <text class="setting-text">联系客服</text>
        <text class="setting-arrow">›</text>
      </view>
      <view class="setting-item" @tap="handleLogout">
        <text class="setting-text logout-text">退出登录</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const appointmentCount = ref(0)
const petCount = ref(0)

const userInfo = computed(() => userStore.userInfo)

onMounted(() => {
  // TODO: 加载预约和宠物数量
})

function formatPhone(phone?: string) {
  if (!phone) return '未绑定手机号'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

function getLevelText(level?: number) {
  switch (level) {
    case 1:
      return 'VIP'
    case 2:
      return '普通会员'
    default:
      return '会员'
  }
}

function goToReminders() {
  uni.navigateTo({
    url: '/pages/reminders/list'
  })
}

function goToBills() {
  uni.navigateTo({
    url: '/pages/bills/list'
  })
}

function goToAbout() {
  uni.showModal({
    title: '关于我们',
    content: '宠物医院管理系统\n版本: 1.0.0\n致力于为爱宠提供最优质的医疗服务',
    showCancel: false
  })
}

function contactService() {
  uni.makePhoneCall({
    phoneNumber: '400-123-4567',
    fail: (err) => {
      // 忽略用户取消的错误
      console.log('用户取消拨打', err)
    }
  })
}

function handleLogout() {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
        uni.reLaunch({
          url: '/pages/login/index'
        })
      }
    }
  })
}
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 120rpx;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 32rpx 24rpx;
  margin-bottom: 24rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.user-name {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-color);
}

.user-phone {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.user-level {
  flex-shrink: 0;
}

.level-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #FFFFFF;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 600;
}

.quick-links {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  margin-bottom: 24rpx;
}

.link-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid var(--border-color);
}

.link-item:last-child {
  border-bottom: none;
}

.link-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.link-icon {
  font-size: 40rpx;
}

.link-text {
  font-size: 28rpx;
  color: var(--text-color);
}

.link-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.link-badge {
  background: #3B82F6;
  color: #FFFFFF;
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  font-size: 20rpx;
  font-weight: 600;
}

.link-arrow {
  font-size: 48rpx;
  color: #CBD5E1;
}

.settings {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
}

.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid var(--border-color);
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-text {
  font-size: 28rpx;
  color: var(--text-color);
}

.logout-text {
  color: var(--error-color);
}

.setting-arrow {
  font-size: 48rpx;
  color: #CBD5E1;
}
</style>
