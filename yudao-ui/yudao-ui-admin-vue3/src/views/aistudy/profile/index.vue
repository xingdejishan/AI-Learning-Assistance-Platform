<template>
  <el-row :gutter="12">
    <el-col :lg="10" :md="24">
      <ContentWrap title="学习画像">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="总经验">{{ profile.totalXp || 0 }}</el-descriptions-item>
          <el-descriptions-item label="当前等级">{{ profile.levelNo || 1 }}</el-descriptions-item>
          <el-descriptions-item label="连续学习">{{ profile.currentStreak || 0 }} 天</el-descriptions-item>
          <el-descriptions-item label="最长连续">{{ profile.longestStreak || 0 }} 天</el-descriptions-item>
          <el-descriptions-item label="最近活跃">{{ profile.lastActiveDate || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-button class="mt-12px" type="primary" :loading="generating" @click="generate">
          生成学习报告
        </el-button>
      </ContentWrap>
    </el-col>
    <el-col :lg="14" :md="24">
      <ContentWrap title="AI 成长报告">
        <el-empty v-if="!report.title" description="点击生成学习报告" />
        <div v-else>
          <h3>{{ report.title }}</h3>
          <p>{{ report.summary }}</p>
          <el-divider />
          <el-descriptions :column="1">
            <el-descriptions-item label="优势">{{ (report.strongPoints || []).join('；') }}</el-descriptions-item>
            <el-descriptions-item label="待提升">{{ (report.weakPoints || []).join('；') }}</el-descriptions-item>
            <el-descriptions-item label="下一步">{{ (report.nextPlan || []).join('；') }}</el-descriptions-item>
            <el-descriptions-item label="风险提醒">{{ report.riskReminder || '暂无明显风险' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </ContentWrap>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import * as LearningFlowApi from '@/api/aistudy/learningFlow'

defineOptions({ name: 'AiStudyProfile' })

const profile = ref<LearningFlowApi.LearningProfileVO>({})
const report = ref<LearningFlowApi.ReportVO>({})
const generating = ref(false)

const loadProfile = async () => {
  profile.value = await LearningFlowApi.getProfile()
}

const generate = async () => {
  generating.value = true
  try {
    report.value = await LearningFlowApi.generateReport()
    await loadProfile()
  } finally {
    generating.value = false
  }
}

onMounted(loadProfile)
</script>
