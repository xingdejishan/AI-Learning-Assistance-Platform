<template>
  <el-row :gutter="12" class="ai-chat-page">
    <el-col :lg="16" :md="24" :xs="24">
      <ContentWrap :title="t('aiStudy.chat.title')">
        <div class="chat-answer">
          <div v-if="currentAnswer">
            <div class="chat-question">{{ currentQuestion }}</div>
            <el-divider />
            <div class="answer-text">{{ currentAnswer }}</div>
            <div v-if="knowledgePoints.length" class="mt-12px">
              <el-tag
                v-for="point in knowledgePoints"
                :key="point"
                class="mr-8px mb-8px"
                type="success"
              >
                {{ point }}
              </el-tag>
            </div>
            <div class="mt-12px flex items-center gap-12px">
              <el-tag type="info"
                >{{ t('aiStudy.chat.conversation') }}: {{ conversationId }}</el-tag
              >
              <el-tag v-if="mastery !== undefined" type="warning">
                {{ t('aiStudy.chat.mastery') }}: {{ mastery }}%
              </el-tag>
              <el-tag v-if="xpGained" type="success">
                {{ t('aiStudy.chat.xpGained') }} +{{ xpGained }}
              </el-tag>
            </div>
            <el-alert
              v-if="nextSuggestion"
              class="mt-12px"
              type="info"
              :closable="false"
              :title="nextSuggestion"
            />
            <el-card v-if="quiz?.question" class="mt-12px" shadow="never">
              <template #header>{{ t('aiStudy.chat.quiz') }}</template>
              <div>{{ quiz.question }}</div>
              <div v-if="quiz.answer" class="quiz-answer">
                {{ t('aiStudy.chat.referenceAnswer') }}: {{ quiz.answer }}
              </div>
              <el-input
                v-model="quizAnswer"
                class="mt-12px"
                type="textarea"
                :rows="3"
                :placeholder="t('aiStudy.chat.answerPlaceholder')"
              />
              <el-button
                class="mt-12px"
                type="primary"
                :loading="quizSubmitting"
                @click="handleSubmitQuiz"
              >
                {{ t('aiStudy.chat.submitQuiz') }}
              </el-button>
              <el-alert
                v-if="quizResult"
                class="mt-12px"
                :title="
                  quizResult.correct ? t('aiStudy.chat.correct') : t('aiStudy.chat.needReview')
                "
                :type="quizResult.correct ? 'success' : 'warning'"
                show-icon
                :closable="false"
              >
                <div>{{ t('aiStudy.chat.score') }}: {{ quizResult.score }}</div>
                <div>{{ t('aiStudy.chat.feedback') }}: {{ quizResult.feedback }}</div>
                <div>{{ t('aiStudy.common.xp') }}: {{ quizResult.xpGained || 0 }}</div>
                <div>{{ t('aiStudy.chat.latestMastery') }}: {{ quizResult.mastery || 0 }}%</div>
              </el-alert>
            </el-card>
          </div>
          <el-empty v-else :description="t('aiStudy.chat.empty')" />
        </div>

        <el-form class="mt-12px" label-width="88px">
          <el-form-item :label="t('aiStudy.chat.relatedSkill')">
            <el-select
              v-model="skillId"
              clearable
              filterable
              :placeholder="t('aiStudy.chat.skillPlaceholder')"
            >
              <el-option
                v-for="item in skillOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id!"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="t('aiStudy.chat.question')">
            <el-input
              v-model="question"
              type="textarea"
              :rows="5"
              :placeholder="t('aiStudy.chat.questionPlaceholder')"
            />
          </el-form-item>
        </el-form>

        <div class="text-right">
          <el-button type="primary" :loading="sending" @click="sendQuestion">
            <Icon class="mr-5px" icon="ep:promotion" />{{ t('aiStudy.chat.send') }}
          </el-button>
        </div>
      </ContentWrap>
    </el-col>
    <el-col :lg="8" :md="24" :xs="24">
      <ContentWrap :title="t('aiStudy.chat.history')">
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

const { t } = useI18n()
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
  skillOptions.value = flattenSkills(await SkillApi.getSkillTree()).filter(
    (item) => item.id !== undefined
  )
}

const sendQuestion = async () => {
  if (!question.value.trim()) {
    message.warning(t('aiStudy.chat.inputQuestion'))
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
    message.warning(t('aiStudy.chat.quizIncomplete'))
    return
  }
  if (!quizAnswer.value.trim()) {
    message.warning(t('aiStudy.chat.inputQuizAnswer'))
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
    message.success(
      quizResult.value.correct ? t('aiStudy.chat.correctMessage') : t('aiStudy.chat.improveMessage')
    )
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
.ai-chat-page {
  font-size: var(--app-font-size-base);
}

.chat-answer {
  min-height: 260px;
  padding: 16px;
  background:
    linear-gradient(90deg, rgb(122 162 255 / 4%) 1px, transparent 1px),
    linear-gradient(rgb(122 162 255 / 4%) 1px, transparent 1px), var(--app-card-bg);
  background-size: 18px 18px;
  border: 1px solid var(--app-border);
  border-radius: 0;
  box-shadow: inset 0 0 0 1px rgb(255 183 220 / 6%);
}

.chat-question {
  font-family: var(--app-pixel-font);
  font-size: var(--app-font-size-base);
  font-weight: 600;
  color: var(--app-accent);
  text-shadow: var(--app-pixel-shadow);
}

.answer-text {
  font-size: var(--app-font-size-base);
  line-height: 1.8;
}

.history-question {
  font-family: var(--app-pixel-font);
  font-size: var(--app-font-size-base);
  font-weight: 600;
  color: var(--app-text);
}

.history-answer {
  margin-top: 6px;
  font-size: var(--app-font-size-base);
  line-height: 1.6;
  color: var(--el-text-color-secondary);
}

.quiz-answer {
  margin-top: 8px;
  font-size: var(--app-font-size-base);
  color: var(--el-text-color-secondary);
}
</style>
