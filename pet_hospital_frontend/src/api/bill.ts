import api from './index'

export interface Bill {
    id?: number
    customerId: number
    relatedType: string // PRESCRIPTION
    relatedId: number
    totalAmount: number
    discountAmount: number
    finalAmount: number
    paymentMethod?: string
    status: number // 0:UNPAID, 1:PAID
    createdAt?: string
    paidAt?: string
    customer?: {
        name: string
        level?: number
    }
}

export const billApi = {
    listUnpaid: () =>
        api.get<any, { code: number; data: Bill[] }>('/bills/unpaid'),

    listPaid: () =>
        api.get<any, { code: number; data: Bill[] }>('/bills/paid'),

    createFromPrescription: (id: number) =>
        api.post<any, { code: number; data: null }>(`/bills/create/prescription/${id}`),

    createFromLabTest: (id: number) =>
        api.post<any, { code: number; data: null }>(`/bills/create/lab-test/${id}`),

    createFromSurgery: (id: number) =>
        api.post<any, { code: number; data: null }>(`/bills/create/surgery/${id}`),

    pay: (id: number, paymentMethod: string) =>
        api.post<any, { code: number; data: null }>(`/bills/pay/${id}`, null, {
            params: { paymentMethod }
        })
}
