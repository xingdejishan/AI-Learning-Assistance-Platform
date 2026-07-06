<template>
  <div class="ai-home">
    <el-card shadow="never" class="ai-home__hero">
      <div class="ai-home__hero-main">
        <div>
          <div class="ai-home__eyebrow">{{ t('aiStudy.home.platform') }}</div>
          <h1>{{ t('aiStudy.home.title') }}</h1>
          <p>{{ t('aiStudy.home.subtitle') }}</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="12" class="mt-12px">
      <el-col
        v-for="item in metrics"
        :key="item.name"
        :xl="metricsColSpan"
        :lg="metricsColSpan"
        :md="12"
        :sm="12"
        :xs="24"
      >
        <el-card shadow="never" class="ai-home__metric">
          <div class="ai-home__metric-icon" :style="{ color: item.color, background: item.bg }">
            <Icon :icon="item.icon" :size="24" />
          </div>
          <div>
            <div class="ai-home__metric-label">{{ item.name }}</div>
            <div class="ai-home__metric-value">
              <CountTo :start-val="0" :end-val="item.value" :duration="1800" />
              <span v-if="item.suffix">{{ item.suffix }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :xl="16" :lg="16" :md="24" :sm="24" :xs="24">
        <div class="dashboard-chart-card">
          <div class="dashboard-chart-header">
            <span class="dashboard-chart-marker"></span>
            <span class="dashboard-chart-title">{{ t('aiStudy.home.studyTrend') }}</span>
          </div>

          <div class="dashboard-chart-body">
            <Echart class="dashboard-chart" height="100%" :options="studyTrendOptions" />
          </div>
        </div>
      </el-col>

      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24" class="lt-lg:mt-12px">
        <div class="dashboard-chart-card">
          <div class="dashboard-chart-header">
            <span class="dashboard-chart-marker"></span>
            <span class="dashboard-chart-title">{{ t('aiStudy.home.skillDistribution') }}</span>
          </div>

          <div class="dashboard-chart-body">
            <Echart class="dashboard-chart" height="100%" :options="skillDistributionOptions" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :span="24">
        <div class="dashboard-chart-card">
          <div class="dashboard-chart-header">
            <span class="dashboard-chart-marker"></span>
            <span class="dashboard-chart-title">{{ t('aiStudy.home.aiResumeStats') }}</span>
          </div>

          <div class="dashboard-chart-body">
            <Echart class="dashboard-chart" height="100%" :options="aiSupportOptions" />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import type { EChartsOption } from 'echarts'

defineOptions({ name: 'Index' })

const { t } = useI18n()

const themeColor = (name: string, fallback: string) => {
  if (typeof window === 'undefined') return fallback
  return getComputedStyle(document.documentElement).getPropertyValue(name).trim() || fallback
}

const metrics = computed(() => [
  {
    name: t('aiStudy.home.studentTotal'),
    value: 2846,
    icon: 'ep:school',
    color: 'var(--app-success)',
    bg: 'var(--app-success-soft)'
  },
  {
    name: t('aiStudy.home.todayRecords'),
    value: 126,
    icon: 'ep:reading',
    color: 'var(--app-primary)',
    bg: 'var(--app-primary-soft)'
  },
  {
    name: t('aiStudy.home.aiChats'),
    value: 482,
    icon: 'tabler:brain',
    color: 'var(--app-accent)',
    bg: 'var(--app-accent-soft)'
  },
  {
    name: t('aiStudy.home.resumeDiagnosis'),
    value: 78,
    icon: 'ep:document-checked',
    color: 'var(--app-success)',
    bg: 'var(--app-success-soft)'
  },
  {
    name: t('aiStudy.home.skillCompletion'),
    value: 68,
    suffix: '%',
    icon: 'ep:trend-charts',
    color: 'var(--app-warning)',
    bg: 'rgb(255 209 102 / 14%)'
  }
])

const metricsColSpan = computed(() => 24 / metrics.value.length)

const weekdays = computed(() => [
  t('aiStudy.home.monday'),
  t('aiStudy.home.tuesday'),
  t('aiStudy.home.wednesday'),
  t('aiStudy.home.thursday'),
  t('aiStudy.home.friday'),
  t('aiStudy.home.saturday'),
  t('aiStudy.home.sunday')
])

const studyTrendOptions = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 20, right: 20, top: 20, bottom: 20, containLabel: true },
  xAxis: {
    type: 'category',
    data: weekdays.value,
    axisTick: { show: false }
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: t('aiStudy.home.studyDuration'),
      type: 'line',
      smooth: true,
      data: [120, 150, 132, 178, 210, 186, 225],
      areaStyle: { opacity: 0.16 },
      lineStyle: { width: 3, color: themeColor('--app-primary', '#7aa2ff') },
      itemStyle: { color: themeColor('--app-primary', '#7aa2ff') }
    }
  ]
}))

const skillDistributionOptions = computed<EChartsOption>(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c}%'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: t('aiStudy.home.skillDistribution'),
      type: 'pie',
      radius: ['40%', '68%'],
      center: ['58%', '55%'],
      data: [
        { value: 18, name: t('aiStudy.home.beginner') },
        { value: 27, name: t('aiStudy.home.basic') },
        { value: 31, name: t('aiStudy.home.skilled') },
        { value: 16, name: t('aiStudy.home.advanced') },
        { value: 8, name: t('aiStudy.home.expert') }
      ]
    }
  ]
}))

const aiSupportOptions = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  legend: {
    data: [t('aiStudy.home.aiChats'), t('aiStudy.home.resumeDiagnosis')]
  },
  grid: { left: 20, right: 20, top: 50, bottom: 20, containLabel: true },
  xAxis: {
    type: 'category',
    data: [
      t('aiStudy.home.week1'),
      t('aiStudy.home.week2'),
      t('aiStudy.home.week3'),
      t('aiStudy.home.week4')
    ]
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: t('aiStudy.home.aiChats'),
      type: 'bar',
      data: [86, 102, 118, 125],
      itemStyle: { color: themeColor('--app-primary', '#7aa2ff') }
    },
    {
      name: t('aiStudy.home.resumeDiagnosis'),
      type: 'bar',
      data: [24, 32, 28, 34],
      itemStyle: { color: themeColor('--app-accent', '#ffb7dc') }
    }
  ]
}))
</script>

<style lang="scss" scoped>
.ai-home {
  padding: 12px;
  font-size: var(--app-font-size-base);
}

.ai-home__hero {
  background:
    linear-gradient(135deg, rgb(122 162 255 / 16%), rgb(255 183 220 / 10%)),
    linear-gradient(90deg, rgb(255 255 255 / 4%) 1px, transparent 1px),
    linear-gradient(rgb(255 255 255 / 4%) 1px, transparent 1px), var(--app-card-bg);
  background-size:
    auto,
    18px 18px,
    18px 18px;
  border-color: var(--app-border-strong);
}

.ai-home__hero-main {
  display: flex;
  min-height: 128px;
  align-items: center;
  justify-content: space-between;
  gap: 16px;

  h1 {
    margin: 8px 0;
    font-size: calc(var(--app-title-font-size) + 10px);
    font-weight: 700;
    line-height: 1.3;
    color: var(--app-text);
    text-shadow:
      3px 3px 0 rgb(0 0 0 / 55%),
      0 0 18px rgb(122 162 255 / 18%);
  }

  p {
    margin: 0;
    font-size: var(--app-font-size-base);
    color: var(--el-text-color-secondary);
  }
}

.ai-home__eyebrow {
  font-size: var(--app-font-size-small);
  font-weight: 600;
  color: var(--app-accent);
  text-shadow: var(--app-pixel-shadow);
}

.ai-home__metric {
  :deep(.el-card__body) {
    display: flex;
    min-height: 100px;
    align-items: center;
    gap: 14px;
  }
}

.ai-home__metric-icon {
  display: flex;
  width: 46px;
  height: 46px;
  flex: none;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  box-shadow: inset 0 0 0 1px rgb(255 255 255 / 12%);
  image-rendering: pixelated;
}

.ai-home__metric-label {
  font-size: var(--app-font-size-small);
  color: var(--el-text-color-secondary);
}

.ai-home__metric-value {
  margin-top: 6px;
  font-size: calc(var(--app-title-font-size) + 6px);
  font-weight: 700;
  color: var(--app-text);
  text-shadow: var(--app-pixel-shadow);
}

.dashboard-chart-card {
  position: relative;
  overflow: hidden;
  min-height: 308px;
  background:
    linear-gradient(135deg, rgb(122 162 255 / 8%), rgb(255 183 220 / 5%)),
    var(--app-card-bg);
  border: 1px solid var(--app-border-strong, var(--app-border));
  box-shadow:
    var(--app-pixel-shadow),
    0 12px 26px rgb(0 0 0 / 22%);
}

.dashboard-chart-card::after {
  position: absolute;
  right: 14px;
  bottom: 12px;
  width: 76px;
  height: 76px;
  pointer-events: none;
  content: '';
  background:
    linear-gradient(90deg, rgb(255 255 255 / 5%) 1px, transparent 1px),
    linear-gradient(rgb(255 255 255 / 5%) 1px, transparent 1px);
  background-size: 10px 10px;
  opacity: 0.3;
}

.dashboard-chart-header {
  position: relative;
  z-index: 2;
  display: flex;
  height: 48px;
  min-height: 48px;
  align-items: center;
  gap: 8px;
  padding: 0 20px;
  border-bottom: 1px solid var(--app-border, #2b3548);
  background: rgb(8 13 20 / 72%);
}

.dashboard-chart-marker {
  width: 8px;
  height: 8px;
  flex: 0 0 8px;
  background: var(--app-accent, #ffb7dc);
  box-shadow: 0 0 8px rgb(255 183 220 / 70%);
}

.dashboard-chart-title {
  min-width: 0;
  overflow: hidden;
  color: var(--app-text, #f5f7ff);
  font-family: var(--app-pixel-font);
  font-size: var(--app-title-font-size, 18px);
  font-weight: 700;
  line-height: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dashboard-chart-body {
  position: relative;
  z-index: 1;
  height: 260px;
  padding: 12px 20px 20px;
  overflow: hidden;
}

.dashboard-chart {
  position: relative;
  z-index: 1;
  width: 100% !important;
  height: 100% !important;
}

.dashboard-chart-body :deep(canvas) {
  max-width: 100%;
  max-height: 100%;
}
</style>
