<template>
  <div>
    <el-row :gutter="12">
      <el-col v-for="item in metrics" :key="item.label" :lg="4" :md="8" :xs="24">
        <el-card shadow="never">
          <el-statistic :title="item.label" :value="item.value" :suffix="item.suffix" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :lg="8" :md="24" :xs="24">
        <ContentWrap title="近 7 日学习时长趋势">
          <Echart :options="studyDurationOptions" :height="280" />
        </ContentWrap>
      </el-col>
      <el-col :lg="8" :md="24" :xs="24">
        <ContentWrap title="技能掌握度分布">
          <Echart :options="skillDistributionOptions" :height="280" />
        </ContentWrap>
      </el-col>
      <el-col :lg="8" :md="24" :xs="24">
        <ContentWrap title="AI 辅导与简历诊断次数">
          <Echart :options="aiResumeOptions" :height="280" />
        </ContentWrap>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import * as DashboardApi from '@/api/aistudy/dashboard'

defineOptions({ name: 'AiStudyDashboard' })

const data = ref<DashboardApi.DashboardVO>({
  totalStudyDuration: 0,
  todayLearningRecordCount: 0,
  aiChatCount: 0,
  resumeDiagnosisCount: 0,
  skillCompletionRate: 0,
  studyDurationTrend: [],
  skillProgressDistribution: [],
  aiAndResumeTrend: []
})

const metrics = computed(() => [
  { label: '学习总时长', value: data.value.totalStudyDuration, suffix: '分钟' },
  { label: '今日学习记录', value: data.value.todayLearningRecordCount },
  { label: 'AI 辅导次数', value: data.value.aiChatCount },
  { label: '简历诊断次数', value: data.value.resumeDiagnosisCount },
  { label: '技能树完成率', value: data.value.skillCompletionRate, suffix: '%' }
])

const studyDurationOptions = computed<any>(() => ({
  tooltip: {},
  xAxis: { type: 'category', data: data.value.studyDurationTrend.map((item) => item.date) },
  yAxis: { type: 'value' },
  series: [{ type: 'line', smooth: true, data: data.value.studyDurationTrend.map((item) => item.duration) }]
}))

const skillDistributionOptions = computed<any>(() => ({
  tooltip: { trigger: 'item' },
  series: [{ type: 'pie', radius: ['42%', '70%'], data: data.value.skillProgressDistribution }]
}))

const aiResumeOptions = computed<any>(() => ({
  tooltip: {},
  xAxis: { type: 'category', data: data.value.aiAndResumeTrend.map((item) => item.date) },
  yAxis: { type: 'value' },
  series: [{ type: 'bar', data: data.value.aiAndResumeTrend.map((item) => item.count) }]
}))

const loadData = async () => {
  data.value = await DashboardApi.getDashboard()
}

onMounted(loadData)
</script>
