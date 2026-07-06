<template>
  <div class="ai-learning-flow-page">
    <el-row :gutter="12">
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic
            :title="t('aiStudy.common.totalXp')"
            :value="summary.profile?.totalXp || 0"
          />
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic
            :title="t('aiStudy.common.levelNo')"
            :value="summary.profile?.levelNo || 1"
          />
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic
            :title="t('aiStudy.profile.currentStreak')"
            :value="summary.profile?.currentStreak || 0"
            :suffix="t('aiStudy.common.day')"
          />
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic
            :title="t('aiStudy.learningFlow.todayReviewCount')"
            :value="summary.todayReviewCount || 0"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :lg="12" :md="24">
        <ContentWrap :title="t('aiStudy.learningFlow.skillMastery')">
          <el-table :data="summary.masteryRanking || []">
            <el-table-column prop="skillName" :label="t('aiStudy.common.skill')" />
            <el-table-column prop="mastery" :label="t('aiStudy.common.mastery')" width="180">
              <template #default="{ row }">
                <el-progress :percentage="row.mastery || 0" />
              </template>
            </el-table-column>
          </el-table>
        </ContentWrap>
      </el-col>
      <el-col :lg="12" :md="24">
        <ContentWrap :title="t('aiStudy.learningFlow.stageDistribution')">
          <el-table :data="summary.stageDistribution || []">
            <el-table-column prop="stage" :label="t('aiStudy.review.stage')" />
            <el-table-column prop="count" :label="t('aiStudy.learningFlow.count')" />
          </el-table>
        </ContentWrap>
      </el-col>
    </el-row>

    <ContentWrap class="mt-12px" :title="t('aiStudy.learningFlow.recentEvents')">
      <el-table :data="summary.recentEvents || []">
        <el-table-column prop="type" :label="t('aiStudy.learningFlow.event')" width="180" />
        <el-table-column prop="xp" :label="t('aiStudy.common.xp')" width="120" />
        <el-table-column prop="time" :label="t('aiStudy.common.time')" />
      </el-table>
    </ContentWrap>
  </div>
</template>

<script setup lang="ts">
import * as LearningFlowApi from '@/api/aistudy/learningFlow'

defineOptions({ name: 'AiStudyLearningFlow' })

const { t } = useI18n()
const summary = ref<LearningFlowApi.LearningFlowSummaryVO>({})

const loadData = async () => {
  summary.value = await LearningFlowApi.getSummary()
}

onMounted(loadData)
</script>

<style scoped>
.ai-learning-flow-page {
  font-size: var(--app-font-size-base);
}
</style>
