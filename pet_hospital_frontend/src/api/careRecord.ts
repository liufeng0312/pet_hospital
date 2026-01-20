import api from './index'

export interface CareRecord {
    id?: number
    hospitalizationId: number
    content: string
    operatorId: number
    recordTime?: string
    operator?: {
        name?: string
    }
    hospitalization?: {
        bedNumber?: string
        pet?: {
            name?: string
        }
    }
}

export const careRecordApi = {
    list: (hospitalizationId: number) =>
        api.get<any, { code: number; data: CareRecord[] }>('/care-records', {
            params: { hospitalizationId }
        }),

    listAll: (params: { page: number; size: number }) =>
        api.get<any, { code: number; data: { records: CareRecord[]; total: number } }>('/care-records', {
            params
        }),

    add: (data: CareRecord) =>
        api.post<any, { code: number; data: boolean }>('/care-records', data)
}
