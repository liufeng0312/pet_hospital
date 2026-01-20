<template>
  <view class="login-container">
    <view class="login-header">
      <image class="logo" src="/static/images/logo.png" mode="aspectFit"></image>
      <text class="title">宠物医院</text>
      <text class="subtitle">专业、温情、全天候</text>
    </view>
    
    <view class="login-content">
      <button 
        class="wechat-login-btn" 
        open-type="getUserInfo"
        @getuserinfo="handleWechatLogin"
      >
        <text class="btn-icon">📱</text>
        <text class="btn-text">微信一键登录</text>
      </button>
      
      <view class="tips">
        <text class="tips-text">登录即表示同意</text>
        <text class="tips-link">《用户协议》</text>
        <text class="tips-text">和</text>
        <text class="tips-link">《隐私政策》</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)

// 微信登录
async function handleWechatLogin(e: any) {
  if (!e.detail.userInfo) {
    uni.showToast({
      title: '请授权登录',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  
  try {
    // 获取微信登录code
    const loginRes = await new Promise<any>((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: resolve,
        fail: reject
      })
    })
    
    if (!loginRes.code) {
      throw new Error('获取登录凭证失败')
    }
    
    // 调用后端登录接口
    const res = await authApi.wechatLogin(loginRes.code)
    
    if (res.code === 200) {
      // 判断是否需要绑定手机号
      if (res.data.token) {
        // 已绑定，直接登录
        // Fix: Ensure user is not null, though API should return it. 
        // If null, we might need to handle it, but for now assuming API contract or providing empty object as fallback if that matches type, 
        // or just casting if we are sure. Let's provide a fallback or assertion if we can't change type.
        // Actually, let's just assert it's there or handle it.
        if (res.data.user) {
             userStore.login(res.data.token, res.data.user)
        } else {
             // Handle edge case where token exists but user doesn't? Should not happen.
             console.error("Login successful but user info missing")
        }
        
        uni.showToast({
          title: '登录成功',
          icon: 'success'
        })
        
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1000)
      } else {
        // 需要绑定手机号
        const openid = res.data.openid
        
        // 弹出输入框获取手机号
        uni.showModal({
          title: '绑定手机号',
          editable: true,
          placeholderText: '请输入手机号',
          success: async (modalRes) => {
                if (modalRes.confirm && modalRes.content && openid) {
                  try {
                    // 调用绑定接口
                    const bindRes = await authApi.bindPhone(modalRes.content, openid)
                
                if (bindRes.code === 200) {
                  // 保存登录信息
                  userStore.login(bindRes.data.token, bindRes.data.user)
                  
                  uni.showToast({
                    title: '绑定成功',
                    icon: 'success'
                  })
                  
                  setTimeout(() => {
                    uni.switchTab({
                      url: '/pages/index/index'
                    })
                  }, 1000)
                }
              } catch (error: any) {
                uni.showToast({
                  title: error.msg || '绑定失败',
                  icon: 'none'
                })
              }
            }
          }
        })
      }
    }
  } catch (error: any) {
    console.error('登录失败:', error)
    uni.showToast({
      title: error.msg || '登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 40rpx;
}

.login-header {
  text-align: center;
  margin-bottom: 120rpx;
}

.logo {
  width: 160rpx;
  height: 160rpx;
  margin-bottom: 32rpx;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: 700;
  color: #FFFFFF;
  margin-bottom: 16rpx;
}

.subtitle {
  display: block;
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
}

.login-content {
  width: 100%;
}

.wechat-login-btn {
  width: 100%;
  height: 96rpx;
  background: #3B82F6;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(59, 130, 246, 0.3);
  margin-bottom: 40rpx;
}

.btn-icon {
  font-size: 40rpx;
  margin-right: 16rpx;
}

.btn-text {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
}

.tips {
  text-align: center;
}

.tips-text {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

.tips-link {
  font-size: 24rpx;
  color: #FFFFFF;
  text-decoration: underline;
}
</style>
