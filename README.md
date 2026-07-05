# AI伴学与职业成长平台

本项目基于精简后台底座改造，保留系统管理、基础设施、Vue3 管理后台外壳和 AI 伴学业务模块，不引入商城、支付、CRM、ERP、WMS、MES、IoT、IM 等完整版业务模块。

## 项目结构

```text
yudao-server                       后端启动模块
yudao-framework                    公共框架模块
yudao-module-system                系统权限模块
yudao-module-infra                 基础设施模块
yudao-module-aistudy               AI伴学业务模块
yudao-ui/yudao-ui-admin-vue3       Vue3 管理后台
sql/mysql                          数据库脚本
```

## AI 学习闭环

已新增学习闭环最小实现：

- `ai_knowledge_point`：AI 对话沉淀出的知识点
- `ai_learning_event`：聊天、知识点新增、复习、报告生成等学习事件
- `ai_user_learning_profile`：用户经验、等级、连续学习天数
- `ai_tutor_memory`：AI 辅导记忆，用于下一轮对话上下文

核心流程：

1. 用户在 AI 智能辅导页选择技能并提问。
2. Chatflow 返回结构化 JSON。
3. 后端解析 `knowledgePoints`，写入知识点和学习事件。
4. 后端根据知识点复习阶段和对话次数自动更新技能掌握度。
5. 今日复习页提交“记住了 / 再复习”，更新下一次复习时间。
6. 学习闭环看板和学习画像读取闭环数据展示。

## Dify Chatflow 输出格式

## Dify 配置

后端读取的配置位于 `yudao-server/src/main/resources/application-local.yaml`：

```yaml
aistudy:
  dify:
    base-url: ${DIFY_BASE_URL:https://api.dify.ai/v1}
    chat-api-key: ${DIFY_CHAT_API_KEY:}
    response-mode: blocking
    timeout-seconds: 60
```

Windows PowerShell 配置方式：

```powershell
[Environment]::SetEnvironmentVariable("DIFY_CHAT_API_KEY", "app-你的Key", "User")
[Environment]::SetEnvironmentVariable("DIFY_BASE_URL", "https://api.dify.ai/v1", "User")
```

如果使用本地 Dify，把 `DIFY_BASE_URL` 改成本地地址，例如 `http://127.0.0.1:8088/v1`。

未配置 `DIFY_CHAT_API_KEY` 时，后端会返回 AI 服务不可用错误；配置 `DIFY_BASE_URL` 和 `DIFY_CHAT_API_KEY` 后，会真实调用 Dify Chatflow。

Dify 的最终回答建议返回 JSON 字符串：

```json
{
  "answer": "给学生看的自然语言回答",
  "knowledgePoints": ["知识点 1", "知识点 2"],
  "quiz": {
    "question": "小测问题",
    "answer": "参考答案",
    "difficulty": 1
  },
  "stuckPoints": ["学生当前卡点"],
  "nextSuggestion": "下一步学习建议",
  "tutorMemoryUpdate": {
    "taughtSummary": "本轮讲过什么",
    "stuckPoints": "仍然卡在哪里",
    "nextSuggestion": "下次建议"
  }
}
```

## 数据库脚本

按顺序执行：

```sql
source sql/mysql/quartz.sql;
source sql/mysql/ruoyi-vue-pro.sql;
source sql/mysql/ai_study.sql;
source sql/mysql/ai_study_learning_flow.sql;
source sql/mysql/ai_study_tenant_fix.sql;
source sql/mysql/ai_study_menu.sql;
```

`ai_study_learning_flow.sql` 会创建知识点、学习事件、学习画像、导师记忆等学习闭环表。
`ai_study_tenant_fix.sql` 会补齐 AI 伴学表的租户字段，以及聊天记录里的 Dify 消息与原始响应字段。
`ai_study_menu.sql` 会重建 AI伴学核心菜单并绑定管理员角色权限，不会删除系统管理和基础设施菜单。
