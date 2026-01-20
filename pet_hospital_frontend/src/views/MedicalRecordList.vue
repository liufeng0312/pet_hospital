<template>
    <div class="medical-record-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>病历档案管理</span>
                </div>
            </template>

            <!-- 筛选 -->
            <el-form :inline="true" style="margin-bottom: 20px">
                <el-form-item label="宠物名称">
                    <el-input v-model="searchPetName" placeholder="请输入宠物名称" clearable style="width: 200px" />
                </el-form-item>
                <el-form-item label="医生">
                    <el-select v-model="searchDoctorId" placeholder="全部医生" clearable style="width: 150px">
                        <el-option
                            v-for="doctor in doctors"
                            :key="doctor.id"
                            :label="doctor.name"
                            :value="doctor.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="fetchData"><el-icon><Search /></el-icon> 搜索</el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 数据表格 -->
            <el-table :data="tableData" v-loading="loading" style="width: 100%">
                <el-table-column prop="id" label="ID" min-width="80" />
                <el-table-column prop="pet.name" label="宠物" min-width="120">
                    <template #default="{ row }">
                        {{ row.pet?.name }} ({{ row.pet?.species }})
                    </template>
                </el-table-column>
                <el-table-column prop="customer.name" label="主人" min-width="120" />
                <el-table-column prop="doctor.name" label="医生" min-width="120" />
                <el-table-column prop="diagnosis" label="诊断" min-width="200" show-overflow-tooltip />
                <el-table-column prop="treatmentPlan" label="治疗方案" min-width="200" show-overflow-tooltip />
                <el-table-column prop="visitTime" label="就诊时间" min-width="180" />
                <el-table-column label="操作" min-width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" @click="handleView(row)">查看详情</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 50, 100]"
                :total="total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="fetchData"
                @current-change="fetchData"
                style="margin-top: 20px"
            />
        </el-card>

        <!-- 详情对话框 -->
        <el-dialog
            v-model="dialogVisible"
            title="病历详情"
            width="800px"
        >
            <div v-if="currentRecord">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="病历ID">{{ currentRecord.id }}</el-descriptions-item>
                    <el-descriptions-item label="就诊时间">{{ currentRecord.visitTime }}</el-descriptions-item>
                    <el-descriptions-item label="宠物">
                        {{ currentRecord.pet?.name }} ({{ currentRecord.pet?.species }})
                    </el-descriptions-item>
                    <el-descriptions-item label="主人">{{ currentRecord.customer?.name }}</el-descriptions-item>
                    <el-descriptions-item label="医生">{{ currentRecord.doctor?.name }}</el-descriptions-item>
                    <el-descriptions-item label="体重">{{ currentRecord.pet?.weight }}kg</el-descriptions-item>
                    <el-descriptions-item label="主诉" :span="2">
                        <div style="white-space: pre-wrap">{{ currentRecord.symptoms }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="诊断" :span="2">
                        <div style="white-space: pre-wrap">{{ currentRecord.diagnosis }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="治疗方案" :span="2">
                        <div style="white-space: pre-wrap">{{ currentRecord.treatmentPlan }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentRecord.notes" label="备注" :span="2">
                        <div style="white-space: pre-wrap">{{ currentRecord.notes }}</div>
                    </el-descriptions-item>
                </el-descriptions>
            </div>
            <template #footer>
                <el-button @click="dialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { medicalRecordApi, type MedicalRecord } from '@/api/medicalRecord'
import { employeeApi, type Employee } from '@/api/employee'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<MedicalRecord[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchPetName = ref('')
const searchDoctorId = ref<number>()
const doctors = ref<Employee[]>([])

const dialogVisible = ref(false)
const currentRecord = ref<MedicalRecord | null>(null)

const fetchDoctors = async () => {
    try {
        const res = await employeeApi.getDoctors()
        doctors.value = res.data
    } catch (error) {
        console.error('加载医生列表失败', error)
    }
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await medicalRecordApi.list({
            petName: searchPetName.value || undefined,
            doctorId: searchDoctorId.value,
            page: currentPage.value,
            size: pageSize.value
        })
        if (res.data.records) {
            tableData.value = res.data.records
            total.value = res.data.total
        } else {
            tableData.value = res.data
            total.value = res.data.length
        }
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const resetSearch = () => {
    searchPetName.value = ''
    searchDoctorId.value = undefined
    currentPage.value = 1
    fetchData()
}

const handleView = (row: MedicalRecord) => {
    currentRecord.value = row
    dialogVisible.value = true
}

onMounted(() => {
    fetchDoctors()
    fetchData()
})
</script>

<style scoped>
.medical-record-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
