<template>
  <view class="notifications-page">
    <view v-if="unreadCount > 0" class="action-bar">
      <text class="unread-text">{{ unreadCount }} 条未读</text>
      <button class="mark-all-btn" @click="markAllAsRead">全部标记为已读</button>
    </view>

    <view v-if="loading" class="loading">加载中...</view>

    <view v-else-if="notifications.length === 0" class="empty">
      <text class="empty-icon">🔔</text>
      <text class="empty-text">暂无通知</text>
    </view>

    <view v-else class="notifications-list">
      <view
        v-for="item in notifications"
        :key="item.id"
        class="notification-item"
        :class="{ unread: item.isRead === 0 }"
        @click="handleClick(item)"
      >
        <view class="icon-wrapper">
          <text class="type-icon">{{ getTypeIcon(item.type) }}</text>
          <view v-if="item.isRead === 0" class="unread-dot"></view>
        </view>
        <view class="notification-content">
          <view class="title">{{ item.title }}</view>
          <view class="content">{{ item.content }}</view>
          <view class="time">{{ item.createdAt }}</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { notificationApi, type Notification } from '@/api/notification'

const loading = ref(true)
const notifications = ref<Notification[]>([])
const unreadCount = ref(0)

onMounted(async () => {
  await loadNotifications()
  await loadUnreadCount()
})

async function loadNotifications() {
  try {
    loading.value = true
    const res = await notificationApi.getMyNotifications({ page: 1, size: 50 })
    if (res.code === 200) {
      notifications.value = res.data.records
    }
  } catch (error) {
    console.error('加载通知列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  try {
    const res = await notificationApi.getUnreadCount()
    if (res.code === 200) {
      unreadCount.value = res.data
    }
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

async function markAllAsRead() {
  try {
    const res = await notificationApi.markAllAsRead()
    if (res.code === 200) {
      uni.showToast({ title: '已全部标记为已读', icon: 'success' })
      await loadNotifications()
      unreadCount.value = 0
    }
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

async function handleClick(item: Notification) {
  // 标记为已读
  if (item.isRead === 0) {
    try {
      await notificationApi.markAsRead(item.id!)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  // 跳转到关联页面
  if (item.relatedType && item.relatedId) {
    const pageMap: any = {
      appointment: '/pages/appointment/detail',
      reminder: '/pages/reminders/list',
      consultation: '/pages/consultations/detail'
    }
    const page = pageMap[item.relatedType]
    if (page) {
      uni.navigateTo({
        url: `${page}?id=${item.relatedId}`
      })
    }
  }
}

function getTypeIcon(type: string) {
  const iconMap: any = {
    APPOINTMENT: '📅',
    REMINDER: '⏰',
    CONSULTATION: '💬',
    SYSTEM: '📢'
  }
  return iconMap[type] || '🔔'
}
</script>

<style scoped>
.notifications-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 30rpx;
  background: #fff;
  border-bottom: 1rpx solid #f0f0f0;
}

.unread-text {
  font-size: 28rpx;
  color: #3b82f6;
  font-weight: bold;
}

.mark-all-btn {
  padding: 12rpx 24rpx;
  background: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 16rpx;
  font-size: 24rpx;
}

.loading, .empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.notifications-list {
  padding: 0;
}

.notification-item {
  display: flex;
  background: #fff;
  padding: 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.notification-item.unread {
  background: #eff6ff;
}

.icon-wrapper {
  position: relative;
  margin-right: 24rpx;
}

.type-icon {
  font-size: 48rpx;
}

.unread-dot {
  position: absolute;
  top: 0;
  right: 0;
  width: 16rpx;
  height: 16rpx;
  background: #ef4444;
  border-radius: 50%;
  border: 2rpx solid #fff;
}

.notification-content {
  flex: 1;
}

.title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12rpx;
}

.time {
  font-size: 24rpx;
  color: #999;
}
</style>
