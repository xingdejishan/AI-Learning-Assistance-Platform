<template>
  <ContentWrap>
    <el-form :inline="true" :model="queryParams">
      <el-form-item label="技能名称">
        <el-input v-model="queryParams.name" class="!w-220px" clearable placeholder="请输入技能名称" />
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="queryParams.category" class="!w-180px" clearable placeholder="请输入分类" />
      </el-form-item>
      <el-form-item>
        <el-button @click="getList"><Icon class="mr-5px" icon="ep:search" />搜索</el-button>
        <el-button type="primary" plain @click="openForm()">
          <Icon class="mr-5px" icon="ep:plus" />新增技能
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <el-row :gutter="12" class="ai-skill-grid">
    <el-col :lg="6" :md="24" :xs="24">
      <ContentWrap title="职业成长技能树">
        <div class="ai-skill-nav__hint">
          选择一个技能方向，查看层级、学习进度和推荐下一步。
        </div>
        <el-tree
          ref="treeRef"
          :current-node-key="selectedSkillId"
          :data="treeList"
          default-expand-all
          highlight-current
          node-key="id"
          :props="treeProps"
          @node-click="handleSelectSkill"
        >
          <template #default="{ data }">
            <div class="ai-skill-tree-node">
              <div class="ai-skill-tree-node__name">{{ data.name }}</div>
              <el-tag v-if="data.progress !== undefined" size="small" :type="skillTagType(data.progress)">
                {{ data.progress || 0 }}%
              </el-tag>
            </div>
          </template>
        </el-tree>
      </ContentWrap>
    </el-col>

    <el-col :lg="10" :md="24" :xs="24">
      <ContentWrap title="技能详情">
        <template #actions>
          <el-button v-if="selectedSkill" link type="primary" @click="openForm(selectedSkill)">编辑</el-button>
          <el-button v-if="selectedSkill" link type="danger" @click="handleDelete(selectedSkill.id)">删除</el-button>
        </template>

        <template v-if="selectedSkill">
          <div class="ai-skill-detail">
            <div class="ai-skill-detail__header">
              <div>
                <div class="ai-skill-detail__title">{{ selectedSkill.name }}</div>
                <div class="ai-skill-detail__meta">
                  {{ selectedSkill.category || '未分类' }} · 层级 {{ selectedSkill.level || 1 }} · 排序 {{ selectedSkill.sort || 0 }}
                </div>
              </div>
              <el-tag :type="selectedSkill.status === 0 ? 'success' : 'info'">
                {{ selectedSkill.status === 0 ? '启用' : '停用' }}
              </el-tag>
            </div>

            <el-descriptions :column="1" border>
              <el-descriptions-item label="技能描述">
                {{ selectedSkill.description || '暂无描述，建议补充技能能力边界和目标产出。' }}
              </el-descriptions-item>
              <el-descriptions-item label="前置技能">
                {{ selectedSkill.parentName || '无前置技能' }}
              </el-descriptions-item>
              <el-descriptions-item label="推荐学习内容">
                {{ selectedSkill.recommendation || '结合项目实战、题目练习和复盘总结推进学习。' }}
              </el-descriptions-item>
              <el-descriptions-item label="学习目标">
                {{ selectedSkill.learningGoal || '掌握核心知识点并形成可展示作品。' }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
        <el-empty v-else description="请选择左侧技能节点查看详情" />

        <el-divider content-position="left">子技能</el-divider>
        <el-table :data="selectedChildren" row-key="id" size="small">
          <el-table-column label="名称" prop="name" min-width="150" />
          <el-table-column label="分类" prop="category" width="110" />
          <el-table-column label="层级" prop="level" width="80" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
                {{ row.status === 0 ? '启用' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openForm(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </ContentWrap>
    </el-col>

    <el-col :lg="8" :md="24" :xs="24">
      <el-card shadow="never" class="ai-skill-status-card">
        <template #header>
          <div class="flex items-center justify-between">
            <span>学习状态</span>
            <el-button v-if="selectedSkill" link type="primary" @click="openProgressDialog(selectedSkill)">
              更新进度
            </el-button>
          </div>
        </template>

        <template v-if="selectedSkill">
          <div class="ai-skill-status-card__title">{{ selectedSkill.name }}</div>
          <el-progress :percentage="selectedSkillProgress" :stroke-width="14" />

          <div class="ai-skill-status-grid">
            <div>
              <div class="ai-skill-status-grid__label">掌握分数</div>
              <div class="ai-skill-status-grid__value">{{ selectedSkillScore }}</div>
            </div>
            <div>
              <div class="ai-skill-status-grid__label">当前状态</div>
              <div class="ai-skill-status-grid__value">{{ skillStatusText(selectedSkillProgress) }}</div>
            </div>
          </div>

          <el-alert :title="skillRecommendation(selectedSkillProgress)" type="success" show-icon :closable="false" />

          <el-divider content-position="left">进度记录</el-divider>
          <el-table :data="selectedProgressRows" size="small" max-height="260">
            <el-table-column label="用户" prop="userId" width="90" />
            <el-table-column label="进度" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="skillTagType(row.progress)">{{ row.progress || 0 }}%</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="建议" prop="recommendation" min-width="140" />
          </el-table>
        </template>
        <el-empty v-else description="请选择一个技能查看学习状态" />
      </el-card>
    </el-col>
  </el-row>

  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑技能' : '新增技能'" width="620px">
    <el-form ref="formRef" label-width="96px" :model="form" :rules="rules">
      <el-form-item label="技能名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入技能名称" />
      </el-form-item>
      <el-form-item label="技能分类" prop="category">
        <el-input v-model="form.category" placeholder="例如：Java 后端开发" />
      </el-form-item>
      <el-form-item label="父级技能" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="treeList"
          :props="treeProps"
          check-strictly
          placeholder="请选择父级技能"
          style="width: 100%"
          value-key="id"
        />
      </el-form-item>
      <el-form-item label="层级" prop="level">
        <el-input-number v-model="form.level" :min="1" class="!w-180px" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="0" class="!w-180px" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">启用</el-radio>
          <el-radio :value="1">停用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="技能描述" prop="description">
        <el-input v-model="form.description" :rows="4" placeholder="描述技能能力和学习目标" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="progressDialogVisible" title="更新学习进度" width="520px">
    <el-form label-width="96px" :model="progressForm">
      <el-form-item label="技能名称">
        <el-input :model-value="progressTargetName" disabled />
      </el-form-item>
      <el-form-item label="用户编号">
        <el-input-number v-model="progressForm.userId" :min="1" class="!w-full" />
      </el-form-item>
      <el-form-item label="学习进度">
        <el-slider v-model="progressForm.progress" :show-input="true" />
      </el-form-item>
      <el-form-item label="掌握分数">
        <el-input-number v-model="progressForm.score" :min="0" :max="100" class="!w-full" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="progressDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitProgressForm">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import * as SkillApi from '@/api/aistudy/skill'

defineOptions({ name: 'AiStudySkill' })

type SkillNode = SkillApi.SkillVO & {
  parentName?: string
  recommendation?: string
  learningGoal?: string
}

type SkillProgressRow = {
  userId: number
  skillId: number
  progress: number
  score?: number
  recommendation?: string
}

const message = useMessage()
const loading = ref(false)
const list = ref<SkillNode[]>([])
const treeList = ref<SkillNode[]>([])
const progressList = ref<SkillProgressRow[]>([])
const dialogVisible = ref(false)
const progressDialogVisible = ref(false)
const formRef = ref()
const queryParams = reactive({ name: '', category: '' })
const selectedSkillId = ref<string | number>()
const sameId = (left?: string | number | null, right?: string | number | null) => {
  if (left === undefined || left === null || right === undefined || right === null) {
    return false
  }
  return String(left) === String(right)
}

const selectedSkill = computed(() => list.value.find((item) => sameId(item.id, selectedSkillId.value)))
const selectedChildren = computed(() => {
  if (!selectedSkillId.value) return []
  return list.value.filter((item) => sameId(item.parentId, selectedSkillId.value))
})
const selectedProgressRows = computed(() => {
  if (!selectedSkillId.value) return progressList.value
  return progressList.value.filter((item) => sameId(item.skillId, selectedSkillId.value))
})
const selectedSkillProgress = computed(() => {
  const progressRow = selectedProgressRows.value[0]
  return progressRow?.progress ?? 0
})
const selectedSkillScore = computed(() => {
  const progressRow = selectedProgressRows.value[0]
  return progressRow?.score ?? selectedSkillProgress.value
})
const progressTargetName = computed(() => selectedSkill.value?.name || '未选择')
const form = reactive<SkillApi.SkillVO>({ name: '', parentId: 0, level: 1, sort: 0, status: 0 })
const progressForm = reactive({ userId: 1, skillId: 0, progress: 0, score: 0 })

const treeProps = {
  label: 'name',
  children: 'children'
}

const rules = {
  name: [{ required: true, message: '请输入技能名称', trigger: 'blur' }]
}

const skillTagType = (progress?: number) => {
  if ((progress ?? 0) >= 80) return 'success'
  if ((progress ?? 0) >= 60) return 'warning'
  return 'info'
}

const skillStatusText = (progress?: number) => {
  const value = progress ?? 0
  if (value >= 80) return '已掌握'
  if (value >= 60) return '学习中'
  return '未开始'
}

const skillRecommendation = (progress?: number) => {
  const value = progress ?? 0
  if (value >= 80) return '可以进入下一技能，建议开始更高阶的学习路径。'
  if (value >= 60) return '建议强化练习，优先完成项目实战和专项训练。'
  return '建议继续学习，先补齐基础知识和前置技能。'
}

const hydrateSkillMeta = (items: SkillNode[]) => {
  const nameMap = new Map<number, string>()
  items.forEach((item) => {
    if (item.id) nameMap.set(item.id, item.name)
  })

  const enhance = (nodes: SkillNode[], parentName?: string): SkillNode[] =>
    nodes.map((item) => {
      const progressRow = progressList.value.find((row) => row.skillId === item.id)
      const progress = progressRow?.progress ?? item.progress ?? 0
      const enhanced: SkillNode = {
        ...item,
        parentName,
        progress,
        recommendation: progressRow?.recommendation || skillRecommendation(progress),
        learningGoal: item.description || '完成基础能力掌握并形成可展示的学习成果。'
      }
      if (item.children?.length) {
        enhanced.children = enhance(item.children as SkillNode[], item.name)
      }
      return enhanced
    })

  return enhance(items)
}

const getList = async () => {
  loading.value = true
  try {
    const [skills, tree, progressRows] = await Promise.all([
      SkillApi.getSkillList(queryParams),
      SkillApi.getSkillTree(),
      SkillApi.getSkillProgress()
    ])
    list.value = skills as SkillNode[]
    progressList.value = (progressRows as SkillProgressRow[]).map((item) => ({
      ...item,
      recommendation: skillRecommendation(item.progress)
    }))
    treeList.value = hydrateSkillMeta(tree as SkillNode[])
    if (!selectedSkillId.value && treeList.value.length) {
      selectedSkillId.value = treeList.value[0].id
    }
    syncSelectedFromTree()
  } finally {
    loading.value = false
  }
}

const syncSelectedFromTree = () => {
  if (!selectedSkillId.value) return
  const match = list.value.find((item) => sameId(item.id, selectedSkillId.value))
  if (match) {
    selectedSkillId.value = match.id
  }
}

const handleSelectSkill = (data: SkillNode) => {
  selectedSkillId.value = data.id
}

const openForm = (row?: SkillNode) => {
  Object.assign(
    form,
    row || {
      id: undefined,
      name: '',
      category: '',
      parentId: selectedSkillId.value || 0,
      level: selectedSkillId.value ? (selectedSkill.value?.level || 1) + 1 : 1,
      sort: 0,
      status: 0,
      description: ''
    }
  )
  dialogVisible.value = true
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (form.id) {
    await SkillApi.updateSkill(form)
  } else {
    await SkillApi.createSkill(form)
  }
  message.success('保存成功')
  dialogVisible.value = false
  await getList()
}

const handleDelete = async (id?: number) => {
  if (!id) return
  await message.delConfirm()
  await SkillApi.deleteSkill(id)
  message.success('删除成功')
  if (selectedSkillId.value === id) {
    selectedSkillId.value = undefined
  }
  await getList()
}

const openProgressDialog = (skill: SkillNode) => {
  progressForm.skillId = skill.id || 0
  progressForm.progress = selectedSkillProgress.value
  progressForm.score = selectedSkillScore.value
  progressDialogVisible.value = true
}

const submitProgressForm = async () => {
  await SkillApi.updateSkillProgress(progressForm)
  message.success('学习进度已更新')
  progressDialogVisible.value = false
  await getList()
}

watch(
  () => queryParams,
  () => {
    // 搜索后重载技能树和详情，保留当前选择逻辑。
  },
  { deep: true }
)

watch(
  () => treeList.value,
  () => {
    if (!selectedSkillId.value && treeList.value.length) {
      selectedSkillId.value = treeList.value[0].id
    }
  }
)

onMounted(getList)
</script>

<style scoped lang="scss">
.ai-skill-grid {
  align-items: stretch;
}

.ai-skill-nav__hint {
  margin-bottom: 12px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.6;
}

.ai-skill-tree-node {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.ai-skill-tree-node__name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ai-skill-detail {
  margin-bottom: 12px;
}

.ai-skill-detail__header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.ai-skill-detail__title {
  color: var(--el-text-color-primary);
  font-size: 18px;
  font-weight: 700;
}

.ai-skill-detail__meta {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.ai-skill-status-card {
  min-height: 100%;
}

.ai-skill-status-card__title {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
}

.ai-skill-status-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 16px 0;

  &__label {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }

  &__value {
    margin-top: 6px;
    color: var(--el-text-color-primary);
    font-size: 15px;
    font-weight: 600;
  }
}
</style>
