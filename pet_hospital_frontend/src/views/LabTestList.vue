<template>
    <div class="lab-test-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>检验记录</span>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="queryParams" class="search-form">
                <el-form-item label="关键词">
                    <el-input 
                        v-model="queryParams.searchText" 
                        placeholder="宠物/客户/项目" 
                        style="width: 200px"
                        clearable 
                        @clear="handleSearch"
                        @keyup.enter="handleSearch"
                    />
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
                        <el-option label="待检验" :value="0" />
                        <el-option label="已完成" :value="1" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 列表展示 -->
            <el-table :data="labTests" v-loading="loading" stripe style="width: 100%">
            <el-table-column prop="id" label="单号" width="80" />
            <el-table-column prop="medicalRecord.pet.name" label="宠物" width="120" />
            <el-table-column prop="medicalRecord.pet.customer.name" label="主人" width="120" />
            <el-table-column prop="serviceItem.name" label="检验项目" min-width="150" />
            <el-table-column prop="serviceItem.price" label="费用" width="100">
                <template #default="{ row }">
                    <span class="amount">¥{{ row.serviceItem?.price?.toFixed(2) || '0.00' }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="testTime" label="检验时间" width="180">
                <template #default="{ row }">
                    {{ formatDate(row.testTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.result ? 'success' : 'warning'">
                        {{ row.result ? '已完成' : '待检验' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
                <template #default="{ row }">
                    <el-button 
                        link 
                        type="primary" 
                        size="small" 
                        @click="viewDetail(row)"
                        v-if="row.result"
                    >
                        查看结果
                    </el-button>
                    <span v-else class="text-gray">-</span>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
            <el-pagination
                v-model:current-page="queryParams.page"
                v-model:page-size="queryParams.size"
                :total="total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
        </div>

        <!-- 详情弹窗 -->
        <el-dialog v-model="detailVisible" title="检验结果详情" width="600px">
            <template v-if="currentTest">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="检验项目">{{ currentTest.serviceItem?.name }}</el-descriptions-item>
                    <el-descriptions-item label="宠物信息">{{ currentTest.medicalRecord?.pet?.name }} ({{ currentTest.medicalRecord?.pet?.customer?.name }})</el-descriptions-item>
                    <el-descriptions-item label="检验结果">
                        <div class="result-text">{{ currentTest.result }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item label="检验报告" v-if="currentTest.reportUrl">
                        <el-image 
                            style="width: 100%; max-height: 400px"
                            :src="currentTest.reportUrl" 
                            :preview-src-list="[currentTest.reportUrl]"
                            fit="contain"
                        />
                    </el-descriptions-item>
                </el-descriptions>
            </template>
            </el-dialog>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { labTestApi, type LabTest } from '@/api/labTest'
import dayjs from 'dayjs'

const loading = ref(false)
const labTests = ref<LabTest[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentTest = ref<LabTest | null>(null)

const queryParams = reactive({
    page: 1,
    size: 10,
    status: undefined as number | undefined,
    searchText: ''
})

const fetchData = async () => {
    loading.value = true
    try {
        const res = await labTestApi.list(queryParams)
        labTests.value = res.data.records
        total.value = res.data.total
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    queryParams.page = 1
    fetchData()
}

const resetSearch = () => {
    queryParams.searchText = ''
    queryParams.status = undefined
    queryParams.page = 1
    fetchData()
}

const handleSizeChange = (val: number) => {
    queryParams.size = val
    fetchData()
}

const handleCurrentChange = (val: number) => {
    queryParams.page = val
    fetchData()
}

const viewDetail = (row: LabTest) => {
    currentTest.value = row
    detailVisible.value = true
}

const formatDate = (date?: string) => {
    return date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.lab-test-list {
    padding: 20px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-start;
}

.text-gray {
    color: #909399;
}

.result-text {
    white-space: pre-wrap;
    line-height: 1.5;
}

.amount {
    color: #f56c6c;
    font-weight: bold;
    font-size: 16px;
}
</style>
