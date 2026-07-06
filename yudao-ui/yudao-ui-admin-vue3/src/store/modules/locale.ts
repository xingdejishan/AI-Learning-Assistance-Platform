import { defineStore } from 'pinia'
import { store } from '../index'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import { LocaleDropdownType } from '@/types/localeDropdown'

export const APP_LOCALE_KEY = 'ai-study-locale'
const supportedLocales: LocaleType[] = ['zh-CN', 'en-US']

const elLocaleMap = {
  'zh-CN': zhCn,
  'en-US': en
}

export const normalizeLocale = (locale?: string | null): LocaleType => {
  if (locale === 'en' || locale === 'en-US') return 'en-US'
  return 'zh-CN'
}

const getStoredLocale = (): LocaleType => {
  const stored = localStorage.getItem(APP_LOCALE_KEY) || localStorage.getItem('lang')
  return normalizeLocale(stored)
}

const persistLocale = (locale: LocaleType) => {
  localStorage.setItem(APP_LOCALE_KEY, locale)
  localStorage.removeItem('lang')
}

interface LocaleState {
  currentLocale: LocaleDropdownType
  localeMap: LocaleDropdownType[]
}

export const useLocaleStore = defineStore('locales', {
  state: (): LocaleState => {
    const locale = getStoredLocale()
    return {
      currentLocale: {
        lang: locale,
        elLocale: elLocaleMap[locale]
      },
      localeMap: [
        {
          lang: 'zh-CN',
          name: '中文'
        },
        {
          lang: 'en-US',
          name: 'English'
        }
      ]
    }
  },
  getters: {
    getCurrentLocale(): LocaleDropdownType {
      return this.currentLocale
    },
    getLocaleMap(): LocaleDropdownType[] {
      return this.localeMap
    }
  },
  actions: {
    setCurrentLocale(localeMap: LocaleDropdownType) {
      const locale = supportedLocales.includes(localeMap?.lang) ? localeMap.lang : 'zh-CN'
      this.currentLocale.lang = locale
      this.currentLocale.elLocale = elLocaleMap[locale]
      persistLocale(locale)
    }
  }
})

export const useLocaleStoreWithOut = () => {
  return useLocaleStore(store)
}
