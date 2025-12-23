# chatroom - 项目说明（README.md）

## 项目概述
`chatroom` 是一个基于 Spring Boot 的即时聊天示例项目，支持 WebSocket + STOMP 的实时消息广播、用户认证（JWT）、聊天房间和消息持久化。前端静态资源放在 `src/main/resources/static`，后端使用 Spring MVC、Spring Security、Redis 等组件。

## 技术栈
- Java 21, Spring Boot
- Spring WebSocket / STOMP (`@EnableWebSocketMessageBroker`)
- Spring Security（带 JWT 认证）
- Redis（作为缓存 / 会话支持或消息持久化）
- Maven 构建
- 前端：静态 HTML + JavaScript（SockJS + STOMP 客户端）

## 主要功能
- 用户注册 / 登录（JWT）
- 创建与加入聊天房间
- 私聊 / 群聊消息持久化与历史查询
- 基于 STOMP 的实时消息广播（`/ws` WebSocket 端点）
- 前端页面 `src/main/resources/static/index.html`（包含 JS 客户端）
- 文件上传（`uploads/`）

## 关键路径与类
- WebSocket 端点：`/ws`（在 `WebSocketConfig` 中注册）
- 消息广播主题：`/topic/room.{roomId}`（后端广播到该主题）
- 后端控制器：`com/example/chatroom/controller`（包含 `AuthController`、`ChatController`、`PageController`）
- 安全配置：`com/example/chatroom/config/SecurityConfig`、JWT 过滤器
- 消息监听器：`com/example/chatroom/service/MessageBroadcastListener`（将保存的消息通过 `SimpMessagingTemplate` 广播）

## 目录（简要）
- `src/main/java`：后端源码
- `src/main/resources/static`：前端静态页面（`index.html`、`js/chat-history.js`）
- `src/main/resources/application.yml`：配置
- `redis/`：内置 Redis 配置与数据（用于本地开发）
- `uploads/`：上传文件目录

## 本地运行（Windows）
1. 启动 Redis（如果需要）：项目仓库下有 `redis/`，可按需启动本地 Redis 服务。
2. 使用 PowerShell 脚本快速启动/构建：
   - 构建：运行 `.\build-with-jdk21.ps1`
   - 开发启动：运行 `.\dev-up.ps1`
3. 或者使用 Maven：
   - 打包：`mvn clean package`
   - 运行 JAR：`java -jar target\chatroom-1.0.0.jar`
4. 打开浏览器访问：`http://localhost:8080/`（前端 `index.html`）

## WebSocket / STOMP 使用说明（常见注意点）
- 前端必须使用 SockJS + STOMP：  
  - 创建：`new SockJS('/ws')`  
  - STOMP 客户端：`Stomp.over(socket)`（不要使用 `Stomp.client()` 与 SockJS 混用）
- 连接调用：`stompClient.connect({}, onConnected, onError)`（第一个参数须为 headers 对象，可为空 `{}`）
- 订阅：如订阅房间消息：`stompClient.subscribe('/topic/room.{roomId}', callback)`
- 后端必须启用消息代理：`@EnableWebSocketMessageBroker` + `registry.enableSimpleBroker("/topic")`，并注册端点 `registry.addEndpoint("/ws").withSockJS()`
- Spring Security 可能拦截 STOMP CONNECT：  
  - 放行 `/ws/**`，必要时禁用 CSRF（WebSocket 握手不支持 CSRF token）
- 心跳设置：若连接不稳定，可临时禁用心跳测试：`stompClient.connect({ heartbeat: { out: 0, in: 0 } }, ...)`
- 路径与大小写须一致：前端 `'/ws'` 必须和后端 `addEndpoint("/ws")` 完全匹配

## 常见问题及排查
- 无法建立 STOMP 连接：
  - 检查浏览器 Network 中是否发送 `CONNECT` 帧，是否收到 `CONNECTED`
  - 确保使用 `Stomp.over(new SockJS('/ws'))`
  - 检查后端是否启用了 `enableSimpleBroker` 与 `@EnableWebSocketMessageBroker`
  - 检查 Security 配置是否放行 WebSocket 端点并禁用 CSRF
- 无消息广播但已保存消息：
  - 检查后端 `SimpMessagingTemplate.convertAndSend` 使用的 topic（例如 `/topic/room.{id}`）
  - 前端是否订阅了完全相同的主题路径
- 心跳导致断连：调整或禁用心跳测试网络稳定性

## 开发提示
- 静态页面在 `src/main/resources/static` 下，可直接修改并刷新查看
- 日志查看：检查后端 log（`MessageBroadcastListener` 中有广播日志）
- 若不需要安全验证，可临时注释或放宽 `SecurityConfig` 以便调试 WebSocket

## 构建 / 部署提示
- 使用 Maven 打包并部署生成的 `target\chatroom-1.0.0.jar`
- 若使用容器化或生产环境，请替换内置 Redis 配置并启用安全策略
