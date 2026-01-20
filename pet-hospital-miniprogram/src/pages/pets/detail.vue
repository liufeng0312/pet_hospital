<template>
  <view class="detail-container">
    <view class="pet-header card">
      <image 
        class="pet-avatar-large" 
        :src="pet?.photoUrl || '/static/images/default-pet.png'" 
        mode="aspectFill"
      ></image>
      <view class="pet-basic-info">
        <text class="pet-name-large">{{ pet?.name }}</text>
        <text class="pet-breed">{{ pet?.species }} · {{ pet?.breed || '未知品种' }}</text>
        <text class="pet-age">{{ calculateAge(pet?.birthDate) }}</text>
      </view>
      <button class="edit-btn" @tap="goToEdit">编辑</button>
    </view>
    
    <view class="info-section card">
      <text class="section-title">基本信息</text>
      <view class="info-grid">
        <view class="info-item">
          <text class="info-label">性别</text>
          <text class="info-value">{{ getGenderText(pet?.gender) }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">体重</text>
          <text class="info-value">{{ pet?.weight || '--' }} kg</text>
        </view>
        <view class="info-item">
          <text class="info-label">出生日期</text>
          <text class="info-value">{{ formatDate(pet?.birthDate) }}</text>
        </view>
      </view>
    </view>
    
    <view class="notes-section card" v-if="pet?.notes">
      <text class="section-title">特殊注意事项</text>
      <text class="notes-text">{{ pet.notes }}</text>
    </view>
    
    <view class="history-section card">
      <text class="section-title">就诊历史</text>
      <text class="empty-text">暂无就诊记录</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { petApi, type Pet } from '@/api/pet'

const pet = ref<Pet | null>(null)
const petId = ref<number>(0)

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  petId.value = Number(currentPage.options.id)
  
  if (petId.value) {
    loadPetDetail()
  }
})

async function loadPetDetail() {
  try {
    const res = await petApi.getPetDetail(petId.value)
    if (res.code === 200) {
      pet.value = res.data
    }
  } catch (error) {
    console.error('加载宠物详情失败:', error)
  }
}

function calculateAge(birthDate?: string) {
  if (!birthDate) return '年龄未知'
  
  const birth = new Date(birthDate)
  const now = new Date()
  const years = now.getFullYear() - birth.getFullYear()
  const months = now.getMonth() - birth.getMonth()
  
  if (years === 0) {
    return `${months}个月`
  } else if (months < 0) {
    return `${years - 1}岁${12 + months}个月`
  } else {
    return `${years}岁${months}个月`
  }
}

function getGenderText(gender?: number) {
  const map: Record<number, string> = {
    1: '公',
    2: '母',
    0: '未知'
  }
  return map[gender || 0] || '未知'
}

function formatDate(date?: string) {
  if (!date) return '--'
  const d = new Date(date)
  return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
}

function goToEdit() {
  uni.navigateTo({
    url: `/pages/pets/edit?id=${petId.value}`
  })
}
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 120rpx;
}

.pet-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx 24rpx;
  margin-bottom: 24rpx;
}

.pet-avatar-large {
  width: 200rpx;
  height: 200rpx;
  border-radius: 100rpx;
  margin-bottom: 24rpx;
}

.pet-basic-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 24rpx;
}

.pet-name-large {
  font-size: 40rpx;
  font-weight: 700;
  color: var(--text-color);
}

.pet-breed {
  font-size: 28rpx;
  color: var(--text-secondary);
}

.pet-age {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.edit-btn {
  background: #3B82F6;
  color: #FFFFFF;
  padding: 16rpx 48rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.info-section {
  margin-bottom: 24rpx;
}

.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 20rpx;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.info-label {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.info-value {
  font-size: 28rpx;
  color: var(--text-color);
  font-weight: 500;
}

.notes-section {
  margin-bottom: 24rpx;
}

.notes-text {
  font-size: 26rpx;
  color: var(--text-color);
  line-height: 1.6;
}

.history-section {
  margin-bottom: 24rpx;
}

.empty-text {
  display: block;
  text-align: center;
  padding: 40rpx 0;
  font-size: 26rpx;
  color: var(--text-secondary);
}
</style>
