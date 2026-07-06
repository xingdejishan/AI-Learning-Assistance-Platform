export interface LearningProfile {
  id?: number;
  userId?: number;
  totalXp?: number;
  levelNo?: number;
  currentStreak?: number;
  longestStreak?: number;
  lastActiveDate?: string;
  weeklyXp?: string;
}

export interface KnowledgePoint {
  id?: number;
  userId?: number;
  skillId?: number;
  skillName?: string;
  content?: string;
  stage?: number;
  nextReviewTime?: string;
  lastReviewTime?: string;
}

export interface ReviewSubmitResponse {
  newStage?: number;
  nextReviewTime?: string;
  mastery?: number;
  xpGained?: number;
}

export interface Report {
  title?: string;
  summary?: string;
  strongPoints?: string[];
  weakPoints?: string[];
  nextPlan?: string[];
  riskReminder?: string;
}

export interface LearningFlowSummary {
  profile?: LearningProfile;
  todayReviewCount?: number;
  masteryRanking?: Object[];
  stageDistribution?: Object[];
  recentEvents?: Object[];
}
