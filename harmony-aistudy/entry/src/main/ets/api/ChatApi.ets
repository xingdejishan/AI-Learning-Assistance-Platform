import { HttpClient } from '../common/HttpClient';
import { ChatRecord, ChatRequest, QuizSubmitResponse } from '../model/ChatModels';

export class ChatApi {
  static async send(data: ChatRequest): Promise<ChatRecord> {
    return HttpClient.post<ChatRecord>('/aistudy/chat/send', data);
  }

  static async submitQuiz(data: Object): Promise<QuizSubmitResponse> {
    return HttpClient.post<QuizSubmitResponse>('/aistudy/quiz/submit', data);
  }
}
