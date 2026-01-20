import request, { baseURL } from './request'

export interface Pet {
    id: number
    customerId: number
    name: string
    species: string
    breed?: string
    gender: number
    birthDate?: string
    weight?: number
    photoUrl?: string
    notes?: string
    createdAt: string
    updatedAt: string
}

export interface PetForm {
    name: string
    species: string
    breed?: string
    gender: number
    birthDate?: string
    weight?: number
    photoUrl?: string
    notes?: string
}

export const petApi = {
    // 获取我的宠物列表
    getMyPets: () =>
        request.get<Pet[]>('/api/pets/my-pets'),

    // 获取宠物详情
    getPetDetail: (id: number) =>
        request.get<Pet>(`/api/pets/${id}`),

    // 创建宠物
    createPet: (data: PetForm) =>
        request.post<Pet>('/api/pets/create', data),

    // 更新宠物
    updatePet: (id: number, data: PetForm) =>
        request.put<Pet>(`/api/pets/${id}/update`, data),

    // 上传图片
    uploadImage: (filePath: string): Promise<string> => {
        const token = uni.getStorageSync('token')

        return new Promise((resolve, reject) => {
            uni.uploadFile({
                url: `${baseURL}/api/upload/image`,
                filePath,
                name: 'file',
                header: {
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                success: (res) => {
                    const data = JSON.parse(res.data)
                    if (data.code === 200) {
                        // 后端返回的是相对路径 /uploads/xxx.jpg
                        // 需要拼接完整URL
                        resolve(baseURL + data.data)
                    } else {
                        uni.showToast({
                            title: data.msg || '上传失败',
                            icon: 'none'
                        })
                        reject(data)
                    }
                },
                fail: (err) => {
                    uni.showToast({
                        title: '上传失败',
                        icon: 'none'
                    })
                    reject(err)
                }
            })
        })
    }
}
