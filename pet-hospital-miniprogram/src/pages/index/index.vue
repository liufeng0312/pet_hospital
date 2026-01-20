<template>
  <view class="home-container">
    <!-- 医院信息卡片 -->
    <view class="hospital-card card">
      <view class="hospital-info">
        <text class="hospital-name">宠物医院</text>
        <text class="hospital-desc">专业、温情、全天候的宠物医疗服务</text>
      </view>
      <view class="hospital-contact">
        <view class="contact-item">
          <text class="icon">📍</text>
          <text class="text">上海市浦东新区张江高科技园区</text>
        </view>
        <view class="contact-item" @tap="makePhoneCall">
          <text class="icon">📞</text>
          <text class="text">400-123-4567</text>
        </view>
      </view>
    </view>
    
    <!-- 快捷入口 -->
    <view class="quick-actions">
      <text class="section-title">快捷服务</text>
      <view class="actions-grid">
        <view 
          v-for="action in quickActions" 
          :key="action.title"
          class="action-item"
          @tap="navigateTo(action.path)"
        >
          <view class="action-icon" :style="{ background: action.gradient }">
            <text class="icon-text">{{ action.icon }}</text>
          </view>
          <text class="action-title">{{ action.title }}</text>
        </view>
      </view>
    </view>
    
    <!-- 服务项目 -->
    <view class="services">
      <text class="section-title">医疗服务</text>
      <view 
        v-for="service in services" 
        :key="service.title"
        class="service-card card"
      >
        <view class="service-icon">{{ service.icon }}</view>
        <view class="service-info">
          <text class="service-title">{{ service.title }}</text>
          <text class="service-desc">{{ service.desc }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const quickActions = ref([
  { title: '在线预约', icon: '📅', path: '/pages/appointment/create', gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { title: '我的账单', icon: '💰', path: '/pages/bills/list', gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { title: '病历查询', icon: '📋', path: '/pages/records/list', gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { title: '在线咨询', icon: '💬', path: '/pages/consultations/list', gradient: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)' },
  { title: '健康资讯', icon: '📰', path: '/pages/articles/list', gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { title: '消息通知', icon: '🔔', path: '/pages/notifications/list', gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }
])

const services = ref([
  { title: '全科医疗', icon: '🏥', desc: '从幼宠咨询到老年病管理，提供全生命周期医疗服务' },
  { title: '手术服务', icon: '⚕️', desc: '配备顶级数字化手术室与麻醉监控系统' },
  { title: '重症监护', icon: '🚑', desc: '24小时恒温吸氧监护室，专业护理团队' },
  { title: '影像诊断', icon: '🔬', desc: '进口数字化DR、超声波检查，精准快速' }
])

onMounted(() => {
  // 检查登录状态
  userStore.init()
  if (!userStore.isLoggedIn) {
    uni.reLaunch({
      url: '/pages/login/index'
    })
  }
})

function navigateTo(path: string) {
  if (!path) {
    uni.showToast({
      title: '功能开发中',
      icon: 'none'
    })
    return
  }
  
  // TabBar页面列表
  const tabPages = [
    '/pages/index/index',
    '/pages/pets/list',
    '/pages/appointment/list',
    '/pages/profile/index'
  ]
  
  if (tabPages.includes(path)) {
    uni.switchTab({
      url: path
    })
  } else {
    uni.navigateTo({
      url: path
    })
  }
}

function makePhoneCall() {
  uni.makePhoneCall({
    phoneNumber: '400-123-4567'
  })
}
</script>

<style scoped>
.home-container {
  padding: 24rpx;
  padding-bottom: 120rpx;
}

.hospital-card {
  margin-bottom: 32rpx;
}

.hospital-info {
  margin-bottom: 24rpx;
}

.hospital-name {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 8rpx;
}

.hospital-desc {
  display: block;
  font-size: 26rpx;
  color: var(--text-secondary);
}

.hospital-contact {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.icon {
  font-size: 32rpx;
}

.text {
  font-size: 28rpx;
  color: var(--text-color);
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: 24rpx;
}

.quick-actions {
  margin-bottom: 32rpx;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}

.action-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.icon-text {
  font-size: 48rpx;
}

.action-title {
  font-size: 26rpx;
  color: var(--text-color);
  font-weight: 500;
}

.services {
  margin-bottom: 32rpx;
}

.service-card {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
  margin-bottom: 16rpx;
}

.service-icon {
  font-size: 48rpx;
  flex-shrink: 0;
}

.service-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.service-title {
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-color);
}

.service-desc {
  font-size: 26rpx;
  color: var(--text-secondary);
  line-height: 1.6;
}
</style>
