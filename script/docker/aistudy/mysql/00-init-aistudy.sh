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
EOSQL
