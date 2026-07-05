import request from '@/config/axios'

export interface LearningRecordVO {
  id?: number
  userId?: number
  skillId?: number
  title: string
  duration?: number
  studyDate?: string
  content?: string
}

export const getLearningRecordPage = (params: PageParam) => {
  return request.get({ url: '/aistudy/learning-record/page', params })
}

export const createLearningRecord = (data: LearningRecordVO) => {
  return request.post({ url: '/aistudy/learning-record/create', data })
}

export const updateLearningRecord = (data: LearningRecordVO) => {
  return request.put({ url: '/aistudy/learning-record/update', data })
}

export const deleteLearningRecord = (id: number) => {
  return request.delete({ url: '/aistudy/learning-record/delete?id=' + id })
}

export const getDurationByUser = () => {
  return request.get({ url: '/aistudy/learning-record/duration-by-user' })
}

export const getDurationBySkill = () => {
  return request.get({ url: '/aistudy/learning-record/duration-by-skill' })
}
