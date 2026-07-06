SET NAMES utf8mb4;

CREATE TEMPORARY TABLE IF NOT EXISTS tmp_ai_study_menu_ids (
  id bigint PRIMARY KEY
) ENGINE=Memory;

TRUNCATE TABLE tmp_ai_study_menu_ids;

INSERT IGNORE INTO tmp_ai_study_menu_ids (id)
SELECT id FROM system_menu
WHERE id IN (
  1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308,
  1310, 1311, 1312, 1313,
  1320, 1321, 1322, 1323,
  1330, 1331,
  1340, 1341, 1342, 1343,
  1350, 1360, 1370, 1371, 1380, 1381, 1382
)
   OR parent_id IN (1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308)
   OR component IN (
     'aiStudy/skillTree/index',
     'aiStudy/learningRecord/index',
     'aiStudy/coach/index',
     'aiStudy/resumeDiagnosis/index',
     'aiStudy/studyDashboard/index'
   )
   OR permission LIKE 'ai:%'
   OR permission LIKE 'aistudy:%';

DELETE FROM system_role_menu WHERE menu_id IN (SELECT id FROM tmp_ai_study_menu_ids);
DELETE FROM system_menu WHERE id IN (SELECT id FROM tmp_ai_study_menu_ids);

INSERT INTO system_menu
  (id, name, permission, type, sort, parent_id, path, icon, component, component_name,
   status, visible, keep_alive, always_show, creator, create_time, updater, update_time, deleted)
VALUES
  (1300, 'AI助学系统', '', 1, 1, 0, '/aistudy', 'ep:reading', NULL, NULL,
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1301, '技能树管理', 'aistudy:skill:query', 2, 1, 1300, 'skill', 'ep:connection', 'aistudy/skill/index', 'AiStudySkill',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1302, '学习记录', 'aistudy:learning-record:query', 2, 2, 1300, 'learning-record', 'ep:notebook', 'aistudy/learning-record/index', 'AiStudyLearningRecord',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1303, 'AI 智能辅导', 'aistudy:chat:use', 2, 3, 1300, 'chat', 'ep:chat-dot-round', 'aistudy/chat/index', 'AiStudyChat',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1304, '简历诊断', 'aistudy:resume:query', 2, 4, 1300, 'resume', 'ep:document-checked', 'aistudy/resume/index', 'AiStudyResume',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1305, '学习数据看板', 'aistudy:dashboard:query', 2, 5, 1300, 'dashboard', 'ep:data-analysis', 'aistudy/dashboard/index', 'AiStudyDashboard',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1306, '学习闭环看板', 'aistudy:learning-flow:query', 2, 6, 1300, 'learning-flow', 'ep:trend-charts', 'aistudy/learning-flow/index', 'AiStudyLearningFlow',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1307, '今日复习', 'aistudy:review:query', 2, 7, 1300, 'review', 'ep:calendar', 'aistudy/review/index', 'AiStudyReview',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1308, '学习画像', 'aistudy:profile:query', 2, 8, 1300, 'profile', 'ep:user', 'aistudy/profile/index', 'AiStudyProfile',
   0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),

  (1310, '技能查询', 'aistudy:skill:query', 3, 1, 1301, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1311, '技能新增', 'aistudy:skill:create', 3, 2, 1301, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1312, '技能修改', 'aistudy:skill:update', 3, 3, 1301, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1313, '技能删除', 'aistudy:skill:delete', 3, 4, 1301, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1320, '学习记录查询', 'aistudy:learning-record:query', 3, 1, 1302, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1321, '学习记录新增', 'aistudy:learning-record:create', 3, 2, 1302, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1322, '学习记录修改', 'aistudy:learning-record:update', 3, 3, 1302, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1323, '学习记录删除', 'aistudy:learning-record:delete', 3, 4, 1302, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1330, 'AI 辅导使用', 'aistudy:chat:use', 3, 1, 1303, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1331, 'AI 辅导记录查询', 'aistudy:chat:query', 3, 2, 1303, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1382, '小测题 AI 判题', 'aistudy:quiz:submit', 3, 3, 1303, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1340, '简历诊断查询', 'aistudy:resume:query', 3, 1, 1304, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1341, '简历诊断', 'aistudy:resume:diagnose', 3, 2, 1304, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1342, '简历上传', 'aistudy:resume:upload', 3, 3, 1304, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1343, '简历删除', 'aistudy:resume:delete', 3, 4, 1304, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1350, '学习数据看板查询', 'aistudy:dashboard:query', 3, 1, 1305, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1360, '学习闭环查询', 'aistudy:learning-flow:query', 3, 1, 1306, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1370, '今日复习查询', 'aistudy:review:query', 3, 1, 1307, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1371, '今日复习提交', 'aistudy:review:submit', 3, 2, 1307, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1380, '学习画像查询', 'aistudy:profile:query', 3, 1, 1308, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0'),
  (1381, '学习报告生成', 'aistudy:report:generate', 3, 2, 1308, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), 'admin', NOW(), b'0');

INSERT IGNORE INTO system_role_menu
  (role_id, menu_id, creator, create_time, updater, update_time, deleted)
SELECT role_id, menu_id, 'admin', NOW(), 'admin', NOW(), b'0'
FROM (
  SELECT r.id AS role_id, m.id AS menu_id
  FROM system_role r
  JOIN system_menu m ON m.id IN (
    1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308,
    1310, 1311, 1312, 1313,
    1320, 1321, 1322, 1323,
    1330, 1331, 1382,
    1340, 1341, 1342, 1343,
    1350, 1360, 1370, 1371, 1380, 1381
  )
  WHERE r.deleted = b'0'
    AND (r.id IN (1, 300) OR r.code IN ('super_admin', 'ai_admin', 'admin'))
) t;

DROP TEMPORARY TABLE IF EXISTS tmp_ai_study_menu_ids;
