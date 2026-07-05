import request from '@/config/axios'

export interface DashboardVO {
  totalStudyDuration: number
  todayLearningRecordCount: number
  aiChatCount: number
  resumeDiagnosisCount: number
  skillCompletionRate: number
  studyDurationTrend: any[]
  skillProgressDistribution: any[]
  aiAndResumeTrend: any[]
}

export const getDashboard = (): Promise<DashboardVO> => {
  return request.get({ url: '/aistudy/dashboard/summary' })
}
