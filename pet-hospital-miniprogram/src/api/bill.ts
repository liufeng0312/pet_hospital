import request from './request'

export interface Bill {
    id: number
    customerId: number
    registrationId: number
    totalAmount: number
    paidAmount: number
    paymentMethod: string
    paymentStatus: string
    createdAt: string
}

export const billApi = {
    // 获取我的账单列表
    getMyBills: (params: { page?: number; size?: number }) =>
        request.get<{ records: Bill[]; total: number; pages: number }>('/api/bills/my-bills', params),

    // 获取账单详情
    getDetail: (id: number) =>
        request.get<Bill>(`/api/bills/${id}`)
}
