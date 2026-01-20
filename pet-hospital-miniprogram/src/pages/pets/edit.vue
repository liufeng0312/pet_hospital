<template>
  <view class="edit-container">
    <view class="form-section card">
      <view class="avatar-upload" @tap="chooseImage">
        <image 
          v-if="form.photoUrl" 
          class="avatar-preview" 
          :src="form.photoUrl" 
          mode="aspectFill"
        ></image>
        <view v-else class="avatar-placeholder">
          <text class="upload-icon">📷</text>
          <text class="upload-text">上传照片</text>
        </view>
      </view>
      
      <view class="form-item">
        <text class="form-label">昵称 *</text>
        <input 
          class="form-input" 
          v-model="form.name" 
          placeholder="请输入宠物昵称"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">物种 *</text>
        <picker mode="selector" :range="speciesList" @change="onSpeciesChange">
          <view class="form-input picker-input">
            {{ form.species || '请选择物种' }}
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="form-label">品种</text>
        <input 
          class="form-input" 
          v-model="form.breed" 
          placeholder="请输入品种"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">性别 *</text>
        <picker mode="selector" :range="genderList" :range-key="'label'" @change="onGenderChange">
          <view class="form-input picker-input">
            {{ getGenderText(form.gender) }}
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="form-label">出生日期</text>
        <picker mode="date" :value="form.birthDate" @change="onDateChange">
          <view class="form-input picker-input">
            {{ form.birthDate || '请选择日期' }}
          </view>
        </picker>
      </view>
      
      <view class="form-item">
        <text class="form-label">体重 (kg)</text>
        <input 
          class="form-input" 
          v-model.number="form.weight" 
          type="digit"
          placeholder="请输入体重"
        />
      </view>
      
      <view class="form-item">
        <text class="form-label">特殊注意事项</text>
        <textarea 
          class="form-textarea" 
          v-model="form.notes" 
          placeholder="如过敏史、特殊疾病等"
          maxlength="500"
        ></textarea>
      </view>
    </view>
    
    <cover-view class="submit-btn" @tap="handleSubmit">
      <cover-view class="btn-text">{{ isEdit ? '保存' : '添加' }}</cover-view>
    </cover-view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { petApi, type PetForm } from '@/api/pet'

const isEdit = ref(false)
const petId = ref<number>(0)
const loading = ref(false)

const speciesList = ['猫', '狗', '兔子', '仓鼠', '鸟类', '其他']
const genderList = [
  { label: '公', value: 1 },
  { label: '母', value: 2 },
  { label: '未知', value: 0 }
]

const form = ref<PetForm>({
  name: '',
  species: '',
  breed: '',
  gender: 0,
  birthDate: '',
  weight: undefined,
  photoUrl: '',
  notes: ''
})

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  const id = currentPage.options.id
  
  if (id) {
    isEdit.value = true
    petId.value = Number(id)
    loadPetDetail()
  }
})

async function loadPetDetail() {
  try {
    const res = await petApi.getPetDetail(petId.value)
    if (res.code === 200) {
      form.value = {
        name: res.data.name,
        species: res.data.species,
        breed: res.data.breed,
        gender: res.data.gender,
        birthDate: res.data.birthDate,
        weight: res.data.weight,
        photoUrl: res.data.photoUrl,
        notes: res.data.notes
      }
    }
  } catch (error) {
    console.error('加载宠物详情失败:', error)
  }
}

function onSpeciesChange(e: any) {
  form.value.species = speciesList[e.detail.value]
}

function onGenderChange(e: any) {
  form.value.gender = genderList[e.detail.value].value
}

function onDateChange(e: any) {
  form.value.birthDate = e.detail.value
}

function getGenderText(gender: number) {
  const item = genderList.find(g => g.value === gender)
  return item ? item.label : '请选择性别'
}

async function chooseImage() {
  try {
    const res = await new Promise<any>((resolve, reject) => {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: resolve,
        fail: reject
      })
    })
    
    if (res.tempFilePaths && res.tempFilePaths.length > 0) {
      // 1. 立即显示本地预览图
      const tempFilePath = res.tempFilePaths[0]
      form.value.photoUrl = tempFilePath
      
      uni.showLoading({ title: '上传中...' })
      
      try {
        // 2. 上传到服务器
        const remoteUrl = await petApi.uploadImage(tempFilePath)
        
        // 3. 更新为远程URL
        form.value.photoUrl = remoteUrl
        
        uni.hideLoading()
        uni.showToast({
          title: '上传成功',
          icon: 'success'
        })
      } catch (error) {
        // 上传失败，清空预览
        form.value.photoUrl = ''
        uni.hideLoading()
        console.error('上传图片失败:', error)
      }
    }
  } catch (error) {
    uni.hideLoading()
    console.error('选择图片失败:', error)
  }
}

async function handleSubmit() {
  if (!form.value.name) {
    uni.showToast({
      title: '请输入宠物昵称',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.species) {
    uni.showToast({
      title: '请选择物种',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  
  try {
    if (isEdit.value) {
      await petApi.updatePet(petId.value, form.value)
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
    } else {
      await petApi.createPet(form.value)
      uni.showToast({
        title: '添加成功',
        icon: 'success'
      })
    }
    
    setTimeout(() => {
      uni.navigateBack()
    }, 1000)
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.edit-container {
  min-height: 100vh;
  padding: 24rpx;
  padding-bottom: 240rpx;
}

.form-section {
  padding: 32rpx 24rpx;
}

.avatar-upload {
  display: flex;
  justify-content: center;
  margin-bottom: 40rpx;
}

.avatar-preview {
  width: 200rpx;
  height: 200rpx;
  border-radius: 100rpx;
}

.avatar-placeholder {
  width: 200rpx;
  height: 200rpx;
  border-radius: 100rpx;
  background: #F1F5F9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.upload-icon {
  font-size: 60rpx;
}

.upload-text {
  font-size: 24rpx;
  color: var(--text-secondary);
}

.form-item {
  margin-bottom: 32rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: var(--text-color);
  font-weight: 600;
  margin-bottom: 12rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  padding: 0 24rpx;
  background: #F8FAFC;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: var(--text-color);
  border: 1rpx solid var(--border-color);
}

.picker-input {
  display: flex;
  align-items: center;
  color: var(--text-color);
}

.form-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 20rpx 24rpx;
  background: #F8FAFC;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: var(--text-color);
  border: 1rpx solid var(--border-color);
}

.submit-btn {
  position: fixed;
  bottom: calc(40rpx + env(safe-area-inset-bottom));
  left: 24rpx;
  right: 24rpx;
  height: 96rpx;
  background: #3B82F6;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.btn-text {
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: 600;
}
</style>
