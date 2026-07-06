#!/bin/bash
set -e

mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" <<'EOSQL'
source /sql/mysql/ruoyi-vue-pro.sql;
source /sql/mysql/quartz.sql;
source /sql/mysql/ai_study.sql;
source /sql/mysql/ai_study_learning_flow.sql;
source /sql/mysql/ai_study_menu.sql;
source /sql/mysql/ai_study_tenant_fix.sql;

UPDATE system_tenant SET name = 'AI伴学平台' WHERE id = 1;

UPDATE system_menu
SET status = 1
WHERE deleted = b'0'
  AND id NOT IN (
    1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308,
    1310, 1311, 1312, 1313,
    1320, 1321, 1322, 1323,
    1330, 1331, 1382,
    1340, 1341, 1342, 1343,
    1350, 1360, 1370, 1371, 1380, 1381
  );
EOSQL
