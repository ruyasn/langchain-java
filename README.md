# Agent Platform（智能体平台）

基于 **Java 17**、**Spring Boot 3** 与 **LangChain4j** 的智能体平台，提供 Agent、工作流、插件、知识库与 MCP 能力。

## 模块结构

| 模块 | 说明 |
|------|------|
| `agent-platform-common` | 公共 DTO、模型（Agent、Workflow、Plugin、Knowledge、MCP） |
| `agent-platform-agent` | Agent 服务：LangChain4j ChatModel + ChatMemory，多轮对话与单次执行 |
| `agent-platform-workflow` | 工作流引擎：DAG 定义、拓扑执行、节点类型（START/AGENT/TOOL/CONDITION/END） |
| `agent-platform-plugin` | 插件 SPI：`Plugin` 接口、`PluginRegistry`、示例 Echo 插件 |
| `agent-platform-knowledge` | 知识库 / RAG：创建知识库、文档解析与分块、向量检索、`retrieveForPrompt` |
| `agent-platform-mcp` | MCP 网关：将插件以 Model Context Protocol 工具形式暴露（`/api/mcp/tools`） |
| `agent-platform-api` | REST API：Agent、Workflow、Plugin、Knowledge、MCP 控制器 |
| `agent-platform-app` | Spring Boot 启动模块，聚合上述能力 |

## 能力概览

- **Agent**：注册 Agent 定义（systemPrompt、modelName 等），支持按 session 多轮对话与单次执行。
- **工作流**：定义节点与边，按 DAG 执行；节点可引用 Agent，引擎调用 `AgentService.execute`。
- **插件**：实现 `Plugin` 接口并注册，平台自动发现；提供工具列表与 `executeTool`。
- **知识库**：创建知识库、上传文档/文本、向量检索、RAG 上下文拼接（`retrieveForPrompt`）。
- **MCP**：通过 `McpGateway` 将插件工具以 MCP 风格暴露（`listTools`、`callTool`），便于与 MCP 客户端对接。

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+

### 配置

- **OpenAI**：设置环境变量 `OPENAI_API_KEY`，或在 `application.yml` 中配置 `langchain4j.open-ai.chat-model.*`。未配置时需自行提供 `ChatLanguageModel` Bean。
- **知识库嵌入**：默认使用 LangChain4j 本地嵌入模型（如 `all-minilm-l6-v2`），无需额外 Key。

### 构建与运行

```bash
mvn clean install -DskipTests
cd agent-platform-app
mvn spring-boot:run
```

服务默认端口 **8080**。

### API 示例

- **Agent**
  - `POST /api/agents`：注册 Agent
  - `GET /api/agents`：列表
  - `POST /api/agents/{id}/chat`：多轮对话（body: `{"message":"..."}`，可选 query `sessionId`）
  - `POST /api/agents/{id}/execute`：单次执行（body: `{"task":"..."}`）

- **工作流**
  - `POST /api/workflows`：注册工作流
  - `POST /api/workflows/{id}/run`：执行（body: `{"task":"..."}`）

- **插件**
  - `GET /api/plugins`：列表
  - `POST /api/plugins/{pluginId}/execute?toolName=echo`：执行工具（body 为参数）

- **知识库**
  - `POST /api/knowledge/bases?name=...&description=...`：创建知识库
  - `POST /api/knowledge/bases/{id}/documents`：上传文件（multipart）
  - `POST /api/knowledge/bases/{id}/text`：添加文本（body: `{"text":"...","source":"..."}`）
  - `GET /api/knowledge/bases/{id}/search?q=...&maxResults=5`：向量检索
  - `GET /api/knowledge/bases/{id}/retrieve?q=...&maxResults=5&maxChars=2000`：RAG 上下文

- **MCP**
  - `GET /api/mcp/tools`：列出 MCP 风格工具
  - `POST /api/mcp/tools/{toolName}/call`：调用工具（body 为参数，工具名为 `pluginId.toolName`）

## MCP 说明

当前 MCP 模块通过 **PluginMcpGateway** 将平台插件以 MCP 工具形式暴露，工具名为 `pluginId.toolName`（如 `echo.echo`）。  
如需对接官方 [Model Context Protocol Java SDK](https://github.com/modelcontextprotocol/java-sdk)，可在 `agent-platform-mcp/pom.xml` 中取消注释 `io.modelcontextprotocol.sdk:mcp` 依赖，并扩展 `McpGateway` 实现以调用真实 MCP 客户端。

## 技术栈

- Spring Boot 3.2
- LangChain4j 0.34（Chat、Embedding、Document、RAG）
- Lombok、Jakarta Validation

## License

MIT
