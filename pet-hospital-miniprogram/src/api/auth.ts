import request from './request'

export interface WechatLoginDTO {
    code: string
}

export interface BindPhoneDTO {
    phone: string
    openid: string
}

export interface LoginResponse {
    token: string | null
    user: {
        id: number
        name: string
        phone: string
        level: number
    } | null
    openid?: string
}

export const authApi = {
    // 微信登录
    wechatLogin: (code: string) =>
        request.post<LoginResponse>('/api/auth/wechat-login', { code }),

    // 绑定手机号
    bindPhone: (phone: string, openid: string) =>
        request.post<LoginResponse>('/api/auth/bind-phone', { phone, openid })
}
