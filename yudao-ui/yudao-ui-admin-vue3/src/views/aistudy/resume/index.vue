<template>
  <el-row :gutter="12">
    <el-col :lg="10" :md="24" :xs="24">
      <ContentWrap title="简历上传">
        <el-upload drag :auto-upload="false" :limit="1" :on-change="handleFileChange">
          <Icon icon="ep:upload-filled" :size="48" />
          <div class="el-upload__text">拖拽简历到此处，或点击选择文件</div>
        </el-upload>
        <el-button class="mt-12px" type="primary" :loading="uploading" @click="submitUpload">
          <Icon class="mr-5px" icon="ep:document-checked" />开始诊断
        </el-button>
      </ContentWrap>
      <ContentWrap v-if="current" title="诊断结果">
        <el-statistic title="诊断评分" :value="current.score || 0" />
        <el-divider />
        <h4>AI 分析结果</h4>
        <p>{{ current.analysisResult }}</p>
        <h4>优化建议</h4>
        <p>{{ current.optimizeSuggestion }}</p>
      </ContentWrap>
    </el-col>
    <el-col :lg="14" :md="24" :xs="24">
      <ContentWrap title="历史诊断记录">
        <el-table :data="list">
          <el-table-column label="文件名" prop="fileName" min-width="180" />
          <el-table-column label="评分" prop="score" width="90" />
          <el-table-column label="状态" prop="status" width="90">
            <template #default="{ row }">{{ row.status === 1 ? '已完成' : '待诊断' }}</template>
          </el-table-column>
          <el-table-column label="创建时间" prop="createTime" width="180" />
          <el-table-column label="操作" width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="current = row">查看</el-button>
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
    message.warning('请先选择简历文件')
    return
  }
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    current.value = await ResumeApi.uploadResume(formData)
    message.success('诊断完成')
    await loadList()
  } finally {
    uploading.value = false
  }
}

onMounted(loadList)
</script>
