export interface Skill {
  id?: number;
  name?: string;
  category?: string;
  parentId?: number;
  level?: number;
  description?: string;
  sort?: number;
  status?: number;
  progress?: number;
  score?: number;
  recommendation?: string;
  children?: Skill[];
}

export interface SkillProgress {
  id?: number;
  userId?: number;
  skillId?: number;
  progress?: number;
  status?: number;
  score?: number;
  lastStudyTime?: string;
  recommendation?: string;
}
