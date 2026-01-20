import request from './index'

export interface SurgeryRecord {
    id?: number
    serviceItemId: number
    medicalRecordId: number
    petId: number
    surgeonId: number
    assistantIds?: string

    surgeryType: string
    surgeryName: string
    surgeryDate: string
    duration?: number

    preDiagnosis?: string
    surgeryProcess?: string
    postDiagnosis?: string
    anesthesiaType?: string
    complications?: string
    notes?: string

    status: number
    amount: number

    createdAt?: string
    updatedAt?: string

    // 关联对象
    serviceItem?: {
        name: string
        price: number
    }
    pet?: {
        name: string
        species: string
        customer?: {
            name: string
        }
    }
    surgeon?: {
        name: string
    }
    medicalRecord?: any
}

export interface SurgeryListParams {
    page: number
    size: number
    status?: number
    petName?: string
    startDate?: string
    endDate?: string
}

export const surgeryApi = {
    // 创建手术记录
    create(data: SurgeryRecord) {
        return request.post<SurgeryRecord>('/surgeries', data)
    },

    // 分页查询
    list(params: SurgeryListParams) {
        return request.get<{ records: SurgeryRecord[], total: number }>('/surgeries', { params })
    },

    // 查询详情
    getById(id: number) {
        return request.get<SurgeryRecord>(`/surgeries/${id}`)
    },

    // 查询宠物手术历史
    listByPetId(petId: number) {
        return request.get<SurgeryRecord[]>(`/surgeries/pet/${petId}`)
    },

    // 更新手术记录
    update(id: number, data: Partial<SurgeryRecord>) {
        return request.put(`/surgeries/${id}`, data)
    },

    // 更新状态
    updateStatus(id: number, status: number) {
        return request.put(`/surgeries/${id}/status/${status}`)
    },

    syncPayment() {
        return request.post('/surgeries/sync-payment')
    },

    // 删除
    delete(id: number) {
        return request.delete(`/surgeries/${id}`)
    }
}
