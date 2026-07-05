<script lang="ts" setup>
import { useCssVar } from '@vueuse/core'
import { setCssVar } from '@/utils'
import { colorIsDark, hexToRGB, lighten } from '@/utils/color'
import { useAppStore } from '@/store/modules/app'
import { ThemeSwitch } from '@/layout/components/ThemeSwitch'
import ColorRadioPicker from './components/ColorRadioPicker.vue'
import LayoutRadioPicker from './components/LayoutRadioPicker.vue'
import { isHeaderNavLayout } from '@/utils/layout'
import { useSetting } from './useSetting'

defineOptions({ name: 'Setting' })

const { t } = useI18n()
const appStore = useAppStore()
const layout = computed(() => appStore.getLayout)
const { drawerVisible } = useSetting()

// 主题色相关
const themeColor = ref(appStore.getTheme.elColorPrimary || '#409eff')

const setSystemTheme = (color: string) => {
  setCssVar('--el-color-primary', color)
  appStore.setTheme({ elColorPrimary: color })
}

const setHeaderTheme = (color: string) => {
  const isDarkColor = colorIsDark(color)
  const textColor = isDarkColor ? '#fff' : 'inherit'
  const textHoverColor = isDarkColor ? lighten(color!, 6) : '#f6f6f6'
  const topToolBorderColor = isDarkColor ? color : '#eee'
  setCssVar('--top-header-bg-color', color)
  setCssVar('--top-header-text-color', textColor)
  setCssVar('--top-header-hover-color', textHoverColor)
  appStore.setTheme({
    topHeaderBgColor: color,
    topHeaderTextColor: textColor,
    topHeaderHoverColor: textHoverColor,
    topToolBorderColor
  })
  if (isHeaderNavLayout(unref(layout))) {
    setMenuTheme(color)
  }
}

const setMenuTheme = (color: string) => {
  const primaryColor = useCssVar('--el-color-primary', document.documentElement)
  const isDarkColor = colorIsDark(color)
  const theme: Recordable = {
    // 左侧菜单边框颜色
    leftMenuBorderColor: isDarkColor ? 'inherit' : '#eee',
    // 左侧菜单背景颜色
    leftMenuBgColor: color,
    // 左侧菜单浅色背景颜色
    leftMenuBgLightColor: isDarkColor ? lighten(color!, 6) : color,
    // 左侧菜单选中背景颜色
    leftMenuBgActiveColor: isDarkColor
      ? 'var(--el-color-primary)'
      : hexToRGB(unref(primaryColor) || '#409eff', 0.1),
    // 左侧菜单收起选中背景颜色
    leftMenuCollapseBgActiveColor: isDarkColor
      ? 'var(--el-color-primary)'
      : hexToRGB(unref(primaryColor) || '#409eff', 0.1),
    // 左侧菜单字体颜色
    leftMenuTextColor: isDarkColor ? '#bfcbd9' : '#333',
    // 左侧菜单选中字体颜色
    leftMenuTextActiveColor: isDarkColor ? '#fff' : 'var(--el-color-primary)',
    // logo字体颜色
    logoTitleTextColor: isDarkColor ? '#fff' : 'inherit',
    // logo边框颜色
    logoBorderColor: isDarkColor ? color : '#eee'
  }
  appStore.setTheme(theme)
  appStore.setCssVarTheme()
}

// 监听layout变化，重置一些主题色
watch(
  () => layout.value,
  (n) => {
    if (isHeaderNavLayout(n) && !appStore.getIsDark) {
      setHeaderTheme(unref(themeColor))
    } else {
      setMenuTheme(unref(themeColor))
    }
  }
)

const setUnifiedTheme = (color: string) => {
  themeColor.value = color
  setSystemTheme(color)
  setHeaderTheme(color)
  setMenuTheme(color)
}
</script>

<template>
  <ElDrawer v-model="drawerVisible" :z-index="4000" direction="rtl" size="350px">
    <template #header>
      <span class="text-16px font-700">{{ t('setting.projectSetting') }}</span>
    </template>

    <div class="text-center">
      <!-- 主题 -->
      <ElDivider>{{ t('setting.theme') }}</ElDivider>
      <ThemeSwitch />

      <!-- 布局 -->
      <ElDivider>{{ t('setting.layout') }}</ElDivider>
      <LayoutRadioPicker />

      <!-- 主题颜色 -->
      <ElDivider>主题颜色</ElDivider>
      <ColorRadioPicker
        v-model="themeColor"
        :schema="[
          '#409eff',
          '#009688',
          '#536dfe',
          '#ff5c93',
          '#ee4f12',
          '#0096c7',
          '#9c27b0',
          '#ff9800'
        ]"
        @change="setUnifiedTheme"
      />
    </div>
  </ElDrawer>
</template>
