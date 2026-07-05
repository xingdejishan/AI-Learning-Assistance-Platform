<template>
  <ContentWrap title="今日复习">
    <el-table :data="reviews" v-loading="loading">
      <el-table-column prop="skillName" label="技能" width="180" />
      <el-table-column prop="content" label="知识点" min-width="260" />
      <el-table-column prop="stage" label="阶段" width="100" />
      <el-table-column prop="nextReviewTime" label="下次复习时间" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="success" @click="submit(row.id, true)">记住了</el-button>
          <el-button @click="submit(row.id, false)">再复习</el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script setup lang="ts">
import * as LearningFlowApi from '@/api/aistudy/learningFlow'

defineOptions({ name: 'AiStudyReview' })

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
  message.success(`已提交，掌握度 ${result.mastery || 0}%`)
  await loadData()
}

onMounted(loadData)
</script>
