import api from './index'
import type { PageResult } from './customer'

export interface MedicalRecord {
    id?: number
    registrationId: number
    petId: number
    doctorId: number
    symptoms?: string
    diagnosis?: string
    treatmentPlan?: string
    doctorAdvice?: string
    visitTime?: string
    // 关联字段
    registration?: {
        queueNumber?: number
    }
    pet?: {
        name?: string
        species?: string
    }
    doctor?: {
        name?: string
    }
}

export const medicalRecordApi = {
    list: (params?: { petId?: number; petName?: string; doctorId?: number; page?: number; size?: number }) =>
        api.get<any, { code: number; data: any }>('/medical-records', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: MedicalRecord }>(`/medical-records/${id}`),

    create: (data: MedicalRecord) =>
        api.post<any, { code: number; data: MedicalRecord }>('/medical-records', data),

    update: (id: number, data: Partial<MedicalRecord>) =>
        api.put<any, { code: number; data: boolean }>(`/medical-records/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/medical-records/${id}`),

    listByPet: (petId: number) =>
        api.get<any, { code: number; data: MedicalRecord[] }>(`/medical-records/pet/${petId}`),

    listByRegistration: (registrationId: number) =>
        api.get<any, { code: number; data: MedicalRecord[] }>(`/medical-records/registration/${registrationId}`)
}
