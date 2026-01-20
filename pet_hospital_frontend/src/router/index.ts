import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            name: 'landing',
            component: () => import('../views/LandingPage.vue'),
            meta: { isPublic: true }
        },
        {
            path: '/dashboard',
            name: 'dashboard',
            component: () => import('../views/Home.vue')
        },
        {
            path: '/customers',
            name: 'customers',
            component: () => import('../views/CustomerList.vue')
        },
        {
            path: '/pets',
            name: 'pets',
            component: () => import('../views/PetList.vue')
        },
        {
            path: '/registrations',
            name: 'registrations',
            component: () => import('../views/RegistrationList.vue')
        },
        {
            path: '/queue',
            name: 'queue',
            component: () => import('../views/QueueBoard.vue')
        },
        {
            path: '/doctor-workbench',
            name: 'doctor-workbench',
            component: () => import('../views/DoctorWorkbench.vue')
        },
        {
            path: '/prescriptions',
            name: 'prescriptions',
            component: () => import('../views/PrescriptionList.vue')
        },
        {
            path: '/drugs',
            name: 'drugs',
            component: () => import('../views/DrugList.vue')
        },
        {
            path: '/services',
            name: 'services',
            component: () => import('../views/ServiceItemList.vue')
        },
        {
            path: '/lab-workbench',
            name: 'labWorkbench',
            component: () => import('../views/LabWorkbench.vue')
        },
        {
            path: '/inpatients',
            name: 'inpatients',
            component: () => import('../views/InpatientList.vue')
        },
        {
            path: '/payment',
            name: 'payment',
            component: () => import('../views/PaymentCenter.vue')
        },
        {
            path: '/reports',
            name: 'reports',
            component: () => import('../views/ReportCenter.vue')
        },
        {
            path: '/reminders',
            name: 'reminders',
            component: () => import('../views/ReminderList.vue')
        },
        {
            path: '/employees',
            name: 'employees',
            component: () => import('../views/EmployeeList.vue')
        },
        {
            path: '/consultations',
            name: 'consultations',
            component: () => import('../views/ConsultationList.vue')
        },
        {
            path: '/medical-records',
            name: 'medical-records',
            component: () => import('../views/MedicalRecordList.vue')
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue'),
            meta: { isPublic: true }
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/'
        },
        {
            path: '/articles',
            name: 'ArticleList',
            component: () => import('../views/ArticleList.vue'),
            meta: { title: '健康资讯' }
        },
        {
            path: '/vaccine-records',
            name: 'VaccineRecords',
            component: () => import('../views/VaccineRecordList.vue'),
            meta: { title: '疫苗记录' }
        },
        {
            path: '/vaccine-inventory',
            name: 'VaccineInventory',
            component: () => import('../views/VaccineInventoryList.vue'),
            meta: { title: '疫苗库存' }
        },
        {
            path: '/lab-tests',
            name: 'LabTestList',
            component: () => import('../views/LabTestList.vue'),
            meta: { title: '检验记录' }
        },
        {
            path: '/surgeries',
            name: 'SurgeryList',
            component: () => import('../views/SurgeryList.vue'),
            meta: { title: '手术记录' }
        },
        {
            path: '/doctor-schedule',
            name: 'DoctorSchedule',
            component: () => import('../views/DoctorSchedule.vue'),
            meta: { title: '医生排班' }
        }
    ]
})

router.beforeEach((to, _from, next) => {
    const token = localStorage.getItem('token')

    if (to.path === '/login' && token) {
        // Logged in user trying to access login page - redirect to dashboard
        next('/dashboard')
    } else if (!to.meta.isPublic && !token) {
        // Unauthenticated user trying to access protected page
        next('/login')
    } else {
        next()
    }
})

export default router
