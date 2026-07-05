import request from '@/config/axios'

export interface SkillVO {
  id?: number
  name: string
  category?: string
  parentId?: number
  level?: number
  description?: string
  sort?: number
  status?: number
  progress?: number
  recommendation?: string
  children?: SkillVO[]
}

export const getSkillList = (params?: any): Promise<SkillVO[]> => {
  return request.get({ url: '/aistudy/skill/list', params })
}

export const getSkillTree = (): Promise<SkillVO[]> => {
  return request.get({ url: '/aistudy/skill/tree' })
}

export const createSkill = (data: SkillVO) => {
  return request.post({ url: '/aistudy/skill/create', data })
}

export const updateSkill = (data: SkillVO) => {
  return request.put({ url: '/aistudy/skill/update', data })
}

export const deleteSkill = (id: number) => {
  return request.delete({ url: '/aistudy/skill/delete?id=' + id })
}

export const getSkillProgress = (userId?: number) => {
  return request.get({ url: '/aistudy/skill/progress', params: { userId } })
}

export const updateSkillProgress = (data: any) => {
  return request.post({ url: '/aistudy/skill/progress/update', data })
}

export const evaluateSkill = (progress: number) => {
  return request.get({ url: '/aistudy/skill/evaluate', params: { progress } })
}
