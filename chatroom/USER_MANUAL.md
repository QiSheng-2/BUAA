# Chatroom 项目用户使用手册

## 1. 项目简介
本项目是一个基于 Spring Boot 和 WebSocket 的实时聊天室应用。支持用户注册登录、创建聊天室、好友管理、实时消息推送以及文件上传等功能。

## 2. 环境准备
在运行本项目之前，请确保您的开发环境满足以下要求：

*   **操作系统**: Windows (推荐), Linux, macOS
*   **JDK**: Java 21
*   **构建工具**: Maven 3.6+
*   **数据库**: MySQL 8.0+
*   **缓存**: Redis (项目自带配置，或使用本地安装)

## 3. 快速启动

### 3.1 数据库配置
1.  确保本地 MySQL 服务已启动。
2.  创建一个名为 `chatroom_db` 的数据库。
3.  项目启动时会自动根据实体类更新数据库表结构 (`ddl-auto: update`)。
4.  默认数据库连接信息（可在 `src/main/resources/application.yml` 中修改）：
    *   URL: `jdbc:mysql://localhost:3306/chatroom_db`
    *   Username: `chatuser`
    *   Password: `K9@xQ3#pT7$`

### 3.2 Redis 配置
项目依赖 Redis 进行会话管理或缓存。
*   默认连接地址: `localhost:6379`
*   如果本地未安装 Redis，可参考项目根目录下的 `redis/` 目录进行配置或使用 Docker 启动。

### 3.3 编译与运行
项目提供了便捷的 PowerShell 脚本（Windows环境）：

**方式一：使用脚本启动开发环境**
运行根目录下的 `dev-up.ps1` 脚本（如果脚本包含启动逻辑）：
```powershell
.\dev-up.ps1
```

**方式二：手动编译运行**

1.  **编译打包**:
    ```powershell
    .\build-with-jdk21.ps1
    # 或者使用 maven 命令
    mvn clean package -DskipTests
    ```
2.  **启动应用**:
    ```powershell
    java -jar target/chatroom-1.0.0.jar
    ```

启动成功后，控制台将显示 Spring Boot 启动日志，默认端口为 **8081**。

## 4. 功能使用说明

### 4.1 访问应用
打开浏览器访问：`http://localhost:8081`

### 4.2 用户模块
*   **注册**: 点击首页的注册链接，输入用户名和密码创建新账号。
*   **登录**: 使用注册的账号登录系统，获取 JWT Token。
*   **个人信息**: 支持更新个人资料及头像上传。

### 4.3 聊天功能
*   **公共聊天室**: 登录后可查看并加入已存在的聊天室，或创建新的聊天室。
*   **好友私聊**:
    *   可以通过搜索用户名添加好友。
    *   在好友列表中点击好友头像发起私聊。
*   **实时消息**: 基于 WebSocket 实现，消息实时送达，无需刷新页面。
*   **历史记录**: 支持查看过往聊天记录（前端逻辑位于 `js/chat-history.js`）。

### 4.4 文件传输
*   在聊天窗口中支持发送图片或文件。
*   上传的文件将存储在服务器的 `uploads/` 目录下。
*   文件大小限制：默认为 100MB。

## 5. 配置说明 (`application.yml`)

| 配置项                      | 说明               | 默认值                                  |
| :-------------------------- | :----------------- | :-------------------------------------- |
| `server.port`               | 服务端口           | 8081                                    |
| `spring.datasource.url`     | 数据库连接地址     | jdbc:mysql://localhost:3306/chatroom_db |
| `jwt.expiration`            | Token 过期时间     | 24小时 (86400000ms)                     |
| `app.websocket.require-jwt` | WebSocket 握手鉴权 | false (开发模式关闭，生产建议开启)      |

## 6. 常见问题 (FAQ)

**Q: 启动时报错 "Connection refused: connect"？**
A: 请检查 MySQL 和 Redis 服务是否已在本地启动，且端口号与配置文件一致。

**Q: 无法上传大文件？**
A: 检查 `application.yml` 中的 `spring.servlet.multipart.max-file-size` 配置，目前限制为 100MB。

**Q: WebSocket 连接失败？**
A: 确保前端连接地址正确（`ws://localhost:8081/...`），且未被防火墙拦截。如果开启了 JWT 验证，请确保握手时携带了有效的 Token。
```