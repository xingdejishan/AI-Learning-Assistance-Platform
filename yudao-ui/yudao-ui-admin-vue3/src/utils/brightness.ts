const BRIGHTNESS_KEY = 'ai-study-brightness'
export const DEFAULT_BRIGHTNESS = 100
export const MIN_BRIGHTNESS = 50
export const MAX_BRIGHTNESS = 120

const clampBrightness = (value: number) =>
  Math.min(MAX_BRIGHTNESS, Math.max(MIN_BRIGHTNESS, value || DEFAULT_BRIGHTNESS))

export const getStoredBrightness = () => {
  if (typeof window === 'undefined') return DEFAULT_BRIGHTNESS
  const value = Number(window.localStorage.getItem(BRIGHTNESS_KEY))
  return clampBrightness(Number.isFinite(value) ? value : DEFAULT_BRIGHTNESS)
}

export const applyGlobalBrightness = (value = getStoredBrightness()) => {
  if (typeof window === 'undefined') return
  const brightness = clampBrightness(value)
  document.documentElement.style.setProperty('--app-brightness', String(brightness / 100))
  window.localStorage.setItem(BRIGHTNESS_KEY, String(brightness))
}
