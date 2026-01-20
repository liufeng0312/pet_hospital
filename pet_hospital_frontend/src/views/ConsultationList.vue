<template>
    <div class="consultation-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <span>在线咨询管理</span>
                </div>
            </template>

            <!-- 筛选 -->
            <el-form :inline="true" style="margin-bottom: 20px">
                <el-form-item label="状态">
                    <el-select v-model="searchStatus" placeholder="全部" clearable style="width: 150px">
                        <el-option label="待回复" value="PENDING" />
                        <el-option label="已回复" value="ANSWERED" />
                        <el-option label="已关闭" value="CLOSED" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="fetchData"><el-icon><Search /></el-icon> 查询</el-button>
                    <el-button @click="resetSearch"><el-icon><RefreshLeft /></el-icon> 重置</el-button>
                </el-form-item>
            </el-form>

            <!-- 数据表格 -->
            <el-table :data="tableData" v-loading="loading" style="width: 100%">
                <el-table-column prop="id" label="ID" min-width="80" />
                <el-table-column prop="title" label="咨询标题" min-width="200" />
                <el-table-column prop="content" label="咨询内容" min-width="250" show-overflow-tooltip />
                <el-table-column prop="pet.name" label="宠物" min-width="120">
                    <template #default="{ row }">
                        {{ row.pet?.name }} ({{ row.pet?.species }})
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" min-width="120">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createdAt" label="提交时间" min-width="180" />
                <el-table-column label="操作" min-width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button size="small" @click="handleView(row)">查看</el-button>
                        <el-button 
                            v-if="row.status === 'PENDING'" 
                            type="primary" 
                            size="small" 
                            @click="handleReply(row)"
                        >回复</el-button>
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

        <!-- 查看/回复对话框 -->
        <el-dialog
            v-model="dialogVisible"
            :title="isViewing ? '咨询详情' : '回复咨询'"
            width="700px"
        >
            <div v-if="currentConsultation">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="标题">{{ currentConsultation.title }}</el-descriptions-item>
                    <el-descriptions-item label="宠物">
                        {{ currentConsultation.pet?.name }} ({{ currentConsultation.pet?.species }})
                    </el-descriptions-item>
                    <el-descriptions-item label="提交时间">{{ currentConsultation.createdAt }}</el-descriptions-item>
                    <el-descriptions-item label="咨询内容">
                        <div style="white-space: pre-wrap">{{ currentConsultation.content }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentConsultation.images" label="图片">
                        <div style="display: flex; gap: 10px; flex-wrap: wrap">
                            <el-image
                                v-for="(img, index) in parseImages(currentConsultation.images)"
                                :key="index"
                                :src="img"
                                style="width: 100px; height: 100px"
                                :preview-src-list="parseImages(currentConsultation.images)"
                                fit="cover"
                            />
                        </div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentConsultation.replyContent" label="回复内容">
                        <div style="white-space: pre-wrap">{{ currentConsultation.replyContent }}</div>
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentConsultation.replyTime" label="回复时间">
                        {{ currentConsultation.replyTime }}
                    </el-descriptions-item>
                    <el-descriptions-item v-if="currentConsultation.doctor" label="回复医生">
                        {{ currentConsultation.doctor.name }}
                    </el-descriptions-item>
                </el-descriptions>

                <el-form v-if="!isViewing" style="margin-top: 20px">
                    <el-form-item label="回复内容" label-width="80px">
                        <el-input
                            v-model="replyForm.content"
                            type="textarea"
                            :rows="6"
                            placeholder="请输入回复内容"
                        />
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">{{ isViewing ? '关闭' : '取消' }}</el-button>
                    <el-button v-if="!isViewing" type="primary" @click="handleSubmitReply" :loading="submitting">
                        提交回复
                    </el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { consultationApi, type Consultation } from '@/api/consultation'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<Consultation[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchStatus = ref('')

const dialogVisible = ref(false)
const isViewing = ref(false)
const currentConsultation = ref<Consultation | null>(null)
const submitting = ref(false)

const replyForm = reactive({
    content: ''
})

const getStatusType = (status: string) => {
    const map: Record<string, string> = {
        'PENDING': 'warning',
        'ANSWERED': 'success',
        'CLOSED': 'info'
    }
    return map[status] || 'info'
}

const getStatusText = (status: string) => {
    const map: Record<string, string> = {
        'PENDING': '待回复',
        'ANSWERED': '已回复',
        'CLOSED': '已关闭'
    }
    return map[status] || status
}

const parseImages = (images?: string) => {
    if (!images) return []
    try {
        return JSON.parse(images)
    } catch {
        return []
    }
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await consultationApi.list({
            status: searchStatus.value || undefined,
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
    searchStatus.value = ''
    currentPage.value = 1
    fetchData()
}

const handleView = (row: Consultation) => {
    currentConsultation.value = row
    isViewing.value = true
    dialogVisible.value = true
}

const handleReply = (row: Consultation) => {
    currentConsultation.value = row
    isViewing.value = false
    replyForm.content = ''
    dialogVisible.value = true
}

const handleSubmitReply = async () => {
    if (!replyForm.content.trim()) {
        ElMessage.warning('请输入回复内容')
        return
    }

    submitting.value = true
    try {
        await consultationApi.reply(currentConsultation.value!.id!, {
            replyContent: replyForm.content
        })
        ElMessage.success('回复成功')
        dialogVisible.value = false
        fetchData()
    } catch (error) {
        ElMessage.error('回复失败')
    } finally {
        submitting.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.consultation-list {
    padding: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
</style>
