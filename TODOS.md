# Hybatis DataFrame-like Query DSL 开发计划

本文档记录 `com.hyd.hybatis.query` 包的待开发功能清单。

## 重构说明

由于原有设计存在架构问题，已于 2026-02-13 完全重写整个 query 包。新设计将遵循 Spark DataFrame 的设计理念，核心特点：

1. **列引用封装** - 使用 `ColRef` 统一抽象列引用方式
2. **不可变操作** - `select()/drop()` 等变换操作返回新对象
3. **纯 SQL 生成器** - 不执行 SQL，只生成 `SqlCommand`

## 核心架构设计

### 接口与类

- `Query` - 表示查询的统一接口
- `ColRef` - 列引用接口，支持字符串、Column、Getter
- `AbstractQuery` - Query 的基础实现
- `Table<T>` - 基于实体类的数据源
- `SelectedQuery` - 变换后的查询结果
- `Column` - 表示字段、常量或表达式
- `Filter` - 表示过滤条件

### 设计原则

1. **可执行** - 可生成完整 SELECT...FROM...WHERE... 语句
2. **可嵌套** - 可作为子查询嵌入其他查询
3. **可连接** - 支持 JOIN 操作组合查询
4. **不可变** - 变换操作返回新对象，保证线程安全

## 开发任务清单

### 第一阶段：核心架构

### 1. 基础接口和类
- [x] 创建 `Expression` 抽象类及其实现
  - [x] `Expression` - 表达式抽象基类
  - [x] `AttributeExpression` - 表列/视图列
  - [x] `FunctionExpression` - 函数表达式
  - [x] `AggregateExpression` - 聚合表达式
  - [x] `BinaryExpression` - 二元运算表达式
  - [x] `AliasExpression` - 别名表达式
- [x] 创建 `Column` 接口及其实现
  - [x] `Column` - 列接口
  - [x] `SimpleColumn` - 简单列实现
  - [x] `AggregatedColumn` - 聚合列实现
- [x] 创建 `DataSet` 抽象类
- [x] 创建 `TableDataSet` 类 - 表/视图数据源
- [x] 创建 `JoinDataSet` 类
- [x] 创建 `JoinCondition` 类

### 2. 过滤条件相关
- [x] 创建 `Filter` 接口
- [ ] 创建 `AbstractFilter` 抽象类
- [ ] 创建各种 Filter 实现类
  - [ ] `Equal` - 等于
  - [ ] `GreaterThan` - 大于
  - [ ] `LowerThan` - 小于
  - [ ] `GreaterThanOrEqual` - 大于等于
  - [ ] `LowerThanOrEqual` - 小于等于
  - [ ] `In` - IN 操作
  - [ ] `NotIn` - NOT IN 操作
  - [ ] `Between` - BETWEEN 操作
  - [ ] `Exists` - EXISTS 子查询
  - [ ] `CompositeFilter` - 复合条件（AND/OR/NOT）

### 第二阶段：查询构建功能

### 3. 基础查询功能
- [x] 实现 SELECT/FROM 结构 (DataSet.toSqlCommand)
- [x] 实现列选择 (DataSet.select)
- [x] 实现别名 (Column.as)
- [ ] 实现 LIMIT/OFFSET

### 4. 变换操作
- [x] 实现 `select()` 方法
- [x] 实现 `drop()` 方法
- [ ] 实现 `withColumn()` 方法 - 添加计算列

### 5. 过滤操作
- [x] 实现 `filter()` 方法
- [x] 实现 WHERE 子句生成

### 6. JOIN 操作
- [x] 创建 `JoinDataSet` 类
- [x] 实现 `join()` 方法
- [x] 实现 `JoinCondition.toSqlCommand()`
- [ ] 实现 `leftJoin()` 方法 (基于 join())
- [ ] 实现 `rightJoin()` 方法 (基于 join())
- [ ] 实现 `innerJoin()` 方法 (基于 join())
- [ ] 实现 `crossJoin()` 方法 (基于 join())

### 第三阶段：高级功能

### 8. 排序
- [ ] 创建 `OrderBy` 类
- [ ] 实现 `orderBy()` 方法
- [ ] 实现 `orderByAsc()` 方法
- [ ] 实现 `orderByDesc()` 方法

### 9. 聚合
- [ ] 实现 `groupBy()` 方法
- [ ] 实现 `agg()` 方法
- [ ] 实现 `count()`/`sum()`/`avg()` 等聚合函数

### 10. HAVING 子句
- [ ] 实现 `having()` 方法
- [ ] 生成 HAVING 子句

### 11. 集合操作
- [ ] 实现 `union()` 方法
- [ ] 实现 `unionAll()` 方法
- [ ] 实现 `intersect()` 方法
- [ ] 实现 `except()` 方法

### 12. 去重
- [ ] 实现 `distinct()` 方法
- [ ] 生成 SELECT DISTINCT

### 13. 窗口函数
- [ ] 创建 `WindowSpec` 类
- [ ] 实现 `over()` 方法

### 14. CTE
- [ ] 实现 `with()` 方法
- [ ] 生成 WITH 子句

### 第四阶段：测试

### 15. 单元测试
- [ ] 编写基础查询测试
- [ ] 编写 JOIN 测试
- [ ] 编写聚合测试
- [ ] 编写复杂查询测试

## 完成进度统计

- 总任务数: 19
- 已完成: 13
- 进行中: 0
- 待开始: 6
- 完成度: 68%

## 更新日志

- 2026-02-14: 重构 DataSet 为抽象类，创建 TableDataSet 代表表/视图数据源
- 2026-02-14: 完成 Filter 接口和 DataSet.filter() 方法，支持 WHERE 子句
- 2026-02-14: 取消 Query/Table 接口设计，统一使用 DataSet 作为核心查询对象
- 2026-02-14: 完成 JoinDataSet 和 JoinCondition.toSqlCommand() 实现
- 2026-02-14: 完成 DataSet.toSqlCommand() 基础 SELECT/FROM 实现
- 2026-02-13: 完全重写 query 包，创建新的开发计划
- 2026-02-13: 完成 Expression-based 架构和 DataSet 的 select/drop 方法
