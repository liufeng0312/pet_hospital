<template>
    <div class="queue-board">
        <el-row :gutter="20">
            <!-- 当前叫号 -->
            <el-col :span="8">
                <el-card class="current-card">
                    <template #header>
                        <span class="card-title">当前叫号</span>
                    </template>
                    <div v-if="currentPatient" class="current-patient">
                        <div class="queue-number">{{ currentPatient.queueNumber }}</div>
                        <div class="patient-info">
                            <p class="pet-name">{{ currentPatient.pet?.name }}</p>
                            <p class="customer-name">{{ currentPatient.customer?.name }}</p>
                            <p class="doctor-name" v-if="currentPatient.doctor?.name">
                                医生: {{ currentPatient.doctor?.name }}
                            </p>
                        </div>
                    </div>
                    <el-empty v-else description="暂无就诊患者" />
                </el-card>
            </el-col>

            <!-- 候诊队列 -->
            <el-col :span="16">
                <el-card class="queue-card">
                    <template #header>
                        <div class="queue-header">
                            <span class="card-title">候诊队列</span>
                            <el-tag type="info">{{ waitingList.length }} 位等候中</el-tag>
                        </div>
                    </template>
                    <div class="waiting-list" v-if="waitingList.length > 0">
                        <el-scrollbar height="400px">
                            <div
                                v-for="item in waitingList"
                                :key="item.id"
                                class="waiting-item"
                            >
                                <div class="item-number">{{ item.queueNumber }}</div>
                                <div class="item-info">
                                    <span class="pet-name">{{ item.pet?.name }}</span>
                                    <span class="species-tag">{{ item.pet?.species }}</span>
                                </div>
                                <div class="item-customer">{{ item.customer?.name }}</div>
                            </div>
                        </el-scrollbar>
                    </div>
                    <el-empty v-else description="暂无候诊患者" />
                </el-card>
            </el-col>
        </el-row>

        <!-- 刷新提示 -->
        <div class="refresh-info">
            <el-icon><Refresh /></el-icon>
            每 5 秒自动刷新 | 上次更新: {{ lastUpdate }}
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { registrationApi, type Registration } from '@/api/registration'

const queueData = ref<Registration[]>([])
const lastUpdate = ref('')
let timer: number | null = null

const currentPatient = computed(() => {
    return queueData.value.find(item => item.status === 'IN_PROGRESS')
})

const waitingList = computed(() => {
    return queueData.value.filter(item => item.status === 'WAITING')
})

const fetchQueueData = async () => {
    try {
        const res = await registrationApi.getQueueBoard()
        queueData.value = res.data
        lastUpdate.value = new Date().toLocaleTimeString()
    } catch (error) {
        console.error('获取排队数据失败', error)
    }
}

onMounted(() => {
    fetchQueueData()
    // 每5秒刷新一次
    timer = window.setInterval(fetchQueueData, 5000)
})

onUnmounted(() => {
    if (timer) {
        clearInterval(timer)
    }
})
</script>

<style scoped>
.queue-board {
    padding: 20px;
    min-height: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-title {
    font-size: 18px;
    font-weight: bold;
}

.current-card {
    height: 500px;
}

.current-patient {
    text-align: center;
    padding: 20px;
}

.queue-number {
    font-size: 120px;
    font-weight: bold;
    color: #409eff;
    line-height: 1.2;
}

.patient-info {
    margin-top: 20px;
}

.pet-name {
    font-size: 28px;
    font-weight: bold;
    color: #303133;
    margin: 10px 0;
}

.customer-name {
    font-size: 18px;
    color: #606266;
}

.doctor-name {
    font-size: 16px;
    color: #909399;
    margin-top: 10px;
}

.queue-card {
    height: 500px;
}

.queue-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.waiting-list {
    padding: 10px 0;
}

.waiting-item {
    display: flex;
    align-items: center;
    padding: 15px 20px;
    margin-bottom: 10px;
    background: #f5f7fa;
    border-radius: 8px;
    transition: all 0.3s;
}

.waiting-item:hover {
    background: #ecf5ff;
    transform: translateX(5px);
}

.item-number {
    width: 50px;
    height: 50px;
    line-height: 50px;
    text-align: center;
    background: #409eff;
    color: white;
    border-radius: 50%;
    font-size: 20px;
    font-weight: bold;
    margin-right: 20px;
}

.item-info {
    flex: 1;
}

.item-info .pet-name {
    font-size: 18px;
    font-weight: bold;
    color: #303133;
    margin-right: 10px;
}

.species-tag {
    font-size: 12px;
    color: #909399;
    background: #ebeef5;
    padding: 2px 8px;
    border-radius: 4px;
}

.item-customer {
    font-size: 14px;
    color: #606266;
    min-width: 100px;
    text-align: right;
}

.refresh-info {
    margin-top: 20px;
    text-align: center;
    color: rgba(255, 255, 255, 0.8);
    font-size: 14px;
}

.refresh-info .el-icon {
    margin-right: 5px;
    animation: spin 2s linear infinite;
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}
</style>
