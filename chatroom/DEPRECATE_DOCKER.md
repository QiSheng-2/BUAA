# Docker 已弃用（项目当前默认使用本机开发）

本项目已切换为默认不使用 Docker 进行本地开发。保留 `Dockerfile` 与 `docker-compose*.yml` 作为备用/部署参考，但本地开发时建议使用本机 JDK/Maven + 本地服务（Redis、MySQL）。

如果你想恢复使用 Docker：

1. 确认 Docker Desktop 已正确安装并能访问 Docker Hub（或配置好企业镜像/代理）：
   - Docker Desktop -> Settings -> Resources -> Proxies（如需代理）
2. 构建并启动（在项目根）：
```powershell
# 使用项目内的 settings.xml
docker build -t chatroom:local .
docker-compose -f docker-compose.dev.yml up --build
```

3. 如果在中国大陆建议使用 `settings.xml`（已包含阿里云镜像），以加速 Maven 依赖下载。

