<template>
  <ContentWrap :title="t('aiStudy.review.title')">
    <el-table :data="reviews" v-loading="loading">
      <el-table-column prop="skillName" :label="t('aiStudy.common.skill')" width="180" />
      <el-table-column prop="content" :label="t('aiStudy.review.content')" min-width="260" />
      <el-table-column prop="stage" :label="t('aiStudy.review.stage')" width="100" />
      <el-table-column
        prop="nextReviewTime"
        :label="t('aiStudy.review.nextReviewTime')"
        width="180"
      />
      <el-table-column :label="t('aiStudy.common.actions')" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="success" @click="submit(row.id, true)">
            {{ t('aiStudy.review.remembered') }}
          </el-button>
          <el-button @click="submit(row.id, false)">
            {{ t('aiStudy.review.reviewAgain') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import * as LearningFlowApi from '@/api/aistudy/learningFlow'

defineOptions({ name: 'AiStudyReview' })

const { t } = useI18n()
const message = useMessage()
const loading = ref(false)
const reviews = ref<LearningFlowApi.KnowledgePointVO[]>([])

const loadData = async () => {
  loading.value = true
  try {
    reviews.value = await LearningFlowApi.getTodayReviews()
  } finally {
    loading.value = false
  }
}

const submit = async (knowledgePointId: number, remembered: boolean) => {
  const result = await LearningFlowApi.submitReview({ knowledgePointId, remembered })
  message.success(t('aiStudy.review.submitted', { mastery: result.mastery || 0 }))
  await loadData()
}

onMounted(loadData)
</script>
