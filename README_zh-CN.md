[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Hybatis

一个在 [Spring Boot](https://spring.io/projects/spring-boot) 项目中使用的 MyBatis 扩展框架。

本框架会对满足条件的 Mapper 方法自动生成并注册 MappedStatement，这样能节省开发者的一部分工作量：

1. 开发者不需要针对这些方法在注解或 XML 中编写 SQL；
2. 开发者不需要因为表设计变更，而维护这些方法对应的 SQL。

### 实现原理：

1. 当 SpringBoot 项目启动时，会自动扫描所有 Mapper 接口，找出那些没有注册到 MyBatis 框架的方法；
2. 遍历这些方法，如果方法满足先决条件，则 Hybatis 会为这些方法生成 MappedStatement 并注册到 Mybatis。

注意：你可以随时将 Hybatis 加到现有项目中。对于项目中已经实现的 Mapper 方法，Hybatis 不会对其进行更改。

### Hybatis 有哪些功能？

Hybatis 的目的是简化 Mybatis 中的 insert/update/select 操作。

- 对于 insert/update 操作，当表设计变化时我们往往需要进行维护。Hybatis 可以免去这样的维护。
- 对于 select 查询，当查询条件变化，比如查询表单新增字段时，我们往往需要调整动态 SQL。Hybatis 可以免去这样的调整。

### Hybatis 能处理哪些 Mapper 方法？

1. 方法必须带上 `@HbSelect`、`@HbInsert` 或 `@HbUpdate` 注解，以便识别操作类型；
2. 带上 `@HbSelect` 注解的方法只有一个参数，参数类型满足下面条件之一：
   1. 类型为 `com.hyd.hybatis.Conditions` 
   2. 类型为 `com.hyd.hybatis.Condition`
   3. 包含一个或多个类型为 `com.hyd.hybatis.Condition` 成员的 JavaBean 类；
3. 带上 `@HbInsert` 注解的方法只有一个参数，参数类型为 JavaBean 类；
4. 带上 `@HbUpdate` 注解的方法有两个参数，第一个参数满足条件 2，第二个参数满足条件 3；

### 使用步骤

#### 1. 在 `@SpringBootApplication` 类上面加上 `@Import(HybatisConfigurator.class)` 注解:

```java
@SpringBootApplication
@Import(HybatisConfigurator.class)
public class Application {
    // ...
}
```

#### 2. 以下面的 Mapper 接口为例:

Hybatis 会处理 Mapper 接口中没有被 Mybatis 处理，并满足 Hybatis 自身前提条件的方法。

```java
@Mapper
public interface SampleMapper {

    // 这个方法已经被 Mybatis 处理了，Hybatis 不会处理这个方法。
    @Select("SELECT * FROM sample")
    List<Sample> selectAll();

    // 这个方法既没有 Mybatis 注解，也没有关联到 XML，所以 Mybatis 不会处理这个方法。
    // Hybatis 发现这个方法带上了 @HbInsert 注解，于是会自动生成一个 
    // MappedStatement，并注册到 Mybatis 中。
    @HbInsert(table = "sample")
    int insertSample(Sample sample);
}
```

### 编写查询

Hybatis 提供 `com.hyd.hybatis.Condition` 类来封装一个字段的查询条件。当 Condition 作为 Mapper 方法参数时，Hybatis 
会自动为参数动态生成查询条件：

```java
@Mapper
public interface SampleMapper {

    @HbSelect(table = "sample")
    List<Sample> selectByCondition(Condition condition);
    
    // 使用示例
    default List<Sample> selectById(Long id) {
        Condition condition = Condition.of("id").eq(id);
        return selectByCondition(condition);
    }
}
```

如果有多个查询条件，可以使用 `com.hyd.hybatis.Conditions` 类来封装：

```java
@Mapper
public interface SampleMapper {

    @HbSelect(table = "sample")
    List<Sample> selectByConditions(Conditions conditions);
    
    // 使用示例
    default List<Sample> selectByConditions(String nameKeyword, int minId, int maxId) {
        Conditions conditions = new Conditions()
                .with("id", c -> c.between(minId, maxId))
                .with("name", c -> c.contains(nameKeyword));
        return selectByConditions(conditions);
    }
}

// Conditions 可以通过 SpringMVC 的 Controller 方法直接透传：
@Controller
public class SampleController {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    // 使用示例
    // curl http://host:port/sample/search?id.between=1,9&name.contains=test
    // 将得到查询条件 "id BETWEEN 1 AND 9 AND name LIKE '%test%'"。
    @GetMapping("/sample/search")
    public List<Sample> selectByConditions(Conditions conditions) {
        return sampleMapper.selectByConditions(conditions);
    }
}
```

你也可以将条件封装到 JavaBean 当中：

```java
import com.hyd.hybatis.Condition;

@Data
public class SampleQuery {
    private Condition<String> nameKeyword;
}

@Mapper
public interface SampleMapper {

    @HbSelect(table = "sample")
    List<Sample> selectByQuery(SampleQuery query);

    // 使用示例
    default List<Sample> selectByQuery(String nameKeyword) {
        SampleQuery query = new SampleQuery();
        query.setNameKeyword(new Condition<>().contains(nameKeyword));
        return selectByQuery(query);
    }
}

// SampleQuery 可以通过 SpringMVC 的 Controller 方法直接透传：
@Controller
public class SampleController {

    @Autowired
    private SampleMapper sampleMapper;

    // 使用示例
    // curl http://host:port/sample/search?nameKeyword.contains=test
    // 将得到查询条件 "name_keyword LIKE '%test%'"。
    @GetMapping("/sample/search")
    public List<Sample> selectByConditions(SampleQuery query) {
        return sampleMapper.selectByQuery(query);
    }
}
```

