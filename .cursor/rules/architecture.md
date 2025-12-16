---
description: "This rule provides standards for backend layering (Controller / AppService / Service / Repository)"
alwaysApply: true
---

## 分层架构约定（Controller / AppService / Service / Repository）

本项目采用严格的四层业务分层，请在写代码和重构时遵守以下规则。

---

### 1. `controller` 层（如 `*Controller`）

**职责**

- 仅负责：
  - 接收 HTTP 请求、解析路径/查询参数/请求体
  - 基础参数校验（必填、格式、简单范围等）
  - 将请求数据转换为 DTO/命令对象
  - 调用对应的 `appservice` 层方法
  - 将返回结果包装为 HTTP 响应
- 不做：
  - 不直接访问 `repository`
  - 不实现复杂业务规则或流程编排
  - 不写事务控制逻辑

**约束**

- Controller 只能依赖 `appservice`，不要直接依赖 `service` 或 `repository`。
- 方法命名建议与用例/场景相关，如：`createProduct`, `updateProduct`, `listProducts`。

---

### 2. `appservice` 层（如 `*AppService`）

**职责**

- 负责**编排业务流程**，但不实现细节业务规则：
  - 组合和调用多个 `service` 层服务来完成一个完整用例
  - 处理跨多个 service 的事务边界（如需要）
  - 做简单的防御性检查、权限/上下文判断（若与网关无强耦合）
- 将 Controller 传入的 DTO/命令对象拆分，分发给各个 `service`。

**约束**

- AppService 不直接访问 `repository`（除非特别简单的场景且已达成共识，一般也建议通过 service）。
- AppService 应该依赖多个 `service`（按需），不包含具体业务算法逻辑。
- 一个用例一个方法，方法应表达清晰的业务意图，如：`createProduct`, `purchaseProduct`, `handleSalesReturn`。

---

### 3. `service` 层（如 `ProductCreateService`, `ProductUpdateService`）

**职责**

- 承载**具体业务逻辑**和规则，是领域逻辑的主要承载层：
  - 每个 Service 负责一种相对独立的业务能力/用例步骤
  - 示例：
    - `ProductCreateService`：负责产品创建的所有规则检查与数据处理
    - `ProductUpdateService`：负责产品修改的校验、状态变更等
- 直接使用 `repository` 进行数据读写，实现业务规则所需的数据操作。

**约束**

- 一个 Service 聚焦一个明确的业务能力，避免“巨石 Service”。
- Service 不处理 HTTP 相关概念（请求/响应对象、注解等）。
- Service 应尽量保持无状态（除依赖注入的组件），方便复用与测试。
- Service 可以被多个 AppService 复用。

---

### 4. `repository` 层（如 `*Repository`）

**职责**

- 专注于**数据访问**，提供基础的增删改查：
  - 根据 ID / 条件查询
  - 插入、更新、删除
  - 分页、排序等
- 不写任何业务规则，只负责持久化和数据结构映射。

**约束**

- Repository 不依赖 Controller 或 AppService。
- 命名遵循 Spring Data / 领域含义，如：`ProductRepository`, `AccountRepository`。
- 保持接口简洁、语义清晰，复杂查询可单独抽取自定义实现。

---

### 5. 调用方向总则

- **Controller → AppService → Service → Repository**
- 禁止：
  - Controller 直接调用 Service 或 Repository
  - AppService 直接调用 Repository（如有必须场景，应评估是否抽到 Service）
  - 反向依赖（如 Service 依赖 AppService）
- 主要业务逻辑必须放在 `service` 层，`appservice` 仅做**编排和事务/流程控制**。

---

### 6. 返回值、DTO/VO 与 Entity 使用约定

**总体要求**

- 对外暴露的接口（尤其是 HTTP 接口）以及各层之间的公共方法，**必须使用明确的 DTO/VO 类型作为返回值**，避免语义不清。
- 除极少数基础库/工具方法外，**严禁通过 `Map` / `List<Map<...>>` 等弱类型结构返回业务结果**。
- **避免直接使用 Entity 作为接口返回值**，尤其不允许将持久化实体原样透出到 Controller/外部调用方。

**Controller 层**

- Controller 暴露的所有 HTTP 接口返回值：
  - 数据部分必须是 DTO/VO（或基础类型、枚举），**不得是 Entity、`Map` 或 `List<Map>`**。
  - 如项目中有统一响应包装（如 `Result<T>`），`T` 也必须是 DTO/VO，而不是 Entity 或 `Map`。

**AppService 层**

- AppService 对 Controller 或其他上层调用方暴露的方法：
  - 返回值必须是 DTO/VO 或基础类型、枚举。
  - **禁止使用 `Map<String, Object>`、`List<Map<String, Object>>` 等形式作为业务结果返回值**。
  - 不得直接返回 Entity；如确需使用实体数据，应在 AppService 内部转换为对应 DTO/VO 后再返回。
- AppService 内部私有方法如返回 Entity，仅用于本类内部组合逻辑时可以接受，但不得穿透到 Controller。

**Service 层**

- Service 对 AppService 暴露的方法：
  - 应优先返回**明确的业务类型**：领域对象（非持久化泄漏的抽象）、值对象或专门定义的 DTO/VO。
  - **严禁以 `Map` / `List<Map<...>>` 承载主要业务语义**，不得以此作为对 AppService 的返回结果。
-  - **禁止**直接返回任何持久化 Entity 给 AppService；如需使用实体数据，必须在 Service 内部先转换为对应的业务对象/DTO/VO 后再返回。
- Service 内部私有方法如通过 Entity 在同一 Service 内部传递，可视为实现细节，但不应通过公共接口向上层泄漏。

**Repository 层**

- Repository 可以返回 Entity 或用于持久化的投影对象，但：
  - Entity 只能在 `service` / `appservice` 内部消费与转换，**不得原样作为接口返回给 Controller 或外部调用方**。
  - 如需面向上层或外部输出数据，必须在上层转换为 DTO/VO。
