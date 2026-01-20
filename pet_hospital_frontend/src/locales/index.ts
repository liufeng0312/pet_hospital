import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'

const messages = {
    'zh': zhCN,
    'en': enUS
}

const i18n = createI18n({
    legacy: false, // 使用 Composition API 模式
    locale: localStorage.getItem('language') || 'zh', // 默认语言
    fallbackLocale: 'zh', // 后备语言
    messages
})

export default i18n
