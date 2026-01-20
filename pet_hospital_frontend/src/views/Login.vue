<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <el-icon class="logo-icon" :size="48" color="#3b82f6"><FirstAidKit /></el-icon>
        <h2>宠物医院管理系统</h2>
        <p>专业、高效、智能的动物医疗管理平台</p>
      </div>
      
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large">
        <el-form-item prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="用户名" 
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <div class="login-actions">
          <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码？</el-link>
        </div>

        <el-button 
          class="login-submit" 
          type="primary" 
          :loading="loading" 
          @click="handleLogin"
        >
          立即登录
        </el-button>
      </el-form>

      <div class="login-footer">
        © 2026 Pet Hospital OS. All Rights Reserved.
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, FirstAidKit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        const res = await authApi.login(loginForm)
        if (res.code === 200) {
          ElMessage.success('登录成功')
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('user', JSON.stringify(res.data.user))
          router.push('/dashboard')
        } else {
          ElMessage.error(res.msg || '登录失败')
        }
      } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '网络错误，请稍后再试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 2;
}

.login-box {
  width: 440px;
  padding: 48px;
  background: rgba(255, 255, 255, 0.65); /* More transparent */
  backdrop-filter: blur(24px); /* Increased blur */
  -webkit-backdrop-filter: blur(24px);
  border-radius: 28px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 2;
  animation: fadeIn 0.8s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  background: white;
  padding: 12px;
  border-radius: 20px;
  box-shadow: 0 10px 20px -5px rgba(59, 130, 246, 0.3);
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.logo-icon:hover {
  transform: rotate(10deg);
}

.login-header h2 {
  font-size: 30px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.login-header p {
  color: #475569;
  font-size: 15px;
  font-weight: 500;
}

.login-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}

.login-submit {
  width: 100%;
  height: 52px;
  font-size: 17px;
  font-weight: 700;
  border-radius: 14px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  box-shadow: 0 8px 20px -6px rgba(37, 99, 235, 0.5);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-submit:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 25px -8px rgba(37, 99, 235, 0.6);
  filter: brightness(1.1);
}

.login-footer {
  margin-top: 44px;
  text-align: center;
  color: #64748b;
  font-size: 13px;
  font-weight: 500;
}

:deep(.el-input__wrapper) {
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02) inset !important;
  padding: 6px 18px;
  border: 1px solid rgba(226, 232, 240, 0.8);
  transition: all 0.3s;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}
</style>
