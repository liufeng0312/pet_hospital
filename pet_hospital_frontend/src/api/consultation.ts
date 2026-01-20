import api from './index'

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
    pet?: {
        name: string
        species: string
    }
    doctor?: {
        name: string
    }
}

export const consultationApi = {
    list: (params?: { status?: string; page?: number; size?: number }) =>
        api.get<any, { code: number; data: any }>('/consultations/list', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: Consultation }>(`/consultations/${id}`),

    reply: (id: number, data: { replyContent: string }) =>
        api.put<any, { code: number; data: boolean }>(`/consultations/${id}/reply`, data)
}
