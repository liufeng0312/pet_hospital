<template>
  <view class="pets-container">
    <!-- 宠物列表 -->
    <view v-if="pets.length > 0" class="pets-list">
      <view 
        v-for="pet in pets" 
        :key="pet.id"
        class="pet-card card"
        @tap="goToDetail(pet.id)"
      >
        <image 
          class="pet-avatar" 
          :src="pet.photoUrl || '/static/images/default-pet.png'" 
          mode="aspectFill"
        ></image>
        <view class="pet-info">
          <text class="pet-name">{{ pet.name }}</text>
          <text class="pet-breed">{{ pet.species }} · {{ pet.breed || '未知品种' }}</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>
    
    <!-- 空状态 -->
    <view v-else class="empty-state">
      <text class="empty-icon">🐾</text>
      <text class="empty-text">还没有宠物档案</text>
      <text class="empty-desc">点击下方按钮添加您的宠物</text>
    </view>
    
    <!-- 添加按钮 -->
    <view class="add-btn" @tap="goToAdd">
      <text class="add-icon">+</text>
      <text class="add-text">添加宠物</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { petApi, type Pet } from '@/api/pet'

const pets = ref<Pet[]>([])
const loading = ref(false)

onShow(() => {
  loadPets()
})

async function loadPets() {
  loading.value = true
  try {
    const res = await petApi.getMyPets()
    if (res.code === 200) {
      pets.value = res.data
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  } finally {
    loading.value = false
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

function goToDetail(id: number) {
  uni.navigateTo({
    url: `/pages/pets/detail?id=${id}`
  })
}

function goToAdd() {
  uni.navigateTo({
    url: '/pages/pets/edit'
  })
}
</script>

<style scoped>
.pets-container {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 160rpx;
}

.pets-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.pet-card {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx;
}

.pet-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  flex-shrink: 0;
}

.pet-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.pet-name {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-color);
}

.pet-breed {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.pet-age {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.arrow {
  font-size: 48rpx;
  color: #CBD5E1;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 40rpx;
}

.empty-icon {
  font-size: 120rpx;
  margin-bottom: 24rpx;
}

.empty-text {
  font-size: 32rpx;
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 26rpx;
  color: var(--text-secondary);
}

.add-btn {
  position: fixed;
  bottom: 120rpx;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #3B82F6, #60A5FA);
  color: #FFFFFF;
  padding: 24rpx 64rpx;
  border-radius: 100rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
  box-shadow: 0 8rpx 20rpx rgba(59, 130, 246, 0.4);
  z-index: 100;
  transition: all 0.2s ease;
}

.add-btn:active {
  transform: translateX(-50%) scale(0.96);
  box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.4);
}

.add-icon {
  font-size: 44rpx;
  font-weight: 500;
  margin-top: -6rpx;
  line-height: 1;
}

.add-text {
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 2rpx;
}
</style>
