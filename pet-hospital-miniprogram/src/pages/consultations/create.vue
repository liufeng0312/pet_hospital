<template>
  <view class="create-page">
    <view class="form">
      <view class="form-item">
        <text class="label">咨询标题</text>
        <input
          v-model="form.title"
          class="input"
          placeholder="请输入咨询标题"
          maxlength="100"
        />
      </view>

      <view class="form-item">
        <text class="label">关联宠物（可选）</text>
        <picker mode="selector" :range="pets" range-key="name" @change="onPetChange">
          <view class="picker">
            <text>{{ selectedPet ? selectedPet.name : '请选择宠物' }}</text>
            <text class="arrow">▼</text>
          </view>
        </picker>
      </view>

      <view class="form-item">
        <text class="label">咨询内容</text>
        <textarea
          v-model="form.content"
          class="textarea"
          placeholder="请详细描述您的问题..."
          maxlength="500"
        />
        <text class="char-count">{{ form.content.length }}/500</text>
      </view>

      <view class="form-item">
        <text class="label">上传图片（选填）</text>
        <view class="image-upload">
          <view v-for="(img, index) in images" :key="index" class="image-item">
            <image :src="img" mode="aspectFill" />
            <view class="delete-btn" @click="deleteImage(index)">×</view>
          </view>
          <view v-if="images.length < 3" class="upload-btn" @click="chooseImage">
            <text>+</text>
          </view>
        </view>
      </view>
    </view>

    <view class="bottom-btns">
      <button class="cancel-btn" @click="goBack">取消</button>
      <button class="submit-btn" @click="submit" :disabled="submitting">
        {{ submitting ? '提交中...' : '提交咨询' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { consultationApi } from '@/api/consultation'
import { petApi } from '@/api/pet'

const form = ref({
  title: '',
  petId: undefined as number | undefined,
  content: ''
})

const images = ref<string[]>([])
const pets = ref<any[]>([])
const selectedPet = ref<any>(null)
const submitting = ref(false)

onMounted(async () => {
  await loadPets()
})

async function loadPets() {
  try {
    const res = await petApi.getMyPets()
    if (res.code === 200) {
      pets.value = res.data
    }
  } catch (error) {
    console.error('加载宠物列表失败:', error)
  }
}

function onPetChange(e: any) {
  const index = e.detail.value
  if (index >= 0 && index < pets.value.length) {
    selectedPet.value = pets.value[index]
    form.value.petId = selectedPet.value.id
  }
}

function chooseImage() {
  uni.chooseImage({
    count: 3 - images.value.length,
    sizeType: ['compressed'],
    success: (res) => {
      images.value.push(...res.tempFilePaths)
    }
  })
}

function deleteImage(index: number) {
  images.value.splice(index, 1)
}

async function submit() {
  if (!form.value.title.trim()) {
    uni.showToast({ title: '请输入标题', icon: 'none' })
    return
  }
  if (!form.value.content.trim()) {
    uni.showToast({ title: '请输入咨询内容', icon: 'none' })
    return
  }

  try {
    submitting.value = true
    const data: any = {
      title: form.value.title,
      content: form.value.content
    }
    if (form.value.petId) {
      data.petId = form.value.petId
    }
    if (images.value.length > 0) {
      data.images = JSON.stringify(images.value)
    }

    const res = await consultationApi.create(data)
    if (res.code === 200) {
      uni.showToast({ title: '提交成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  } catch (error) {
    console.error('提交咨询失败:', error)
    uni.showToast({ title: '提交失败，请重试', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

function goBack() {
  uni.navigateBack()
}
</script>

<style scoped>
.create-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx 20rpx 120rpx 20rpx;
}

.form {
  background: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.input {
  width: 100%;
  padding: 24rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.arrow {
  font-size: 20rpx;
  color: #999;
}

.textarea {
  width: 100%;
  height: 300rpx;
  padding: 24rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 12rpx;
  font-size: 28rpx;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-top: 12rpx;
}

.image-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
}

.image-item image {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 48rpx;
  height: 48rpx;
  background: #ef4444;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
}

.upload-btn {
  width: 200rpx;
  height: 200rpx;
  border: 2rpx dashed #d1d5db;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80rpx;
  color: #9ca3af;
}

.bottom-btns {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx;
  background: #fff;
  border-top: 1rpx solid #f0f0f0;
}

.cancel-btn, .submit-btn {
  flex: 1;
  padding: 28rpx;
  border-radius: 24rpx;
  font-size: 32rpx;
  border: none;
}

.cancel-btn {
  background: #f3f4f6;
  color: #6b7280;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: bold;
}
</style>
