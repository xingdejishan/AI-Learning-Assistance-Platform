<script lang="ts" setup>
import { useAppStore } from '@/store/modules/app'
import { useDesign } from '@/hooks/web/useDesign'
import type { VbenLayoutType } from '@/types/layout'

defineOptions({ name: 'LayoutRadioPicker' })

const { t } = useI18n()
const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('layout-radio-picker')

const appStore = useAppStore()

const layout = computed(() => appStore.getLayout)

const layouts: { className: string; label: string; type: VbenLayoutType }[] = [
  {
    className: 'sidebar-nav',
    label: t('setting.vertical'),
    type: 'sidebar-nav'
  },
  {
    className: 'header-nav',
    label: t('setting.horizontal'),
    type: 'header-nav'
  }
]

const allowedLayouts = layouts.map((item) => item.type)

onMounted(() => {
  if (!allowedLayouts.includes(layout.value as VbenLayoutType)) {
    appStore.setLayout('sidebar-nav')
  }
})
</script>

<template>
  <div :class="prefixCls" class="grid grid-cols-2 gap-14px">
    <div
      v-for="item in layouts"
      :key="item.type"
      class="layout-card"
      @click="appStore.setLayout(item.type)"
    >
      <div
        :aria-label="item.label"
        :class="[
          `${prefixCls}__${item.className}`,
          'layout-card__preview',
          {
            'is-active': layout === item.type
          }
        ]"
        :title="item.label"
      >
      </div>
      <span class="layout-card__label">
        {{ item.label }}
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-layout-radio-picker;

.#{$prefix-cls} {
  .layout-card {
    display: flex;
    min-height: 88px;
    cursor: pointer;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }

  .layout-card__preview {
    position: relative;
    width: 76px;
    height: 54px;
    overflow: hidden;
    border: 2px solid #e5e7eb;
    border-radius: 6px;
    background-color: #f3f4f6;
    transition: border-color var(--el-transition-duration);
  }

  .layout-card__label {
    width: 76px;
    color: var(--el-text-color-regular);
    font-size: 12px;
    line-height: 18px;
    text-align: center;
  }

  &__sidebar-nav {
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 28%;
      height: 100%;
      background-color: #273352;
      border-radius: 4px 0 0 4px;
      content: '';
    }

    &::after {
      position: absolute;
      top: 8px;
      right: 8px;
      width: 52%;
      height: 6px;
      background-color: #fff;
      border-radius: 999px;
      content: '';
      box-shadow:
        0 12px 0 #fff,
        0 24px 0 #fff;
    }
  }

  &__header-nav {
    &::before {
      position: absolute;
      top: 0;
      left: 0;
      z-index: 1;
      width: 100%;
      height: 33%;
      background-color: #273352;
      border-radius: 4px 4px 0 0;
      content: '';
    }

    &::after {
      position: absolute;
      right: 8px;
      bottom: 8px;
      width: 58%;
      height: 6px;
      background-color: #fff;
      border-radius: 999px;
      content: '';
      box-shadow:
        0 -12px 0 #fff,
        0 -24px 0 #fff;
    }
  }

  .is-active {
    border-color: var(--el-color-primary);
  }
}
</style>
