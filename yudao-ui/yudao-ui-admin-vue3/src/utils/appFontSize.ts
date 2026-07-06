import { ElementPlusSize } from '@/types/elementPlus'

export const APP_FONT_SIZE_KEY = 'ai-study-font-size'
const fontSizes: ElementPlusSize[] = ['small', 'default', 'large']

export const normalizeAppFontSize = (size?: string | null): ElementPlusSize => {
  return fontSizes.includes(size as ElementPlusSize) ? (size as ElementPlusSize) : 'default'
}

export const getStoredAppFontSize = (): ElementPlusSize => {
  return normalizeAppFontSize(
    localStorage.getItem(APP_FONT_SIZE_KEY) ||
      localStorage.getItem('currentSize') ||
      localStorage.getItem('componentSize') ||
      localStorage.getItem('size')
  )
}

export const persistAppFontSize = (size: ElementPlusSize) => {
  localStorage.setItem(APP_FONT_SIZE_KEY, normalizeAppFontSize(size))
  localStorage.removeItem('currentSize')
  localStorage.removeItem('componentSize')
  localStorage.removeItem('size')
}

export const applyAppFontSize = (size: ElementPlusSize) => {
  const normalized = normalizeAppFontSize(size)
  const root = document.documentElement
  root.classList.remove('app-font-small', 'app-font-default', 'app-font-large')
  root.classList.add(`app-font-${normalized}`)
  document.body?.setAttribute('data-font-size', normalized)
}
