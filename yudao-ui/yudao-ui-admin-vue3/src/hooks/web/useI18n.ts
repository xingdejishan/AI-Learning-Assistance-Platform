import { i18n } from '@/plugins/vueI18n'

type I18nGlobalTranslation = {
  (key: string): string
  (key: string, locale: string): string
  (key: string, locale: string, list: unknown[]): string
  (key: string, locale: string, named: Record<string, unknown>): string
  (key: string, list: unknown[]): string
  (key: string, named: Record<string, unknown>): string
}

type I18nTranslationRestParameters = [string, any]

const titleKeyMap: Record<string, string> = {
  首页: 'router.home',
  AI助学系统: 'aiStudy.menu.root',
  AI学习辅助平台: 'aiStudy.menu.root',
  技能树管理: 'aiStudy.menu.skillTree',
  学习记录: 'aiStudy.menu.learningRecord',
  'AI 智能辅导': 'aiStudy.menu.chat',
  简历诊断: 'aiStudy.menu.resume',
  学习数据看板: 'aiStudy.menu.dashboard',
  学习闭环看板: 'aiStudy.menu.learningFlow',
  今日复习: 'aiStudy.menu.review',
  学习画像: 'aiStudy.menu.profile'
}

const getKey = (namespace: string | undefined, key: string) => {
  const mappedKey = titleKeyMap[key] || key
  if (!namespace) {
    return mappedKey
  }
  if (mappedKey.startsWith(namespace)) {
    return mappedKey
  }
  return `${namespace}.${mappedKey}`
}

export const useI18n = (
  namespace?: string
): {
  t: I18nGlobalTranslation
} => {
  const normalFn = {
    t: (key: string) => {
      return getKey(namespace, key)
    }
  }

  if (!i18n) {
    return normalFn
  }

  const { t, ...methods } = i18n.global

  const tFn: I18nGlobalTranslation = (key: string, ...arg: any[]) => {
    if (!key) return ''
    const mappedKey = getKey(namespace, key)
    if (!mappedKey.includes('.') && !namespace) return mappedKey
    //@ts-ignore
    return t(mappedKey, ...(arg as I18nTranslationRestParameters))
  }
  return {
    ...methods,
    t: tFn
  }
}

export const t = (key: string) => key
