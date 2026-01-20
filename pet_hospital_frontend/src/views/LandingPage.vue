<template>
  <div class="min-h-screen bg-slate-50 font-body text-slate-900">
    <!-- Navigation -->
    <nav :class="['fixed top-0 left-0 right-0 z-50 transition-all duration-300 px-6 py-4', 
                 scrolled ? 'bg-white/80 backdrop-blur-md shadow-sm xl:px-32' : 'bg-transparent xl:px-32']">
      <div class="max-w-7xl mx-auto flex justify-between items-center">
        <div class="flex items-center gap-2 cursor-pointer" @click="scrollTo('hero')">
          <div class="bg-primary p-2 rounded-xl shadow-lg shadow-primary/20">
            <el-icon :size="24" color="white"><FirstAidKit /></el-icon>
          </div>
          <span class="text-xl font-heading font-extrabold bg-gradient-to-r from-slate-900 to-primary bg-clip-text text-transparent italic">
            PET CARE+
          </span>
        </div>
        
        <div class="hidden md:flex items-center gap-8 font-semibold text-slate-600">
          <a @click="scrollTo('services')" class="hover:text-primary transition-colors cursor-pointer">医疗服务</a>
          <a @click="scrollTo('about')" class="hover:text-primary transition-colors cursor-pointer">关于我们</a>
          <a @click="scrollTo('team')" class="hover:text-primary transition-colors cursor-pointer">专家团队</a>
          <a @click="scrollTo('contact')" class="hover:text-primary transition-colors cursor-pointer">联系我们</a>
        </div>

        <div class="flex items-center gap-4">
          <button @click="$router.push('/login')" 
                  class="bg-primary text-white px-6 py-2.5 rounded-full font-bold shadow-lg shadow-primary/30 hover:shadow-primary/40 hover:-translate-y-0.5 transition-all">
            管理入口
          </button>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section id="hero" class="relative pt-32 pb-20 overflow-hidden">
      <!-- Background Elements -->
      <div class="absolute top-0 right-0 -z-10 w-1/2 h-full bg-gradient-to-l from-blue-50 to-transparent"></div>
      <div class="absolute -top-24 -right-24 w-96 h-96 bg-primary/5 rounded-full blur-3xl"></div>
      
      <div class="max-w-7xl mx-auto px-6 grid md:grid-cols-2 gap-12 items-center">
        <div class="space-y-8 animate-slide-up">
          <div class="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary/10 text-primary text-sm font-bold border border-primary/20">
            <span class="flex h-2 w-2 rounded-full bg-primary animate-pulse"></span>
            专业、温情、全天候
          </div>
          <h1 class="text-5xl md:text-6xl font-heading font-extrabold leading-tight text-slate-900">
            给宠物<span class="text-primary italic">最好的</span><br/>医疗与关怀
          </h1>
          <p class="text-lg text-slate-600 leading-relaxed max-w-lg">
            我们不仅仅是一家医院，更是您宠物的健康守护者。拥有顶尖的医疗团队与最先进的设备，为每一份信任提供最专业的保障。
          </p>
          <div class="flex flex-wrap gap-4">
            <button @click="scrollTo('contact')" 
                    class="bg-primary text-white px-8 py-4 rounded-full text-lg font-bold shadow-xl shadow-primary/20 hover:shadow-primary/30 hover:-translate-y-1 transition-all flex items-center gap-2 group">
              立即预约 
              <el-icon class="group-hover:translate-x-1 transition-transform"><Calendar /></el-icon>
            </button>
            <button @click="scrollTo('services')" 
                    class="bg-white text-slate-900 border border-slate-200 px-8 py-4 rounded-full text-lg font-bold hover:bg-slate-50 transition-all">
              了解服务
            </button>
          </div>
          
          <!-- Trust Badges -->
          <div class="flex items-center gap-8 pt-4 border-t border-slate-100">
            <div class="flex -space-x-3">
              <div v-for="i in 4" :key="i" class="w-10 h-10 rounded-full border-2 border-white bg-slate-200 shadow-sm overflow-hidden">
                <img :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${i}`" alt="User">
              </div>
              <div class="w-10 h-10 rounded-full border-2 border-white bg-primary flex items-center justify-center text-[10px] text-white font-bold">
                +1k
              </div>
            </div>
            <div class="text-sm">
              <p class="font-bold text-slate-900">5,000+ 宠物主人的选择</p>
              <div class="flex gap-0.5 text-orange-400">
                <el-icon v-for="i in 5" :key="i"><StarFilled /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <div class="relative animate-fade-in">
          <div class="relative z-10 rounded-[2.5rem] overflow-hidden shadow-2xl rotate-2">
             <img src="https://images.unsplash.com/photo-1583337130417-3346a1be7dee?q=80&w=1000&auto=format&fit=crop" 
                  alt="Happy Dog" 
                  class="w-full h-[500px] object-cover">
          </div>
          <!-- Floating Cards -->
          <div class="absolute -left-12 bottom-12 z-20 bg-white/90 backdrop-blur p-4 rounded-2xl shadow-xl flex items-center gap-4 animate-bounce-slow">
            <div class="bg-emerald-500 p-2 rounded-xl">
              <el-icon color="white" :size="20"><FirstAidKit /></el-icon>
            </div>
            <div>
              <p class="text-xs text-slate-500 font-medium">急诊响应</p>
              <p class="text-sm font-bold text-slate-900">24/7 全天候待命</p>
            </div>
          </div>

          <div class="absolute -right-8 top-12 z-20 bg-white/90 backdrop-blur p-4 rounded-2xl shadow-xl flex items-center gap-4 animate-bounce-slow" style="animation-delay: -1.5s">
            <div class="bg-primary p-2 rounded-xl">
              <el-icon color="white" :size="20"><User /></el-icon>
            </div>
            <div>
              <p class="text-xs text-slate-500 font-medium">专家坐诊</p>
              <p class="text-sm font-bold text-slate-900">资深兽医师团队</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="py-20 bg-primary/5">
      <div class="max-w-7xl mx-auto px-6 grid grid-cols-2 lg:grid-cols-4 gap-8">
        <div v-for="stat in stats" :key="stat.label" class="text-center group">
          <p class="text-4xl font-heading font-black text-primary mb-2 group-hover:scale-110 transition-transform duration-500">{{ stat.value }}</p>
          <p class="text-slate-500 font-semibold uppercase tracking-wider text-xs">{{ stat.label }}</p>
        </div>
      </div>
    </section>

    <!-- Services Section -->
    <section id="services" class="py-24 max-w-7xl mx-auto px-6">
      <div class="text-center space-y-4 mb-16">
        <h2 class="text-primary font-bold tracking-widest uppercase text-sm">我们的服务</h2>
        <p class="text-4xl font-heading font-extrabold text-slate-900 underline decoration-primary/20 decoration-8 underline-offset-8">
          全方位的健康保障
        </p>
      </div>

      <div class="grid md:grid-cols-3 gap-8">
        <div v-for="service in services" :key="service.title" 
             class="group p-8 rounded-3xl bg-white border border-slate-100 shadow-sm hover:shadow-xl hover:-translate-y-2 hover:border-primary/20 transition-all duration-300">
          <div class="w-16 h-16 rounded-2xl bg-primary/5 flex items-center justify-center mb-6 group-hover:bg-primary transition-colors duration-300">
            <el-icon :size="32" class="text-primary group-hover:text-white"><component :is="service.icon" /></el-icon>
          </div>
          <h3 class="text-xl font-bold text-slate-900 mb-4">{{ service.title }}</h3>
          <p class="text-slate-600 leading-relaxed mb-6">{{ service.desc }}</p>
          <a class="text-primary font-bold flex items-center gap-2 group/link cursor-pointer">
            了解详情 <el-icon class="group-hover/link:translate-x-1 transition-transform"><Right /></el-icon>
          </a>
        </div>
      </div>
    </section>

    <!-- Call to Action -->
    <section class="py-20 px-6">
      <div class="max-w-7xl mx-auto rounded-[3rem] bg-slate-900 overflow-hidden relative">
        <div class="absolute inset-0 opacity-20 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')]"></div>
        <div class="relative z-10 px-8 py-16 md:px-20 text-center md:text-left grid md:grid-cols-2 items-center gap-12">
          <div class="space-y-6">
            <h2 class="text-4xl md:text-5xl font-heading font-black text-white leading-tight">
              准备好为您的爱宠<br/><span class="text-primary">预约检查</span>了吗？
            </h2>
            <p class="text-slate-400 text-lg">
              加入我们的宠物健康计划，享受定期的健康提醒与专家随访服务。
            </p>
            <div class="flex flex-wrap gap-4 justify-center md:justify-start">
              <button @click="scrollTo('contact')" class="bg-primary text-white px-8 py-4 rounded-full font-bold shadow-2xl shadow-primary/40 hover:-translate-y-1 transition-all">
                立即开始预约
              </button>
              <div class="flex items-center gap-4 text-white">
                <div class="w-12 h-12 rounded-full bg-white/10 flex items-center justify-center">
                  <el-icon :size="20"><Phone /></el-icon>
                </div>
                <div>
                  <p class="text-xs text-slate-400">客服电话</p>
                  <p class="text-lg font-bold">400-123-4567</p>
                </div>
              </div>
            </div>
          </div>
          <div class="hidden md:block">
             <img src="https://images.unsplash.com/photo-1548199973-03cce0bbc87b?q=80&w=1000&auto=format&fit=crop" 
                  alt="Caring" 
                  class="rounded-3xl shadow-2xl transform rotate-3">
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="bg-white border-t border-slate-100 pt-20 pb-10">
      <div class="max-w-7xl mx-auto px-6 grid md:grid-cols-4 gap-12 mb-20">
        <div class="col-span-1 md:col-span-2 space-y-6">
          <div class="flex items-center gap-2">
            <div class="bg-primary p-1.5 rounded-lg">
              <el-icon :size="20" color="white"><FirstAidKit /></el-icon>
            </div>
            <span class="text-xl font-heading font-extrabold text-slate-900 tracking-tight">PET CARE+</span>
          </div>
          <p class="text-slate-500 max-w-sm leading-relaxed">
            致力于用科技与温情，构建人宠和谐共处的美好家园。我们提供全链条的宠物医疗解决方案。
          </p>
          <div class="flex gap-4">
             <div v-for="i in 4" :key="i" class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 hover:bg-primary hover:text-white transition-all cursor-pointer">
               <el-icon><Share /></el-icon>
             </div>
          </div>
        </div>
        
        <div>
          <h4 class="font-bold text-slate-900 mb-6">快速链接</h4>
          <ul class="space-y-4 text-slate-500">
            <li><a class="hover:text-primary transition-colors cursor-pointer">服务项目</a></li>
            <li><a class="hover:text-primary transition-colors cursor-pointer">专家团队</a></li>
            <li><a class="hover:text-primary transition-colors cursor-pointer">健康科普</a></li>
            <li><a class="hover:text-primary transition-colors cursor-pointer">关于我们</a></li>
          </ul>
        </div>

        <div>
           <h4 class="font-bold text-slate-900 mb-6">联系我们</h4>
           <ul class="space-y-4 text-slate-500 text-sm">
             <li class="flex items-start gap-3">
               <el-icon class="mt-1 text-primary"><Location /></el-icon>
               <span>上海市浦东新区张江高科技园区<br/>爱宠路888号</span>
             </li>
             <li class="flex items-center gap-3">
               <el-icon class="text-primary"><Message /></el-icon>
               <span>contact@petcare-plus.com</span>
             </li>
           </ul>
        </div>
      </div>
      
      <div class="max-w-7xl mx-auto px-6 pt-8 border-t border-slate-50 text-center md:text-left md:flex justify-between text-slate-400 text-sm font-medium">
        <p>© 2026 Pet Care+ 宠物医院版权所有</p>
        <div class="flex gap-8 justify-center mt-4 md:mt-0">
          <a class="hover:text-slate-900 transition-colors cursor-pointer">隐私政策</a>
          <a class="hover:text-slate-900 transition-colors cursor-pointer">服务协议</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import {
  FirstAidKit,
  Calendar,
  User,
  StarFilled,
  Right,
  Phone,
  Location,
  Message,
  Share,
  Ticket,
  Van,
  Odometer,
  Service
} from '@element-plus/icons-vue'

const scrolled = ref(false)

const stats = [
  { label: '服务宠物', value: '15,000+' },
  { label: '专家团队', value: '50+' },
  { label: '成功手术', value: '3,200+' },
  { label: '客户好评', value: '99.8%' }
]

const services = [
  { 
    title: '全科医疗', 
    desc: '从幼宠咨询到老年病管理，我们提供覆盖宠物全生命周期的全科医疗服务。',
    icon: Ticket 
  },
  { 
    title: '软组织与骨科手术', 
    desc: '配备顶级数字化手术室与麻醉监控系统，由资深专家主刀，确保手术安全。',
    icon: FirstAidKit 
  },
  { 
    title: '重症监护 (ICU)', 
    desc: '24小时恒温吸氧监护室，专业护理团队轮班轮守，贴心照料每一位患病宠物。',
    icon: Van 
  },
  { 
    title: '高端影像诊断', 
    desc: '进口数字化DR、超声波检查，提供精准、快速的影像分析报告。',
    icon: Odometer 
  },
  { 
    title: '宠物预防医学', 
    desc: '疫苗注射、内外驱虫、年度体检计划，预防胜于治疗。',
    icon: Service 
  },
  { 
    title: '行为与健康咨询', 
    desc: '专业营养师与调教师提供性格纠正与营养配餐建议。',
    icon: User 
  }
]

const scrollTo = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

const handleScroll = () => {
  scrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Open+Sans:wght@300;400;500;600;700&family=Poppins:wght@400;500;600;700&display=swap');

.font-heading {
  font-family: 'Poppins', sans-serif;
}

.font-body {
  font-family: 'Open Sans', sans-serif;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes bounce-slow {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.animate-fade-in {
  animation: fadeIn 1s ease-out forwards;
}

.animate-slide-up {
  animation: slideUp 0.8s ease-out forwards;
}

.animate-bounce-slow {
  animation: bounce-slow 4s ease-in-out infinite;
}

/* Custom button focus states */
button:focus {
  outline: 2px solid theme('colors.primary');
  outline-offset: 2px;
}
</style>
