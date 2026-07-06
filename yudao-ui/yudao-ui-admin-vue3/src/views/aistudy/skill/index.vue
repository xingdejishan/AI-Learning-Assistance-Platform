<template>
  <ContentWrap>
    <el-form :inline="true" :model="queryParams">
      <el-form-item :label="t('aiStudy.common.skillName')">
        <el-input
          v-model="queryParams.name"
          class="!w-220px"
          clearable
          :placeholder="t('aiStudy.skill.placeholderName')"
        />
      </el-form-item>
      <el-form-item :label="t('aiStudy.common.category')">
        <el-input
          v-model="queryParams.category"
          class="!w-180px"
          clearable
          :placeholder="t('aiStudy.skill.placeholderCategory')"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="getList">
          <Icon class="mr-5px" icon="ep:search" />{{ t('aiStudy.common.search') }}
        </el-button>
        <el-button type="primary" plain @click="openForm()">
          <Icon class="mr-5px" icon="ep:plus" />{{ t('aiStudy.skill.addSkill') }}
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <el-row :gutter="12" class="ai-skill-grid">
    <el-col :lg="6" :md="24" :xs="24">
      <ContentWrap :title="t('aiStudy.skill.careerTree')">
        <div class="ai-skill-nav__hint">{{ t('aiStudy.skill.treeHint') }}</div>
        <el-tree
          ref="treeRef"
          v-loading="loading"
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
              <el-tag
                v-if="data.progress !== undefined"
                size="small"
                :type="skillTagType(data.progress)"
              >
                {{ data.progress || 0 }}%
              </el-tag>
            </div>
          </template>
        </el-tree>
      </ContentWrap>
    </el-col>

    <el-col :lg="10" :md="24" :xs="24">
      <ContentWrap :title="t('aiStudy.skill.details')">
        <template #actions>
          <el-button v-if="selectedSkill" link type="primary" @click="openForm(selectedSkill)">
            {{ t('aiStudy.common.edit') }}
          </el-button>
          <el-button
            v-if="selectedSkill"
            link
            type="danger"
            @click="handleDelete(selectedSkill.id)"
          >
            {{ t('aiStudy.common.delete') }}
          </el-button>
        </template>

        <template v-if="selectedSkill">
          <div class="ai-skill-detail">
            <div class="ai-skill-detail__header">
              <div>
                <div class="ai-skill-detail__title">{{ selectedSkill.name }}</div>
                <div class="ai-skill-detail__meta">
                  {{ selectedSkill.category || t('aiStudy.common.noCategory') }} ·
                  {{ t('aiStudy.common.level') }} {{ selectedSkill.level || 1 }} ·
                  {{ t('aiStudy.skill.sort') }} {{ selectedSkill.sort || 0 }}
                </div>
              </div>
              <el-tag :type="selectedSkill.status === 0 ? 'success' : 'info'">
                {{
                  selectedSkill.status === 0
                    ? t('aiStudy.common.enabled')
                    : t('aiStudy.common.disabled')
                }}
              </el-tag>
            </div>

            <el-descriptions :column="1" border>
              <el-descriptions-item :label="t('aiStudy.skill.description')">
                {{ selectedSkill.description || t('aiStudy.skill.noDescription') }}
              </el-descriptions-item>
              <el-descriptions-item :label="t('aiStudy.skill.parentSkill')">
                {{ selectedSkill.parentName || t('aiStudy.skill.noParent') }}
              </el-descriptions-item>
              <el-descriptions-item :label="t('aiStudy.skill.recommendation')">
                {{ selectedSkill.recommendation || t('aiStudy.skill.defaultRecommendation') }}
              </el-descriptions-item>
              <el-descriptions-item :label="t('aiStudy.skill.learningGoal')">
                {{ selectedSkill.learningGoal || t('aiStudy.skill.defaultGoal') }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </template>
        <el-empty v-else :description="t('aiStudy.skill.selectedEmpty')" />

        <el-divider content-position="left">{{ t('aiStudy.skill.subSkills') }}</el-divider>
        <el-table :data="selectedChildren" row-key="id" size="small">
          <el-table-column :label="t('aiStudy.common.name')" prop="name" min-width="150" />
          <el-table-column :label="t('aiStudy.common.category')" prop="category" width="110" />
          <el-table-column :label="t('aiStudy.common.level')" prop="level" width="80" />
          <el-table-column :label="t('aiStudy.common.status')" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
                {{ row.status === 0 ? t('aiStudy.common.enabled') : t('aiStudy.common.disabled') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="t('aiStudy.common.actions')" width="150" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="openForm(row)">
                {{ t('aiStudy.common.edit') }}
              </el-button>
              <el-button link type="danger" @click="handleDelete(row.id)">
                {{ t('aiStudy.common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </ContentWrap>
    </el-col>

    <el-col :lg="8" :md="24" :xs="24">
      <el-card shadow="never" class="ai-skill-status-card">
        <template #header>
          <div class="flex items-center justify-between">
            <span>{{ t('aiStudy.skill.learningStatus') }}</span>
            <el-button
              v-if="selectedSkill"
              link
              type="primary"
              @click="openProgressDialog(selectedSkill)"
            >
              {{ t('aiStudy.skill.updateProgress') }}
            </el-button>
          </div>
        </template>

        <template v-if="selectedSkill">
          <div class="ai-skill-status-card__title">{{ selectedSkill.name }}</div>
          <el-progress :percentage="selectedSkillProgress" :stroke-width="14" />

          <div class="ai-skill-status-grid">
            <div>
              <div class="ai-skill-status-grid__label">{{ t('aiStudy.skill.masteryScore') }}</div>
              <div class="ai-skill-status-grid__value">{{ selectedSkillScore }}</div>
            </div>
            <div>
              <div class="ai-skill-status-grid__label">{{ t('aiStudy.skill.currentStatus') }}</div>
              <div class="ai-skill-status-grid__value">
                {{ skillStatusText(selectedSkillProgress) }}
              </div>
            </div>
          </div>

          <el-alert
            :title="skillRecommendation(selectedSkillProgress)"
            type="success"
            show-icon
            :closable="false"
          />

          <el-divider content-position="left">{{ t('aiStudy.skill.progressRecords') }}</el-divider>
          <el-table :data="selectedProgressRows" size="small" max-height="260">
            <el-table-column :label="t('aiStudy.common.userId')" prop="userId" width="90" />
            <el-table-column :label="t('aiStudy.skill.progress')" width="100">
              <template #default="{ row }">
                <el-tag size="small" :type="skillTagType(row.progress)">
                  {{ row.progress || 0 }}%
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              :label="t('aiStudy.skill.suggestion')"
              prop="recommendation"
              min-width="140"
            />
          </el-table>
        </template>
        <el-empty v-else :description="t('aiStudy.skill.statusEmpty')" />
      </el-card>
    </el-col>
  </el-row>

  <el-dialog
    v-model="dialogVisible"
    :title="form.id ? t('aiStudy.skill.editSkill') : t('aiStudy.skill.addSkill')"
    width="620px"
  >
    <el-form ref="formRef" label-width="96px" :model="form" :rules="rules">
      <el-form-item :label="t('aiStudy.common.skillName')" prop="name">
        <el-input v-model="form.name" :placeholder="t('aiStudy.skill.placeholderName')" />
      </el-form-item>
      <el-form-item :label="t('aiStudy.common.category')" prop="category">
        <el-input v-model="form.category" :placeholder="t('aiStudy.skill.categoryPlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('aiStudy.skill.parentSkill')" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="treeList"
          :props="treeProps"
          check-strictly
          :placeholder="t('aiStudy.skill.parentPlaceholder')"
          style="width: 100%"
          value-key="id"
        />
      </el-form-item>
      <el-form-item :label="t('aiStudy.common.level')" prop="level">
        <el-input-number v-model="form.level" :min="1" class="!w-180px" />
      </el-form-item>
      <el-form-item :label="t('aiStudy.skill.sort')" prop="sort">
        <el-input-number v-model="form.sort" :min="0" class="!w-180px" />
      </el-form-item>
      <el-form-item :label="t('aiStudy.common.status')" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="0">{{ t('aiStudy.common.enabled') }}</el-radio>
          <el-radio :value="1">{{ t('aiStudy.common.disabled') }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item :label="t('aiStudy.skill.description')" prop="description">
        <el-input
          v-model="form.description"
          :rows="4"
          :placeholder="t('aiStudy.skill.descriptionPlaceholder')"
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">{{ t('aiStudy.common.cancel') }}</el-button>
      <el-button type="primary" @click="submitForm">{{ t('aiStudy.common.save') }}</el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="progressDialogVisible"
    :title="t('aiStudy.skill.updateProgress')"
    width="520px"
  >
    <el-form label-width="96px" :model="progressForm">
      <el-form-item :label="t('aiStudy.common.skillName')">
        <el-input :model-value="progressTargetName" disabled />
      </el-form-item>
      <el-form-item :label="t('aiStudy.common.userId')">
        <el-input-number v-model="progressForm.userId" :min="1" class="!w-full" />
      </el-form-item>
      <el-form-item :label="t('aiStudy.skill.progress')">
        <el-slider v-model="progressForm.progress" :show-input="true" />
      </el-form-item>
      <el-form-item :label="t('aiStudy.skill.masteryScore')">
        <el-input-number v-model="progressForm.score" :min="0" :max="100" class="!w-full" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="progressDialogVisible = false">
        {{ t('aiStudy.common.cancel') }}
      </el-button>
      <el-button type="primary" @click="submitProgressForm">
        {{ t('aiStudy.common.save') }}
      </el-button>
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

const { t } = useI18n()
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
  if (left === undefined || left === null || right === undefined || right === null) return false
  return String(left) === String(right)
}

const selectedSkill = computed(() =>
  list.value.find((item) => sameId(item.id, selectedSkillId.value))
)
const selectedChildren = computed(() => {
  if (!selectedSkillId.value) return []
  return list.value.filter((item) => sameId(item.parentId, selectedSkillId.value))
})
const selectedProgressRows = computed(() => {
  if (!selectedSkillId.value) return progressList.value
  return progressList.value.filter((item) => sameId(item.skillId, selectedSkillId.value))
})
const selectedSkillProgress = computed(() => selectedProgressRows.value[0]?.progress ?? 0)
const selectedSkillScore = computed(
  () => selectedProgressRows.value[0]?.score ?? selectedSkillProgress.value
)
const progressTargetName = computed(
  () => selectedSkill.value?.name || t('aiStudy.skill.selectedNone')
)
const form = reactive<SkillApi.SkillVO>({ name: '', parentId: 0, level: 1, sort: 0, status: 0 })
const progressForm = reactive({ userId: 1, skillId: 0, progress: 0, score: 0 })

const treeProps = {
  label: 'name',
  children: 'children'
}

const rules = computed(() => ({
  name: [{ required: true, message: t('aiStudy.skill.ruleName'), trigger: 'blur' }]
}))

const skillTagType = (progress?: number) => {
  if ((progress ?? 0) >= 80) return 'success'
  if ((progress ?? 0) >= 60) return 'warning'
  return 'info'
}

const skillStatusText = (progress?: number) => {
  const value = progress ?? 0
  if (value >= 80) return t('aiStudy.skill.mastered')
  if (value >= 60) return t('aiStudy.skill.learning')
  return t('aiStudy.skill.notStarted')
}

const skillRecommendation = (progress?: number) => {
  const value = progress ?? 0
  if (value >= 80) return t('aiStudy.skill.highRecommendation')
  if (value >= 60) return t('aiStudy.skill.mediumRecommendation')
  return t('aiStudy.skill.lowRecommendation')
}

const flattenSkills = (items: SkillNode[]): SkillNode[] =>
  items.flatMap((item) => [item, ...flattenSkills((item.children || []) as SkillNode[])])

const hydrateSkillMeta = (items: SkillNode[]) => {
  const enhance = (nodes: SkillNode[], parentName?: string): SkillNode[] =>
    nodes.map((item) => {
      const progressRow = progressList.value.find((row) => sameId(row.skillId, item.id))
      const progress = progressRow?.progress ?? item.progress ?? 0
      const enhanced: SkillNode = {
        ...item,
        parentName,
        progress,
        recommendation: progressRow?.recommendation || skillRecommendation(progress),
        learningGoal: item.description || t('aiStudy.skill.defaultGoal')
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
    progressList.value = (progressRows as SkillProgressRow[]).map((item) => ({
      ...item,
      recommendation: skillRecommendation(item.progress)
    }))
    treeList.value = hydrateSkillMeta(tree as SkillNode[])
    list.value = skills?.length ? (skills as SkillNode[]) : flattenSkills(treeList.value)
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
  if (match) selectedSkillId.value = match.id
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
  message.success(t('aiStudy.skill.saved'))
  dialogVisible.value = false
  await getList()
}

const handleDelete = async (id?: number) => {
  if (!id) return
  await message.delConfirm()
  await SkillApi.deleteSkill(id)
  message.success(t('aiStudy.skill.deleted'))
  if (sameId(selectedSkillId.value, id)) selectedSkillId.value = undefined
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
  message.success(t('aiStudy.skill.progressUpdated'))
  progressDialogVisible.value = false
  await getList()
}

onMounted(getList)
</script>

<style scoped lang="scss">
.ai-skill-grid {
  align-items: stretch;
  font-size: var(--app-font-size-base);
}

.ai-skill-nav__hint {
  margin-bottom: 12px;
  font-size: var(--app-font-size-small);
  line-height: 1.6;
  color: var(--el-text-color-secondary);
}

.ai-skill-tree-node {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  font-size: var(--app-font-size-base);
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
  font-size: var(--app-title-font-size);
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.ai-skill-detail__meta {
  margin-top: 6px;
  font-size: var(--app-font-size-small);
  color: var(--el-text-color-secondary);
}

.ai-skill-status-card {
  min-height: 100%;
}

.ai-skill-status-card__title {
  margin-bottom: 12px;
  font-size: var(--app-font-size-large);
  font-weight: 600;
}

.ai-skill-status-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin: 16px 0;

  &__label {
    font-size: var(--app-font-size-small);
    color: var(--el-text-color-secondary);
  }

  &__value {
    margin-top: 6px;
    font-size: var(--app-font-size-base);
    font-weight: 600;
    color: var(--el-text-color-primary);
  }
}
</style>
