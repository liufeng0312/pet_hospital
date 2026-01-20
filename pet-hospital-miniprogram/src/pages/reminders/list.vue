<template>
  <view class="reminders-page">
    <view class="header">
      <text class="title">健康提醒</text>
    </view>

    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>

    <view v-else-if="reminders.length === 0" class="empty">
      <text class="empty-icon">📋</text>
      <text class="empty-text">暂无提醒</text>
    </view>

    <view v-else class="reminders-list">
      <view
        v-for="reminder in reminders"
        :key="reminder.id"
        class="reminder-card"
        :class="{ unread: reminder.status === 0 }"
        @click="handleMarkAsRead(reminder)"
      >
        <view class="reminder-type">
          <text class="type-icon">{{ getTypeIcon(reminder.type) }}</text>
          <text class="type-text">{{ getTypeName(reminder.type) }}</text>
        </view>
        <view class="reminder-content">
          <text>{{ getContent(reminder) }}</text>
        </view>
        <view class="reminder-footer">
          <text class="due-date">{{ formatDate(reminder.dueDate) }}</text>
          <text v-if="reminder.status === 0" class="status-badge">未读</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { reminderApi, type Reminder } from '@/api/reminder'

const loading = ref(true)
const reminders = ref<Reminder[]>([])

onMounted(async () => {
  await loadReminders()
})

async function loadReminders() {
  try {
    loading.value = true
    const res = await reminderApi.getMyReminders()
    if (res.code === 200) {
      reminders.value = res.data
    }
  } catch (error) {
    console.error('加载提醒失败:', error)
  } finally {
    loading.value = false
  }
}

async function handleMarkAsRead(reminder: Reminder) {
  if (reminder.status === 1) return

  try {
    const res = await reminderApi.markAsRead(reminder.id)
    if (res.code === 200) {
      reminder.status = 1
      uni.showToast({
        title: '已标记为已读',
        icon: 'success'
      })
    }
  } catch (error) {
    console.error('标记失败:', error)
  }
}

function getTypeIcon(type: string): string {
  const icons: Record<string, string> = {
    VACCINE: '💉',
    BIRTHDAY: '🎂',
    CHECKUP: '🏥',
    MEDICINE: '💊'
  }
  return icons[type] || '📌'
}

function getTypeName(type: string): string {
  const names: Record<string, string> = {
    VACCINE: '疫苗提醒',
    BIRTHDAY: '生日祝福',
    CHECKUP: '体检提醒',
    MEDICINE: '用药提醒'
  }
  return names[type] || '提醒'
}

function getContent(reminder: Reminder): string {
  if (reminder.content) return reminder.content
  
  const templates: Record<string, string> = {
    VACCINE: '您的宠物疫苗即将到期，请及时接种',
    BIRTHDAY: '今天是您宠物的生日，祝生日快乐！',
    CHECKUP: '该进行定期体检了',
    MEDICINE: '请按时给宠物服药'
  }
  return templates[reminder.type] || '您有一条新提醒'
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = date.getTime() - now.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) return '今天'
  if (days === 1) return '明天'
  if (days > 0) return `${days}天后`
  return dateStr
}
</script>

<style scoped>
.reminders-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.header {
  padding: 30rpx 20rpx;
  background: #fff;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.loading, .empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
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

.reminders-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.reminder-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.reminder-card.unread {
  border-left: 6rpx solid #3b82f6;
}

.reminder-type {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.type-icon {
  font-size: 36rpx;
  margin-right: 12rpx;
}

.type-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.reminder-content {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20rpx;
}

.reminder-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.due-date {
  font-size: 24rpx;
  color: #999;
}

.status-badge {
  font-size: 22rpx;
  color: #3b82f6;
  background: #eff6ff;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
</style>
