<template>
    <div class="inpatient-list">
        <el-card>
            <template #header>
                <div class="card-header">
                    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                        <el-tab-pane label="床位看板" name="board"></el-tab-pane>
                        <el-tab-pane label="住院记录" name="list"></el-tab-pane>
                        <el-tab-pane label="护理记录" name="care"></el-tab-pane>
                    </el-tabs>
                    <div class="legend" v-if="activeTab === 'board'">
                        <span class="legend-item"><span class="dot free"></span>空闲</span>
                        <span class="legend-item"><span class="dot occupied"></span>占用</span>
                    </div>
                </div>
            </template>

            <!-- 住院列表 -->
            <div v-if="activeTab === 'list'" v-loading="listLoading">
                <!-- 搜索表单 -->
                <el-form :inline="true" :model="listSearchForm" class="search-form" style="margin-bottom: 16px;">
                    <el-form-item label="宠物名">
                        <el-input v-model="listSearchForm.petName" placeholder="请输入宠物名" clearable style="width: 160px" />
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="listSearchForm.status" placeholder="全部" clearable style="width: 120px">
                            <el-option label="住院中" value="ACTIVE" />
                            <el-option label="已出院" value="DISCHARGED" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="入院日期">
                        <el-date-picker
                            v-model="listSearchForm.startDate"
                            type="date"
                            placeholder="开始日期"
                            style="width: 150px"
                            value-format="YYYY-MM-DD"
                        />
                    </el-form-item>
                    <el-form-item label="至">
                        <el-date-picker
                            v-model="listSearchForm.endDate"
                            type="date"
                            placeholder="结束日期"
                            style="width: 150px"
                            value-format="YYYY-MM-DD"
                        />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleListSearch">
                            <el-icon><Search /></el-icon> 查询
                        </el-button>
                        <el-button @click="handleListReset">
                            <el-icon><RefreshLeft /></el-icon> 重置
                        </el-button>
                    </el-form-item>
                </el-form>
                
                <el-table :data="recordList" style="width: 100%" stripe>
                    <el-table-column prop="bedNumber" label="床位号" min-width="100" />
                    <el-table-column prop="pet.name" label="宠物" min-width="120" />
                    <el-table-column prop="pet.customer.name" label="主人" min-width="120" />
                    <el-table-column prop="pet.customer.phone" label="电话" min-width="130" />
                    <el-table-column label="入院时间" min-width="180">
                        <template #default="{ row }">
                            {{ formatTime(row.startTime) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="出院时间" min-width="180">
                        <template #default="{ row }">
                            {{ formatTime(row.endTime) || '-' }}
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" min-width="100">
                        <template #default="{ row }">
                            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
                                {{ row.status === 'ACTIVE' ? '住院中' : '已出院' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" fixed="right">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="handleViewDetails(row)">
                                查看详情
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <div style="margin-top: 20px; text-align: left;">
                    <el-pagination
                        v-model:current-page="currentPage"
                        v-model:page-size="pageSize"
                        :total="total"
                        layout="total, prev, pager, next"
                        @current-change="fetchRecords"
                    />
                </div>
            </div>

            <!-- 护理记录列表 -->
            <div v-if="activeTab === 'care'" v-loading="careListLoading">
                <!-- 搜索表单 -->
                <el-form :inline="true" :model="careSearchForm" class="search-form" style="margin-bottom: 16px;">
                    <el-form-item label="宠物名">
                        <el-input v-model="careSearchForm.petName" placeholder="请输入宠物名" clearable style="width: 160px" />
                    </el-form-item>
                    <el-form-item label="执行人">
                        <el-select v-model="careSearchForm.operatorId" placeholder="全部" clearable style="width: 150px">
                            <el-option
                                v-for="doc in doctors"
                                :key="doc.id"
                                :label="doc.name"
                                :value="doc.id"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="护理日期">
                        <el-date-picker
                            v-model="careSearchForm.startDate"
                            type="date"
                            placeholder="开始日期"
                            style="width: 150px"
                            value-format="YYYY-MM-DD"
                        />
                    </el-form-item>
                    <el-form-item label="至">
                        <el-date-picker
                            v-model="careSearchForm.endDate"
                            type="date"
                            placeholder="结束日期"
                            style="width: 150px"
                            value-format="YYYY-MM-DD"
                        />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleCareSearch">
                            <el-icon><Search /></el-icon> 查询
                        </el-button>
                        <el-button @click="handleCareReset">
                            <el-icon><RefreshLeft /></el-icon> 重置
                        </el-button>
                    </el-form-item>
                </el-form>
                
                <el-table :data="careRecordList" style="width: 100%" stripe>
                    <el-table-column prop="recordTime" label="时间" min-width="180">
                        <template #default="{ row }">
                            {{ formatTime(row.recordTime) }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="content" label="护理内容" />
                    <el-table-column prop="operator.name" label="执行人" min-width="120" />
                    <el-table-column prop="hospitalization.pet.name" label="关联宠物" min-width="120" />
                    <el-table-column prop="hospitalization.bedNumber" label="床位" min-width="100" />
                </el-table>

                <div style="margin-top: 20px; text-align: left;">
                    <el-pagination
                        v-model:current-page="careCurrentPage"
                        v-model:page-size="carePageSize"
                        :total="careTotal"
                        layout="total, prev, pager, next"
                        @current-change="fetchCareRecordsGlobal"
                    />
                </div>
            </div>

            <div class="bed-grid" v-if="activeTab === 'board'" v-loading="loading">
                <div
                    v-for="bed in beds"
                    :key="bed.bedNumber"
                    class="bed-card"
                    :class="{ occupied: bed.isOccupied }"
                    @click="handleBedClick(bed)"
                >
                    <div class="bed-number">{{ bed.bedNumber }}</div>
                    <div v-if="bed.isOccupied" class="bed-info">
                        <div class="pet-name">{{ bed.hospitalization?.pet?.name }}</div>
                        <div class="customer-name">{{ bed.hospitalization?.pet?.customer?.name }}</div>
                        <div class="time">{{ formatTime(bed.hospitalization?.startTime) }}</div>
                    </div>
                    <div v-else class="bed-empty">
                        <el-icon><Plus /></el-icon>
                        <div>办理入院</div>
                    </div>
                </div>
            </div>
        </el-card>

        <!-- 办理入院弹窗 -->
        <el-dialog v-model="admitDialogVisible" title="办理入院" width="500px">
            <el-form :model="admitForm" label-width="100px">
                <el-form-item label="床位号">
                    <el-input v-model="currentBedNumber" disabled />
                </el-form-item>
                <el-form-item label="选择宠物" required>
                    <el-select
                        v-model="admitForm.petId"
                        filterable
                        remote
                        placeholder="请输入宠物名搜索"
                        :remote-method="searchPets"
                        :loading="searchLoading"
                    >
                        <el-option
                            v-for="item in petOptions"
                            :key="item.id"
                            :label="item._source === 'queue' ? `【今日就诊】${item.name} (${item.customer?.name})` : `${item.name} (${item.customer?.name})`"
                            :value="item.id"
                        />
                    </el-select>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="admitDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAdmit" :loading="submitting">确定</el-button>
            </template>
        </el-dialog>

        <!-- 住院详情抽屉 -->
        <el-drawer v-model="drawerVisible" title="住院详情" size="50%">
            <div v-if="currentHospitalization" class="drawer-content">
                <el-descriptions title="基本信息" border :column="2">
                    <el-descriptions-item label="床位号">{{ currentHospitalization.bedNumber }}</el-descriptions-item>
                    <el-descriptions-item label="状态">
                        <el-tag type="success">住院中</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="宠物">{{ currentHospitalization.pet?.name }}</el-descriptions-item>
                    <el-descriptions-item label="主人">{{ currentHospitalization.pet?.customer?.name }}</el-descriptions-item>
                    <el-descriptions-item label="联系电话">{{ currentHospitalization.pet?.customer?.phone }}</el-descriptions-item>
                    <el-descriptions-item label="入院时间">{{ formatTime(currentHospitalization.startTime) }}</el-descriptions-item>
                </el-descriptions>

                <div class="care-section">
                    <div class="section-header">
                        <h3>护理记录</h3>
                        <el-button 
                            v-if="currentHospitalization?.status === 'ACTIVE'" 
                            type="primary" 
                            size="small" 
                            @click="showAddCareDialog"
                        >添加记录</el-button>
                    </div>
                    
                    <el-timeline>
                        <el-timeline-item
                            v-for="record in careRecords"
                            :key="record.id"
                            :timestamp="formatTime(record.recordTime)"
                            placement="top"
                        >
                            <el-card class="care-card">
                                <div class="card-header">
                                    <h4>{{ record.operator?.name }}</h4>
                                    <span class="record-time">{{ formatTime(record.recordTime) }}</span>
                                </div>
                                <p>{{ record.content }}</p>
                            </el-card>
                        </el-timeline-item>
                    </el-timeline>
                </div>
            </div>
            <template #footer>
                <div style="flex: auto" v-if="currentHospitalization?.status === 'ACTIVE'">
                    <el-popconfirm title="确定办理出院吗？" @confirm="handleDischarge">
                        <template #reference>
                            <el-button type="danger">办理出院</el-button>
                        </template>
                    </el-popconfirm>
                </div>
            </template>
        </el-drawer>

        <!-- 添加护理记录弹窗 -->
        <el-dialog v-model="careDialogVisible" title="添加护理记录" width="500px">
            <el-form :model="careForm" label-width="80px">
                <el-form-item label="执行人" required>
                    <el-select v-model="careForm.operatorId" placeholder="选择医生/护士" style="width: 100%">
                        <el-option
                            v-for="doc in doctors"
                            :key="doc.id"
                            :label="doc.name"
                            :value="doc.id"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="护理时间" required>
                    <el-date-picker
                        v-model="careForm.recordTime"
                        type="datetime"
                        placeholder="选择护理时间"
                        style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="护理内容" required>
                    <el-input 
                        v-model="careForm.content" 
                        type="textarea" 
                        :rows="4" 
                        placeholder="请输入体温、进食、排便等情况"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="careDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAddCare" :loading="careSubmitting">提交</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Search, RefreshLeft } from '@element-plus/icons-vue'
import { hospitalizationApi, type BedStatus, type Hospitalization } from '@/api/hospitalization'
import { careRecordApi, type CareRecord } from '@/api/careRecord'
import { petApi, type Pet } from '@/api/pet'
import { employeeApi, type Employee } from '@/api/employee'
import { registrationApi } from '@/api/registration'
import dayjs from 'dayjs'

const activeTab = ref('board')
const listLoading = ref(false)
const recordList = ref<Hospitalization[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 全局护理记录分页
const careListLoading = ref(false)
const careRecordList = ref<CareRecord[]>([])
const careCurrentPage = ref(1)
const carePageSize = ref(10)
const careTotal = ref(0)

// 住院记录搜索表单
const listSearchForm = reactive({
    petName: '',
    status: '',
    startDate: '',
    endDate: ''
})

// 护理记录搜索表单
const careSearchForm = reactive({
    petName: '',
    operatorId: undefined as number | undefined,
    startDate: '',
    endDate: ''
})

const loading = ref(false)
const beds = ref<BedStatus[]>([])
const doctors = ref<Employee[]>([])
const admitDialogVisible = ref(false)
const drawerVisible = ref(false)
const submitting = ref(false)

const currentBedNumber = ref('')
const currentHospitalization = ref<Hospitalization | null>(null)

// 宠物搜索
const searchLoading = ref(false)
const petOptions = ref<(Pet & { _source?: string })[]>([])
const admitForm = reactive({
    petId: undefined as number | undefined
})

// 护理记录
const careRecords = ref<CareRecord[]>([])
const careDialogVisible = ref(false)
const careSubmitting = ref(false)
const careForm = reactive({
    content: '',
    operatorId: undefined as number | undefined,
    recordTime: new Date()
})

const fetchBeds = async () => {
    loading.value = true
    try {
        const res = await hospitalizationApi.getBedStatus()
        beds.value = res.data
    } catch (error) {
        ElMessage.error('加载床位失败')
    } finally {
        loading.value = false
    }
}

const handleBedClick = (bed: BedStatus) => {
    if (bed.isOccupied) {
        currentHospitalization.value = bed.hospitalization!
        fetchCareRecords(bed.hospitalization!.id!)
        drawerVisible.value = true
    } else {
        currentBedNumber.value = bed.bedNumber
        admitForm.petId = undefined
        // 获取今日挂号的宠物作为推荐
        fetchQueuePets()
        admitDialogVisible.value = true
    }
}

const fetchQueuePets = async () => {
    searchLoading.value = true
    try {
        const res = await registrationApi.getQueueBoard()
        // 过滤掉已经在住院的宠物
        const hospitalizedPetIds = beds.value
            .filter(b => b.isOccupied && b.hospitalization?.petId)
            .map(b => b.hospitalization!.petId)
            
        // 提取宠物信息并去重
        const pets = new Map()
        res.data.forEach(reg => {
            if (reg.pet && !hospitalizedPetIds.includes(reg.petId)) {
                pets.set(reg.petId, {
                    ...reg.pet,
                    id: reg.petId,
                    customer: reg.customer,
                    _source: 'queue' // 标记来源
                })
            }
        })
        petOptions.value = Array.from(pets.values())
    } catch (error) {
        console.error(error)
    } finally {
        searchLoading.value = false
    }
}

const searchPets = async (query: string) => {
    if (query) {
        searchLoading.value = true
        try {
            const res = await petApi.list({ keyword: query })
            petOptions.value = res.data.records
        } catch (error) {
            console.error(error)
        } finally {
            searchLoading.value = false
        }
    } else {
        // 如果清空搜索，恢复显示今日挂号的宠物
        fetchQueuePets()
    }
}

const handleAdmit = async () => {
    if (!admitForm.petId) {
        ElMessage.warning('请选择宠物')
        return
    }
    
    submitting.value = true
    try {
        await hospitalizationApi.admit({
            petId: admitForm.petId,
            bedNumber: currentBedNumber.value
        })
        ElMessage.success('办理入院成功')
        admitDialogVisible.value = false
        fetchBeds()
    } catch (error: any) {
        ElMessage.error(error.response?.data?.msg || '办理失败')
    } finally {
        submitting.value = false
    }
}

const handleDischarge = async () => {
    try {
        await hospitalizationApi.discharge(currentHospitalization.value!.id!)
        ElMessage.success('办理出院成功')
        drawerVisible.value = false
        fetchBeds()
    } catch (error: any) {
        ElMessage.error('办理失败')
    }
}

// 护理记录相关
const fetchCareRecords = async (hospitalizationId: number) => {
    try {
        const res = await careRecordApi.list(hospitalizationId)
        careRecords.value = res.data
    } catch (error) {
        console.error(error)
    }
}



const formatTime = (time?: string) => {
    return time ? dayjs(time).format('MM-DD HH:mm') : ''
}


// ... existing code ...

const showAddCareDialog = () => {
    careForm.content = ''
    careForm.operatorId = undefined
    careForm.recordTime = new Date()
    careDialogVisible.value = true
}

// ... existing code ...

const handleAddCare = async () => {
    if (!careForm.content || !careForm.operatorId || !careForm.recordTime) {
        ElMessage.warning('请填写完整信息')
        return
    }
    
    careSubmitting.value = true
    try {
        await careRecordApi.add({
            hospitalizationId: currentHospitalization.value!.id!,
            content: careForm.content,
            operatorId: careForm.operatorId,
            recordTime: dayjs(careForm.recordTime).format('YYYY-MM-DD HH:mm:ss')
        })
        ElMessage.success('添加成功')
        careDialogVisible.value = false
        fetchCareRecords(currentHospitalization.value!.id!)
    } catch (error) {
        ElMessage.error('添加失败')
    } finally {
        careSubmitting.value = false
    }
}

const handleTabChange = (val: string) => {
    if (val === 'list') {
        fetchRecords()
    } else if (val === 'care') {
        fetchCareRecordsGlobal()
    } else {
        fetchBeds()
    }
}

const fetchCareRecordsGlobal = async () => {
    careListLoading.value = true
    try {
        const res = await careRecordApi.listAll({
            page: careCurrentPage.value,
            size: carePageSize.value,
            petName: careSearchForm.petName,
            operatorId: careSearchForm.operatorId,
            startDate: careSearchForm.startDate,
            endDate: careSearchForm.endDate
        })
        careRecordList.value = res.data.records
        careTotal.value = res.data.total
    } catch (error) {
        ElMessage.error('加载护理记录失败')
    } finally {
        careListLoading.value = false
    }
}

const handleCareSearch = () => {
    careCurrentPage.value = 1
    fetchCareRecordsGlobal()
}

const handleCareReset = () => {
    careSearchForm.petName = ''
    careSearchForm.operatorId = undefined
    careSearchForm.startDate = ''
    careSearchForm.endDate = ''
    careCurrentPage.value = 1
    fetchCareRecordsGlobal()
}

const fetchRecords = async () => {
    listLoading.value = true
    try {
        const res = await hospitalizationApi.list({
            page: currentPage.value,
            size: pageSize.value,
            petName: listSearchForm.petName,
            status: listSearchForm.status,
            startDate: listSearchForm.startDate,
            endDate: listSearchForm.endDate
        })
        recordList.value = res.data.records
        total.value = res.data.total
    } catch (error) {
        ElMessage.error('加载记录失败')
    } finally {
        listLoading.value = false
    }
}

const handleListSearch = () => {
    currentPage.value = 1
    fetchRecords()
}

const handleListReset = () => {
    listSearchForm.petName = ''
    listSearchForm.status = ''
    listSearchForm.startDate = ''
    listSearchForm.endDate = ''
    currentPage.value = 1
    fetchRecords()
}

const handleViewDetails = (row: Hospitalization) => {
    currentHospitalization.value = row
    fetchCareRecords(row.id!)
    drawerVisible.value = true
}

const fetchDoctors = async () => {
    try {
        const res = await employeeApi.getDoctors()
        doctors.value = res.data
    } catch (error) {
        console.error('获取医生列表失败', error)
    }
}

onMounted(() => {
    fetchBeds()
    fetchDoctors()
})
</script>

<style scoped>
.inpatient-list {
    padding: 20px;
}
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.legend {
    display: flex;
    gap: 15px;
}
.legend-item {
    display: flex;
    align-items: center;
    font-size: 14px;
}
.dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    margin-right: 5px;
}
.dot.free { background-color: #67C23A; }
.dot.occupied { background-color: #F56C6C; }

.bed-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
    margin-top: 20px;
}
.bed-card {
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    height: 120px;
    cursor: pointer;
    position: relative;
    transition: all 0.3s;
    background-color: #f0f9eb; /* 默认绿色背景 */
}
.bed-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}
.bed-card.occupied {
    background-color: #fef0f0; /* 占用红色背景 */
    border-color: #fde2e2;
}
.bed-number {
    position: absolute;
    top: 5px;
    left: 10px;
    font-weight: bold;
    font-size: 16px;
    color: #606266;
}
.bed-empty {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #67C23A;
}
.bed-info {
    padding: 30px 10px 10px;
    text-align: center;
}
.pet-name {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 5px;
}
.customer-name {
    font-size: 12px;
    color: #909399;
}
.time {
    font-size: 12px;
    color: #C0C4CC;
    margin-top: 5px;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20px 0;
}
.care-card {
    padding: 10px;
}
.care-card h4 {
    margin: 0 0 5px 0;
    font-size: 14px;
}
.care-card p {
    margin: 0;
    color: #606266;
}
</style>
