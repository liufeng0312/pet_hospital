<template>
  <div class="workbench-container">
    <!-- Header Greeting & KPIs -->
    <div class="header-section animate-fade-in">
      <div class="greeting-area">
        <h1>{{ greeting }}, <span>{{ currentUser.name || '亲爱的伙伴' }}</span></h1>
        <p class="date-text">{{ currentDate }} · {{ weatherPrompt }}</p>
      </div>
      <div class="kpi-grid">
        <div v-for="kpi in kpis" :key="kpi.label" class="kpi-card glass-morphism">
          <div class="kpi-icon" :style="{ backgroundColor: kpi.color + '20', color: kpi.color }">
            <el-icon><component :is="kpi.icon" /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ kpi.value }}</span>
            <span class="kpi-label">{{ kpi.label }}</span>
          </div>
          <div class="kpi-trend" :class="kpi.trendType">
            <el-icon><CaretTop v-if="kpi.trendType === 'up'" /><CaretBottom v-else /></el-icon>
            {{ kpi.trend }}
          </div>
        </div>
      </div>
    </div>

    <!-- Main Bento Grid -->
    <div class="bento-grid">
      <!-- Row 1: Appointment Trends (Left) & Quick Actions (Right) -->
      <div class="bento-item bento-large glass-morphism animate-slide-up" style="animation-delay: 0.1s">
        <div class="item-header">
          <h3>
            <el-icon style="margin-right: 8px; vertical-align: middle; color: #3b82f6;"><DataLine /></el-icon>
            预约与挂号趋势
          </h3>
          <el-radio-group v-model="chartTimeRange" size="small">
            <el-radio-button label="7d">近7天</el-radio-button>
            <el-radio-button label="30d">近30天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="trendChartRef" class="trend-chart"></div>
      </div>

      <div class="bento-item bento-single glass-morphism animate-slide-up" style="animation-delay: 0.2s">
        <div class="item-header">
          <h3>快捷工作入口</h3>
        </div>
        <div class="actions-grid">
          <div v-for="action in quickActions" :key="action.title" class="action-item" @click="$router.push(action.path)">
            <div class="action-icon" :style="{ background: action.gradient }">
              <el-icon><component :is="action.icon" /></el-icon>
            </div>
            <span>{{ action.title }}</span>
          </div>
        </div>
      </div>

      <!-- Row 2: Recent Activity (Left) & Pending Tasks (Right) -->
      <div class="bento-item bento-medium glass-morphism animate-slide-up" style="animation-delay: 0.3s">
        <div class="item-header">
          <h3>
            <el-icon style="margin-right: 8px; vertical-align: middle; color: #8b5cf6;"><Notification /></el-icon>
            最近动态
          </h3>
          <el-button link type="primary" @click="$router.push('/registrations')">查看全部</el-button>
        </div>
        <div class="activity-list">
          <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
            <div class="activity-dot" :style="{ backgroundColor: activity.color }"></div>
            <div class="activity-content">
              <p class="activity-text">{{ activity.text }}</p>
              <span class="activity-time">{{ activity.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-item bento-medium glass-morphism animate-slide-up" style="animation-delay: 0.4s">
        <div class="item-header">
          <h3>
            <el-icon style="margin-right: 8px; vertical-align: middle; color: #f59e0b;"><CircleCheck /></el-icon>
            智能提醒
          </h3>
          <el-tag size="small" type="warning" effect="light">{{ todoItems.length }}条提醒</el-tag>
        </div>
        <div class="todo-list">
          <div v-for="(todo, index) in todoItems" :key="index" class="todo-item" @click="$router.push('/reminders')">
            <div class="todo-content">
              <span :class="{ 'todo-done': todo.done }">{{ todo.text }}</span>
              <span class="todo-tag" :style="{ color: todo.tagColor, backgroundColor: todo.tagColor + '10' }">{{ todo.tag }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="bento-item bento-medium glass-morphism animate-slide-up" style="animation-delay: 0.5s">
        <div class="item-header">
          <h3>健康知识库</h3>
          <el-icon color="#94a3b8"><InfoFilled /></el-icon>
        </div>
        <div class="knowledge-card" v-if="healthTip">
          <div class="knowledge-thumb">
            
            <!-- if cover image exists shows image instead -->
            <img v-if="healthTip.coverImage" :src="healthTip.coverImage" style="width:100%; height:100%; object-fit:cover; border-radius:16px;" />
          </div>
          <p class="knowledge-title">{{ healthTip.title }}</p>
          <p class="knowledge-desc">{{ healthTip.summary || '暂无摘要' }}</p>
          <el-button link type="primary" size="small" @click="$router.push('/articles')">阅读更多</el-button>
        </div>
        <div class="knowledge-card" v-else>
          <p class="knowledge-desc">暂无健康资讯，请在系统配置中添加。</p>
        </div>
      </div>

      <!-- Footer: Status Bar -->
      <div class="bento-item bento-full glass-morphism animate-slide-up" style="animation-delay: 0.6s">
        <div class="status-bar">
          <div class="status-info">
            <div class="status-pulse"></div>
            <span class="status-text">系统全模块运行良好 · 最后同步: {{ lastSyncTime }}</span>
          </div>
          <div class="pro-tip">
            <el-icon><Lightbulb /></el-icon>
            <span>小技巧: 使用 `Ctrl + F` 可以快速搜索病历库。</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import {
  Tickets,
  FirstAidKit,
  Money,
  DataLine,
  User,
  Wallet,
  PriceTag,
  CaretTop,
  CaretBottom,
  InfoFilled,
  Notification,
  CircleCheck,
  MagicStick,
  Opportunity as Lightbulb,
  Loading
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { statisticsApi, type DashboardVO } from '@/api/statistics'

const currentUser = ref<any>({})
const chartTimeRange = ref('7d')
const trendChartRef = ref<HTMLElement | null>(null)
let myChart: echarts.ECharts | null = null

const currentDate = new Date().toLocaleDateString('zh-CN', { 
  year: 'numeric', 
  month: 'long', 
  day: 'numeric',
  weekday: 'long' 
})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早安'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
})

const weatherPrompt = ref('今日气候宜人，适合进行日常诊疗。')
const lastSyncTime = ref(new Date().toLocaleTimeString())

// Real Data State
const dashboardData = ref<DashboardVO>({
  kpis: { todayRegistrations: 0, inProgressPets: 0, pendingBills: 0, yesterdayRevenue: 0 },
  trendChart: { dates: [], total: [], appointment: [] },
  activities: [],
  todos: [],
  healthTip: undefined
})

const kpis = computed(() => [
  { label: '今日挂号', value: dashboardData.value.kpis.todayRegistrations.toString(), icon: Tickets, color: '#3b82f6', trend: '-', trendType: 'up' },
  { label: '在诊宠物', value: dashboardData.value.kpis.inProgressPets.toString(), icon: FirstAidKit, color: '#10b981', trend: '-', trendType: 'up' },
  { label: '待处理账单', value: dashboardData.value.kpis.pendingBills.toString(), icon: Wallet, color: '#f59e0b', trend: '-', trendType: 'down' },
  { label: '昨日营收', value: '¥' + dashboardData.value.kpis.yesterdayRevenue.toString(), icon: Money, color: '#8b5cf6', trend: '-', trendType: 'up' }
])

const quickActions = [
  { title: '快速挂号', icon: Tickets, path: '/registrations', gradient: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%)' },
  { title: '医生接诊', icon: FirstAidKit, path: '/doctor-workbench', gradient: 'linear-gradient(135deg, #34d399 0%, #10b981 100%)' },
  { title: '收银结算', icon: Money, path: '/payment', gradient: 'linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%)' },
  { title: '经营报表', icon: DataLine, path: '/reports', gradient: 'linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%)' },
  { title: '库存检查', icon: PriceTag, path: '/drugs', gradient: 'linear-gradient(135deg, #f87171 0%, #ef4444 100%)' },
  { title: '档案中心', icon: User, path: '/pets', gradient: 'linear-gradient(135deg, #818cf8 0%, #6366f1 100%)' }
]

const recentActivities = computed(() => dashboardData.value.activities)
const todoItems = computed(() => dashboardData.value.todos)
const healthTip = computed(() => dashboardData.value.healthTip)

const initChart = () => {
  if (!trendChartRef.value) return
  myChart = echarts.init(trendChartRef.value)
  
  const dates = dashboardData.value.trendChart.dates
  const totalData = dashboardData.value.trendChart.total
  const appData = dashboardData.value.trendChart.appointment
  
  const option = {
    tooltip: { 
      trigger: 'axis', 
      backgroundColor: 'rgba(255, 255, 255, 0.9)', 
      borderRadius: 12, 
      padding: 12,
      textStyle: { color: '#1e293b' },
      borderWidth: 0,
      shadowBlur: 10,
      shadowColor: 'rgba(0,0,0,0.1)'
    },
    grid: { left: '20', right: '20', bottom: '10', top: '30', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: { lineStyle: { color: '#e2e8f0' } },
      axisLabel: { color: '#94a3b8', fontSize: 11 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisLabel: { color: '#94a3b8', fontSize: 11 },
      splitLine: { lineStyle: { color: '#f1f5f9', type: 'dashed' } }
    },
    series: [
      {
        name: '挂号总量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: totalData,
        itemStyle: { color: '#3b82f6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.2)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0)' }
          ])
        }
      },
      {
        name: '预约量',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: appData,
        itemStyle: { color: '#10b981' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.2)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0)' }
          ])
        }
      }
    ]
  }
  myChart.setOption(option)
}

const fetchDashboardData = async () => {
  try {
    const res = await statisticsApi.getDashboard()
    if (res.data) {
      dashboardData.value = res.data
      initChart()
      lastSyncTime.value = new Date().toLocaleTimeString()
    }
  } catch (error) {
    console.error('Fetch dashboard failed', error)
  }
}

onMounted(() => {
  const userJson = localStorage.getItem('user')
  if (userJson) currentUser.value = JSON.parse(userJson)
  
  fetchDashboardData()
  window.addEventListener('resize', () => myChart?.resize())
})

onUnmounted(() => {
  window.removeEventListener('resize', () => myChart?.resize())
})
</script>

<style scoped>
.workbench-container {
  max-width: 1400px;
  margin: 0 auto;
  padding-bottom: 40px;
}

/* Glassmorphism Core */
.glass-morphism {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 10px 40px -10px rgba(0, 0, 0, 0.05);
  border-radius: 28px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.glass-morphism:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 50px -15px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.6);
}

/* Header Section */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  gap: 24px;
}

.greeting-area h1 {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.greeting-area h1 span {
  background: linear-gradient(135deg, #1e293b 0%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.date-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.kpi-grid {
  display: flex;
  gap: 16px;
  flex-grow: 1;
  justify-content: flex-end;
}

.kpi-card {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 200px;
  position: relative;
}

.kpi-icon {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}

.kpi-info {
  display: flex;
  flex-direction: column;
}

.kpi-value {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
}

.kpi-label {
  font-size: 13px;
  color: #64748b;
  font-weight: 600;
}

.kpi-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
  font-weight: 700;
}

.kpi-trend.up { color: #10b981; }
.kpi-trend.down { color: #ef4444; }

/* Bento Grid */
.bento-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.bento-item {
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.bento-large {
  grid-column: span 2;
  height: 420px;
}

.bento-single {
  grid-column: span 1;
  height: 420px;
}

.bento-medium {
  grid-column: span 1;
  min-height: 280px;
}

.bento-full {
  grid-column: span 3;
  padding: 20px 32px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.item-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
}

.trend-chart {
  height: 330px;
  width: 100%;
}

/* Actions Grid */
.actions-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  flex-grow: 1;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 18px;
  padding: 12px 8px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s;
}

.action-item:hover {
  background: white;
  transform: translateY(-4px);
  box-shadow: 0 10px 25px -10px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-item span {
  font-size: 13px;
  font-weight: 700;
  color: #334155;
}

/* Activity List */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.activity-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.activity-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
  border: 2px solid white;
  box-shadow: 0 0 0 1px rgba(0,0,0,0.05);
}

.activity-text {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
  line-height: 1.5;
  margin-bottom: 2px;
}

.activity-time {
  font-size: 12px;
  color: #94a3b8;
  font-weight: 500;
}

/* Todo List */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s;
}

.todo-item:hover {
  background: rgba(255, 255, 255, 0.6);
}

.todo-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.todo-content span {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.todo-done {
  text-decoration: line-through;
  color: #94a3b8 !important;
}

.todo-tag {
  font-size: 10px;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 6px;
  align-self: flex-start;
}

/* Knowledge Card */
.knowledge-card {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(139, 92, 246, 0.05) 100%);
  border-radius: 20px;
  padding: 20px;
  height: 100%;
}

.knowledge-thumb {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 8px 16px -4px rgba(59, 130, 246, 0.1);
}

.knowledge-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
}

.knowledge-desc {
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 12px;
}

/* Status Bar */
.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.status-pulse {
  width: 10px;
  height: 10px;
  background: #10b981;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
  70% { transform: scale(1); box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

.status-text {
  font-size: 13px;
  color: #475569;
  font-weight: 600;
}

.pro-tip {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #3b82f6;
  font-size: 13px;
  font-weight: 600;
  background: rgba(59, 130, 246, 0.1);
  padding: 8px 16px;
  border-radius: 12px;
}

/* Animations */
.animate-fade-in {
  animation: fadeIn 0.8s ease-out forwards;
}

.animate-slide-up {
  opacity: 0;
  animation: slideUp 0.6s ease-out forwards;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 1200px) {
  .bento-grid {
    grid-template-columns: 1fr;
  }
  .bento-large, .bento-medium, .bento-full {
    grid-column: span 1;
  }
  .header-section {
    flex-direction: column;
    align-items: flex-start;
  }
  .kpi-grid {
    justify-content: flex-start;
    width: 100%;
    overflow-x: auto;
    padding-bottom: 8px;
  }
}
</style>
