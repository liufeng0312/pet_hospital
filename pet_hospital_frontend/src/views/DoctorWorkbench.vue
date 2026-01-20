<template>
    <div class="doctor-workbench">
        <el-row :gutter="20">
            <!-- 左侧：宠物信息和病历历史 -->
            <el-col :span="8">
                <!-- 挂号选择 -->
                <el-card class="selection-card">
                    <template #header>
                        <span>选择挂号</span>
                    </template>
                    <el-select
                        v-model="selectedRegistrationId"
                        filterable
                        placeholder="请选择待就诊的挂号"
                        style="width: 100%"
                        @change="onRegistrationChange"
                    >
                        <el-option
                            v-for="reg in registrations"
                            :key="reg.id"
                            :label="`#${reg.queueNumber} - ${reg.pet?.name} (${reg.customer?.name})`"
                            :value="reg.id"
                        />
                    </el-select>
                </el-card>

                <!-- 宠物信息 -->
                <el-card v-if="currentPet" class="pet-info-card">
                    <template #header>
                        <span>宠物信息</span>
                    </template>
                    <div class="pet-info">
                        <div class="info-item">
                            <span class="label">姓名:</span>
                            <span class="value">{{ currentPet.name }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">物种:</span>
                            <span class="value">{{ currentPet.species }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">品种:</span>
                            <span class="value">{{ currentPet.breed || '-' }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">性别:</span>
                            <span class="value">{{ getGenderLabel(currentPet.gender) }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">体重:</span>
                            <span class="value">{{ currentPet.weight ? currentPet.weight + ' kg' : '-' }}</span>
                        </div>
                        <div class="info-item">
                            <span class="label">主人:</span>
                            <span class="value">{{ currentPet.customer?.name }}</span>
                        </div>
                        <div class="info-item" v-if="currentPet.notes">
                            <span class="label">注意事项:</span>
                            <span class="value notes">{{ currentPet.notes }}</span>
                        </div>
                    </div>
                </el-card>

                <!-- 病历历史 -->
                <el-card v-if="currentPet" class="history-card">
                    <template #header>
                        <div class="history-header">
                            <span>病历历史</span>
                            <el-tag type="info" size="small">{{ medicalHistory.length }} 条记录</el-tag>
                        </div>
                    </template>
                    <el-timeline v-if="medicalHistory.length > 0">
                        <el-timeline-item
                            v-for="record in medicalHistory"
                            :key="record.id"
                            :timestamp="formatDate(record.visitTime)"
                            placement="top"
                        >
                            <el-card class="history-item" @click="viewHistoryDetail(record)">
                                <div class="history-content">
                                    <p class="diagnosis">
                                        <strong>诊断:</strong> {{ record.diagnosis || '-' }}
                                    </p>
                                    <p class="doctor">医生: {{ record.doctor?.name }}</p>
                                </div>
                            </el-card>
                        </el-timeline-item>
                    </el-timeline>
                    <el-empty v-else description="暂无病历记录" />
                </el-card>
            </el-col>

            <!-- 右侧：病历输入表单 -->
            <el-col :span="16">
                <el-card class="form-card">
                    <template #header>
                        <div class="form-header">
                            <span>病历录入</span>
                            <el-button
                                v-if="currentForm.id"
                                type="warning"
                                size="small"
                                @click="resetForm"
                            >新建病历</el-button>
                        </div>
                    </template>
                    <el-form
                        ref="formRef"
                        :model="currentForm"
                        label-width="100px"
                        :disabled="!selectedRegistrationId"
                    >
                        <el-form-item label="主诉/症状" required>
                            <el-input
                                v-model="currentForm.symptoms"
                                type="textarea"
                                :rows="4"
                                placeholder="请描述宠物的症状和主诉"
                            />
                        </el-form-item>
                        <el-form-item label="诊断结果" required>
                            <el-input
                                v-model="currentForm.diagnosis"
                                type="textarea"
                                :rows="4"
                                placeholder="请输入诊断结果"
                            />
                        </el-form-item>
                        <el-form-item label="治疗方案">
                            <el-input
                                v-model="currentForm.treatmentPlan"
                                type="textarea"
                                :rows="4"
                                placeholder="请输入治疗方案"
                            />
                        </el-form-item>
                        <el-form-item label="医嘱">
                            <el-input
                                v-model="currentForm.doctorAdvice"
                                type="textarea"
                                :rows="3"
                                placeholder="请输入医嘱和注意事项"
                            />
                        </el-form-item>
                        <el-form-item>
                            <el-button
                                type="primary"
                                @click="handleSave"
                                :loading="submitting"
                                :disabled="!selectedRegistrationId"
                            >
                                {{ currentForm.id ? '更新病历' : '保存病历' }}
                            </el-button>
                            <el-button @click="resetForm">清空</el-button>
                            <el-button
                                v-if="savedMedicalRecordId"
                                type="success"
                                @click="showPrescriptionDialog"
                            >
                                开具处方
                            </el-button>
                            <el-button
                                v-if="savedMedicalRecordId"
                                type="warning"
                                @click="showLabTestDialog"
                            >
                                开具项目
                            </el-button>
                            <el-button
                                v-if="savedMedicalRecordId"
                                type="success"
                                @click="handleCompleteAndNext"
                                :loading="submitting"
                            >
                                完成并呼叫下一位
                            </el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-col>
        </el-row>

        <!-- 检查开具弹窗 -->
        <el-dialog v-model="labTestDialogVisible" title="开具项目" width="500px" append-to-body>
            <el-form :model="labTestForm" label-width="100px">
                <el-form-item label="服务项目" required>
                    <el-select
                        v-model="labTestForm.serviceId"
                        filterable
                        placeholder="请选择服务项目"
                        style="width: 100%"
                    >
                        <el-option
                            v-for="item in serviceItems"
                            :key="item.id"
                            :label="`${item.name} (¥${item.price})`"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="价格" v-if="selectedService">
                    <span style="color: #f56c6c; font-weight: bold">¥{{ selectedService.price }}</span>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="labTestDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleCreateLabTest" :loading="labTestSubmitting">
                    开具
                </el-button>
            </template>
        </el-dialog>

        <!-- 处方开具弹窗 -->
        <el-dialog v-model="prescriptionDialogVisible" title="开具处方" width="1000px" append-to-body>
            <div class="prescription-form">
                <el-button type="primary" size="small" @click="addPrescriptionItem" style="margin-bottom: 15px">
                    <el-icon><Plus /></el-icon>添加药品
                </el-button>
                
                <el-table :data="prescriptionItems" border style="width: 100%">
                    <el-table-column label="药品" min-width="200">
                        <template #default="{ row, $index }">
                            <el-select
                                v-model="row.drugId"
                                filterable
                                placeholder="选择药品"
                                @change="onDrugChange($index)"
                            >
                                <el-option
                                    v-for="drug in drugs"
                                    :key="drug.id"
                                    :label="drug.name"
                                    :value="drug.id"
                                />
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column label="规格" min-width="120">
                        <template #default="{ row }">
                            {{ row.specification || '-' }}
                        </template>
                    </el-table-column>
                    <el-table-column label="数量" min-width="150">
                        <template #default="{ row }">
                            <el-input-number v-model="row.quantity" :min="1" size="small" />
                        </template>
                    </el-table-column>
                    <el-table-column label="单价" min-width="100">
                        <template #default="{ row }">
                            ¥{{ row.price?.toFixed(2) || '0.00' }}
                        </template>
                    </el-table-column>
                    <el-table-column label="小计" min-width="100">
                        <template #default="{ row }">
                            ¥{{ ((row.price || 0) * row.quantity).toFixed(2) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="用法用量">
                        <template #default="{ row }">
                            <el-input v-model="row.dosage" placeholder="如：每日两次" size="small" />
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" min-width="80">
                        <template #default="{ $index }">
                            <el-button type="danger" size="small" @click="removePrescriptionItem($index)">
                                删除
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="total-amount">
                    <strong>总金额：</strong>
                    <span class="amount">¥{{ calculateTotalAmount().toFixed(2) }}</span>
                </div>
            </div>
            <template #footer>
                <el-button @click="prescriptionDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleCreatePrescription" :loading="prescriptionSubmitting">
                    保存处方
                </el-button>
            </template>
        </el-dialog>

        <!-- 病历详情弹窗 -->
        <el-dialog v-model="detailDialogVisible" title="病历详情" width="600px" append-to-body>
            <div v-if="selectedHistory" class="detail-content">
                <el-descriptions :column="1" border>
                    <el-descriptions-item label="就诊时间">
                        {{ formatDate(selectedHistory.visitTime) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="医生">
                        {{ selectedHistory.doctor?.name }}
                    </el-descriptions-item>
                    <el-descriptions-item label="主诉/症状">
                        {{ selectedHistory.symptoms || '-' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="诊断结果">
                        {{ selectedHistory.diagnosis || '-' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="治疗方案">
                        {{ selectedHistory.treatmentPlan || '-' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="医嘱">
                        {{ selectedHistory.doctorAdvice || '-' }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Plus, Microphone } from '@element-plus/icons-vue'
import { registrationApi, type Registration } from '@/api/registration'
import { petApi, type Pet } from '@/api/pet'
import { medicalRecordApi, type MedicalRecord } from '@/api/medicalRecord'
import { prescriptionApi, type Prescription, type PrescriptionItem } from '@/api/prescription'
import { drugApi, type Drug } from '@/api/drug'
import dayjs from 'dayjs'

const selectedRegistrationId = ref<number>()
const registrations = ref<Registration[]>([])
const currentPet = ref<Pet>()
const currentUser = ref<any>()
const medicalHistory = ref<MedicalRecord[]>([])
const submitting = ref(false)
const savedMedicalRecordId = ref<number>()

const currentForm = reactive<MedicalRecord>({
    registrationId: 0,
    petId: 0,
    doctorId: 1, // TODO: 从登录用户获取
    symptoms: '',
    diagnosis: '',
    treatmentPlan: '',
    doctorAdvice: '',
    visitTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
})

// 病历详情弹窗
const detailDialogVisible = ref(false)
const selectedHistory = ref<MedicalRecord>()

// 处方相关
const prescriptionDialogVisible = ref(false)

// 检查相关
import { serviceItemApi, type ServiceItem } from '@/api/serviceItem'
import { labTestApi } from '@/api/labTest'
import { surgeryApi } from '@/api/surgery'

const labTestDialogVisible = ref(false)
const serviceItems = ref<ServiceItem[]>([])
const labTestSubmitting = ref(false)
const labTestForm = reactive({
    serviceId: undefined as number | undefined,
    testTime: new Date()
})

const selectedService = computed(() => 
    serviceItems.value.find(item => item.id === labTestForm.serviceId)
)

const showLabTestDialog = async () => {
    try {
        const res = await serviceItemApi.listActive()
        // 显示所有服务项目（包括检查、化验、手术、治疗等）
        serviceItems.value = res.data
        labTestForm.serviceId = undefined
        labTestForm.testTime = new Date()
        labTestDialogVisible.value = true
    } catch (error) {
        ElMessage.error('加载服务项目失败')
    }
}

const handleCreateLabTest = async () => {
    if (!labTestForm.serviceId) {
        ElMessage.warning('请选择服务项目')
        return
    }

    if (!selectedRegistrationId.value || !currentPet.value) {
        ElMessage.warning('请先选择病人')
        return
    }

    labTestSubmitting.value = true
    try {
        const selectedItem = serviceItems.value.find(item => item.id === labTestForm.serviceId)
        
        // 如果是手术类服务，创建手术记录
        if (selectedItem?.category === 'SURGERY') {
            const surgeryPayload = {
                serviceItemId: labTestForm.serviceId,
                medicalRecordId: savedMedicalRecordId.value!,
                petId: currentPet.value.id!,
                surgeonId: currentUser.value?.id || 0,
                surgeryType: selectedItem.category,
                surgeryName: selectedItem.name || '',
                surgeryDate: dayjs(labTestForm.testTime).format('YYYY-MM-DD HH:mm:ss'),
                status: 0, // 待支付
                amount: selectedItem.price || 0
            }
            await surgeryApi.create(surgeryPayload)
            ElMessage.success('手术记录创建成功')
        } else {
            // 其他服务类型，创建检查单
            const payload = {
                medicalRecordId: savedMedicalRecordId.value!,
                serviceId: labTestForm.serviceId,
                testTime: dayjs(labTestForm.testTime).format('YYYY-MM-DD HH:mm:ss')
            }
            await labTestApi.create(payload)
            ElMessage.success('服务项目开具成功')
        }
        
        labTestDialogVisible.value = false
    } catch (error: any) {
        console.error('Create Service Item Failed:', error)
        ElMessage.error(error.response?.data?.msg || error.message || '开具项目失败')
    } finally {
        labTestSubmitting.value = false
    }
}
const prescriptionSubmitting = ref(false)
const prescriptionItems = ref<PrescriptionItem[]>([])
const drugs = ref<Drug[]>([])


const fetchWaitingRegistrations = async () => {
    try {
        // 只获取就诊中的挂号供下拉框选择
        const res = await registrationApi.list({ status: 'IN_PROGRESS', page: 1, size: 100 })
        registrations.value = res.data.records
    } catch (error) {
        console.error('获取挂号列表失败', error)
    }
}

const onRegistrationChange = async (registrationId: number) => {
    if (!registrationId) {
        currentPet.value = undefined
        medicalHistory.value = []
        return
    }

    const registration = registrations.value.find(r => r.id === registrationId)
    if (!registration || !registration.petId) {
        return
    }

    // 获取宠物信息
    try {
        const petRes = await petApi.getById(registration.petId)
        currentPet.value = petRes.data
        
        // 获取病历历史
        const historyRes = await medicalRecordApi.listByPet(registration.petId)
        medicalHistory.value = historyRes.data

        // 重置表单
        resetForm()
        currentForm.registrationId = registrationId
        currentForm.petId = registration.petId
    } catch (error) {
        console.error('获取宠物信息失败', error)
        ElMessage.error('获取宠物详情失败，请重试')
    }
}

const handleSave = async () => {
    if (!currentForm.symptoms || !currentForm.diagnosis) {
        ElMessage.warning('请填写症状和诊断结果')
        return
    }

    submitting.value = true
    try {
        if (currentForm.id) {
            await medicalRecordApi.update(currentForm.id, currentForm)
            ElMessage.success('病历更新成功')
            savedMedicalRecordId.value = currentForm.id
        } else {
            currentForm.visitTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
            const res = await medicalRecordApi.create(currentForm)
            ElMessage.success('病历保存成功')
            savedMedicalRecordId.value = res.data.id
        }
        
        // 刷新病历历史
        if (currentPet.value?.id) {
            const historyRes = await medicalRecordApi.listByPet(currentPet.value.id)
            medicalHistory.value = historyRes.data
        }
        
        // 不重置表单，保留病历ID以便开具处方
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '保存失败')
    } finally {
        submitting.value = false
    }
}

const resetForm = () => {
    currentForm.id = undefined
    currentForm.symptoms = ''
    currentForm.diagnosis = ''
    currentForm.treatmentPlan = ''
    currentForm.doctorAdvice = ''
    savedMedicalRecordId.value = undefined
    // 注意：不清空medicalHistory，因为它在onRegistrationChange中已经加载了新病人的历史
}

// 完成当前挂号并呼叫下一位
const handleCompleteAndNext = async () => {
    if (!selectedRegistrationId.value) {
        ElMessage.warning('请先选择挂号记录')
        return
    }

    try {
        await ElMessageBox.confirm('确认完成当前病人的诊疗并呼叫下一位？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
        })

        submitting.value = true

        // 完成当前挂号
        await registrationApi.complete(selectedRegistrationId.value)
        
        // 查找下一个候诊中的病人并自动叫号
        const allRes = await registrationApi.list({ page: 1, size: 100 })
        const nextPatient = allRes.data.records.find((r: any) => r.status === 'WAITING')
        
        if (nextPatient) {
            // 自动叫号（将状态从 WAITING 改为 IN_PROGRESS）
            await registrationApi.callNext(nextPatient.id)
            ElMessage.success(`已自动叫号：${nextPatient.queueNumber}号 - ${nextPatient.pet?.name}，请从列表选择`)
        } else {
            ElMessage.info('没有候诊病人')
        }
        
        // 刷新挂号列表（会显示刚叫号的病人）
        await fetchWaitingRegistrations()
        
        // 清空选择，让用户手动选择
        selectedRegistrationId.value = undefined
        currentPet.value = null
        medicalHistory.value = []
        resetForm()
    } catch (error: any) {
        if (error !== 'cancel') {
            ElMessage.error(error.response?.data?.msg || '操作失败')
        }
    } finally {
        submitting.value = false
    }
}

// 处方相关方法
const showPrescriptionDialog = async () => {
    // 加载药品列表
    try {
        const res = await drugApi.listActive()
        drugs.value = res.data
        prescriptionItems.value = []
        prescriptionDialogVisible.value = true
    } catch (error) {
        ElMessage.error('加载药品列表失败')
    }
}

const addPrescriptionItem = () => {
    prescriptionItems.value.push({
        drugId: 0,
        quantity: 1,
        dosage: '',
        price: 0
    })
}

const removePrescriptionItem = (index: number) => {
    prescriptionItems.value.splice(index, 1)
}

const onDrugChange = (index: number) => {
    const item = prescriptionItems.value[index]
    const drug = drugs.value.find(d => d.id === item.drugId)
    if (drug) {
        item.price = drug.price
        item.specification = drug.specification
    }
}

const calculateTotalAmount = () => {
    return prescriptionItems.value.reduce((sum, item) => {
        return sum + (item.price || 0) * item.quantity
    }, 0)
}

const handleCreatePrescription = async () => {
    if (prescriptionItems.value.length === 0) {
        ElMessage.warning('请至少添加一个药品')
        return
    }

    if (prescriptionItems.value.some(item => !item.drugId)) {
        ElMessage.warning('请选择药品')
        return
    }

    prescriptionSubmitting.value = true
    try {
        const prescription: Prescription = {
            medicalRecordId: savedMedicalRecordId.value!,
            items: prescriptionItems.value
        }
        
        await prescriptionApi.create(prescription)
        ElMessage.success('处方开具成功')
        prescriptionDialogVisible.value = false
        prescriptionItems.value = []
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '开具处方失败')
    } finally {
        prescriptionSubmitting.value = false
    }
}


const viewHistoryDetail = (record: MedicalRecord) => {
    selectedHistory.value = record
    detailDialogVisible.value = true
}

const formatDate = (date?: string) => {
    if (!date) return '-'
    return new Date(date).toLocaleString('zh-CN')
}

const getGenderLabel = (gender?: number) => {
    const map: Record<number, string> = {
        1: '公',
        2: '母',
        0: '未知'
    }
    return map[gender || 0] || '未知'
}

onMounted(() => {
    // Load current user from localStorage
    const userJson = localStorage.getItem('user')
    if (userJson) {
        currentUser.value = JSON.parse(userJson)
    }
    
    fetchWaitingRegistrations()
})
</script>

<style scoped>
.doctor-workbench {
    padding: 20px;
    min-height: 100vh;
    background: #f5f7fa;
}

.selection-card,
.pet-info-card,
.history-card,
.form-card {
    margin-bottom: 20px;
}

.pet-info {
    padding: 10px 0;
}

.info-item {
    display: flex;
    margin-bottom: 12px;
    line-height: 1.5;
}

.info-item .label {
    width: 80px;
    color: #909399;
    font-size: 14px;
}

.info-item .value {
    flex: 1;
    color: #303133;
    font-size: 14px;
}

.info-item .value.notes {
    color: #e6a23c;
    font-weight: 500;
}

.history-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.history-card {
    max-height: 500px;
    overflow-y: auto;
}

.history-item {
    cursor: pointer;
    transition: all 0.3s;
}

.history-item:hover {
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.history-content {
    font-size: 13px;
}

.history-content .diagnosis {
    margin: 0 0 8px 0;
    color: #303133;
}

.history-content .doctor {
    margin: 0;
    color: #909399;
    font-size: 12px;
}

.form-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.form-card {
    min-height: 600px;
}

.detail-content {
    padding: 10px 0;
}

.prescription-form {
    padding: 10px 0;
}

.total-amount {
    margin-top: 20px;
    text-align: right;
    font-size: 18px;
}

.total-amount .amount {
    color: #f56c6c;
    font-weight: bold;
    font-size: 24px;
    margin-left: 10px;
}
</style>
