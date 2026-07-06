<template>
  <el-row :gutter="12" class="ai-profile-page">
    <el-col :lg="10" :md="24">
      <ContentWrap :title="t('aiStudy.profile.title')">
        <el-descriptions :column="1" border>
          <el-descriptions-item :label="t('aiStudy.common.totalXp')">
            {{ profile.totalXp || 0 }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('aiStudy.profile.currentLevel')">
            {{ profile.levelNo || 1 }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('aiStudy.profile.currentStreak')">
            {{ profile.currentStreak || 0 }} {{ t('aiStudy.common.day') }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('aiStudy.profile.longestStreak')">
            {{ profile.longestStreak || 0 }} {{ t('aiStudy.common.day') }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('aiStudy.profile.lastActiveDate')">
            {{ profile.lastActiveDate || '-' }}
          </el-descriptions-item>
        </el-descriptions>
        <el-button class="mt-12px" type="primary" :loading="generating" @click="generate">
          {{ t('aiStudy.profile.generateReport') }}
        </el-button>
      </ContentWrap>
    </el-col>
    <el-col :lg="14" :md="24">
      <ContentWrap :title="t('aiStudy.profile.reportTitle')">
        <el-empty v-if="!report.title" :description="t('aiStudy.profile.reportEmpty')" />
        <div v-else>
          <h3>{{ report.title }}</h3>
          <p>{{ report.summary }}</p>
          <el-divider />
          <el-descriptions :column="1">
            <el-descriptions-item :label="t('aiStudy.profile.strongPoints')">
              {{ (report.strongPoints || []).join('；') }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('aiStudy.profile.weakPoints')">
              {{ (report.weakPoints || []).join('；') }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('aiStudy.profile.nextPlan')">
              {{ (report.nextPlan || []).join('；') }}
            </el-descriptions-item>
            <el-descriptions-item :label="t('aiStudy.profile.riskReminder')">
              {{ report.riskReminder || t('aiStudy.profile.noRisk') }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </ContentWrap>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import * as LearningFlowApi from '@/api/aistudy/learningFlow'

defineOptions({ name: 'AiStudyProfile' })

const { t } = useI18n()
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

<style scoped>
.ai-profile-page {
  font-size: var(--app-font-size-base);
}

h3 {
  font-size: var(--app-title-font-size);
}
</style>
