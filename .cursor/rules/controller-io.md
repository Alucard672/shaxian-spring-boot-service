---
description: "Controller 层入参/出参统一规范"
alwaysApply: true
---

## Controller 输入输出规范

本规则用于统一所有 `*Controller` 的 HTTP 输入输出方式，避免风格不一致、难以演进。

---

### 1. 输入参数总则

- **复杂输入必须使用 `xxxRequest` 对象**
  - 复杂输入指：包含多个字段、嵌套结构、筛选条件、排序、时间范围、状态枚举等。
  - Controller 方法中，不允许直接罗列多个业务字段作为参数（除 id、分页参数外）。
  - 约定：
    - 创建、更新类接口：`CreateXxxRequest`, `UpdateXxxRequest`
    - 查询筛选类接口：`XxxQueryRequest` / `XxxSearchRequest`
  - `xxxRequest` 一般通过 `@RequestBody` 接收 JSON 请求体。

- **简单标量参数使用 Path / Query，不放进 RequestBody**
  - 仅 id 一类简单标识符：统一使用 Path 参数（`@PathVariable`）。
  - 分页页码、页大小：统一使用 Query 参数（`@RequestParam`）。
  - 其他简单标量（如开关、少量过滤项）优先用 Query 参数，只有在语义上必须与复杂筛选一起演进时才放入 `xxxRequest`。

---

### 2. Path 参数规范（适用于 ID 等简单标识）

- **使用 `@PathVariable` 接收 ID 等主键/业务唯一键**
  - 示例：`GET /stores/{storeId}`、`DELETE /products/{productId}`。
  - Controller 方法签名示例：
    - `public Xxx get(@PathVariable Long id)`
    - `public void delete(@PathVariable Long id)`
- **一个接口如既有 Path ID 又有复杂条件**
  - Path：主资源标识（如 `storeId`）。
  - Body：复杂过滤、更新内容等（`xxxRequest`）。

---

### 3. 分页参数规范（pageNo / pageSize）

- **统一使用 Query 参数 + `@RequestParam`**
  - `pageNo`：页码，从 1 开始。
  - `pageSize`：每页条数。
  - 示例：`GET /products?pageNo=1&pageSize=20`。
- **Controller 方法示例**
  - `public PageResult<XxxVO> list(@RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestBody XxxQueryRequest request)`
  - 如筛选条件较少且稳定，也可以全部使用 Query 参数：
    - `list(@RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam(required = false) String keyword)`

---

### 4. Request 命名与放置位置

- **命名约定**
  - 入参对象必须以 `Request` 结尾：`XxxRequest`。
  - 将用途体现到名称中：`CreateXxxRequest`, `UpdateXxxRequest`, `SearchXxxRequest`。
- **包结构强制要求**
  - 所有 Controller 入参 `XxxRequest` 必须放在**对应业务的 `dto.request` 包**中，而不是 Controller 包下。
  - 统一包路径规范：
    - 业务维度包：`com.shaxian.biz.dto.<biz>.request`
    - 示例：
      - 门店相关：`com.shaxian.biz.dto.store.request.CreateStoreRequest`
      - 染整相关：`com.shaxian.biz.dto.dyeing.request.DyeingOrderQueryRequest`
      - 联系我们：`com.shaxian.biz.dto.contact.request.ContactCreateRequest`
  - 禁止的做法：
    - 不允许在 `controller` 包下再建 `request` 子包（如 `com.shaxian.biz.controller.store.request`）❌
    - 不允许将 `Request` 类放在 `entity`、`model`、`repository`、`service` 等包下 ❌
  - 业务模块如何划分 `<biz>`：
    - 通常与当前 Controller 所在子包一一对应，例如 `com.shaxian.biz.controller.store.StoreController`
      对应 `com.shaxian.biz.dto.store.request.*`。
    - 若多个 Controller 共享同一业务 DTO，可共用同一个 `<biz>` 维度包，避免重复定义。

---

### 5. 出参（响应）规范（简要）

- **Controller 不直接返回实体对象（Entity）**
  - 统一返回 `VO`/`DTO`，由下层转换完成。
- **列表 / 分页结果**
  - 推荐统一使用 `PageResult<T>` 或类似封装类型承载分页信息。
- **错误处理**
  - 使用全局异常处理器 / 统一响应封装，避免 Controller 手动拼接错误 JSON。

---

### 6. 违例示例（不要这样写）

- 将多个业务字段直接作为 Controller 方法参数：
  - `create(String name, String address, String contact, String phone, ...)` ❌
- 使用 `@RequestBody Long id` 仅为传一个 ID：
  - `public Xxx get(@RequestBody Long id)` ❌
- 将 `pageNo`、`pageSize` 放在 `RequestBody` 中：
  - `@RequestBody XxxQueryRequest{ Integer pageNo; Integer pageSize; ... }` ❌（分页参数应在 Query 中）

---

### 7. 执行层面的要求

- 新增或修改 Controller 时，如发现不符合上述规范，应**顺手重构**为 `xxxRequest + Path + Query` 的组合形式。
- 如遇到特殊业务场景确实无法遵守本规范，需在代码中加简要注释说明原因，并在评审中确认。