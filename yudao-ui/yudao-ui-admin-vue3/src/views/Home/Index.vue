<template>
  <div class="ai-home">
    <el-card shadow="never" class="ai-home__hero">
      <div class="ai-home__hero-main">
        <div>
          <div class="ai-home__eyebrow">AI伴学平台</div>
          <h1>AI伴学与职业成长平台</h1>
          <p>围绕学习进度、AI 辅导和求职能力，持续沉淀成长数据</p>
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
            <span>近 7 日学习时长趋势</span>
          </template>
          <Echart :height="320" :options="studyTrendOptions" />
        </el-card>
      </el-col>

      <el-col :xl="8" :lg="8" :md="24" :sm="24" :xs="24" class="lt-lg:mt-12px">
        <el-card shadow="never">
          <template #header>
            <span>技能掌握度分布</span>
          </template>
          <Echart :height="320" :options="skillDistributionOptions" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>
            <span>AI 辅导与简历诊断次数统计</span>
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

const metrics = [
  {
    name: '学生总数',
    value: 2846,
    icon: 'ep:school',
    color: '#0f766e',
    bg: 'rgba(15, 118, 110, 0.12)'
  },
  {
    name: '今日学习记录',
    value: 126,
    icon: 'ep:reading',
    color: '#2563eb',
    bg: 'rgba(37, 99, 235, 0.12)'
  },
  {
    name: 'AI 辅导次数',
    value: 482,
    icon: 'tabler:brain',
    color: '#7c3aed',
    bg: 'rgba(124, 58, 237, 0.12)'
  },
  {
    name: '简历诊断次数',
    value: 78,
    icon: 'ep:document-checked',
    color: '#059669',
    bg: 'rgba(5, 150, 105, 0.12)'
  },
  {
    name: '技能树完成率',
    value: 68,
    suffix: '%',
    icon: 'ep:trend-charts',
    color: '#f59e0b',
    bg: 'rgba(245, 158, 11, 0.14)'
  }
]

const metricsColSpan = 24 / metrics.length

const studyTrendOptions: EChartsOption = {
  tooltip: { trigger: 'axis' },
  grid: { left: 20, right: 20, top: 20, bottom: 20, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
    axisTick: { show: false }
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: '学习时长',
      type: 'line',
      smooth: true,
      data: [120, 150, 132, 178, 210, 186, 225],
      areaStyle: {
        opacity: 0.16
      },
      lineStyle: {
        width: 3,
        color: '#2563eb'
      },
      itemStyle: {
        color: '#2563eb'
      }
    }
  ]
}

const skillDistributionOptions: EChartsOption = {
  tooltip: {
    trigger: 'item',
    formatter: '{b}：{c}%'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '技能掌握度',
      type: 'pie',
      radius: ['40%', '68%'],
      center: ['58%', '55%'],
      data: [
        { value: 18, name: '入门' },
        { value: 27, name: '基础' },
        { value: 31, name: '熟练' },
        { value: 16, name: '进阶' },
        { value: 8, name: '精通' }
      ]
    }
  ]
}

const aiSupportOptions: EChartsOption = {
  tooltip: { trigger: 'axis' },
  legend: {
    data: ['AI 辅导', '简历诊断']
  },
  grid: { left: 20, right: 20, top: 50, bottom: 20, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['第一周', '第二周', '第三周', '第四周']
  },
  yAxis: { type: 'value' },
  series: [
    {
      name: 'AI 辅导',
      type: 'bar',
      data: [86, 102, 118, 125],
      itemStyle: { color: '#7c3aed' }
    },
    {
      name: '简历诊断',
      type: 'bar',
      data: [24, 32, 28, 34],
      itemStyle: { color: '#059669' }
    }
  ]
}
</script>

<style lang="scss" scoped>
.ai-home {
  padding: 12px;
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
    font-size: 28px;
    font-weight: 700;
    line-height: 1.3;
    color: var(--el-text-color-primary);
  }

  p {
    margin: 0;
    font-size: 14px;
    color: var(--el-text-color-secondary);
  }
}

.ai-home__eyebrow {
  font-size: 13px;
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
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.ai-home__metric-value {
  margin-top: 6px;
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}
</style>
