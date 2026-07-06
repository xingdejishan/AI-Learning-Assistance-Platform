<script lang="ts" setup>
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'

defineOptions({ name: 'Footer' })

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('footer')

const appStore = useAppStore()

const title = computed(() => appStore.getTitle)

// 添加当前年份计算属性
const currentYear = computed(() => new Date().getFullYear())
</script>

<template>
  <div
    :class="prefixCls"
    class="h-[var(--app-footer-height)] bg-[var(--app-content-bg-color)] text-center leading-[var(--app-footer-height)] text-[var(--el-text-color-placeholder)] dark:bg-[var(--el-bg-color)] overflow-hidden pixel-footer"
  >
    <span>Copyright ©{{ currentYear }} {{ title }}</span>
  </div>
</template>

<style scoped>
.pixel-footer {
  font-family: var(--app-pixel-font);
  font-size: var(--app-font-size-small);
  background:
    linear-gradient(90deg, rgb(122 162 255 / 6%), rgb(255 183 220 / 5%)),
    var(--app-content-bg-color);
  border-top: 1px solid var(--app-border);
}
</style>
