<template>
  <el-row :gutter="12" class="ai-resume-page">
    <el-col :lg="10" :md="24" :xs="24">
      <ContentWrap :title="t('aiStudy.resume.upload')">
        <el-upload drag :auto-upload="false" :limit="1" :on-change="handleFileChange">
          <Icon icon="ep:upload-filled" :size="48" />
          <div class="el-upload__text">{{ t('aiStudy.resume.uploadTip') }}</div>
        </el-upload>
        <el-button class="mt-12px" type="primary" :loading="uploading" @click="submitUpload">
          <Icon class="mr-5px" icon="ep:document-checked" />{{ t('aiStudy.resume.start') }}
        </el-button>
      </ContentWrap>
      <ContentWrap v-if="current" :title="t('aiStudy.resume.result')">
        <el-statistic :title="t('aiStudy.resume.score')" :value="current.score || 0" />
        <el-divider />
        <h4>{{ t('aiStudy.resume.analysis') }}</h4>
        <p>{{ current.analysisResult }}</p>
        <h4>{{ t('aiStudy.resume.suggestion') }}</h4>
        <p>{{ current.optimizeSuggestion }}</p>
      </ContentWrap>
    </el-col>
    <el-col :lg="14" :md="24" :xs="24">
      <ContentWrap :title="t('aiStudy.resume.history')">
        <el-table :data="list">
          <el-table-column :label="t('aiStudy.resume.fileName')" prop="fileName" min-width="180" />
          <el-table-column :label="t('aiStudy.resume.score')" prop="score" width="90" />
          <el-table-column :label="t('aiStudy.common.status')" prop="status" width="90">
            <template #default="{ row }">
              {{ row.status === 1 ? t('aiStudy.resume.completed') : t('aiStudy.resume.pending') }}
            </template>
          </el-table-column>
          <el-table-column :label="t('aiStudy.common.createTime')" prop="createTime" width="180" />
          <el-table-column :label="t('aiStudy.common.actions')" width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="current = row">
                {{ t('aiStudy.common.view') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </ContentWrap>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import type { UploadFile } from 'element-plus'
import * as ResumeApi from '@/api/aistudy/resume'

defineOptions({ name: 'AiStudyResume' })

const { t } = useI18n()
const message = useMessage()
const uploading = ref(false)
const selectedFile = ref<File>()
const current = ref<ResumeApi.ResumeVO>()
const list = ref<ResumeApi.ResumeVO[]>([])

const handleFileChange = (file: UploadFile) => {
  selectedFile.value = file.raw
}

const loadList = async () => {
  list.value = await ResumeApi.getResumeList()
}

const submitUpload = async () => {
  if (!selectedFile.value) {
    message.warning(t('aiStudy.resume.selectFile'))
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    current.value = await ResumeApi.uploadResume(formData)
    message.success(t('aiStudy.resume.completedMessage'))
    await loadList()
  } finally {
    uploading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped>
.ai-resume-page,
h4,
p {
  font-size: var(--app-font-size-base);
}
</style>
