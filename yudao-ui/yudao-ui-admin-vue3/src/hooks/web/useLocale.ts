import { i18n } from '@/plugins/vueI18n'
import { normalizeLocale, useLocaleStoreWithOut } from '@/store/modules/locale'
import { setHtmlPageLang } from '@/plugins/vueI18n/helper'

const setI18nLanguage = (locale: LocaleType) => {
  const localeStore = useLocaleStoreWithOut()

  if (i18n.mode === 'legacy') {
    i18n.global.locale = locale
  } else {
    ;(i18n.global.locale as any).value = locale
  }
  localeStore.setCurrentLocale({
    lang: locale
  })
  setHtmlPageLang(locale)
}

export const useLocale = () => {
  // Switching the language will change the locale of useI18n
  // And submit to configuration modification
  const changeLocale = async (locale: LocaleType) => {
    const normalizedLocale = normalizeLocale(locale)
    const globalI18n = i18n.global

    const langModule = await import(`../../locales/${normalizedLocale}.ts`)

    globalI18n.setLocaleMessage(normalizedLocale, langModule.default)

    setI18nLanguage(normalizedLocale)
  }

  return {
    changeLocale
  }
}
