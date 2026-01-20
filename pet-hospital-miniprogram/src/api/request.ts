// API 基础配置
const baseURL = 'http://localhost:8080'

interface RequestConfig {
    url: string
    method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
    data?: any
    params?: any
    header?: any
}

interface Response<T = any> {
    code: number
    msg: string
    data: T
}

// 请求拦截器
function request<T = any>(config: RequestConfig): Promise<Response<T>> {
    const token = uni.getStorageSync('token')

    return new Promise((resolve, reject) => {
        uni.request({
            url: `${baseURL}${config.url}`,
            method: config.method || 'GET',
            data: config.data,
            header: {
                'Content-Type': 'application/json',
                'Authorization': token ? `Bearer ${token}` : '',
                ...config.header
            },
            success: (res: any) => {
                const data = res.data as Response<T>

                if (data.code === 200) {
                    resolve(data)
                } else if (data.code === 401) {
                    // Token过期，跳转登录
                    uni.removeStorageSync('token')
                    uni.removeStorageSync('user')
                    uni.reLaunch({
                        url: '/pages/login/index'
                    })
                    reject(data)
                } else {
                    uni.showToast({
                        title: data.msg || '请求失败',
                        icon: 'none'
                    })
                    reject(data)
                }
            },
            fail: (err) => {
                uni.showToast({
                    title: '网络错误',
                    icon: 'none'
                })
                reject(err)
            }
        })
    })
}

export default {
    get: <T = any>(url: string, params?: any) =>
        request<T>({ url, method: 'GET', data: params }),

    post: <T = any>(url: string, data?: any) =>
        request<T>({ url, method: 'POST', data }),

    put: <T = any>(url: string, data?: any) =>
        request<T>({ url, method: 'PUT', data }),

    delete: <T = any>(url: string, params?: any) =>
        request<T>({ url, method: 'DELETE', data: params })
}

export { baseURL }
