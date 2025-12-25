# Chatroom - 实时聊天室系统

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/downloads/#java21)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Chatroom** 是一个基于 Spring Boot 和 WebSocket 构建的高性能实时聊天应用。它支持用户注册登录、创建聊天室、好友私聊、实时消息推送、聊天记录保存和搜索以及文件上传等功能。项目采用前后端分离的思想（虽然目前静态资源集成在后端），后端提供 RESTful API 和 WebSocket 服务，前端使用 HTML5 + JavaScript (SockJS + STOMP) 实现。

## ✨ 功能特性

*   **用户认证**：基于 JWT (JSON Web Token) 的安全认证机制，支持注册、登录。
*   **实时通信**：使用 WebSocket + STOMP 协议实现低延迟的消息传输。
*   **聊天模式**：
    *   **公共聊天室**：支持创建和加入多人聊天室，实时群聊。
    *   **好友私聊**：支持添加好友，进行一对一私密聊天。
*   **消息持久化**：所有聊天记录存储在 MySQL 数据库中，支持历史记录查询。
*   **文件传输**：支持发送图片和文件（存储在本地 `uploads/` 目录）。
*   **状态管理**：实时感知用户在线状态。
*   **高可用支持**：集成 Redis 用于缓存和会话管理（可选）。
*   **ai助手**：可以实现基本的功能。

## 🛠 技术栈

**后端**
*   **Java 21**: 编程语言
*   **Spring Boot 3**: 核心框架
*   **Spring Security**: 安全认证与授权
*   **Spring WebSocket**: 实时通信
*   **Spring Data JPA**: 数据持久化
*   **MySQL**: 关系型数据库
*   **Redis**: 缓存与消息中间件
*   **Maven**: 项目构建工具

**前端**
*   **HTML5 / CSS3**: 页面结构与样式
*   **JavaScript (ES6+)**: 交互逻辑
*   **SockJS**: WebSocket 兼容库
*   **STOMP.js**: 消息协议客户端
*   **Bootstrap** (可选): UI 框架

## 🚀 快速开始

### 前置要求
*   JDK 21+
*   Maven 3.6+
*   MySQL 8.0+
*   Redis (可选，推荐安装)

### 1. 克隆项目
```bash
git clone https://github.com/yourusername/chatroom.git
cd chatroom
```

### 2. 配置数据库
1.  启动 MySQL 服务。
2.  创建一个名为 `chatroom_db` 的数据库。
3.  修改 `src/main/resources/application.yml` 中的数据库连接信息（如果需要）：
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/chatroom_db
        username: chatuser
        password: your_password
    ```

### 3. 启动 Redis
确保本地 Redis 服务已启动，默认端口 `6379`。
或者使用项目提供的 `redis/` 目录下的配置启动（如果适用）。

### 4. 编译与运行
**Windows (PowerShell)**
```powershell
# 编译
.\build-with-jdk21.ps1
# 或者
mvn clean package -DskipTests

# 运行
java -jar target/chatroom-1.0.0.jar
```

**Linux / macOS**
```bash
mvn clean package -DskipTests
java -jar target/chatroom-1.0.0.jar
```

启动成功后，访问 `http://localhost:8081` 即可体验。

## 📂 项目结构

```
chatroom/
├── src/
│   ├── main/
│   │   ├── java/com/example/chatroom/  # 后端源码
│   │   │   ├── config/                 # 配置类 (Security, WebSocket, Redis)
│   │   │   ├── controller/             # 控制器 (API 接口)
│   │   │   ├── dto/                    # 数据传输对象
│   │   │   ├── entity/                 # 数据库实体
│   │   │   ├── repository/             # 数据访问层
│   │   │   ├── service/                # 业务逻辑层
│   │   │   └─ websocket/              # WebSocket 相关处理
│   │   └── resources/
│   │       ├── application.yml         # 配置文件
│   │       └── static/                 # 前端静态资源 (HTML, JS, CSS)
├── redis/                              # Redis 配置及数据
├── uploads/                            # 文件上传存储目录
├── build-with-jdk21.ps1                # 构建脚本
└── pom.xml                             # Maven 依赖配置
```

## ⚙️ 关键配置

| 配置项 | 说明 | 默认值 |
| :--- | :--- | :--- |
| `server.port` | 服务端口 | 8081 |
| `jwt.secret` | JWT 签名密钥 | (请在生产环境中修改) |
| `jwt.expiration` | Token 过期时间 | 24小时 |
| `spring.servlet.multipart.max-file-size` | 最大上传文件大小 | 100MB |
| `app.websocket.require-jwt` | WebSocket 握手鉴权开关 | false (开发环境) |

## ❓ 常见问题

**Q: WebSocket 连接失败 (403 Forbidden)?**
A: 检查 `SecurityConfig` 是否放行了 `/ws/**` 路径。如果开启了 JWT 验证，请确保前端连接时携带了有效的 Token。

**Q: 数据库表没有自动创建？**
A: 检查 `application.yml` 中 `spring.jpa.hibernate.ddl-auto` 是否设置为 `update`。

**Q: 无法发送大文件？**
A: 默认限制为 100MB，可在 `application.yml` 中修改 `spring.servlet.multipart.max-file-size`。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源。

