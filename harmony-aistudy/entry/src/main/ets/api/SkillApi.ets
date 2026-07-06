import { HttpClient } from '../common/HttpClient';
import { Skill, SkillProgress } from '../model/SkillModels';

export class SkillApi {
  static async list(): Promise<Skill[]> {
    return HttpClient.get<Skill[]>('/aistudy/skill/list');
  }

  static async progress(): Promise<SkillProgress[]> {
    return HttpClient.get<SkillProgress[]>('/aistudy/skill/progress');
  }
}
