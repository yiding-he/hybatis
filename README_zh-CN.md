[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

<img src="docs/imgs/logo.png" alt="LOGO" width="300">

一个在 [Spring Boot](https://spring.io/projects/spring-boot) 项目中使用的 MyBatis 扩展框架。

Hybatis 提供的主要功能：

1. 直接执行 SQL
2. 提供 CrudMapper 基础接口
3. 从 SpringMVC Controller 直接透传给 Mapper 的查询条件封装

## 0. 启用 Hybatis

启用 Hybatis 需要先编译本项目然后包含到你的项目依赖关系中。如果你用 Maven，则可以直接用 `mvn install -maven.test.skip=true` 来安装到本地 Maven 库。

### 将 Hybatis 加入项目

在包含了 MyBatis 框架的 Spring Boot 
项目中的配置类上添加注解 `@Import(HybatisConfigurator.class)` 即可。例如：

```java
@SpringBootApplication
@Import(HybatisConfigurator.class)  // 加上这行
public class MyApplication {

   public static void main(String[] args) {
      SpringApplication.run(MyApplication.class, args);
   }
}
```

## 1. 执行 SQL

启用 Hybatis 后，你就有了一个随时可以通过 `@Autowired` 来注入的 `Hybatis` 对象。下面是一个例子:

```java
@Service
public class SampleService {

    @Autowired
    private Hybatis hybatis;

    // 执行 insert 语句
    public void insert(User user) {
        val sql = "insert into user(id, name) values(?,?)";
        hybatis.execute(sql, user.getId(), user.getName());
    }

    // 或者使用封装的 Insert 对象
    public void insert2(User user) {
        hybatis.execute(Sql.Insert("user")
                .Values("id", user.getId())
                .Values("name", user.getName())
        );
    }
    
    // 查询结果封装为 Row 对象，Row 是 Map 的子类
    public List<Row> queryUsersByKeyword(String userNameKeyword) {
        return hybatis.queryList(
                "select * from user where name like ?", "%" + userNameKeyword + "%");
    }
    
    // 执行数据库事务
    public void insertUsers(List<User> users) {
        val sql = "insert into user(id, name) values(?,?)";
        hybatis.runTransaction(() -> {
            for (User user : users) {
                hybatis.execute(sql, user.getId(), user.getName());
            }
        });
    }
    
    // 执行批处理
    public void insertUsers2(List<User> users) {
        var batchCommand = new BatchCommand();
        batchCommand.setStatement("insert into user(id, name) values(?, ?)");
        batchCommand.setParams(users.stream().map(
                user -> List.of(user.getId(), user.getName())
        ));
        var effectedRows = hybatis.execute(batchCommand);
    }
    
    // 大量数据自动分批次插入
    public void insertUsers3(Stream<User> users) {
        var sql = "insert into user(id, name) values(?,?)";
        var batchSize = 100;
        var batchExecutor = new BatchExecutor(hybatis, sql, batchSize);
        
        users
            .map(user -> List.of(user.getId(), user.getName()))
            .forEach(list -> batchExecutor.feed(list));
        
        batchExecutor.finish();
    }
}
```


## 2. 处理动态条件

本框架会对满足条件的 Mapper 方法自动生成并注册 MappedStatement，这样能节省开发者的一部分工作量：

1. 开发者不需要针对这些方法在注解或 XML 中编写 SQL；
2. 开发者不需要因为表设计变更，而维护这些方法对应的 SQL。

### 实现原理：

1. 当 SpringBoot 项目启动时，Hybatis 会自动扫描所有 Mapper 接口，找出那些没有注册到 MyBatis 框架的方法；
2. 遍历这些方法，如果方法满足先决条件，则 Hybatis 会为这些方法生成 MappedStatement 并注册到 Mybatis。

注意：你可以随时将 Hybatis 加到现有项目中。对于项目中已经实现的 Mapper 方法，Hybatis 不会对其进行更改。

### Hybatis 能处理哪些 Mapper 方法？

1. 方法必须带上 `@HbSelect`、`@HbInsert` 或 `@HbUpdate` 注解，以便识别操作类型；
2. 带上 `@HbSelect` 注解的方法只有一个参数，参数类型满足下面条件之一：
   1. 类型为 `com.hyd.hybatis.Conditions` 
   2. 包含一个或多个类型为 `com.hyd.hybatis.Condition` 成员的 JavaBean 类；
3. 带上 `@HbInsert` 注解的方法只有一个参数，参数类型为 JavaBean 类；
4. 带上 `@HbUpdate` 注解的方法有两个参数，第一个参数满足条件 2，第二个参数满足条件 3；

_另：如果一个 @HbSelect 方法返回值为数字类型时，Hybatis 会认为这是一个 count 方法，将生成 `select count(1) from ...` 这样的 SQL。_

### 使用步骤

_示例数据库打包在 `src/test/data/employees.7z` 文件中，将文件内容解压到项目根目录下的
`.local/` 文件夹下（请自行创建 .local 文件夹）即可运行 src/test/java 下的
`HybatisSpringBootTestApplication` 类。_

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
    // curl "http://host:port/sample/search?column=id,name&id.between=1,9&name.contains=test&limit=5"
    // 将得到查询语句 "SELECT id, name FROM sample WHERE id BETWEEN 1 AND 9 AND name LIKE '%test%' LIMIT 5"。
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

