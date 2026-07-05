import request from '@/config/axios'

export interface ChatRecordVO {
  id?: number
  userId?: number
  question: string
  answer?: string
  conversationId?: string
  modelName?: string
  messageId?: string
  knowledgePoints?: string[]
  quiz?: {
    question?: string
    answer?: string
    difficulty?: number
  }
  mastery?: number
  xpGained?: number
  nextSuggestion?: string
  createTime?: string
}

export const ask = (data: {
  question: string
  conversationId?: string
  skillId?: number
  tutorCode?: string
}): Promise<ChatRecordVO> => {
  return request.post({ url: '/aistudy/chat/ask', data })
}

export const sendAiChatMessage = (data: {
  skillId?: number
  message: string
  conversationId?: string
}): Promise<ChatRecordVO> => {
  return request.post({ url: '/aistudy/chat/send', data })
}

export const getHistory = (params?: { userId?: number; conversationId?: string }) => {
  return request.get({ url: '/aistudy/chat/history', params })
}
