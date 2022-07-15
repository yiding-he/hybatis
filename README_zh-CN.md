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
2. 带上 `@HbSelect` 注解的方法只有一个参数，参数类型为 `com.hyd.hybatis.Conditions` 或者包含一个或多个查询成员的 JavaBean 类；
3. 带上 `@HbInsert` 注解的方法只有一个参数，参数类型为 JavaBean 类；
4. 带上 `@HbUpdate` 注解的方法有两个参数，第一个参数满足条件 2，第二个参数满足条件 3；

### 如何

#### 1. Add `@Import(HybatisConfigurator.class)` to your `@SpringBootApplication` class:

```java
@SpringBootApplication
@Import(HybatisConfigurator.class)
public class Application {
    // ...
}
```

#### 2. Sample Mapper class:

Any unmapped method in the Mapper class will be processed by Hybatis, 
and be mapped automatically if possible.

```java
@Mapper
public interface SampleMapper {
    
    // Mapped if method is annotated by Mybatis annotation or associated with XML script.
    // Hybatis will ignore this method
    @Select("SELECT * FROM sample")
    List<Sample> selectAll();
    
    // Unmapped method, will be processed by Hybatis
    int insertSample(Sample sample);
}
```

You don't need to write any code for unmapped methods. Method `insertSample()` can be used instantly.

```java
@Service
public class SampleService {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    public int saveNewSample(Sample sample) {
        return sampleMapper.insertSample(sample);
    }
}
```

For querying, Hybatis provides a `Condition` class. To use it, 
you need to create a query class:

```java
@Data
@HbQuery(table = "sample")
public class SampleQuery {
    private Condition<String> name;
    private Condition<Long> id;
}
```

Then you can use `SampleQuery` as parameter in mapper methods:

```java
@Mapper
public interface SampleMapper {

    // Unmapped method, will be processed by Hybatis
    List<Sample> selectSamples(SampleQuery query);

}
```

For SpringMVC framework, `SampleQuey` can be easily parsed by a SpringMVC Controller:

```java
@RestController
public class SampleController {
    
    @Autowired
    private SampleMapper sampleMapper;
    
    // Query sample:
    // curl "http://localhost:8080/query?name.contains=John&id.in=1,2,3"
    // "contains" and "in" are directives defined in `Condition` class.
    @GetMapping("/query")
    public List<Sample> selectSamples(SampleQuery query) {
        return sampleMapper.selectSamples(query);
    }
}
```