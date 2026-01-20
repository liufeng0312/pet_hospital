import request from './request'

export interface Consultation {
    id?: number
    customerId?: number
    petId?: number
    title: string
    content: string
    images?: string
    status?: string
    replyContent?: string
    replyDoctorId?: number
    replyTime?: string
    createdAt?: string
    updatedAt?: string
    pet?: any
    doctor?: any
}

export const consultationApi = {
    // 创建咨询
    create: (data: Consultation) => {
        return request.post('/api/consultations/create', data)
    },

    // 获取我的咨询列表
    getMyConsultations: (params?: { status?: string; page?: number; size?: number }) => {
        return request.get('/api/consultations/my-consultations', params)
    },

    // 获取咨询详情
    getDetail: (id: number) => {
        return request.get(`/api/consultations/${id}`)
    }
}
