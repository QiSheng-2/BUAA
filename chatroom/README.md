# 聊天室 — 项目概览与开发指南

本 README 简洁说明项目结构、核心模块、主要数据流、已选的 ID 策略（方案 A）、本地运行/测试方法。

---

## 核心概要
- 技术栈：Java 21、Spring Boot 3.3.x、Spring WebSocket（STOMP + SockJS）、Spring Data JPA、Redis、MySQL。  
- 打包：Maven（pom.xml）  
- 前端：单页静态资源，位于 `src/main/resources/static/index.html`（原生 JS + STOMP 客户端）。

- 

---

## 项目结构（要点）

- `src/main/java/com/example/chatroom/`
  - `ChatroomApplication.java` — Spring Boot 启动类
  - `config/` — 配置类（安全、WebSocket、Redis、静态资源等）
  - `controller/` — REST 与 WebSocket 映射，例如：`AuthController`、`ChatController`、`RoomController`、`MessageController`、`FileController`、`UserController`、`FriendController`、`SearchController`、`ReceiptController`
  - `service/` — 业务逻辑核心：`MessageService`、`RoomService`、`PresenceService`、`ReceiptService`、`FriendService`、`UserProfileService`、`BotService`、`SearchService`、`MessageBroadcastListener` 等
  - `entity/` — JPA 实体：`User`、`Message`、`MessageReceipt`、`ChatRoom`、`RoomMember`、`Friendship` 等
  - `repository/` — Spring Data JPA 仓库接口
  - `websocket/` 与 `listener/` — WebSocket 连接/订阅监听与 presence 处理
- `src/main/resources/static/index.html` — 客户端页面（聊天 UI、上传、国际化、STOMP）
- `pom.xml` — 构建与依赖清单（包含 JUnit5、Spring Boot 等）

---

## 本地开发（不使用 Docker）
为了简化本地开发与调试，项目默认支持在开发机器上直接运行（不依赖 Docker）。如果你的本地环境已准备好（JDK21、Maven、Redis、MySQL），请按下列步骤运行：

1. 配置 Java 与 Maven
   - 确保 `JAVA_HOME` 指向 JDK 21，且 `mvn -v` 能正确显示 Maven 版本。
   - 如果你没有 JDK21，可以使用仓库内的 `build-with-jdk21.ps1` 快速设置并构建。

2. 可选：把项目根下的 `settings.xml` 复制到 Maven 用户配置以加速依赖下载（推荐在中国大陆）

```powershell
Copy-Item .\settings.xml "$env:USERPROFILE\.m2\settings.xml" -Force
```

3. 启动本地开发环境（默认不使用 Docker）

```powershell
# 在项目根
# 默认：本地构建 + 运行 jar
.\dev-up.ps1

# 如果你更愿意使用 Docker（可选），使用：
.\dev-up.ps1 -UseDocker
```

`dev-up.ps1` 会：
- 读取 `.env`（如果存在）并注入环境变量
- 使用 `mvn -DskipTests clean package` 构建项目
- 找到输出 jar 并在后台以 `java -jar` 启动应用

4. 本地服务依赖
   - MySQL：创建数据库 `chatroom` 并在 `application.yml` 或环境变量中配置连接字符串
   - Redis：在 `application.yml` 中使用 `localhost:6379`（或修改为你的 Redis 地址）

5. 运行与调试
   - 使用 IDEA 直接运行 `ChatroomApplication`（推荐，支持断点与热重载）
   - 或者使用命令行启动：

```powershell
mvn -DskipTests spring-boot:run
# 或运行已打包的 jar
java -jar target/chatroom-1.0.0.jar
```

---

## 核心数据模型（简述）
- `User`（实体）：数据库主键 `id: Long`，唯一 `username: String`（作为应用层用户标识）
- `Message`：消息内容、`senderId`（字符串 username）、`targetId`（房间 ID 或 `private_x_y`）、`contentType`、`searchableContent` 等
- `MessageReceipt`：每条消息每个用户的已读状态（`isRead` / `readAt`）
- `ChatRoom`、`RoomMember`、`Friendship`：房间与社交模型（`Friendship` 在 `user_id` / `friend_id` 中保存用户名字符串）

---

## 主要运行时流程
1. 客户端 → STOMP `/app/chat.message` → `ChatController` → `MessageService.saveMessage(dto)`
   - 持久化 `Message`，为接收者创建 `MessageReceipt` 记录
   - 在事务提交后发布 `MessageSavedEvent`（使用 TransactionSynchronization）
2. `MessageBroadcastListener` 监听 `MessageSavedEvent` 并执行 STOMP 广播（群组或私聊）
   - 确保广播在 DB 提交后执行，避免广播与持久化不一致的问题
3. 客户端标记房间已读 → `ReceiptController` → `ReceiptService.markRoomAsRead(...)`（批量更新）
4. 搜索：`SearchService` 使用 `searchableContent` 字段（当前基于数据库全文/LIKE，后期可接入 ES）
5. 文件上传：`FileController` 返回文件 URL，客户端可发送包含 URL 的消息；前端支持缩略图与 lightbox 预览
6. 机器人：以 `@Bot` 开头的消息触发 `BotService` 回复并广播为 Bot 消息

---

## 其他说明
- 项目已移除默认对 Docker 的依赖，推荐使用本机 JDK/Maven + 本地服务进行开发。
- 如需部署到容器或使用 Docker，请联系项目维护者以获取部署指南（或在仓库历史中查看早期 Docker 配置）。

