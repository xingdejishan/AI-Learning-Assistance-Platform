<template>
  <div>
    <el-row :gutter="12">
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic title="总经验" :value="summary.profile?.totalXp || 0" />
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic title="等级" :value="summary.profile?.levelNo || 1" />
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic title="连续学习" :value="summary.profile?.currentStreak || 0" suffix="天" />
        </el-card>
      </el-col>
      <el-col :lg="6" :md="12" :xs="24">
        <el-card shadow="never">
          <el-statistic title="今日待复习" :value="summary.todayReviewCount || 0" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="mt-12px">
      <el-col :lg="12" :md="24">
        <ContentWrap title="技能掌握度">
          <el-table :data="summary.masteryRanking || []">
            <el-table-column prop="skillName" label="技能" />
            <el-table-column prop="mastery" label="掌握度" width="180">
              <template #default="{ row }">
                <el-progress :percentage="row.mastery || 0" />
              </template>
            </el-table-column>
          </el-table>
        </ContentWrap>
      </el-col>
      <el-col :lg="12" :md="24">
        <ContentWrap title="知识点复习阶段">
          <el-table :data="summary.stageDistribution || []">
            <el-table-column prop="stage" label="阶段" />
            <el-table-column prop="count" label="知识点数量" />
          </el-table>
        </ContentWrap>
      </el-col>
    </el-row>

    <ContentWrap class="mt-12px" title="最近学习事件">
      <el-table :data="summary.recentEvents || []">
        <el-table-column prop="type" label="事件" width="180" />
        <el-table-column prop="xp" label="经验" width="120" />
        <el-table-column prop="time" label="时间" />
      </el-table>
    </ContentWrap>
  </div>
</template>

<script setup lang="ts">
import * as LearningFlowApi from '@/api/aistudy/learningFlow'

defineOptions({ name: 'AiStudyLearningFlow' })

const summary = ref<LearningFlowApi.LearningFlowSummaryVO>({})

const loadData = async () => {
  summary.value = await LearningFlowApi.getSummary()
}

onMounted(loadData)
</script>
