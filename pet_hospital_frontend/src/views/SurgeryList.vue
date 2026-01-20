<template>
    <div class="surgery-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>手术记录管理</span>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchParams" class="search-form">
                <el-form-item label="日期范围">
                    <el-date-picker
                        v-model="dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 260px"
                        @change="handleDateChange"
                    />
                </el-form-item>
                
                <el-form-item label="宠物名称">
                    <el-input
                        v-model="searchParams.petName"
                        placeholder="请输入宠物名称"
                        style="width: 200px"
                        clearable
                        @clear="fetchData"
                        @keyup.enter="fetchData"
                    />
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select v-model="searchParams.status" placeholder="全部" clearable style="width: 120px">
                        <el-option label="待支付" :value="0" />
                        <el-option label="待手术" :value="1" />
                        <el-option label="手术中" :value="2" />
                        <el-option label="已完成" :value="3" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="fetchData">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                    <el-button type="success" plain @click="handleSyncPayment">同步支付状态</el-button>
                </el-form-item>
            </el-form>

            <!-- 数据表格 -->
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="记录号" min-width="80" />
                <el-table-column prop="surgeryName" label="手术名称" min-width="150" />
                <el-table-column label="宠物" min-width="120">
                    <template #default="{ row }">
                        {{ row.pet?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="主人" min-width="120">
                    <template #default="{ row }">
                        {{ row.pet?.customer?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="主刀医生" min-width="100">
                    <template #default="{ row }">
                        {{ row.surgeon?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="surgeryDate" label="手术日期" min-width="180" />
                <el-table-column prop="amount" label="费用" min-width="100">
                    <template #default="{ row }">
                        <span class="amount">¥{{ row.amount?.toFixed(2) || '0.00' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusLabel(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="viewDetail(row.id)">
                            查看详情
                        </el-button>
                        <el-button
                            v-if="row.status === 1"
                            type="warning"
                            size="small"
                            @click="updateStatus(row.id, 2)"
                        >开始手术</el-button>
                        <el-button
                            v-if="row.status === 2"
                            type="success"
                            size="small"
                            @click="updateStatus(row.id, 3)"
                        >完成手术</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-wrapper">
                <el-pagination
                    v-model:current-page="searchParams.page"
                    v-model:page-size="searchParams.size"
                    :total="total"
                    :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="fetchData"
                    @current-change="fetchData"
                />
            </div>
        </el-card>

        <!-- 详情弹窗 -->
        <el-dialog v-model="detailDialogVisible" title="手术记录详情" width="800px">
            <div v-if="currentDetail" class="detail-content">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="记录号">{{ currentDetail.id }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="getStatusType(currentDetail.status)">
                            {{ getStatusLabel(currentDetail.status) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="手术名称">{{ currentDetail.surgeryName }}</el-descriptions-item>
                    <el-descriptions-item label="手术类型">{{ currentDetail.surgeryType }}</el-descriptions-item>
                    <el-descriptions-item label="宠物">{{ currentDetail.pet?.name }}</el-descriptions-item>
                    <el-descriptions-item label="主人">{{ currentDetail.pet?.customer?.name }}</el-descriptions-item>
                    <el-descriptions-item label="主刀医生">{{ currentDetail.surgeon?.name }}</el-descriptions-item>
                    <el-descriptions-item label="手术日期">{{ currentDetail.surgeryDate }}</el-descriptions-item>
                    <el-descriptions-item label="手术时长">{{ currentDetail.duration || '-' }} 分钟</el-descriptions-item>
                    <el-descriptions-item label="麻醉方式">{{ currentDetail.anesthesiaType || '-' }}</el-descriptions-item>
                    <el-descriptions-item label="费用">
                        <span class="amount">¥{{ currentDetail.amount?.toFixed(2) }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="术前诊断" :span="2">
                        <div style="white-space: pre-wrap">{{ currentDetail.preDiagnosis || '-' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="手术过程" :span="2">
                        <div style="white-space: pre-wrap">{{ currentDetail.surgeryProcess || '-' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="术后诊断" :span="2">
                        <div style="white-space: pre-wrap">{{ currentDetail.postDiagnosis || '-' }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentDetail.complications" label="并发症" :span="2">
                        <div style="white-space: pre-wrap">{{ currentDetail.complications }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentDetail.notes" label="备注" :span="2">
                        <div style="white-space: pre-wrap">{{ currentDetail.notes }}</div>
                    </el-descriptions-item>
                </el-descriptions>
            </div>
            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { surgeryApi, type SurgeryRecord } from '@/api/surgery'

const loading = ref(false)
const tableData = ref<SurgeryRecord[]>([])
const total = ref(0)

const searchParams = reactive({
    status: undefined as number | undefined,
    petName: '',
    startDate: '',
    endDate: '',
    page: 1,
    size: 10
})

const dateRange = ref([])

const detailDialogVisible = ref(false)
const currentDetail = ref<SurgeryRecord>()

const handleDateChange = (val: any) => {
    if (val) {
        searchParams.startDate = val[0]
        searchParams.endDate = val[1]
    } else {
        searchParams.startDate = ''
        searchParams.endDate = ''
    }
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await surgeryApi.list({
            status: searchParams.status,
            page: searchParams.page,
            size: searchParams.size,
            petName: searchParams.petName,
            startDate: searchParams.startDate,
            endDate: searchParams.endDate
        })
        tableData.value = res.data.records
        total.value = res.data.total
    } catch (error) {
        console.error('获取手术记录列表失败', error)
    } finally {
        loading.value = false
    }
}

const resetSearch = () => {
    searchParams.status = undefined
    searchParams.petName = ''
    searchParams.startDate = ''
    searchParams.endDate = ''
    searchParams.page = 1
    dateRange.value = []
    fetchData()
}

const handleSyncPayment = async () => {
    try {
        await surgeryApi.syncPayment()
        ElMessage.success('支付状态已同步')
        fetchData()
    } catch (error: any) {
        ElMessage.error('同步失败')
    }
}

const viewDetail = async (id: number) => {
    try {
        const res = await surgeryApi.getById(id)
        currentDetail.value = res.data
        detailDialogVisible.value = true
    } catch (error) {
        console.error('获取手术详情失败', error)
    }
}

const updateStatus = async (id: number, status: number) => {
    try {
        // 确认提示
        const confirmMessages: Record<number, string> = {
            2: '确认开始手术吗？',
            3: '确认手术已完成吗？'
        }
        
        const message = confirmMessages[status]
        if (message) {
            await ElMessageBox.confirm(message, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            })
        }
        
        await surgeryApi.updateStatus(id, status)
        ElMessage.success('状态更新成功')
        fetchData()
    } catch (error: any) {
        if (error !== 'cancel') {
            ElMessage.error(error.response?.data?.msg || '操作失败')
        }
    }
}

const getStatusType = (status?: number) => {
    const map: Record<number, string> = {
        0: 'warning',
        1: 'info',
        2: 'primary',
        3: 'success'
    }
    return map[status || 0] || 'info'
}

const getStatusLabel = (status?: number) => {
    const map: Record<number, string> = {
        0: '待支付',
        1: '待手术',
        2: '手术中',
        3: '已完成'
    }
    return map[status || 0] || '未知'
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.surgery-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-form {
    margin-bottom: 20px;
}

.amount {
    color: #f56c6c;
    font-weight: bold;
    font-size: 16px;
}

.pagination-wrapper {
    margin-top: 20px;
    display: flex;
    justify-content: flex-start;
}

.detail-content {
    padding: 10px 0;
}
</style>
