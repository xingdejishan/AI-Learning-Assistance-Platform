<template>
  <el-row :gutter="12">
    <el-col :lg="16" :md="24" :xs="24">
      <ContentWrap title="AI 智能辅导">
        <div class="chat-answer">
          <div v-if="currentAnswer">
            <div class="chat-question">{{ currentQuestion }}</div>
            <el-divider />
            <div class="answer-text">{{ currentAnswer }}</div>
            <div v-if="knowledgePoints.length" class="mt-12px">
              <el-tag v-for="point in knowledgePoints" :key="point" class="mr-8px mb-8px" type="success">
                {{ point }}
              </el-tag>
            </div>
            <div class="mt-12px flex items-center gap-12px">
              <el-tag type="info">会话：{{ conversationId }}</el-tag>
              <el-tag v-if="mastery !== undefined" type="warning">掌握度：{{ mastery }}%</el-tag>
              <el-tag v-if="xpGained" type="success">经验 +{{ xpGained }}</el-tag>
            </div>
            <el-alert v-if="nextSuggestion" class="mt-12px" type="info" :closable="false" :title="nextSuggestion" />
            <el-card v-if="quiz?.question" class="mt-12px" shadow="never">
              <template #header>小测题</template>
              <div>{{ quiz.question }}</div>
              <div v-if="quiz.answer" class="quiz-answer">参考答案：{{ quiz.answer }}</div>
              <el-input
                v-model="quizAnswer"
                class="mt-12px"
                type="textarea"
                :rows="3"
                placeholder="请输入你的答案"
              />
              <el-button class="mt-12px" type="primary" :loading="quizSubmitting" @click="handleSubmitQuiz">
                提交并由 AI 判题
              </el-button>
              <el-alert
                v-if="quizResult"
                class="mt-12px"
                :title="quizResult.correct ? '回答正确' : '还需要复习'"
                :type="quizResult.correct ? 'success' : 'warning'"
                show-icon
                :closable="false"
              >
                <div>得分：{{ quizResult.score }}</div>
                <div>反馈：{{ quizResult.feedback }}</div>
                <div>XP：+{{ quizResult.xpGained || 0 }}</div>
                <div>最新掌握度：{{ quizResult.mastery || 0 }}%</div>
              </el-alert>
            </el-card>
          </div>
          <el-empty v-else description="输入学习或求职问题，AI 会沉淀知识点并生成复习任务" />
        </div>

        <el-form class="mt-12px" label-width="88px">
          <el-form-item label="关联技能">
            <el-select v-model="skillId" clearable filterable placeholder="请选择技能，便于更新掌握度">
              <el-option v-for="item in skillOptions" :key="item.id" :label="item.name" :value="item.id!" />
            </el-select>
          </el-form-item>
          <el-form-item label="问题">
            <el-input v-model="question" type="textarea" :rows="5" placeholder="请输入你的问题" />
          </el-form-item>
        </el-form>

        <div class="text-right">
          <el-button type="primary" :loading="sending" @click="sendQuestion">
            <Icon class="mr-5px" icon="ep:promotion" />发送
          </el-button>
        </div>
      </ContentWrap>
    </el-col>
    <el-col :lg="8" :md="24" :xs="24">
      <ContentWrap title="历史对话">
        <el-timeline>
          <el-timeline-item v-for="item in history" :key="item.id" :timestamp="item.createTime">
            <div class="history-question">{{ item.question }}</div>
            <div class="history-answer">{{ item.answer }}</div>
          </el-timeline-item>
        </el-timeline>
      </ContentWrap>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import * as ChatApi from '@/api/aistudy/chat'
import * as LearningFlowApi from '@/api/aistudy/learningFlow'
import * as SkillApi from '@/api/aistudy/skill'

defineOptions({ name: 'AiStudyChat' })

const message = useMessage()
const question = ref('')
const currentQuestion = ref('')
const currentAnswer = ref('')
const conversationId = ref('')
const sending = ref(false)
const skillId = ref<number>()
const mastery = ref<number>()
const xpGained = ref<number>()
const nextSuggestion = ref('')
const knowledgePoints = ref<string[]>([])
const quiz = ref<ChatApi.ChatRecordVO['quiz']>()
const quizAnswer = ref('')
const quizResult = ref<LearningFlowApi.QuizSubmitRespVO>()
const quizSubmitting = ref(false)
const history = ref<ChatApi.ChatRecordVO[]>([])
const skillOptions = ref<SkillApi.SkillVO[]>([])
const currentChatId = ref<number>()

const flattenSkills = (items: SkillApi.SkillVO[]): SkillApi.SkillVO[] => {
  return items.flatMap((item) => [item, ...flattenSkills(item.children || [])])
}

const loadHistory = async () => {
  history.value = await ChatApi.getHistory({ conversationId: conversationId.value || undefined })
}

const loadSkills = async () => {
  skillOptions.value = flattenSkills(await SkillApi.getSkillTree()).filter((item) => item.id !== undefined)
}

const sendQuestion = async () => {
  if (!question.value.trim()) {
    message.warning('请输入问题')
    return
  }
  sending.value = true
  try {
    const data = await ChatApi.sendAiChatMessage({
      message: question.value,
      conversationId: conversationId.value,
      skillId: skillId.value
    })
    currentQuestion.value = data.question
    currentAnswer.value = data.answer || ''
    currentChatId.value = data.id
    conversationId.value = data.conversationId || ''
    knowledgePoints.value = data.knowledgePoints || []
    quiz.value = data.quiz
    quizAnswer.value = ''
    quizResult.value = undefined
    mastery.value = data.mastery
    xpGained.value = data.xpGained
    nextSuggestion.value = data.nextSuggestion || ''
    question.value = ''
    await loadHistory()
  } finally {
    sending.value = false
  }
}

const handleSubmitQuiz = async () => {
  if (!skillId.value || !quiz.value?.question || !quiz.value?.answer) {
    message.warning('当前小测题信息不完整')
    return
  }
  if (!quizAnswer.value.trim()) {
    message.warning('请先填写你的答案')
    return
  }
  quizSubmitting.value = true
  try {
    quizResult.value = await LearningFlowApi.submitQuiz({
      skillId: skillId.value,
      chatId: currentChatId.value,
      question: quiz.value.question,
      referenceAnswer: quiz.value.answer,
      userAnswer: quizAnswer.value
    })
    mastery.value = quizResult.value.mastery
    xpGained.value = quizResult.value.xpGained
    message.success(quizResult.value.correct ? '回答正确' : '回答还需改进')
  } finally {
    quizSubmitting.value = false
  }
}

onMounted(() => {
  loadHistory()
  loadSkills()
})
</script>

<style scoped>
.chat-answer {
  min-height: 260px;
  padding: 16px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background: var(--el-fill-color-blank);
}
.chat-question {
  font-weight: 600;
}
.answer-text {
  line-height: 1.8;
}
.history-question {
  color: var(--el-text-color-primary);
  font-weight: 600;
}
.history-answer {
  margin-top: 6px;
  color: var(--el-text-color-secondary);
  line-height: 1.6;
}
.quiz-answer {
  margin-top: 8px;
  color: var(--el-text-color-secondary);
}
</style>
