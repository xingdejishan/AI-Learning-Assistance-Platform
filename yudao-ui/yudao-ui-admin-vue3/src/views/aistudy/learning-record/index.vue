<template>
  <ContentWrap>
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="标题">
        <el-input v-model="queryParams.title" class="!w-220px" clearable placeholder="请输入学习标题" />
      </el-form-item>
      <el-form-item label="技能编号">
        <el-input-number v-model="queryParams.skillId" :min="1" class="!w-160px" />
      </el-form-item>
      <el-form-item>
        <el-button @click="getList"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button type="primary" plain @click="openForm()">
          <Icon class="mr-5px" icon="ep:plus" />新增
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="标题" prop="title" min-width="160" />
      <el-table-column label="用户编号" prop="userId" width="110" />
      <el-table-column label="技能编号" prop="skillId" width="110" />
      <el-table-column label="学习时长/分钟" prop="duration" width="140" />
      <el-table-column label="学习日期" prop="studyDate" width="120" />
      <el-table-column label="学习内容" prop="content" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openForm(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination v-model:limit="queryParams.pageSize" v-model:page="queryParams.pageNo" :total="total" @pagination="getList" />
  </ContentWrap>

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑学习记录' : '新增学习记录'" width="600px">
    <el-form label-width="110px" :model="form">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="技能编号">
        <el-input-number v-model="form.skillId" :min="1" />
      </el-form-item>
      <el-form-item label="学习时长">
        <el-input-number v-model="form.duration" :min="0" />
      </el-form-item>
      <el-form-item label="学习日期">
        <el-date-picker v-model="form.studyDate" type="date" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="学习内容">
        <el-input v-model="form.content" type="textarea" :rows="4" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import * as LearningRecordApi from '@/api/aistudy/learningRecord'

defineOptions({ name: 'AiStudyLearningRecord' })

const message = useMessage()
const loading = ref(false)
const list = ref<LearningRecordApi.LearningRecordVO[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const queryParams = reactive<any>({ pageNo: 1, pageSize: 10, title: '', skillId: undefined })
const form = reactive<LearningRecordApi.LearningRecordVO>({ title: '', duration: 0 })

const getList = async () => {
  loading.value = true
  try {
    const data = await LearningRecordApi.getLearningRecordPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const openForm = (row?: LearningRecordApi.LearningRecordVO) => {
  Object.assign(form, row || { id: undefined, title: '', skillId: undefined, duration: 0, studyDate: '', content: '' })
  dialogVisible.value = true
}

const submitForm = async () => {
  if (form.id) {
    await LearningRecordApi.updateLearningRecord(form)
  } else {
    await LearningRecordApi.createLearningRecord(form)
  }
  message.success('保存成功')
  dialogVisible.value = false
  await getList()
}

const handleDelete = async (id: number) => {
  await message.delConfirm()
  await LearningRecordApi.deleteLearningRecord(id)
  message.success('删除成功')
  await getList()
}

onMounted(getList)
</script>
