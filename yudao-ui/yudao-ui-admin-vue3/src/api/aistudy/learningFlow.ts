import request from '@/config/axios'

export interface LearningProfileVO {
  id?: number
  userId?: number
  totalXp?: number
  levelNo?: number
  currentStreak?: number
  longestStreak?: number
  lastActiveDate?: string
  weeklyXp?: string
}

export interface KnowledgePointVO {
  id: number
  userId?: number
  skillId?: number
  skillName?: string
  content: string
  stage?: number
  nextReviewTime?: string
  lastReviewTime?: string
}

export interface LearningFlowSummaryVO {
  profile?: LearningProfileVO
  todayReviewCount?: number
  masteryRanking?: any[]
  stageDistribution?: any[]
  recentEvents?: any[]
}

export interface ReviewSubmitRespVO {
  newStage?: number
  nextReviewTime?: string
  mastery?: number
  xpGained?: number
}

export interface QuizSubmitReqVO {
  skillId: number
  chatId?: number
  question: string
  referenceAnswer: string
  userAnswer: string
}

export interface QuizSubmitRespVO {
  correct?: boolean
  score?: number
  feedback?: string
  xpGained?: number
  mastery?: number
  totalXp?: number
  levelNo?: number
}

export interface ReportVO {
  title?: string
  summary?: string
  strongPoints?: string[]
  weakPoints?: string[]
  nextPlan?: string[]
  riskReminder?: string
}

export const getSummary = (): Promise<LearningFlowSummaryVO> => {
  return request.get({ url: '/aistudy/learning-flow/summary' })
}

export const getTodayReviews = (): Promise<KnowledgePointVO[]> => {
  return request.get({ url: '/aistudy/review/today' })
}

export const submitReview = (data: { knowledgePointId: number; remembered: boolean }): Promise<ReviewSubmitRespVO> => {
  return request.post({ url: '/aistudy/review/submit', data })
}

export const submitQuiz = (data: QuizSubmitReqVO): Promise<QuizSubmitRespVO> => {
  return request.post({ url: '/aistudy/quiz/submit', data })
}

export const getProfile = (): Promise<LearningProfileVO> => {
  return request.get({ url: '/aistudy/profile' })
}

export const generateReport = (): Promise<ReportVO> => {
  return request.post({ url: '/aistudy/report/generate' })
}
