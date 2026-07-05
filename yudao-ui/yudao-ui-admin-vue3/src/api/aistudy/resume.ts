import request from '@/config/axios'

export interface ResumeVO {
  id?: number
  userId?: number
  fileUrl?: string
  fileName?: string
  status?: number
  score?: number
  analysisResult?: string
  optimizeSuggestion?: string
  createTime?: string
}

export const uploadResume = (data: FormData): Promise<ResumeVO> => {
  return request.post({ url: '/aistudy/resume/upload', data })
}

export const diagnoseResume = (params: { fileUrl: string; fileName: string; userId?: number }) => {
  return request.post({ url: '/aistudy/resume/diagnose', params })
}

export const getResumeList = (userId?: number) => {
  return request.get({ url: '/aistudy/resume/list', params: { userId } })
}

export const getResume = (id: number) => {
  return request.get({ url: '/aistudy/resume/get?id=' + id })
}
