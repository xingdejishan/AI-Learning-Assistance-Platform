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
        <el-card shadow="never">
          <template #header>
            <span>{{ t('aiStudy.home.studyTrend') }}</span>
          </template>
          <Echart :height="320" :options="studyTrendOptions" />
        </el-card>
      </el-col>

      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24" class="lt-lg:mt-12px">
        <el-card shadow="never">
          <template #header>
            <span>{{ t('aiStudy.home.skillDistribution') }}</span>
          </template>
          <Echart :height="320" :options="skillDistributionOptions" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <span>{{ t('aiStudy.home.aiResumeStats') }}</span>
          </template>
          <Echart :height="320" :options="aiSupportOptions" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
import type { EChartsOption } from 'echarts'

defineOptions({ name: 'Index' })

const { t } = useI18n()

const metrics = computed(() => [
  {
    name: t('aiStudy.home.studentTotal'),
    value: 2846,
    icon: 'ep:school',
    color: '#0f766e',
    bg: 'rgba(15, 118, 110, 0.12)'
  },
  {
    name: t('aiStudy.home.todayRecords'),
    value: 126,
    icon: 'ep:reading',
    color: '#2563eb',
    bg: 'rgba(37, 99, 235, 0.12)'
  },
  {
    name: t('aiStudy.home.aiChats'),
    value: 482,
    icon: 'tabler:brain',
    color: '#7c3aed',
    bg: 'rgba(124, 58, 237, 0.12)'
  },
  {
    name: t('aiStudy.home.resumeDiagnosis'),
    value: 78,
    icon: 'ep:document-checked',
    color: '#059669',
    bg: 'rgba(5, 150, 105, 0.12)'
  },
  {
    name: t('aiStudy.home.skillCompletion'),
    value: 68,
    suffix: '%',
    icon: 'ep:trend-charts',
    color: '#f59e0b',
    bg: 'rgba(245, 158, 11, 0.14)'
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
      lineStyle: { width: 3, color: '#2563eb' },
      itemStyle: { color: '#2563eb' }
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
      itemStyle: { color: '#7c3aed' }
    },
    {
      name: t('aiStudy.home.resumeDiagnosis'),
      type: 'bar',
      data: [24, 32, 28, 34],
      itemStyle: { color: '#059669' }
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
    linear-gradient(135deg, rgb(37 99 235 / 12%), rgb(124 58 237 / 8%)), var(--el-bg-color);
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
    color: var(--el-text-color-primary);
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
  color: var(--el-color-primary);
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
}

.ai-home__metric-label {
  font-size: var(--app-font-size-small);
  color: var(--el-text-color-secondary);
}

.ai-home__metric-value {
  margin-top: 6px;
  font-size: calc(var(--app-title-font-size) + 6px);
  font-weight: 700;
  color: var(--el-text-color-primary);
}
</style>
