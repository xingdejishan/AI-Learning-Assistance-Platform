<template>
  <div class="ai-pixel-home">
    <section class="ai-pixel-hero">
      <div class="ai-pixel-hero-top">
        <span class="accent-script">{{ t('aiStudy.hero.badge') }}</span>
      </div>

      <div class="ai-pixel-hero-main">
        <h1 class="ai-pixel-title">
          <span class="matrix-dot-base matrix-white">{{ t('aiStudy.hero.titleLine1') }}</span>
          <span class="ai-pixel-title-row">
            <span class="matrix-dot-base matrix-pink">{{ t('aiStudy.hero.titleLine2Left') }}</span>
            <span class="matrix-dot-base matrix-blue">{{ t('aiStudy.hero.titleLine2Right') }}</span>
          </span>
        </h1>

        <p class="ai-pixel-subtitle">{{ t('aiStudy.hero.subtitle') }}</p>
        <p class="ai-pixel-subtitle-note">{{ t('aiStudy.hero.subtitleNote') }}</p>
      </div>
    </section>

    <section class="ai-pixel-grid">
      <article
        v-for="module in moduleCards"
        :key="module.code"
        class="ai-pixel-module-card"
        :class="module.modifier"
      >
        <div class="ai-pixel-module-header">
          <span class="ai-pixel-module-code">{{ module.code }}</span>
          <span class="pixel-led"></span>
        </div>
        <h2 class="ai-pixel-module-title">{{ module.title }}</h2>
        <p class="ai-pixel-module-desc">{{ module.desc }}</p>
        <div v-for="line in module.meta" :key="line" class="ai-pixel-module-line">{{ line }}</div>
        <div v-if="module.progress" class="ai-pixel-progress-block">{{ module.progress }}</div>
        <div v-if="module.tags" class="ai-pixel-tags">
          <span v-for="tag in module.tags" :key="tag">{{ tag }}</span>
        </div>
      </article>
    </section>

    <el-row :gutter="12" class="ai-pixel-section">
      <el-col
        v-for="item in metrics"
        :key="item.name"
        :xl="metricsColSpan"
        :lg="metricsColSpan"
        :md="12"
        :sm="12"
        :xs="24"
      >
        <div class="ai-pixel-metric-card">
          <div class="ai-pixel-metric-icon" :style="{ color: item.color, background: item.bg }">
            <Icon :icon="item.icon" :size="24" />
          </div>
          <div>
            <div class="ai-pixel-metric-label">{{ item.name }}</div>
            <div class="ai-pixel-metric-value">
              <CountTo :start-val="0" :end-val="item.value" :duration="1800" />
              <span v-if="item.suffix">{{ item.suffix }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="ai-pixel-section">
      <el-col :xl="16" :lg="16" :md="24" :sm="24" :xs="24">
        <div class="pixel-chart-card">
          <div class="pixel-chart-header">
            <span class="pixel-led"></span>
            <span class="pixel-chart-title">{{ t('aiStudy.home.studyTrend') }}</span>
          </div>

          <div class="pixel-chart-body">
            <Echart class="pixel-chart" height="100%" :options="studyTrendOptions" />
          </div>
        </div>
      </el-col>

      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24" class="ai-pixel-chart-col">
        <div class="pixel-chart-card">
          <div class="pixel-chart-header">
            <span class="pixel-led pixel-led--pink"></span>
            <span class="pixel-chart-title">{{ t('aiStudy.home.skillDistribution') }}</span>
          </div>

          <div class="pixel-chart-body">
            <Echart class="pixel-chart" height="100%" :options="skillDistributionOptions" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="ai-pixel-section">
      <el-col :span="24">
        <div class="pixel-chart-card">
          <div class="pixel-chart-header">
            <span class="pixel-led pixel-led--green"></span>
            <span class="pixel-chart-title">{{ t('aiStudy.home.aiResumeStats') }}</span>
          </div>

          <div class="pixel-chart-body">
            <Echart class="pixel-chart" height="100%" :options="aiSupportOptions" />
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

const chartFontFamily =
  'ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace'

const pixelProgress = '■■■■■■□□□ 70%'

const chartTooltip = () => ({
  backgroundColor: '#05070A',
  borderColor: '#1A202C',
  borderWidth: 1,
  textStyle: {
    color: '#E2E8F0',
    fontFamily: chartFontFamily,
    fontSize: 12
  }
})

const chartTextStyle = () => ({
  color: '#E2E8F0',
  fontFamily: chartFontFamily
})

const chartAxisStyle = () => ({
  axisLabel: { color: '#64748B', fontFamily: chartFontFamily },
  axisLine: { lineStyle: { color: '#1A202C' } },
  axisTick: { show: false },
  splitLine: { lineStyle: { color: '#1A202C' } }
})

const moduleCards = computed(() => [
  {
    code: t('aiStudy.hero.modelCode'),
    title: t('aiStudy.hero.modelTitle'),
    desc: t('aiStudy.hero.modelDesc'),
    meta: [t('aiStudy.hero.modelOptimization'), t('aiStudy.hero.modelLoss')],
    modifier: 'is-blue'
  },
  {
    code: t('aiStudy.hero.pipelineCode'),
    title: t('aiStudy.hero.pipelineTitle'),
    desc: t('aiStudy.hero.pipelineDesc'),
    meta: [t('aiStudy.hero.pipelineEpoch')],
    progress: pixelProgress,
    modifier: 'is-pink'
  },
  {
    code: t('aiStudy.hero.metricsCode'),
    title: t('aiStudy.hero.metricsTitle'),
    desc: t('aiStudy.hero.metricsDesc'),
    meta: [],
    tags: [t('aiStudy.hero.tagSkillTree'), t('aiStudy.hero.tagResumeAi'), t('aiStudy.hero.tagActive')],
    modifier: 'is-green'
  }
])

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
  color: ['#00E5FF'],
  textStyle: chartTextStyle(),
  tooltip: { trigger: 'axis', ...chartTooltip() },
  grid: { left: 20, right: 20, top: 20, bottom: 20, containLabel: true },
  xAxis: {
    type: 'category',
    data: weekdays.value,
    ...chartAxisStyle()
  },
  yAxis: { type: 'value', ...chartAxisStyle() },
  series: [
    {
      name: t('aiStudy.home.studyDuration'),
      type: 'line',
      smooth: true,
      data: [120, 150, 132, 178, 210, 186, 225],
      areaStyle: { opacity: 0.18, color: themeColor('--app-primary', '#00E5FF') },
      lineStyle: { width: 3, color: themeColor('--app-primary', '#00E5FF') },
      itemStyle: { color: themeColor('--app-primary', '#00E5FF') }
    }
  ]
}))

const skillDistributionOptions = computed<EChartsOption>(() => ({
  color: ['#00E5FF', '#FF8BB2', '#50FA7B', '#FFB86C', '#8BE9FD'],
  textStyle: chartTextStyle(),
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c}%',
    ...chartTooltip()
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    textStyle: {
      color: '#64748B',
      fontFamily: chartFontFamily
    }
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
  color: ['#00E5FF', '#FF8BB2'],
  textStyle: chartTextStyle(),
  tooltip: { trigger: 'axis', ...chartTooltip() },
  legend: {
    data: [t('aiStudy.home.aiChats'), t('aiStudy.home.resumeDiagnosis')],
    textStyle: {
      color: '#64748B',
      fontFamily: chartFontFamily
    }
  },
  grid: { left: 20, right: 20, top: 50, bottom: 20, containLabel: true },
  xAxis: {
    type: 'category',
    data: [
      t('aiStudy.home.week1'),
      t('aiStudy.home.week2'),
      t('aiStudy.home.week3'),
      t('aiStudy.home.week4')
    ],
    ...chartAxisStyle()
  },
  yAxis: { type: 'value', ...chartAxisStyle() },
  series: [
    {
      name: t('aiStudy.home.aiChats'),
      type: 'bar',
      data: [86, 102, 118, 125],
      itemStyle: { color: themeColor('--app-primary', '#00E5FF') }
    },
    {
      name: t('aiStudy.home.resumeDiagnosis'),
      type: 'bar',
      data: [24, 32, 28, 34],
      itemStyle: { color: themeColor('--app-accent', '#FF8BB2') }
    }
  ]
}))
</script>

<style lang="scss" scoped>
.ai-pixel-home {
  min-height: calc(100vh - 96px);
  padding: 18px;
  color: #e2e8f0;
  font-family:
    ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
    monospace;
  font-size: var(--app-font-size-base);
  background:
    radial-gradient(circle at 18% 0, rgb(0 229 255 / 13%), transparent 32%),
    radial-gradient(circle at 82% 12%, rgb(255 139 178 / 11%), transparent 30%),
    linear-gradient(90deg, rgb(255 255 255 / 3%) 1px, transparent 1px),
    linear-gradient(rgb(255 255 255 / 3%) 1px, transparent 1px), #000;
  background-size:
    auto,
    auto,
    28px 28px,
    28px 28px;
}

.ai-pixel-hero {
  position: relative;
  overflow: hidden;
  min-height: 360px;
  padding: 38px 42px 44px;
  background:
    linear-gradient(135deg, rgb(5 7 10 / 94%), rgb(5 7 10 / 80%)),
    linear-gradient(90deg, rgb(0 229 255 / 8%) 1px, transparent 1px),
    linear-gradient(rgb(255 139 178 / 7%) 1px, transparent 1px);
  background-size:
    auto,
    18px 18px,
    18px 18px;
  border: 1px solid #1a202c;
  box-shadow:
    0 0 0 1px rgb(255 255 255 / 3%) inset,
    0 24px 44px rgb(0 0 0 / 32%);
}

.ai-pixel-hero::before,
.ai-pixel-hero::after {
  position: absolute;
  pointer-events: none;
  content: '';
}

.ai-pixel-hero::before {
  inset: 18px;
  border: 1px solid rgb(0 229 255 / 12%);
}

.ai-pixel-hero::after {
  right: 38px;
  bottom: 32px;
  width: 132px;
  height: 132px;
  background:
    linear-gradient(90deg, rgb(255 255 255 / 9%) 2px, transparent 2px),
    linear-gradient(rgb(255 255 255 / 9%) 2px, transparent 2px);
  background-size: 12px 12px;
  opacity: 0.22;
}

.ai-pixel-hero-top {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: flex-end;
}

.accent-script {
  display: inline-block;
  color: #ff8bb2;
  font-family: Georgia, ui-serif, serif;
  font-size: clamp(24px, 4vw, 56px);
  font-style: italic;
  font-weight: 700;
  letter-spacing: 0.04em;
  text-shadow: 0 0 18px rgb(255 139 178 / 25%);
  transform: rotate(-4deg);
}

.ai-pixel-hero-main {
  position: relative;
  z-index: 1;
  max-width: 1120px;
  margin-top: 12px;
}

.ai-pixel-title {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 0;
  font-family: 'Arial Black', Impact, ui-sans-serif, system-ui, sans-serif;
  font-size: clamp(48px, 8vw, 118px);
  font-weight: 900;
  line-height: 0.95;
}

.ai-pixel-title-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.28em;
}

.matrix-dot-base {
  display: inline-block;
  color: transparent;
  line-height: 0.95;
  text-transform: uppercase;
  user-select: none;
  background-size: 4px 4px;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  -webkit-background-clip: text;
}

.matrix-white {
  background-image: radial-gradient(circle, #fff 35%, transparent 40%);
}

.matrix-pink {
  background-image: radial-gradient(circle, #ff8bb2 35%, transparent 40%);
}

.matrix-blue {
  background-image: radial-gradient(circle, #00e5ff 35%, transparent 40%);
}

.ai-pixel-subtitle {
  max-width: 860px;
  margin: 28px 0 0;
  color: #e2e8f0;
  font-family: ui-sans-serif, system-ui, sans-serif;
  font-size: clamp(16px, 2vw, 24px);
  font-weight: 700;
  line-height: 1.8;
  text-shadow: 0 0 12px rgb(0 229 255 / 15%);
}

.ai-pixel-subtitle-note {
  margin: 10px 0 0;
  color: #64748b;
  font-size: clamp(12px, 1.4vw, 16px);
  line-height: 1.8;
}

.ai-pixel-grid {
  display: grid;
  width: 100%;
  max-width: 1120px;
  margin: 24px auto 0;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 24px;
}

.ai-pixel-module-card,
.ai-pixel-metric-card,
.pixel-chart-card {
  background: #05070a;
  border: 1px solid #1a202c;
  border-radius: 0;
}

.ai-pixel-module-card {
  min-height: 210px;
  padding: 20px;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.ai-pixel-module-card:hover {
  border-color: #00e5ff;
  box-shadow: 0 0 22px rgb(0 229 255 / 16%);
  transform: translateY(-2px);
}

.ai-pixel-module-card.is-pink:hover {
  border-color: #ff8bb2;
  box-shadow: 0 0 22px rgb(255 139 178 / 16%);
}

.ai-pixel-module-card.is-green:hover {
  border-color: #50fa7b;
  box-shadow: 0 0 22px rgb(80 250 123 / 14%);
}

.ai-pixel-module-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.ai-pixel-module-code,
.ai-pixel-module-line,
.ai-pixel-tags {
  font-size: var(--app-font-size-small);
}

.ai-pixel-module-code {
  color: #00e5ff;
  font-weight: 800;
  line-height: 1;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.pixel-led {
  width: 8px;
  height: 8px;
  flex: 0 0 8px;
  background: #00e5ff;
  box-shadow: 0 0 12px rgb(0 229 255 / 80%);
  animation: pixelPulse 1.6s infinite steps(2, end);
}

.pixel-led--pink {
  background: #ff8bb2;
  box-shadow: 0 0 12px rgb(255 139 178 / 80%);
}

.pixel-led--green {
  background: #50fa7b;
  box-shadow: 0 0 12px rgb(80 250 123 / 75%);
}

.ai-pixel-module-title {
  margin: 0;
  color: #fff;
  font-size: var(--app-title-font-size);
  font-weight: 800;
  line-height: 1.25;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.ai-pixel-module-desc {
  min-height: 48px;
  margin: 12px 0 16px;
  color: #64748b;
  font-size: var(--app-font-size-base);
  line-height: 1.6;
}

.ai-pixel-module-line {
  color: #e2e8f0;
  line-height: 1.8;
}

.ai-pixel-progress-block {
  margin-top: 10px;
  color: #ff8bb2;
  font-size: var(--app-font-size-small);
  line-height: 1.6;
  letter-spacing: -0.06em;
  white-space: nowrap;
}

.ai-pixel-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;

  span {
    padding: 4px 8px;
    color: #50fa7b;
    border: 1px solid rgb(80 250 123 / 32%);
  }
}

.ai-pixel-section {
  margin-top: 12px;
}

.ai-pixel-metric-card {
  display: flex;
  min-height: 100px;
  align-items: center;
  gap: 14px;
  padding: 18px;
  box-shadow: inset 0 0 0 1px rgb(255 255 255 / 3%);
}

.ai-pixel-metric-icon {
  display: flex;
  width: 46px;
  height: 46px;
  flex: none;
  align-items: center;
  justify-content: center;
  border: 1px solid #1a202c;
  image-rendering: pixelated;
}

.ai-pixel-metric-label {
  color: #64748b;
  font-size: var(--app-font-size-small);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.ai-pixel-metric-value {
  margin-top: 6px;
  color: #e2e8f0;
  font-size: calc(var(--app-title-font-size) + 6px);
  font-weight: 800;
  text-shadow: 3px 3px 0 rgb(0 0 0 / 65%);
}

.pixel-chart-card {
  position: relative;
  overflow: hidden;
  min-height: 308px;
  box-shadow: 0 14px 28px rgb(0 0 0 / 24%);
}

.pixel-chart-card::after {
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
  opacity: 0.28;
}

.pixel-chart-header {
  position: relative;
  z-index: 2;
  display: flex;
  height: 48px;
  min-height: 48px;
  align-items: center;
  gap: 10px;
  padding: 0 20px;
  border-bottom: 1px solid #1a202c;
  background: #05070a;
}

.pixel-chart-title {
  min-width: 0;
  overflow: hidden;
  color: #e2e8f0;
  font-size: var(--app-title-font-size, 18px);
  font-weight: 800;
  line-height: 1;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pixel-chart-body {
  position: relative;
  z-index: 1;
  height: 260px;
  padding: 16px 20px 20px;
  overflow: hidden;
}

.pixel-chart {
  position: relative;
  z-index: 1;
  width: 100% !important;
  height: 100% !important;
}

.pixel-chart-body :deep(canvas) {
  max-width: 100%;
  max-height: 100%;
}

.ai-pixel-home :deep(.el-card),
.ai-pixel-home :deep(.el-button),
.ai-pixel-home :deep(.el-input__wrapper) {
  border-radius: 0;
}

.ai-pixel-home :deep(.el-card) {
  color: #e2e8f0;
  background: #05070a;
  border-color: #1a202c;
}

@keyframes pixelPulse {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.35;
  }
}

@media (max-width: 1200px) {
  .ai-pixel-chart-col {
    margin-top: 12px;
  }
}

@media (max-width: 960px) {
  .ai-pixel-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .ai-pixel-home {
    padding: 12px;
  }

  .ai-pixel-hero {
    min-height: 320px;
    padding: 30px 24px 34px;
  }

  .ai-pixel-title-row {
    justify-content: flex-start;
  }
}
</style>
