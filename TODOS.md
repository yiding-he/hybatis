# Hybatis DataFrame-like Query DSL 开发计划

本文档记录 `com.hyd.hybatis.query` 包的待开发功能清单。

## 高优先级

### 1. 实现 ORDER BY 子子句
- [ ] 添加 `orderBy(Column... columns)` 方法
- [ ] 添加 `orderByAsc(Column... columns)` 方法
- [ ] 添加 `orderByDesc(Column... columns)` 方法
- [ ] 在 `Query` 接口中定义方法签名
- [ ] 在 `AbstractQuery` 中实现具体逻辑
- [ ] 在 `toSqlCommand()` 中生成 ORDER BY SQL 片段
- [ ] 编写测试用例

### 2. 增强 GROUP BY + AGG 聚合支持
- [ ] 添加 `agg(Column... aggregationColumns)` 方法
- [ ] 在 `AbstractQuery` 中添加聚合列列表字段
- [ ] 修改 SQL 生成逻辑，将聚合列与分组列分开处理
- [ ] 编写测试用例验证 `groupBy().agg()` 链式调用
- [ ] 示例用法：`groupBy(col("dept_id")).agg(Column.count(col("*")), Column.sum(col("salary")))`

## 中优先级

### 3. 实现集合操作
- [ ] 添加 `union(Query other)` 方法
- [ ] 添加 `unionAll(Query other)` 方法
- [ ] 添加 `intersect(Query other)` 方法
- [ ] 添加 `except(Query other)` 方法
- [ ] 考虑创建新的 Query 子类 `SetOperation` 来处理集合操作
- [ ] 生成对应的 SQL (UNION, UNION ALL, INTERSECT, EXCEPT)
- [ ] 编写测试用例

### 4. 添加更多 JOIN 类型
- [ ] 添加 `innerJoin(Query other, String... columns)` 方法到 `Query` 接口
- [ ] 添加 `crossJoin(Query other)` 方法到 `Query` 接口
- [ ] 在 `Join.JoinBuilder` 中添加对应的构建方法
- [ ] 生成 INNER JOIN 和 CROSS JOIN SQL
- [ ] 编写测试用例

### 5. 实现 HAVING 子句
- [ ] 添加 `List<Filter> getHaving()` 方法到 `Query` 接口
- [ ] 添加 `having(Filter... filters)` 方法
- [ ] 在 `AbstractQuery` 中添加 having 字段和实现
- [ ] 在 SQL 生成逻辑中添加 HAVING 子句
- [ ] 编写测试用例，验证分组后的过滤条件

## 低优先级

### 6. 添加 SELECT DISTINCT 支持
- [ ] 添加 `boolean isDistinct()` 方法到 `Query` 接口
- [ ] 添加 `Query distinct()` 方法
- [ ] 在 `AbstractQuery` 中添加 distinct 标记字段
- [ ] 在 `getSelectFragment()` 中生成 DISTINCT 关键字
- [ ] 编写测试用例

### 7. 实现 FOR UPDATE 锁定支持
- [ ] 添加 `boolean isForUpdate()` 方法到 `Query` 接口
- [ ] 添加 `Query forUpdate()` 方法
- [ ] 在 `AbstractQuery` 中添加 forUpdate 标记字段
- [ ] 在 SQL 生成逻辑末尾添加 FOR UPDATE 子句
- [ ] 编写测试用例

### 8. 添加 CTE (Common Table Expressions) 支持
- [ ] 添加 `Query with(String alias, Query subquery)` 方法
- [ ] 设计 CTE 存储结构（可能需要 Map<String, Query>）
- [ ] 生成 WITH 子句（WITH alias AS (subquery), ...）
- [ ] 处理多个 CTE 和递归 CTE
- [ ] 编写测试用例

### 9. 实现窗口函数支持
- [ ] 创建 `WindowSpec` 类定义窗口规范
- [ ] 添加 `static ExpColumn over(Column column, WindowSpec spec)` 方法
- [ ] 支持 PARTITION BY, ORDER BY, ROWS BETWEEN 等窗口语法
- [ ] 生成对应的 SQL
- [ ] 编写测试用例（如 rowNumber(), rank(), denseRank()）

### 10. 增强 CASE WHEN 表达式
- [ ] 评估现有 `CaseColumn` 的功能完整性
- [ ] 根据实际需求决定是否需要增强
- [ ] 可能需要支持更复杂的嵌套条件
- [ ] 如果不需要增强，将此任务标记为已完成

## 完成进度统计

- 总任务数: 10
- 已完成: 0
- 进行中: 0
- 待开始: 10
- 完成度: 0%

## 更新日志

- 2026-02-13: 创建开发计划文档
