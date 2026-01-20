<template>
    <div class="reminder-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>智能提醒中心</span>
                    <el-button type="primary" @click="handleGenerate" :loading="generating">
                        <el-icon><Refresh /></el-icon> 扫描生成今日提醒
                    </el-button>
                </div>
            </template>

            <!-- 搜索栏 -->
            <el-form :inline="true" :model="searchForm" class="search-form">
                <el-form-item label="提醒类型">
                    <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 150px">
                        <el-option label="疫苗提醒" value="VACCINE" />
                        <el-option label="生日提醒" value="BIRTHDAY" />
                        <el-option label="回访提醒" value="FOLLOW_UP" />
                    </el-select>
                </el-form-item>
                
                <el-form-item label="状态">
                    <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
                        <el-option label="未发送" :value="0" />
                        <el-option label="已发送" :value="1" />
                    </el-select>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <el-icon><Search /></el-icon> 查询
                    </el-button>
                    <el-button @click="resetSearch">
                        <el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>


            <el-table :data="reminders" style="width: 100%" v-loading="loading">
                <el-table-column prop="type" label="提醒类型" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="getObjectType(row.type)">{{ getTypeName(row.type) }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="dueDate" label="到期/生日" min-width="150" />
                <el-table-column prop="customer.name" label="客户" min-width="120" />
                <el-table-column prop="customer.phone" label="联系电话" min-width="150" />
                <el-table-column prop="pet.name" label="宠物" min-width="120" />
                <el-table-column prop="status" label="状态" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="row.status === 1 ? 'success' : 'info'">
                            {{ row.status === 1 ? '已发送' : '未发送' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="sentTime" label="发送时间">
                    <template #default="{ row }">
                        {{ formatTime(row.sentTime) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button 
                            type="primary" 
                            size="small" 
                            @click="handleSend(row)"
                            :disabled="row.status === 1"
                        >
                            发送通知
                        </el-button>
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
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search, RefreshLeft } from '@element-plus/icons-vue'
import { reminderApi, type Reminder } from '@/api/reminder'
import dayjs from 'dayjs'

const loading = ref(false)
const generating = ref(false)
const reminders = ref<Reminder[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
    type: '',
    status: undefined as number | undefined
})

const fetchData = async () => {
    loading.value = true
    try {
        const params = {
            page: currentPage.value,
            size: pageSize.value,
            ...searchForm
        }
        const res = await reminderApi.list(params)
        if (res.data.records) {
            reminders.value = res.data.records
            total.value = res.data.total
        } else {
            reminders.value = res.data
            total.value = res.data.length
        }
    } catch (error) {
        ElMessage.error('加载失败')
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    currentPage.value = 1
    fetchData()
}

const resetSearch = () => {
    searchForm.type = ''
    searchForm.status = undefined
    currentPage.value = 1
    fetchData()
}

const handleGenerate = async () => {
    generating.value = true
    try {
        await reminderApi.generate()
        ElMessage.success('已生成最新提醒')
        fetchData()
    } catch (error) {
        ElMessage.error('生成失败')
    } finally {
        generating.value = false
    }
}

const handleSend = async (row: Reminder) => {
    try {
        await reminderApi.send(row.id!)
        ElMessage.success('发送成功')
        fetchData()
    } catch (error) {
        ElMessage.error('发送失败')
    }
}

const getTypeName = (type: string) => {
    const map: Record<string, string> = {
        'VACCINE': '疫苗接种',
        'BIRTHDAY': '生日祝福',
        'FOLLOW_UP': '诊后回访'
    }
    return map[type] || type
}

const getObjectType = (type: string) => {
    return type === 'BIRTHDAY' ? 'danger' : 'warning'
}

const formatTime = (time?: string) => {
    return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.reminder-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
