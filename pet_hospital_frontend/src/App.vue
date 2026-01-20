<template>
  <el-container class="layout-container">
    <el-header v-if="!isLoginPage && !isLandingPage">
      <div class="header-content">
        <div class="logo-area">
          <el-icon class="logo-icon" :size="32" color="#3b82f6"><FirstAidKit /></el-icon>
          <span>{{ $t('header.systemName') }}</span>
          <!-- 侧边栏收缩按钮 -->
          <el-tooltip :content="isCollapse ? '展开菜单' : '收起菜单'" placement="bottom">
            <el-icon class="collapse-icon" :size="20" @click="toggleCollapse">
              <Expand v-if="isCollapse" />
              <Fold v-else />
            </el-icon>
          </el-tooltip>
        </div>
        <div class="header-right">
          <!-- 官网链接图标 -->
          <el-tooltip :content="$t('header.visitWebsite')" placement="bottom">
            <el-icon class="website-icon" :size="20" @click="goToWebsite">
              <Promotion />
            </el-icon>
          </el-tooltip>
          <!-- 语言切换 -->
          <el-dropdown @command="handleLanguageChange" trigger="click">
            <el-button text class="language-btn">
              <span class="lang-text">{{ currentLanguage === 'zh' ? '中' : 'EN' }}</span>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh" :class="{ 'is-active': currentLanguage === 'zh' }">
                  简体中文
                </el-dropdown-item>
                <el-dropdown-item command="en" :class="{ 'is-active': currentLanguage === 'en' }">
                  English
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <!-- 主题切换 -->
          <el-tooltip :content="isDark ? '切换为浅色' : '切换为深色'" placement="bottom">
            <el-icon class="theme-icon" :size="20" @click="toggleTheme">
              <Moon v-if="!isDark" />
              <Sunny v-else />
            </el-icon>
          </el-tooltip>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              <el-avatar :size="32" :src="currentUser.avatar" class="user-avatar-small">
                {{ currentUser.name?.charAt(0) }}
              </el-avatar>
              {{ currentUser.name }} <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">{{ $t('header.profile') }}</el-dropdown-item>
                <el-dropdown-item command="logout" divided>{{ $t('header.logout') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 个人中心弹窗 -->
    <el-dialog 
      v-model="profileDialogVisible" 
      title="个人中心" 
      width="600px"
      :close-on-click-modal="false"
    >
      <!-- 头像区域 -->
      <div class="profile-header">
        <el-upload
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
          class="avatar-uploader"
        >
          <el-avatar :size="80" :src="profileForm.avatar" class="profile-avatar clickable">
            {{ profileForm.name?.charAt(0) || 'U' }}
          </el-avatar>
        </el-upload>
        <div class="avatar-upload-hint">点击头像更换</div>
        <div class="profile-title">
          <h3>{{ profileForm.name }}</h3>
          <el-tag :type="getRoleType(profileForm.role)" size="small">
            {{ getRoleLabel(profileForm.role) }}
          </el-tag>
        </div>
      </div>

      <el-divider />

      <!-- 基础信息区 -->
      <div class="info-section">
        <h4 class="section-title">基础信息</h4>
        <el-form :model="profileForm" label-width="90px" :rules="profileRules" ref="profileFormRef">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="工号">
                <el-input :model-value="String(profileForm.id)" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="角色">
                <el-input :model-value="getRoleLabel(profileForm.role)" disabled />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="用户名">
            <el-input :model-value="profileForm.username" disabled />
          </el-form-item>

          <el-form-item label="姓名" prop="name">
            <el-input v-model="profileForm.name" placeholder="请输入姓名" />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input v-model="profileForm.phone" placeholder="请输入手机号" maxlength="11" />
          </el-form-item>

          <el-form-item label="注册时间" v-if="profileForm.createdAt">
            <el-input :model-value="formatDate(profileForm.createdAt)" disabled />
          </el-form-item>
        </el-form>
      </div>

      <el-divider />

      <!-- 密码修改区 -->
      <div class="info-section">
        <el-collapse v-model="activeCollapse">
          <el-collapse-item title="修改密码" name="password">
            <el-form :model="profileForm" label-width="90px" :rules="profileRules" ref="passwordFormRef">
              <el-form-item label="新密码" prop="password">
                <el-input 
                  v-model="profileForm.password" 
                  type="password" 
                  placeholder="不修改请留空" 
                  show-password 
                  clearable
                />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="profileForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入新密码" 
                  show-password 
                  clearable
                />
              </el-form-item>
              <el-alert 
                title="密码要求：至少6个字符" 
                type="info" 
                :closable="false"
                show-icon
              />
            </el-form>
          </el-collapse-item>
        </el-collapse>
      </div>

      <template #footer>
        <el-button @click="profileDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateProfile" :loading="submitting">
          保存修改
        </el-button>
      </template>
    </el-dialog>
    
    <el-container class="main-container">
      <el-aside v-if="!isLoginPage && !isLandingPage" :width="isCollapse ? '64px' : '240px'" class="glass-sidebar">
        <el-scrollbar>
          <el-menu
            :default-active="$route.path"
            class="el-menu-vertical"
            router
            unique-opened
            :collapse="isCollapse"
            :collapse-transition="false"
          >
            <el-menu-item index="/dashboard">
              <el-icon><Menu /></el-icon>
              <span>{{ $t('menu.dashboard') }}</span>
            </el-menu-item>

            <!-- 接诊服务 -->
            <el-sub-menu index="1">
              <template #title>
                <el-icon><EditPen /></el-icon>
                <span>接诊服务</span>
              </template>
              <el-menu-item index="/registrations">
                <el-icon><Tickets /></el-icon>
                <span>{{ $t('menu.registration') }}</span>
              </el-menu-item>
              <el-menu-item index="/queue">
                <el-icon><Timer /></el-icon>
                <span>{{ $t('menu.queue') }}</span>
              </el-menu-item>
              <el-menu-item index="/consultations">
                <el-icon><ChatLineRound /></el-icon>
                <span>{{ $t('menu.consultation') }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 诊疗服务 -->
            <el-sub-menu index="2">
              <template #title>
                <el-icon><FirstAidKit /></el-icon>
                <span>诊疗服务</span>
              </template>
              <el-menu-item index="/doctor-workbench">
                <el-icon><Monitor /></el-icon>
                <span>{{ $t('menu.doctorWorkbench') }}</span>
              </el-menu-item>
              <el-menu-item index="/lab-workbench">
                <el-icon><Aim /></el-icon>
                <span>{{ $t('menu.labWorkbench') }}</span>
              </el-menu-item>
              <el-menu-item index="/inpatients">
                <el-icon><House /></el-icon>
                <span>{{ $t('menu.inpatient') }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 医疗记录 -->
            <el-sub-menu index="3">
              <template #title>
                <el-icon><Document /></el-icon>
                <span>{{ $t('menu.records') }}</span>
              </template>
              <el-menu-item index="/medical-records">
                <el-icon><Collection /></el-icon>
                <span>{{ $t('menu.medicalRecords') }}</span>
              </el-menu-item>
              <el-menu-item index="/services">
                <el-icon><Grid /></el-icon>
                <span>{{ $t('menu.services') }}</span>
              </el-menu-item>
              <el-menu-item index="/prescriptions">
                <el-icon><Tickets /></el-icon>
                <span>{{ $t('menu.prescriptions') }}</span>
              </el-menu-item>
              <el-menu-item index="/vaccine-records">
                <el-icon><Memo /></el-icon>
                <span>{{ $t('menu.vaccineRecords') }}</span>
              </el-menu-item>
              <el-menu-item index="/lab-tests">
                <el-icon><Aim /></el-icon>
                <span>{{ $t('menu.labTests') }}</span>
              </el-menu-item>
              <el-menu-item index="/surgeries">
                <el-icon><Scissor /></el-icon>
                <span>{{ $t('menu.surgeries') }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 库存管理 -->
            <el-sub-menu index="4">
              <template #title>
                <el-icon><Box /></el-icon>
                <span>库存管理</span>
              </template>
              <el-menu-item index="/drugs">
                <el-icon><Microphone /></el-icon>
                <span>{{ $t('menu.drugs') }}</span>
              </el-menu-item>
              <el-menu-item index="/vaccine-inventory">
                <el-icon><Memo /></el-icon>
                <span>{{ $t('menu.vaccineInventory') }}</span>
              </el-menu-item>
              
            </el-sub-menu>

            <!-- 客户管理 -->
            <el-sub-menu index="5">
              <template #title>
                <el-icon><User /></el-icon>
                <span>客户管理</span>
              </template>
              <el-menu-item index="/customers">
                <el-icon><UserFilled /></el-icon>
                <span>{{ $t('menu.customers') }}</span>
              </el-menu-item>
              <el-menu-item index="/pets">
                <el-icon><Star /></el-icon>
                <span>{{ $t('menu.pets') }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 财务管理 -->
            <el-sub-menu index="6">
              <template #title>
                <el-icon><Money /></el-icon>
                <span>{{ $t('menu.finance') }}</span>
              </template>
              <el-menu-item index="/payment">
                <el-icon><CreditCard /></el-icon>
                <span>{{ $t('menu.payment') }}</span>
              </el-menu-item>
              <el-menu-item index="/reports">
                <el-icon><DataLine /></el-icon>
                <span>{{ $t('menu.reports') }}</span>
              </el-menu-item>
            </el-sub-menu>

            <!-- 系统管理 -->
            <el-sub-menu index="7">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>{{ $t('menu.system') }}</span>
              </template>
              <el-menu-item index="/employees">
                <el-icon><Avatar /></el-icon>
                <span>{{ $t('menu.employees') }}</span>
              </el-menu-item>
              <el-menu-item index="/doctor-schedule">
                <el-icon><Calendar /></el-icon>
                <span>医生排班</span>
              </el-menu-item>
              <el-menu-item index="/articles">
                <el-icon><Reading /></el-icon>
                <span>{{ $t('menu.articles') }}</span>
              </el-menu-item>
              <!-- 消息中心 -->
            <el-menu-item index="/reminders">
              <el-icon><Bell /></el-icon>
              <span>{{ $t('menu.reminders') }}</span>
            </el-menu-item>
            </el-sub-menu>

            
          </el-menu>
        </el-scrollbar>
      </el-aside>
      <el-main :style="{ padding: (isLoginPage || isLandingPage) ? '0' : '24px' }" :class="['main-content', { 'is-login': isLoginPage, 'is-landing': isLandingPage }]">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>




<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import {
  User,
  Tickets,
  FirstAidKit,
  CreditCard,
  Box,
  Monitor,
  Microphone,
  House,
  Document,
  Collection,
  Menu,
  ArrowDown,
  Money,
  DataLine,
  Bell,
  UserFilled,
  Setting,
  Reading,
  EditPen,
  Timer,
  Aim,
  Scissor,
  Star,
  Grid,
  Avatar,
  ChatLineRound,
  Promotion,
  Operation,
  Moon,
  Sunny,
  Expand,
  Fold,
  Calendar
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { employeeApi } from '@/api/employee'
import { authApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const { locale, t } = useI18n()

const isLoginPage = computed(() => route.path === '/login')
const isLandingPage = computed(() => route.path === '/')

// 当前登录用户
const currentUser = ref<any>({
  id: 0,
  name: '未登录',
  username: '',
  role: '',
  phone: '',
  avatar: ''
})

// 当前语言
const currentLanguage = ref('zh')

// 当前主题
const isDark = ref(false)

// 侧边栏收缩状态
const isCollapse = ref(false)

// 加载语言设置
onMounted(() => {
  const userJson = localStorage.getItem('user')
  if (userJson) {
    const user = JSON.parse(userJson)
    currentUser.value = user
    
    // 处理头像URL - 如果有avatar且不是完整URL，构建完整URL
    if (user.avatar && !user.avatar.startsWith('http')) {
      currentUser.value.avatar = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${user.avatar}`
    }
  }
})

// 监听路由变化，如果用户信息更新，重新从 localStorage 读取
watch(() => route.path, () => {
  const userJson = localStorage.getItem('user')
  if (userJson) {
    const user = JSON.parse(userJson)
    currentUser.value = user
    
    // 处理头像URL
    if (user.avatar && !user.avatar.startsWith('http')) {
      currentUser.value.avatar = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${user.avatar}`
    }
  }
  
  // 加载语言设置
  const savedLanguage = localStorage.getItem('language') || 'zh'
  currentLanguage.value = savedLanguage
  
  // 加载主题设置
  const savedTheme = localStorage.getItem('theme') || 'light'
  isDark.value = savedTheme === 'dark'
  if (isDark.value) {
    document.documentElement.classList.add('dark-theme')
  }
  
  // 加载侧边栏收缩状态
  const savedCollapse = localStorage.getItem('sidebarCollapse')
  if (savedCollapse) {
    isCollapse.value = savedCollapse === 'true'
  }
})

const profileDialogVisible = ref(false)
const profileForm = reactive({
  id: 0,
  name: '',
  phone: '',
  username: '',
  role: '',
  createdAt: '',
  avatar: '',
  avatarPath: '', // 存储原始路径用于保存
  password: '',
  confirmPassword: ''
})
const submitting = ref(false)
const activeCollapse = ref<string[]>([])
const profileFormRef = ref()
const passwordFormRef = ref()

// 表单验证规则
const profileRules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { 
      validator: (rule: any, value: any, callback: any) => {
        if (profileForm.password && value !== profileForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 角色标签映射
const getRoleLabel = (role: string) => {
  const roleMap: Record<string, string> = {
    'ADMIN': '管理员',
    'DOCTOR': '医生',
    'NURSE': '护士',
    'CASHIER': '收银员',
    'RECEPTIONIST': '前台'
  }
  return roleMap[role] || '未知'
}

const getRoleType = (role: string) => {
  const typeMap: Record<string, any> = {
    'ADMIN': 'danger',
    'DOCTOR': 'primary', 
    'NURSE': 'success',
    'CASHIER': 'warning',
    'RECEPTIONIST': 'info'
  }
  return typeMap[role] || ''
}

// 上传配置
const uploadUrl = ref(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/upload/avatar`)
const uploadHeaders = ref({
  'Authorization': `Bearer ${localStorage.getItem('token')}` 
})

// 上传前验证
const beforeAvatarUpload = (file: any) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
    return false
  }
  return true
}

// 上传成功回调
const handleAvatarSuccess = (response: any) => {
  if (response.code === 200) {
    console.log('上传成功，返回数据：', response.data)
    const baseUrl = response.data.url // 例如: /uploads/avatars/xxx.jpeg
    // 构建完整URL用于显示（带时间戳避免缓存）
    const fullAvatarUrl = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${baseUrl}?t=${Date.now()}`
    console.log('头像URL：', fullAvatarUrl)
    
    // 更新表单（用于显示）
    profileForm.avatar = fullAvatarUrl
    // 保存原始路径用于提交（不带时间戳）
    profileForm.avatarPath = baseUrl
    
    // 立即更新currentUser以在顶部导航栏显示
    currentUser.value.avatar = fullAvatarUrl
    
    ElMessage.success('头像上传成功，请点击"保存修改"按钮保存')
  } else {
    console.error('上传失败：', response)
    ElMessage.error(response.msg || '上传失败')
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const handleCommand = (command: string) => {
  if (command === 'profile') {
    showProfile()
  } else if (command === 'logout') {
    handleLogout()
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
        await authApi.logout()
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        ElMessage.success('已退出登录')
        router.push('/login')
    } catch (error) {
        console.error('Logout error:', error)
        // 即使请求失败，也清除本地状态并跳转
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
    }
  }).catch(() => {})
}

const showProfile = async () => {
  if (!currentUser.value.id) return
  try {
    const res = await employeeApi.getById(currentUser.value.id)
    const user = res.data
    profileForm.id = user.id!
    profileForm.name = user.name
    profileForm.phone = user.phone
    profileForm.username = user.username || ''
    profileForm.role = user.role || ''
    profileForm.createdAt = user.createdAt || ''
    
    // 处理头像URL
    const avatarPath = user.avatar || ''
    profileForm.avatarPath = avatarPath
    if (avatarPath) {
      // 如果已经是完整URL，直接使用
      if (avatarPath.startsWith('http')) {
        profileForm.avatar = avatarPath
      } else {
        // 否则构建完整URL
        profileForm.avatar = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${avatarPath}`
      }
    } else {
      profileForm.avatar = ''
    }
    
    profileForm.password = ''
    profileForm.confirmPassword = ''
    activeCollapse.value = []
    profileDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

const handleUpdateProfile = async () => {
  // 验证基础信息表单
  try {
    await profileFormRef.value.validate()
  } catch (error) {
    ElMessage.warning('请正确填写基础信息')
    return
  }

  // 如果要修改密码，验证密码表单
  if (profileForm.password) {
    if (profileForm.password.length < 6) {
      ElMessage.warning('密码至少6个字符')
      return
    }
    if (profileForm.password !== profileForm.confirmPassword) {
      ElMessage.warning('两次输入密码不一致')
      return
    }
  }
  
  submitting.value = true
  try {
    // 使用原始路径保存到数据库
    const avatarToSave = profileForm.avatarPath || profileForm.avatar
    console.log('保存头像路径：', avatarToSave)
    
    await employeeApi.update(profileForm.id, {
      id: profileForm.id,
      name: profileForm.name,
      phone: profileForm.phone,
      avatar: avatarToSave,
      password: profileForm.password || undefined
    } as any)
    
    ElMessage.success('保存成功')
    profileDialogVisible.value = false
    
    // 同步更新本地存储和当前状态（使用完整URL）
    const updatedUser = { 
      ...currentUser.value, 
      name: profileForm.name, 
      phone: profileForm.phone, 
      avatar: profileForm.avatar // 完整URL用于显示
    }
    localStorage.setItem('user', JSON.stringify(updatedUser))
    currentUser.value = updatedUser
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

// 跳转到官网
const goToWebsite = () => {
  window.open('/', '_blank')
}

// 切换语言
const handleLanguageChange = (lang: string) => {
  currentLanguage.value = lang
  localStorage.setItem('language', lang)
  // 实际切换i18n语言
  locale.value = lang
  ElMessage.success(t('message.languageSwitched'))
}

// 切换主题
const toggleTheme = () => {
  isDark.value = !isDark.value
  const theme = isDark.value ? 'dark' : 'light'
  localStorage.setItem('theme', theme)
  
  if (isDark.value) {
    document.documentElement.classList.add('dark-theme')
  } else {
    document.documentElement.classList.remove('dark-theme')
  }
  
  ElMessage.success(isDark.value ? '已切换为深色主题' : '已切换为浅色主题')
}

// 切换侧边栏收缩
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
  localStorage.setItem('sidebarCollapse', String(isCollapse.value))
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: url('@/assets/login-bg.png') no-repeat center center fixed;
  background-size: cover;
  overflow: hidden;
}

/* Add a light overlay to make content readable */
.layout-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(249, 250, 251, 0.96); /* Slightly more opaque neutral off-white */
  z-index: 0;
}

.main-container {
  position: relative;
  z-index: 1;
}

.glass-sidebar {
  background: rgba(255, 255, 255, 0.45) !important;
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-right: 1px solid rgba(226, 232, 240, 0.5);
  z-index: 10;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.collapse-icon {
  margin-left: 16px;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s ease;
}

.collapse-icon:hover {
  color: #3b82f6;
  transform: scale(1.1);
}

.el-menu {
  border-right: none;
  background-color: transparent !important;
  padding: 10px 0;
}

:deep(.el-sub-menu__title) {
  color: #475569 !important;
  font-weight: 600;
  margin: 4px 12px;
  border-radius: 12px;
}

:deep(.el-menu-item) {
  color: #64748b !important;
  margin: 4px 12px;
  border-radius: 12px;
  height: 48px;
  font-weight: 500;
}

:deep(.el-menu-item:hover), :deep(.el-sub-menu__title:hover) {
  background-color: rgba(59, 130, 246, 0.08) !important;
  color: #2563eb !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
  color: white !important;
  box-shadow: 0 8px 16px -4px rgba(37, 99, 235, 0.3);
  font-weight: 700;
}

/* 收缩状态下菜单项居中 */
:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-menu--collapse .el-menu-item),
:deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0 !important;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

:deep(.el-menu--collapse .el-menu-item .el-icon),
:deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin: 0 !important;
}

.el-header {
  background: rgba(240, 249, 255, 0.8) !important; /* Subtle blue tint */
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(186, 230, 253, 0.6); /* Enhanced separation border */
  line-height: 60px;
  z-index: 20;
  position: relative;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info span {
  margin-left: 10px;
}

/* 全局页面样式 - 统一el-card边距 */
.el-card {
  margin: 20px;
}

.search-form {
  margin-bottom: 20px;
}

/* 统一 el-card 内容区域的 padding */
.el-card__body {
  padding: 20px !important;
}

.logo-area span {
  font-size: 20px;
  font-weight: 800;
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}


.logo-icon {
  background: white;
  padding: 6px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.15);
}

.website-icon {
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s ease;
}

.website-icon:hover {
  color: #3b82f6;
  transform: scale(1.1);
}

.language-btn {
  font-size: 14px;
  color: #64748b;
  transition: all 0.3s ease;
}

.language-btn:hover {
  color: #3b82f6;
}

.lang-text {
  margin-left: 4px;
}

.theme-icon {
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s ease;
}

.theme-icon:hover {
  color: #3b82f6;
  transform: scale(1.1);
}

:deep(.el-dropdown-menu__item.is-active) {
  color: #3b82f6;
  font-weight: 600;
}


.el-main {
  background-color: transparent;
  padding: 24px;
}

.main-content {
  overflow-y: auto;
  height: calc(100vh - 60px);
}

.main-content.is-login {
  height: 100vh;
  overflow: hidden;
}

.main-content.is-landing {
  height: 100vh;
  overflow-y: auto;
}

/* Webkit scrollbar for premium feel */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.05);
  border-radius: 10px;
}
::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.1);
}

/* 个人中心弹窗样式 */
.profile-header {
  text-align: center;
  padding: 20px 0;
}

.profile-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 12px;
}

.profile-title h3 {
  margin: 8px 0;
  font-size: 20px;
  color: #303133;
}

.info-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 16px;
  font-weight: 600;
}

.el-collapse {
  border: none;
}

:deep(.el-collapse-item__header) {
  font-weight: 600;
  color: #606266;
  background-color: transparent;
  border: none;
}

:deep(.el-collapse-item__wrap) {
  border: none;
}

/* 头部小头像 */
.user-avatar-small {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-right: 8px;
  vertical-align: middle;
  font-weight: bold;
}

/* 头像上传器 */
.avatar-uploader {
  cursor: pointer;
  position: relative;
  display: inline-block;
}

.profile-avatar.clickable {
  cursor: pointer;
  transition: all 0.3s;
}

.profile-avatar.clickable:hover {
  opacity: 0.8;
  transform: scale(1.05);
}

.avatar-upload-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  margin-bottom: 16px;
  text-align: center;
  cursor: pointer;
}

.avatar-upload-hint:hover {
  color: #409EFF;
}

:deep(.el-collapse-item__content) {
  padding: 16px 0;
}

/* 暗色主题样式 */
:root.dark-theme {
  /* 背景和边框 */
  .layout-container {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  }
  
  .layout-container::before {
    background: rgba(15, 23, 42, 0.96);
  }
  
  .glass-sidebar {
    background: rgba(30, 41, 59, 0.7) !important;
    border-right: 1px solid rgba(71, 85, 105, 0.3);
  }
  
  .el-header {
    background: rgba(15, 23, 42, 0.9) !important;
    border-bottom: 1px solid rgba(71, 85, 105, 0.5);
  }
  
  /* 文字颜色 */
  .logo-area span {
    background: linear-gradient(135deg, #e2e8f0 0%, #60a5fa 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  
  :deep(.el-sub-menu__title),
  :deep(.el-menu-item) {
    color: #cbd5e1 !important;
  }
  
  :deep(.el-menu-item:hover),
  :deep(.el-sub-menu__title:hover) {
    background-color: rgba(59, 130, 246, 0.15) !important;
    color: #60a5fa !important;
  }
  
  :deep(.el-menu-item.is-active) {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
    color: white !important;
  }
  
  /* 子菜单背景 */
  :deep(.el-sub-menu__title) {
    background-color: transparent;
  }
  
  :deep(.el-menu--inline) {
    background-color: rgba(15, 23, 42, 0.5) !important;
  }
  
  :deep(.el-menu) {
    background-color: transparent !important;
  }
  
  /* 卡片和容器 */
  :deep(.el-card) {
    background: rgba(30, 41, 59, 0.6);
    border-color: rgba(71, 85, 105, 0.3);
    color: #e2e8f0;
  }
  
  :deep(.el-card__header) {
    border-bottom-color: rgba(71, 85, 105, 0.3);
    color: #f1f5f9;
  }
  
  /* 表格 */
  :deep(.el-table) {
    background-color: rgba(30, 41, 59, 0.6);
    color: #e2e8f0;
  }
  
  :deep(.el-table th.el-table__cell) {
    background-color: rgba(51, 65, 85, 0.8);
    color: #f1f5f9;
  }
  
  :deep(.el-table tr) {
    background-color: transparent;
  }
  
  :deep(.el-table td.el-table__cell) {
    border-bottom-color: rgba(71, 85, 105, 0.3);
  }
  
  :deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
    background-color: rgba(51, 65, 85, 0.3);
  }
  
  :deep(.el-table tbody tr:hover > td.el-table__cell) {
    background-color: rgba(59, 130, 246, 0.1) !important;
  }
  
  /* 表单组件 */
  :deep(.el-input__wrapper) {
    background-color: rgba(51, 65, 85, 0.5);
    box-shadow: 0 0 0 1px rgba(71, 85, 105, 0.3) inset;
  }
  
  :deep(.el-input__inner) {
    color: #e2e8f0;
  }
  
  :deep(.el-input__wrapper:hover) {
    box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.5) inset;
  }
  
  :deep(.el-select .el-input.is-focus .el-input__wrapper) {
    box-shadow: 0 0 0 1px #3b82f6 inset;
  }
  
  :deep(.el-textarea__inner) {
    background-color: rgba(51, 65, 85, 0.5);
    color: #e2e8f0;
    border-color: rgba(71, 85, 105, 0.3);
  }
  
  /* 下拉框 */
  :deep(.el-select-dropdown) {
    background-color: rgba(30, 41, 59, 0.95);
    border-color: rgba(71, 85, 105, 0.5);
  }
  
  :deep(.el-select-dropdown__item) {
    color: #e2e8f0;
  }
  
  :deep(.el-select-dropdown__item:hover) {
    background-color: rgba(59, 130, 246, 0.15);
  }
  
  :deep(.el-select-dropdown__item.selected) {
    color: #60a5fa;
  }
  
  /* 对话框 */
  :deep(.el-dialog) {
    background-color: rgba(30, 41, 59, 0.98);
    border: 1px solid rgba(71, 85, 105, 0.5);
  }
  
  :deep(.el-dialog__title) {
    color: #f1f5f9;
  }
  
  :deep(.el-dialog__body) {
    color: #e2e8f0;
  }
  
  /* 按钮 */
  :deep(.el-button--default) {
    background-color: rgba(51, 65, 85, 0.6);
    border-color: rgba(71, 85, 105, 0.5);
    color: #e2e8f0;
  }
  
  :deep(.el-button--default:hover) {
    background-color: rgba(71, 85, 105, 0.8);
    border-color: rgba(100, 116, 139, 0.7);
  }
  
  /* Tag */
  :deep(.el-tag) {
    border-color: rgba(71, 85, 105, 0.5);
  }
  
  /* Icon colors */
  .website-icon,
  .theme-icon,
  .language-btn,
  .collapse-icon {
    color: #94a3b8;
  }
  
  .website-icon:hover,
  .theme-icon:hover,
  .language-btn:hover,
  .collapse-icon:hover {
    color: #60a5fa;
  }
  
  /* Dropdown */
  :deep(.el-dropdown-menu) {
    background-color: rgba(30, 41, 59, 0.98);
    border-color: rgba(71, 85, 105, 0.5);
  }
  
  :deep(.el-dropdown-menu__item) {
    color: #e2e8f0;
  }
  
  :deep(.el-dropdown-menu__item:hover) {
    background-color: rgba(59, 130, 246, 0.15);
    color: #60a5fa;
  }
  
  .el-dropdown-link {
    color: #e2e8f0;
  }
}
</style>
