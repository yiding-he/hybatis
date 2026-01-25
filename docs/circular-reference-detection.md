# 循环引用检测机制

## 问题描述

在使用 EXISTS 子查询时，如果用户不小心在子查询中使用了与外层查询相同的查询对象，会导致无限递归，最终抛出 `StackOverflowError`。

例如，以下代码会导致循环调用：

```java
var d = Table.of(Department.class);
var query = d.filter(
    exists(d.join(...).filter(...))  // d 被同时用于外层和子查询
);
```

## 解决方案

Hybatis 现在包含了循环引用检测机制，能够在运行时检测到这种情况并提供清晰的错误信息。

### 实现原理

1. **查询上下文追踪器** (`QueryContextTracker`): 使用线程本地的堆栈来追踪当前正在处理的查询对象
2. **检测点**: 在可能产生递归调用的关键点添加检测，包括：
   - `Exists.toSqlFragment()`
   - `Wrap.getFromFragment()`
   - `Join.getFromFragment()`
3. **错误处理**: 一旦检测到循环引用，立即抛出包含详细信息的 `IllegalStateException`

### 异常信息示例

```
IllegalStateException: Detected circular reference in query: Table. 
This would cause infinite recursion when generating SQL. 
Make sure you are not using the same query object in both outer query and EXISTS subquery.
```

## 使用建议

### 正确的使用方式

```java
var d = Table.of(Department.class);
var dSub = Table.of(Department.class);  // 创建不同的查询对象
var query = d.filter(
    exists(dSub.join(...).filter(...))  // 使用不同的对象
);
```

### 常见错误模式

1. **直接的循环引用**:
   ```java
   // 错误
   var query = d.filter(exists(d.filter(...)));
   
   // 正确
   var query = d.filter(exists(Table.of(Department.class).filter(...)));
   ```

2. **间接的循环引用**:
   ```java
   // 错误
   var wrap = new Wrap(d);
   d.filter(exists(wrap));  // d -> wrap -> d
   
   // 正确
   var wrap = new Wrap(Table.of(Department.class));
   d.filter(exists(wrap));  // d -> wrap -> new object
   ```

## 性能考虑

- 检测机制使用 `ArrayDeque`，查询操作的时间复杂度为 O(n)，其中 n 是查询嵌套的深度
- 对于正常查询（无循环引用），性能开销很小
- 线程本地变量确保了多线程环境下的安全性

## 测试

框架包含了完整的测试用例来验证循环引用检测机制：

- `ExistsTest.testCircularReferenceDetection()` - 验证 EXISTS 的循环检测
- `CircularReferenceDetectionTest.testDetectCircularReferenceInExists()` - 详细测试
- `CircularReferenceDetectionTest.testDetectCircularReferenceInWrap()` - Wrap 类测试
- `CircularReferenceDetectionTest.testNormalExistsWorks()` - 验证正常查询不受影响

## 向后兼容性

此改进完全向后兼容，不会影响现有正常工作的查询代码。只有存在循环引用的代码会受到影响，而这些代码本来就无法正常工作（会抛出 StackOverflowError）。