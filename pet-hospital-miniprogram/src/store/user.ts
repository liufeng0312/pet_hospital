import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

interface User {
    id: number
    name: string
    phone: string
    level: number
    avatar?: string
}

export const useUserStore = defineStore('user', () => {
    const token = ref<string>('')
    const userInfo = ref<User | null>(null)

    const isLoggedIn = computed(() => !!token.value)

    // 初始化从本地存储加载
    function init() {
        token.value = uni.getStorageSync('token') || ''
        const user = uni.getStorageSync('user')
        if (user) {
            userInfo.value = JSON.parse(user)
        }
    }

    // 登录
    function login(tokenValue: string, user: User) {
        token.value = tokenValue
        userInfo.value = user
        uni.setStorageSync('token', tokenValue)
        uni.setStorageSync('user', JSON.stringify(user))
    }

    // 登出
    function logout() {
        token.value = ''
        userInfo.value = null
        uni.removeStorageSync('token')
        uni.removeStorageSync('user')
    }

    return {
        token,
        userInfo,
        isLoggedIn,
        init,
        login,
        logout
    }
})
