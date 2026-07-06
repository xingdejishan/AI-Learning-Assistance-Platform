# AI Study Docker 一键启动

启动除 HarmonyOS 客户端以外的服务：

```powershell
.\start-aistudy-docker.ps1
```

如果仓库路径包含中文，脚本会先同步到英文 Docker 工作区再构建：

```text
C:\Users\zhang\Documents\aistudy-docker-workspace
```

服务地址：

- 管理后台：http://localhost:8080
- 后端接口：http://localhost:48080/admin-api
- MySQL：localhost:13306，root/root
- Redis：localhost:16379

HarmonyOS 客户端的 `baseUrl` 要填写电脑局域网 IP：

```text
http://你的电脑局域网IP:48080/admin-api
```

查看日志：

```powershell
docker compose --env-file script/docker/aistudy/docker.env -f docker-compose.aistudy.yml logs -f server
```

停止：

```powershell
.\stop-aistudy-docker.ps1
```

清空数据库并重新初始化：

```powershell
docker compose --env-file script/docker/aistudy/docker.env -f docker-compose.aistudy.yml down -v
.\start-aistudy-docker.ps1
```

如果需要 AI 对话能力，编辑 `script/docker/aistudy/docker.env`：

```text
AISTUDY_DIFY_CHAT_API_KEY=你的后端服务使用的 Dify Key
```

Key 只进入后端容器，不会写入 HarmonyOS 客户端。
