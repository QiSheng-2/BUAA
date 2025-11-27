# 使用官方 OpenJDK 17 镜像
# 原来的写法 (删除)
# FROM openjdk:17-jdk-slim

# 新的写法 (推荐，基于 Ubuntu/Debian)
FROM eclipse-temurin:17-jdk-jammy

# 或者如果你想要极小的体积 (基于 Alpine Linux)
# FROM eclipse-temurin:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制构建好的 jar 包（注意：jar 名需与你打包后一致）
COPY target/chatroom-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]