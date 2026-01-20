<template>
    <div class="lab-workbench">
        <el-row :gutter="20">
            <!-- 左侧：待检验队列 -->
            <el-col :span="8">
                <el-card class="queue-card">
                    <template #header>
                        <div class="card-header">
                            <el-radio-group v-model="activeTab" size="small" @change="handleTabChange">
                                <el-radio-button label="pending">待检验 ({{ pendingCount }})</el-radio-button>
                                <el-radio-button label="completed">已检验 ({{ completedCount }})</el-radio-button>
                            </el-radio-group>
                        </div>
                    </template>
                    <el-input
                        v-model="searchText"
                        placeholder="搜索宠物/检查项目"
                        prefix-icon="Search"
                        clearable
                        style="margin-bottom: 15px"
                    />
                    <div class="queue-list" v-loading="loadingList">
                        <el-empty v-if="testList.length === 0" :description="activeTab === 'pending' ? '暂无待检验项目' : '暂无检验记录'" />
                        <div
                            v-else
                            v-for="test in filteredTests"
                            :key="test.id"
                            class="queue-item"
                            :class="{ active: currentTest?.id === test.id }"
                            @click="selectTest(test)"
                        >
                            <div class="item-header">
                                <span class="pet-name">{{ test.medicalRecord?.pet?.name }}</span>
                                <el-tag size="small">{{ test.serviceItem?.name }}</el-tag>
                            </div>
                            <div class="item-info">
                                <span>客户: {{ test.medicalRecord?.pet?.customer?.name }}</span>
                                <span>{{ formatTime(test.testTime) }}</span>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>

            <!-- 右侧：结果录入 -->
            <el-col :span="16">
                <el-card v-if="currentTest" class="result-card">
                    <el-template #header>
                        <div class="card-header">
                            <span>{{ activeTab === 'pending' ? '结果录入' : '检验详情' }} - {{ currentTest.serviceItem?.name }}</span>
                            <el-tag :type="activeTab === 'pending' ? 'warning' : 'success'">
                                {{ activeTab === 'pending' ? '待录入' : '已完成' }}
                            </el-tag>
                        </div>
                    </el-template>
                    
                    <el-descriptions border :column="2" class="mb-20">
                        <el-descriptions-item label="宠物">{{ currentTest.medicalRecord?.pet?.name }}</el-descriptions-item>
                        <el-descriptions-item label="客户">{{ currentTest.medicalRecord?.pet?.customer?.name }}</el-descriptions-item>
                        <el-descriptions-item label="项目">{{ currentTest.serviceItem?.name }}</el-descriptions-item>
                        <el-descriptions-item label="开单时间">{{ formatTime(currentTest.testTime) }}</el-descriptions-item>
                    </el-descriptions>

                    <el-form :model="resultForm" label-width="100px" :disabled="activeTab === 'completed'">
                        <el-form-item label="检验结果" required>
                            <el-input 
                                v-model="resultForm.result" 
                                type="textarea" 
                                :rows="6"
                                placeholder="请输入详细检验结果..." 
                            />
                        </el-form-item>
                        <el-form-item label="图片报告">
                            <div v-if="activeTab === 'completed' && resultForm.reportUrl">
                                <el-image 
                                    :src="resultForm.reportUrl" 
                                    :preview-src-list="[resultForm.reportUrl]"
                                    style="width: 200px; border-radius: 4px"
                                />
                            </div>
                            <el-upload
                                v-else
                                class="avatar-uploader"
                                action=""
                                :show-file-list="false"
                                :http-request="uploadImage"
                                :before-upload="beforeAvatarUpload"
                                :disabled="activeTab === 'completed'"
                            >
                                <img v-if="resultForm.reportUrl" :src="resultForm.reportUrl" class="avatar" />
                                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                            </el-upload>
                            <div class="el-upload__tip" v-if="activeTab === 'pending'">只能上传 jpg/png 文件，且不超过 2MB</div>
                        </el-form-item>
                        <el-form-item v-if="activeTab === 'pending'">
                            <el-button type="primary" @click="handleSubmit" :loading="submitting">
                                提交结果
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
                <el-empty v-else description="请从左侧选择待检验项目" />
            </el-col>
        </el-row>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { labTestApi, type LabTest } from '@/api/labTest'
import dayjs from 'dayjs'

const loadingList = ref(false)
const testList = ref<LabTest[]>([])
const pendingCount = ref(0)
const completedCount = ref(0)
const currentTest = ref<LabTest | null>(null)
const submitting = ref(false)
const searchText = ref('')
const activeTab = ref('pending')

const resultForm = reactive({
    result: '',
    reportUrl: ''
})

const filteredTests = computed(() => {
    if (!searchText.value) return testList.value
    const kw = searchText.value.toLowerCase()
    return testList.value.filter(t => 
        t.medicalRecord?.pet?.name?.toLowerCase().includes(kw) ||
        t.serviceItem?.name?.toLowerCase().includes(kw)
    )
})

const fetchCounts = async () => {
    try {
        const [pendingRes, completedRes] = await Promise.all([
            labTestApi.list({ status: 0, page: 1, size: 1 }),
            labTestApi.list({ status: 1, page: 1, size: 1 })
        ])
        pendingCount.value = pendingRes.data.total
        completedCount.value = completedRes.data.total
    } catch (error) {
        console.error('加载数量统计失败', error)
    }
}

const fetchTests = async () => {
    loadingList.value = true
    try {
        const status = activeTab.value === 'pending' ? 0 : 1
        const res = await labTestApi.list({ status, page: 1, size: 50 })
        testList.value = res.data.records
        // 同时更新统计数据
        fetchCounts()
    } catch (error) {
        ElMessage.error('加载列表失败')
    } finally {
        loadingList.value = false
    }
}

const handleTabChange = () => {
    currentTest.value = null
    fetchTests()
}

const selectTest = (test: LabTest) => {
    currentTest.value = test
    if (activeTab.value === 'completed') {
        resultForm.result = test.result || ''
        resultForm.reportUrl = test.reportUrl || ''
    } else {
        resultForm.result = ''
        resultForm.reportUrl = ''
    }
}

const uploadImage = async (options: any) => {
    try {
        const res = await labTestApi.uploadImage(options.file)
        // 拼接后端地址
        const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080'
        resultForm.reportUrl = baseURL + res.data
        ElMessage.success('上传成功')
    } catch (error) {
        ElMessage.error('上传失败')
    }
}

const beforeAvatarUpload = (rawFile: File) => {
    if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
        ElMessage.error('图片必须是 JPG 或 PNG 格式!')
        return false
    } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('图片大小不能超过 2MB!')
        return false
    }
    return true
}

const handleSubmit = async () => {
    if (!resultForm.result) {
        ElMessage.warning('请输入检验结果')
        return
    }

    submitting.value = true
    try {
        await labTestApi.complete(currentTest.value!.id!, resultForm)
        ElMessage.success('结果提交成功')
        currentTest.value = null
        fetchTests()
    } catch (error) {
        ElMessage.error('提交失败')
    } finally {
        submitting.value = false
    }
}

const formatTime = (time?: string) => {
    return time ? dayjs(time).format('YYYY-MM-DD HH:mm') : ''
}

onMounted(() => {
    fetchTests()
})
</script>

<style scoped>
.lab-workbench {
    padding: 20px;
}

.queue-card {
    height: calc(100vh - 120px);
    display: flex;
    flex-direction: column;
}

.queue-list {
    flex: 1;
    overflow-y: auto;
}

.queue-item {
    padding: 12px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
    transition: background 0.2s;
}

.queue-item:hover {
    background: #f5f7fa;
}

.queue-item.active {
    background: #ecf5ff;
    border-right: 3px solid #409eff;
}

.item-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
}

.pet-name {
    font-weight: bold;
}

.item-info {
    font-size: 12px;
    color: #909399;
    display: flex;
    justify-content: space-between;
}

.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    line-height: 178px;
}

.avatar {
    width: 178px;
    height: 178px;
    display: block;
}

.mb-20 {
    margin-bottom: 20px;
}
</style>
