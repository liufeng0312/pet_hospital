<template>
    <div class="prescription-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>处方管理</span>
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
                
                <el-form-item label="关键词">
                    <el-input
                        v-model="searchParams.searchText"
                        placeholder="宠物/客户/医生姓名"
                        style="width: 200px"
                        clearable
                        @clear="fetchData"
                        @keyup.enter="fetchData"
                    />
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select v-model="searchParams.status" placeholder="全部" clearable style="width: 120px">
                        <el-option label="未支付" :value="0" />
                        <el-option label="已支付" :value="1" />
                        <el-option label="已发药" :value="2" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="fetchData">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch">重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 数据表格 -->
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
                <el-table-column prop="id" label="处方号" min-width="100" />
                <el-table-column prop="medicalRecord.pet.name" label="宠物" min-width="120">
                    <template #default="{ row }">
                        {{ row.medicalRecord?.pet?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="主人" min-width="120">
                    <template #default="{ row }">
                        {{ row.medicalRecord?.pet?.customer?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="medicalRecord.doctor.name" label="医生" min-width="100">
                    <template #default="{ row }">
                        {{ row.medicalRecord?.doctor?.name || '-' }}
                    </template>
                </el-table-column>
                <el-table-column prop="totalAmount" label="金额" min-width="120">
                    <template #default="{ row }">
                        <span class="amount">¥{{ row.totalAmount?.toFixed(2) || '0.00' }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusLabel(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="创建时间" min-width="180" />
                <el-table-column label="操作" min-width="250" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" size="small" @click="viewDetail(row.id)">
                            查看详情
                        </el-button>
                        <el-button
                            v-if="row.status === 1"
                            type="warning"
                            size="small"
                            @click="handleDispense(row.id)"
                        >标记已发药</el-button>
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

        <!-- 处方详情弹窗 -->
        <el-dialog v-model="detailDialogVisible" title="处方详情" width="700px">
            <div v-if="currentDetail" class="detail-content">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="处方号">{{ currentDetail.id }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag :type="getStatusType(currentDetail.status)">
                            {{ getStatusLabel(currentDetail.status) }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="总金额">
                        <span class="amount">¥{{ currentDetail.totalAmount?.toFixed(2) }}</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">
                        {{ currentDetail.createdAt }}
                    </el-descriptions-item>
                </el-descriptions>

                <el-divider>处方明细</el-divider>
                
                <el-table :data="currentDetail.items" border style="width: 100%">
                    <el-table-column prop="drug.name" label="药品名称" />
                    <el-table-column prop="drug.specification" label="规格" min-width="120" />
                    <el-table-column prop="quantity" label="数量" min-width="80">
                        <template #default="{ row }">
                            {{ row.quantity }} {{ row.drug?.unit || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="price" label="单价" min-width="100">
                        <template #default="{ row }">
                            ¥{{ row.price?.toFixed(2) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="小计" min-width="100">
                        <template #default="{ row }">
                            ¥{{ (row.price * row.quantity).toFixed(2) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="dosage" label="用法用量" />
                </el-table>
            </div>
            <template #footer>
                <el-button @click="detailDialogVisible = false">关闭</el-button>
                <el-button type="primary" @click="handlePrint">打印处方</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { prescriptionApi, type Prescription } from '@/api/prescription'

const loading = ref(false)
const tableData = ref<Prescription[]>([])
const total = ref(0)

const searchParams = reactive({
    status: undefined as number | undefined,
    searchText: '',
    startDate: '',
    endDate: '',
    page: 1,
    size: 10
})

const dateRange = ref([])

const detailDialogVisible = ref(false)
const currentDetail = ref<Prescription>()

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
        const res = await prescriptionApi.list({
            status: searchParams.status,
            page: searchParams.page,
            size: searchParams.size,
            searchText: searchParams.searchText,
            startDate: searchParams.startDate,
            endDate: searchParams.endDate
        })
        tableData.value = res.data.records
        total.value = res.data.total
    } catch (error) {
        console.error('获取处方列表失败', error)
    } finally {
        loading.value = false
    }
}

const resetSearch = () => {
    searchParams.status = undefined
    searchParams.searchText = ''
    searchParams.startDate = ''
    searchParams.endDate = ''
    searchParams.page = 1
    dateRange.value = []
    fetchData()
}

const viewDetail = async (id: number) => {
    try {
        const res = await prescriptionApi.getById(id)
        currentDetail.value = res.data
        detailDialogVisible.value = true
    } catch (error) {
        console.error('获取处方详情失败', error)
    }
}



const handleDispense = async (id: number) => {
    try {
        await prescriptionApi.updateStatus(id, 2)
        ElMessage.success('已标记为已发药')
        fetchData()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '操作失败')
    }
}

const handlePrint = () => {
    window.print()
}

const getStatusType = (status?: number) => {
    const map: Record<number, string> = {
        0: 'warning',
        1: 'success',
        2: 'info'
    }
    return map[status || 0] || 'info'
}

const getStatusLabel = (status?: number) => {
    const map: Record<number, string> = {
        0: '未支付',
        1: '已支付',
        2: '已发药'
    }
    return map[status || 0] || '未知'
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.prescription-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.search-bar {
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

@media print {
    /* 隐藏所有非打印内容 */
    body * {
        visibility: hidden;
    }
    
    /* 只显示弹窗内容 */
    .el-dialog,
    .el-dialog *,
    .detail-content,
    .detail-content * {
        visibility: visible;
    }
    
    /* 隐藏弹窗装饰元素 */
    .el-dialog__header,
    .el-dialog__footer,
    .el-dialog__close {
        display: none !important;
    }
    
    /* 让弹窗占据整个打印页面 */
    .el-dialog {
        position: absolute;
        top: 0;
        left: 0;
        width: 100% !important;
        margin: 0 !important;
        max-width: none !important;
    }
    
    .el-dialog__body {
        padding: 40px;
    }
    
    /* 确保表格正常显示 */
    .el-table,
    .el-table * {
        visibility: visible;
    }
    
    /* 添加打印页眉 */
    .detail-content::before {
        content: "处方单";
        display: block;
        text-align: center;
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 20px;
    }
}
</style>
