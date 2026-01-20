import api from './index'

export interface DashboardVO {
    kpis: {
        todayRegistrations: number
        inProgressPets: number
        pendingBills: number
        yesterdayRevenue: number
    }
    trendChart: {
        dates: string[]
        total: number[]
        appointment: number[]
    }
    activities: {
        text: string
        time: string
        color: string
    }[]
    todos: {
        text: string
        tag: string
        tagColor: string
        done: boolean
    }[]
    healthTip?: {
        id: number
        title: string
        summary: string
        coverImage: string
    }
}

export const statisticsApi = {
    getDashboard: () => {
        return api.get<any, { code: number; data: DashboardVO }>('/stats/dashboard')
    }
}
