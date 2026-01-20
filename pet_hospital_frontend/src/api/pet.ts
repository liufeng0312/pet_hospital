import api from './index'

export interface Pet {
    id?: number
    customerId: number
    name: string
    species: string
    breed?: string
    gender?: number
    birthDate?: string
    weight?: number
    photoUrl?: string
    notes?: string
    createdAt?: string
    customer?: {
        id: number
        name: string
        phone: string
    }
}

export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
}

export const petApi = {
    list: (params: { keyword?: string; customerId?: number; species?: string; page?: number; size?: number }) =>
        api.get<any, { code: number; data: PageResult<Pet> }>('/pets', { params }),

    getById: (id: number) =>
        api.get<any, { code: number; data: Pet }>(`/pets/${id}`),

    getByCustomerId: (customerId: number) =>
        api.get<any, { code: number; data: Pet[] }>(`/pets/customer/${customerId}`),

    create: (data: Pet) =>
        api.post<any, { code: number; data: boolean }>('/pets', data),

    update: (id: number, data: Pet) =>
        api.put<any, { code: number; data: boolean }>(`/pets/${id}`, data),

    delete: (id: number) =>
        api.delete<any, { code: number; data: boolean }>(`/pets/${id}`)
}

export const uploadApi = {
    uploadImage: (file: File) => {
        const formData = new FormData()
        formData.append('file', file)
        return api.post<any, { code: number; data: string }>('/upload/image', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    }
}
