<script lang="ts" setup>
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'
import routerSearch from '@/components/RouterSearch/index.vue'
import { applyGlobalBrightness } from '@/utils/brightness'
import { applyAppFontSize } from '@/utils/appFontSize'

defineOptions({ name: 'APP' })

const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('app')
const appStore = useAppStore()
const currentSize = computed(() => appStore.getCurrentSize)
const greyMode = computed(() => appStore.getGreyMode)

// 根据浏览器当前主题设置系统主题色
const setDefaultTheme = () => {
  appStore.setIsDark(true)
}
setDefaultTheme()
applyGlobalBrightness()
watch(currentSize, (size) => applyAppFontSize(size), { immediate: true })
</script>
<template>
  <ConfigGlobal :size="currentSize">
    <RouterView :class="greyMode ? `${prefixCls}-grey-mode` : ''" />
    <routerSearch />
  </ConfigGlobal>
</template>
<style lang="scss">
$prefix-cls: #{$namespace}-app;

.size {
  width: 100%;
  height: 100%;
}

html,
body {
  @extend .size;

  padding: 0 !important;
  margin: 0;
  overflow: hidden;
  filter: brightness(var(--app-brightness, 1));

  #app {
    @extend .size;
  }
}

.#{$prefix-cls}-grey-mode {
  filter: grayscale(100%);
}
</style>
