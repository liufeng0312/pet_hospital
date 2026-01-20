import api from './index'
import type { PageResult } from './customer'

export interface LabTest {
    id?: number
    medicalRecordId: number
    serviceId: number
    result?: string
    reportUrl?: string
    testTime?: string
    status?: number // 0: Pending, 1: Completed
    medicalRecord?: {
        pet?: {
            name?: string
            customer?: {
                name?: string
            }
        }
    }
    serviceItem?: {
        name?: string
    }
}

export const labTestApi = {
    list: (params: { status?: number; searchText?: string; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<LabTest> }>('/lab-tests', { params }),

    create: (data: LabTest) =>
        api.post<any, { code: number; data: LabTest }>('/lab-tests', data),

    complete: (id: number, data: { result: string; reportUrl?: string }) =>
        api.put<any, { code: number; data: LabTest }>(`/lab-tests/${id}/result`, data),

    uploadImage: (file: File) => {
        const formData = new FormData()
        formData.append('file', file)
        return api.post<any, { code: number; data: string }>('/upload/image', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
    }
}
