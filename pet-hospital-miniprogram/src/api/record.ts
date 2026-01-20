import request from './request'

export interface MedicalRecord {
    id: number
    petId: number
    pet?: {
        name: string
        species?: string
    }
    doctorId: number
    doctor?: {
        name: string
    }
    visitTime: string
    symptoms: string
    diagnosis: string
    treatment: string
    notes: string
}

export const recordApi = {
    // 获取我的病历列表
    getMyRecords: (params: { petId?: number; page?: number; size?: number }) =>
        request.get<{ records: MedicalRecord[]; total: number; pages: number }>('/api/medical-records/my-records', params),

    // 获取病历详情
    getDetail: (id: number) =>
        request.get<MedicalRecord>(`/api/medical-records/${id}`)
}
