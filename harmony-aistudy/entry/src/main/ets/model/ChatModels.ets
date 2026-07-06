export interface Quiz {
  question?: string;
  answer?: string;
  difficulty?: number;
}

export interface ChatRequest {
  skillId?: number;
  message: string;
  question: string;
  conversationId?: string;
  tutorCode: string;
}

export interface ChatRecord {
  id?: number;
  question?: string;
  answer?: string;
  conversationId?: string;
  modelName?: string;
  messageId?: string;
  knowledgePoints?: string[];
  quiz?: Quiz;
  mastery?: number;
  xpGained?: number;
  totalXp?: number;
  levelNo?: number;
  nextSuggestion?: string;
}

export interface QuizSubmitResponse {
  correct?: boolean;
  score?: number;
  feedback?: string;
  xpGained?: number;
  mastery?: number;
  totalXp?: number;
  levelNo?: number;
}
