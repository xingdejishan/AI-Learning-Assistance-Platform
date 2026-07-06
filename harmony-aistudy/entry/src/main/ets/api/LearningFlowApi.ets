import { HttpClient } from '../common/HttpClient';
import { KnowledgePoint, LearningFlowSummary, LearningProfile, Report, ReviewSubmitResponse } from '../model/LearningFlowModels';

export class LearningFlowApi {
  static async todayReviews(): Promise<KnowledgePoint[]> {
    return HttpClient.get<KnowledgePoint[]>('/aistudy/review/today');
  }

  static async submitReview(knowledgePointId: number, remembered: boolean): Promise<ReviewSubmitResponse> {
    return HttpClient.post<ReviewSubmitResponse>('/aistudy/review/submit', { knowledgePointId, remembered });
  }

  static async profile(): Promise<LearningProfile> {
    return HttpClient.get<LearningProfile>('/aistudy/profile');
  }

  static async generateReport(): Promise<Report> {
    return HttpClient.post<Report>('/aistudy/report/generate');
  }

  static async summary(): Promise<LearningFlowSummary> {
    return HttpClient.get<LearningFlowSummary>('/aistudy/learning-flow/summary');
  }
}
